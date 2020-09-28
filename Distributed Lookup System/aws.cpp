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
#include <iostream>
#include <vector>
#include <sstream>

using namespace std;

#define PORT "3490"
#define PORT_UDP_A "21991"
#define PORT_UDP_B "22991"
#define PORT_UDP_C "23991"
#define PORT_UDP "24991"
#define PORT_TCP_c "25991"
#define PORT_TCP_m "26991"
#define BACKLOG 20
#define MAXDATASIZE 3000
#define MAXBUFLEN 3000

using namespace std;

//this function uses code from Beej's Guide to Network Programming.
void sigchld_handler(int s)
{
	// waitpid() might overwrite errno, so we save and restore it:
	int saved_errno = errno;
	while(waitpid(-1, NULL, WNOHANG) > 0);
	errno = saved_errno;
}

//this function uses code from Beej's Guide to Network Programming.
void *get_in_addr(struct sockaddr *sa)
{
	if (sa->sa_family == AF_INET) {
		return &(((struct sockaddr_in*)sa)->sin_addr);
	}
	return &(((struct sockaddr_in6*)sa)->sin6_addr);
}

//this function uses code from Beej's Guide to Network Programming.
void UDP_send(int servernum, string message){

	int sockfd;
	struct addrinfo hints, *servinfo, *p;
	int rv, numbytes;

	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_DGRAM;
	if(servernum==0){
		if ((rv = getaddrinfo("127.0.0.1", PORT_UDP_A, &hints, &servinfo)) != 0) {
			fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
			return;
		}
	}
	else if(servernum==1){
		if ((rv = getaddrinfo("127.0.0.1", PORT_UDP_B, &hints, &servinfo)) != 0) {
			fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
			return;
		}
	}
	else if(servernum==2){
		if ((rv = getaddrinfo("127.0.0.1", PORT_UDP_C, &hints, &servinfo)) != 0) {
			fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
			return;
		}
	}
	

	for(p = servinfo; p != NULL; p = p->ai_next) {
		if ((sockfd = socket(p->ai_family, p->ai_socktype,
				p->ai_protocol)) == -1) {
			perror("talker: socket");
			continue;
		}
		break;
	}
	if (p == NULL) {
		fprintf(stderr, "talker: failed to create socket\n");
		return;
	}

	char *msg = new char[message.length()+1];
	strcpy(msg,message.c_str());
	if ((numbytes = sendto(sockfd, msg, strlen(msg), 0,
			p->ai_addr, p->ai_addrlen)) == -1) {
		perror("talker: sendto");
		exit(1);
	}

	freeaddrinfo(servinfo);
	//printf("talker: sent %d bytes to %s\n", numbytes, "serverA");
	close(sockfd);
}

////this function uses code from Beej's Guide to Network Programming.
string UDP_recv(){
	int sockfd;
	struct addrinfo hints, *servinfo, *p;
	int rv;
	int yes=1;
	int numbytes;
	struct sockaddr_storage their_addr;
	char buf[MAXBUFLEN];
	socklen_t addr_len;

	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC; // set to AF_INET to force IPv4
	hints.ai_socktype = SOCK_DGRAM;// UDP
	hints.ai_flags = AI_PASSIVE; // use my IP

	if ((rv = getaddrinfo("127.0.0.1", PORT_UDP, &hints, &servinfo)) != 0) {
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
		//return;
	}
	// loop through all the results and bind to the first we can
	for(p = servinfo; p != NULL; p = p->ai_next) {
		if ((sockfd = socket(p->ai_family, p->ai_socktype,
				p->ai_protocol)) == -1) {
			perror("listener: socket");
			continue;
		}
		if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &yes,
				sizeof(int)) == -1) {
			perror("setsockopt");
			exit(1);
		}
		if (bind(sockfd, p->ai_addr, p->ai_addrlen) == -1) {
			close(sockfd);
			perror("listener: bind");
			continue;
		}

		break;
	}


	if (p == NULL) {
		fprintf(stderr, "listener: failed to bind socket\n");
		//return;
	}

	freeaddrinfo(servinfo);

	//printf("listener: waiting to recvfrom...\n");
	addr_len = sizeof their_addr;

	if ((numbytes = recvfrom(sockfd, buf, MAXBUFLEN-1 , 0,
		(struct sockaddr *)&their_addr, &addr_len)) == -1) {
		perror("recvfrom");
		exit(1);
	}
	
	//printf("listener: got packet from %s\n",
		// inet_ntop(their_addr.ss_family,
		// 	get_in_addr((struct sockaddr *)&their_addr),
		// 	s, sizeof s));
	//printf("listener: packet is %d bytes long\n", numbytes);
	buf[numbytes] = '\0';
	//printf("listener: packet contains \"%s\"\n", buf);
	string msg(buf);
	close(sockfd);

	return msg;
}


int main(void){
	//in the main function, I used the code from Beej's Guide to Network Programming, for establishing 
	//TCP connection with client and monitor

	//booting up
	cout<<"The AWS is up and running"<<endl;
	
	int sockfd, new_fd,numbytes; // listen on sock_fd, new connection on new_fd
	struct addrinfo hints, *servinfo, *p;
	struct sockaddr_storage their_addr; // connector's address information
	socklen_t sin_size;
	struct sigaction sa;
	int yes=1;
	char s[INET6_ADDRSTRLEN];
	int rv;
	char buf[MAXDATASIZE];

	memset(&hints, 0, sizeof hints);
	hints.ai_family = AF_UNSPEC;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_flags = AI_PASSIVE; // use my IP


	//TCP*****************************************************************************************
	//code from Beej's Guide to Network Programming,
	if ((rv = getaddrinfo(NULL, PORT_TCP_c, &hints, &servinfo)) != 0) {
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
		return 1;
	}
	// loop through all the results and bind to the first we can
	for(p = servinfo; p != NULL; p = p->ai_next) {
		if ((sockfd = socket(p->ai_family, p->ai_socktype,
				p->ai_protocol)) == -1) {
			perror("server: socket");
			continue;
		}
		if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &yes,
				sizeof(int)) == -1) {
			perror("setsockopt");
			exit(1);
		}
		if (bind(sockfd, p->ai_addr, p->ai_addrlen) == -1) {
			close(sockfd);
			perror("server: bind");
			continue;
		}
		break;
	}

	freeaddrinfo(servinfo); // all done with this structure
	
	if (p == NULL) {
		fprintf(stderr, "server: failed to bind\n");
		exit(1);
	}
	if (listen(sockfd, BACKLOG) == -1) {
		perror("listen");
		exit(1);
	}

	sa.sa_handler = sigchld_handler; // reap all dead processes
	sigemptyset(&sa.sa_mask);
	sa.sa_flags = SA_RESTART;
	if (sigaction(SIGCHLD, &sa, NULL) == -1) {
		perror("sigaction");
		exit(1);
	}

	//TCP for monitor****************************************************************
	//code from Beej's Guide to Network Programming,
	int sockfd2, new_fd2; // listen on sock_fd, new connection on new_fd
	struct addrinfo hints2, *servinfo2, *p2;
	struct sockaddr_storage their_addr2; // connector's address information
	socklen_t sin_size2;
	struct sigaction sa2;
	int yes2=1;
	// char s2[INET6_ADDRSTRLEN];
	int rv2;
	// char buf2[MAXDATASIZE];

	memset(&hints2, 0, sizeof hints2);
	hints2.ai_family = AF_UNSPEC;
	hints2.ai_socktype = SOCK_STREAM;
	hints2.ai_flags = AI_PASSIVE; // use my IP

	if ((rv2 = getaddrinfo(NULL, PORT_TCP_m, &hints2, &servinfo2)) != 0) {
		fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv2));
		return 1;
	}
	// loop through all the results and bind to the first we can
	for(p2 = servinfo2; p2 != NULL; p2 = p2->ai_next) {
		if ((sockfd2 = socket(p2->ai_family, p2->ai_socktype,
				p2->ai_protocol)) == -1) {
			perror("server: socket");
			continue;
		}
		if (setsockopt(sockfd2, SOL_SOCKET, SO_REUSEADDR, &yes2,
				sizeof(int)) == -1) {
			perror("setsockopt");
			exit(1);
		}
		if (bind(sockfd2, p2->ai_addr, p2->ai_addrlen) == -1) {
			close(sockfd2);
			perror("server: bind");
			continue;
		}
		break;
	}

	freeaddrinfo(servinfo2); // all done with this structure
	
	if (p2 == NULL) {
		fprintf(stderr, "server: failed to bind\n");
		exit(1);
	}
	if (listen(sockfd2, BACKLOG) == -1) {
		perror("listen");
		exit(1);
	}

	sa2.sa_handler = sigchld_handler; // reap all dead processes
	sigemptyset(&sa2.sa_mask);
	sa2.sa_flags = SA_RESTART;
	if (sigaction(SIGCHLD, &sa2, NULL) == -1) {
		perror("sigaction");
		exit(1);
	}

	sin_size2 = sizeof their_addr2;
	new_fd2 = accept(sockfd2, (struct sockaddr *)&their_addr2, &sin_size2);
	if (new_fd2 == -1) {
		perror("accept");
		exit(1);
		//continue;
	}

	while(1) { // main accept() loop
		//printf("server: waiting for connections...\n");
		sin_size = sizeof their_addr;
		new_fd = accept(sockfd, (struct sockaddr *)&their_addr, &sin_size);
		if (new_fd == -1) {
			perror("accept");
			exit(1);
			//continue;
		}

		inet_ntop(their_addr.ss_family,
			get_in_addr((struct sockaddr *)&their_addr),
			s, sizeof s);
		//printf("server: got connection from %s\n", s);
		
		
		if ((numbytes = recv(new_fd, buf, MAXDATASIZE-1, 0)) == -1) {
			perror("recv");
			exit(1);
		}
		
		buf[numbytes] = '\0';
		

		string msg(buf);
		int pos = msg.find_first_of(":");
		string function = msg.substr(0,pos);
		string input = msg.substr(pos+1);
		//cout<<"function:"<<function<<" input:"<<input<<endl;
		memset(buf, 0, sizeof buf);

		cout<<"The AWS received input=<"<<input<<"> and function=<"<<function<<"> from the client using TCP over port 25991"<<endl;
		
		//send to and receive from server A, B and C through UDP*******************************************************
		//UDP_send() and UDP_recev() functions use code from Beej's Guide to Network Programming,
		vector<string> all_match_words;
		UDP_send(0, msg);//send to server A
		cout<<"The AWS sent <"<<input<<"> and <"<<function<<"> to Backend-Server A"<<endl;
		string msg_UDP = UDP_recv();
		
		//decode the message from server A
		string matchA, similarA, match_defA, similar_defA;
		int match_numA, similar_numA;
		if(function=="search"){
			int pos1 = msg_UDP.find_first_of(":");
			string rest1 = msg_UDP.substr(pos1+1);
			int pos2 = rest1.find_first_of(":");
			matchA = rest1.substr(0,pos2);
			string rest2 = rest1.substr(pos2+1);

			if(matchA=="none"){// no match words
				match_defA = "none";
				match_numA = 0;
				if(rest2.substr(0,4)!="none"){//if there is similar words
					int pos3 = rest2.find_first_of(":");
					similarA = rest2.substr(0,pos3);
					similar_defA = rest2.substr(pos3+1);
					similar_numA = 1;
				}
				else{//if there is no similar words
					similar_numA = 0;
					similarA = "none";
					similar_defA = "none";
				}
				
			}
			else{//there is a match
				match_numA = 1;
				int pos3 = rest2.find_first_of(":");
				match_defA = rest2.substr(0,pos3);
				string rest3 = rest2.substr(pos3+1);
				if(rest3.substr(0,4)!="none"){//if there is similar words
					int pos4 = rest3.find_first_of(":");
					similarA = rest3.substr(0,pos4);
					similar_defA = rest3.substr(pos4+1);
					similar_numA = 1;
				}
				else{//if there is no similar words
					similar_numA = 0;
					similarA = "none";
					similar_defA = "none";

				}
			}
		}
		else{//prefix and suffix
			int pos = msg_UDP.find_first_of(":");
			string rest = msg_UDP.substr(pos+1);

			if(rest=="none"){//there is no match
				match_numA = 0;
			}
			else{
				stringstream ss;
				vector<string> results;
				string s;
				ss.clear();
				ss.str(rest);
				while(1){
					ss >> s;
					if(ss.fail()){
						break;
					} 
					results.push_back(s);
					all_match_words.push_back(s);
				}
				match_numA = results.size();
				// for(int i=0;i<results.size();i++){
				// 	cout<<results[i]<<endl;
				// }
			}
			
		}
		
		//send to server B
		UDP_send(1, msg);
		cout<<"The AWS sent <"<<input<<"> and <"<<function<<"> to Backend-Server B"<<endl;
		msg_UDP = UDP_recv();

		//decode message from server B
		string matchB, similarB, match_defB, similar_defB;
		int match_numB, similar_numB;
		if(function=="search"){
			int pos1 = msg_UDP.find_first_of(":");
			string rest1 = msg_UDP.substr(pos1+1);
			int pos2 = rest1.find_first_of(":");
			matchB = rest1.substr(0,pos2);
			string rest2 = rest1.substr(pos2+1);

			if(matchB=="none"){//no match words
				match_defB = "none";
				match_numB = 0;
				if(rest2.substr(0,4)!="none"){//if there is similar words
					int pos3 = rest2.find_first_of(":");
					similarB = rest2.substr(0,pos3);
					similar_defB = rest2.substr(pos3+1);
					similar_numB = 1;
				}
				else{//if there is no similar words
					similar_numB = 0;
					similarB = "none";
					similar_defB = "none";
				}
				
			}
			else{//there is a match
				match_numB = 1;
				int pos3 = rest2.find_first_of(":");
				match_defB = rest2.substr(0,pos3);
				string rest3 = rest2.substr(pos3+1);
				if(rest3.substr(0,4)!="none"){//if there is similar words
					int pos4 = rest3.find_first_of(":");
					similarB = rest3.substr(0,pos4);
					similar_defB = rest3.substr(pos4+1);
					similar_numB = 1;
				}
				else{//if there is no similar words
					similar_numB = 0;
					similarB = "none";
					similar_defB = "none";

				}
			}
		}
		else{//prefix and suffix
			int pos = msg_UDP.find_first_of(":");
			string rest = msg_UDP.substr(pos+1);

			if(rest=="none"){//there is no match
				match_numB = 0;
			}
			else{
				stringstream ss;
				vector<string> results;
				string s;
				ss.clear();
				ss.str(rest);
				while(1){
					ss >> s;
					if(ss.fail()){
						break;
					} 
					results.push_back(s);
					all_match_words.push_back(s);
				}
				match_numB = results.size();
				// for(int i=0;i<results.size();i++){
				// 	cout<<results[i]<<endl;
				// }
			}
			
		}

		//send to server C
		UDP_send(2, msg);
		cout<<"The AWS sent <"<<input<<"> and <"<<function<<"> to Backend-Server C"<<endl;
		msg_UDP = UDP_recv();

		//decode message from server C
		string matchC, similarC, match_defC, similar_defC;
		int match_numC, similar_numC;
		if(function=="search"){
			int pos1 = msg_UDP.find_first_of(":");
			string rest1 = msg_UDP.substr(pos1+1);
			int pos2 = rest1.find_first_of(":");
			matchC = rest1.substr(0,pos2);
			string rest2 = rest1.substr(pos2+1);

			if(matchC=="none"){//no match 
				match_defC = "none";
				match_numC = 0;
				if(rest2.substr(0,4)!="none"){//if there is similar words
					int pos3 = rest2.find_first_of(":");
					similarC = rest2.substr(0,pos3);
					similar_defC = rest2.substr(pos3+1);
					similar_numC = 1;
				}
				else{//no similar words
					similar_numC = 0;
					similarC = "none";
					similar_defC = "none";
				}
				
			}
			else{//there is a match
				match_numC = 1;
				int pos3 = rest2.find_first_of(":");
				match_defC = rest2.substr(0,pos3);
				string rest3 = rest2.substr(pos3+1);
				if(rest3.substr(0,4)!="none"){//if there is similar words
					int pos4 = rest3.find_first_of(":");
					similarC = rest3.substr(0,pos4);
					similar_defC = rest3.substr(pos4+1);
					similar_numC = 1;
				}
				else{//if there is no similar words
					similar_numC = 0;
					similarC = "none";
					similar_defC = "none";

				}
			}
		}
		else{//prefix and suffix
			int pos = msg_UDP.find_first_of(":");
			string rest = msg_UDP.substr(pos+1);
			
			if(rest=="none"){//there is no match
				match_numC = 0;
			}
			else{
				stringstream ss;
				vector<string> results;
				string s;
				ss.clear();
				ss.str(rest);
				while(1){
					ss >> s;
					if(ss.fail()){
						break;
					} 
					results.push_back(s);
					all_match_words.push_back(s);
				}
				match_numC = results.size();
			// 	for(int i=0;i<results.size();i++){
			// 		cout<<results[i]<<endl;
			// 	}
			}
			
		}

		if(function=="search"){
				cout<<"The AWS received <"<<similar_numA<<"> similar words from Backend-Server <A> using UDP over port <24991>"<<endl;

				cout<<"The AWS received <"<<similar_numB<<"> similar words from Backend-Server <B> using UDP over port <24991>"<<endl;
						
				cout<<"The AWS received <"<<similar_numC<<"> similar words from Backend-Server <C> using UDP over port <24991>"<<endl;
			

			int total_match = match_numA + match_numB + match_numC;
			cout<<"The AWS sent <"<<total_match<<"> matches to client."<<endl;

			string match_tosend = " ";
			string match_def_tosend = " ";

			if(matchA!="none"){
				match_tosend = matchA;
				match_def_tosend = match_defA;
			}
			else if(matchB!="none"){
				match_tosend = matchB;
				match_def_tosend = match_defB;
			}
			else if(matchC!="none"){
				match_tosend = matchC;
				match_def_tosend = match_defC;
			}

			string similar_tosend = " ";
			string similar_def_tosend = " ";

			if(similarA!="none"){
				similar_tosend = similarA;
				similar_def_tosend = similar_defA;
			}
			else if(similarB!="none"){
				similar_tosend = similarB;
				similar_def_tosend = similar_defB;
			}
			else if(similarC!="none"){
				similar_tosend = similarC;
				similar_def_tosend = similar_defC;
			}

			cout<<"The AWS sent <"<<match_tosend<<"> and <"<<similar_tosend<<"> to the monitor via TCP port 26991"<<endl;

			if(match_tosend==" "){
				match_tosend = "none";
				match_def_tosend = "none";
			}
			if(similar_tosend ==" "){
				similar_tosend = "none";
				similar_def_tosend = "none";
			}

			string msg_to_client = "search:"+input+":"+match_tosend+":"+match_def_tosend;
			string msg_to_monitor = "search:"+input+":"+match_tosend+":"+match_def_tosend+":"+similar_tosend+":"+similar_def_tosend;

			char *toclient = new char[msg_to_client.length()+1];
			strcpy(toclient,msg_to_client.c_str());

			if (send(new_fd, toclient, strlen(toclient), 0) == -1){
		 		perror("send");
		 	}

		 	char *tomonitor = new char[msg_to_monitor.length()+1];
			strcpy(tomonitor,msg_to_monitor.c_str());

		 	if (send(new_fd2, tomonitor, strlen(tomonitor), 0) == -1){
		 		perror("send");
		 	}
		}
		else{// for prefix and suffix
			cout<<"The AWS received <"<<match_numA<<"> matches from Backend-Server <A> using UDP over port <24991>"<<endl;
			cout<<"The AWS received <"<<match_numB<<"> matches from Backend-Server <B> using UDP over port <24991>"<<endl;
			cout<<"The AWS received <"<<match_numC<<"> matches from Backend-Server <C> using UDP over port <24991>"<<endl;

			int total_match = match_numA + match_numB + match_numC;
			cout<<"The AWS sent <"<<total_match<<"> matches to client."<<endl;
			cout<<"The AWS sent <"<<total_match<<"> matches to the monitor via TCP port 26991."<<endl;

			string toclientandmonitor = function+":"+input+":";

			if(total_match==0){
				toclientandmonitor =  toclientandmonitor+ "none"; 
			}
			else{
				//cout<<"total match number:"<<all_match_words.size()<<endl;
				for(int i=0;i<all_match_words.size();i++){
					toclientandmonitor = toclientandmonitor+ all_match_words[i] + " ";
				}
				
			}
			char *c_toclientandmonitor = new char[toclientandmonitor.length()+1];
			strcpy(c_toclientandmonitor,toclientandmonitor.c_str());
			
		 	if (send(new_fd, c_toclientandmonitor, strlen(c_toclientandmonitor), 0) == -1){
		 		perror("send");
		 	}
		 	
		 	if (send(new_fd2, c_toclientandmonitor, strlen(c_toclientandmonitor), 0) == -1){
		 		perror("send");
	 	}
		}
		

		close(new_fd); 
	

	}



	close(sockfd);
	return 0;
}