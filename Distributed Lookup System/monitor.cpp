#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <netdb.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <sys/wait.h>

#include <string>
#include <iostream>
#include <sstream>
#include <vector>

using namespace std;

#define MAXDATASIZE 3000
#define PORT_TCP_m "26991"


//this function uses code from Beej's Guide to Network Programming.
void *get_in_addr(struct sockaddr *sa)
{
	if (sa->sa_family == AF_INET) {
		return &(((struct sockaddr_in*)sa)->sin_addr);
	}
	return &(((struct sockaddr_in6*)sa)->sin6_addr);
}

int main(int argc, char* argv[]){
	cout<<"The monitor is up and running."<<endl;

	//uses code from Beej's Guide to Network Programming.
	//establish TCP connection to AWS
	int sockfd, numbytes;
	char buf[MAXDATASIZE];
	struct addrinfo hints, *servinfo, *p;
	int rv;
	char s[INET6_ADDRSTRLEN];


	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_STREAM;

	if ((rv = getaddrinfo(NULL, PORT_TCP_m, &hints, &servinfo)) != 0) {
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
		return 1;
	}

	// loop through all the results and connect to the first we can
	for(p = servinfo; p != NULL; p = p->ai_next) {
		if ((sockfd = socket(p->ai_family, p->ai_socktype,
				p->ai_protocol)) == -1) {
			perror("monitor: socket");
			continue;
		}
		if (connect(sockfd, p->ai_addr, p->ai_addrlen) == -1) {
			close(sockfd);
			//perror("monitor: connect");
			continue;
		}
		break;
	}

	if (p == NULL) {
		fprintf(stderr, "monitor: failed to connect\n");
		return 2;
	}

	inet_ntop(p->ai_family, get_in_addr((struct sockaddr *)p->ai_addr),
		s, sizeof s);
	//printf("monitor: connecting to %s\n", s);

	freeaddrinfo(servinfo); // all done with this structure

	while(1){

		//receives result from AWS
		//uses code from Beej's Guide to Network Programming.
		if ((numbytes = recv(sockfd, buf, MAXDATASIZE-1, 0)) == -1) {
			perror("recv");
			exit(1);
		}
		buf[numbytes] = '\0';
		string msg(buf);
		//cout<<"received: "<<msg<<endl;


		//decoding the received message
		string match, similar, match_def, similar_def;
		int match_num = 0; 
		int similar_num = 0;
		string input, function;
		vector<string> results;


		int pos0 = msg.find_first_of(":");//get function
		function = msg.substr(0,pos0);
		string rest0 = msg.substr(pos0+1);
		int pos1 = rest0.find_first_of(":");
		input = rest0.substr(0,pos1);//get input
		string rest1 = rest0.substr(pos1+1);

		if(function=="search"){
			int pos2 = rest1.find_first_of(":");
			match = rest1.substr(0,pos2);
			string rest2 = rest1.substr(pos2+1);
			if(match=="none"){
				match_def = "none";
				match_num = 0;
				if(rest2.substr(0,4)!="none"){//if there is similar words
					int pos3 = rest2.find_first_of(":");
					similar = rest2.substr(0,pos3);
					similar_def = rest2.substr(pos3+1);
					similar_num = 1;
				}
				else{
					similar_num = 0;
					similar = "none";
					similar_def = "none";
				}
				
			}
			else{//there is a match
				match_num = 1;
				int pos3 = rest2.find_first_of(":");
				match_def = rest2.substr(0,pos3);
				string rest3 = rest2.substr(pos3+1);
				if(rest3.substr(0,4)!="none"){//if there is similar words
					int pos4 = rest3.find_first_of(":");
					similar = rest3.substr(0,pos4);
					similar_def = rest3.substr(pos4+1);
					similar_num = 1;
				}
				else{//if there is no similar words
					similar_num = 0;
					similar = "none";
					similar_def = "none";

				}
			}

			//output results of search
			if(match_num!=0){
				cout<<"Found a match for <"<<input<<">:"<<endl;
				cout<<"<"<<match_def<<">"<<endl;
				if(similar_num!=0){
					cout<<"One edit distance match is <"<<similar<<">:"<<endl;
					cout<<"<"<<similar_def<<">"<<endl;
				}
			}
			else{
				cout<<"Found no matches for <"<<input<<">"<<endl;
			}

		}
		else{//prefix and suffix
			if(rest1=="none"){//there is no match
				match_num = 0;
			}
			else{//get all matches
				stringstream ss;
				string s;
				ss.clear();
				ss.str(rest1);
				while(1){
					ss >> s;
					if(ss.fail()){
						break;
					} 
					results.push_back(s);
				}
				match_num = results.size();
				// for(int i=0;i<results.size();i++){
				// 	cout<<results[i]<<endl;
				// }

			}
			
			//output result for prefix and suffix
			if(match_num!=0){
				cout<<"Found <"<<match_num<<"> matches for <"<<input<<">:"<<endl;
				for(int i=0;i<match_num;i++){
					cout<<"<"<<results[i]<<">"<<endl;
				}
			}
			else{
				cout<<"Found no matches for <"<<input<<">"<<endl;
			}
		}

	}	
	// buf[numbytes] = '\0';
	// printf("monitor: received '%s'\n",buf);
	close(sockfd);

	return 0;
}