#include "serverB.h"

#define PORT_UDP "22991"
#define PORT_UDP_aws "24991"
#define MAXBUFLEN 3000

using namespace std;

//constructor for server B class
serverB::serverB(){
	_result = true;
	one_edit_num = 0;
	getWords();
	
}

void serverB::getWords(){//function for getting all words and definitions from dictionary
	ifstream configFile("backendB.txt");
	string line;
	if(!configFile.is_open()){
		throw invalid_argument("Cannot open file");
	}

	while(getline(configFile,line)){
		int pos = line.find_first_of(":");
		string word = line.substr(0,pos-1);
		string def = line.substr(pos+3);
		//cout<<word<<":"<<def<<endl;
		words.insert(pair<string,string>(word,def));

		for(int i=0;i<=word.length();i++){//create prefix map, key: prefix, value: words begin with that prefix
			//cout<<word[i]<<endl;
			//prefix
			string pref = word.substr(0,i);
			//cout<<pref<<endl;
			map<string,vector<string> >::iterator it = prefix_map.find(pref);
			if(it!=prefix_map.end()){//prefix already in the map, add to the vector
				vector<string> word_vec = it->second;
				word_vec.push_back(word);
				it->second = word_vec;
			}
			else{//not in the map, insert the new pair to the map
				vector<string> word_vec;
				word_vec.push_back(word);
				prefix_map.insert(pair<string,vector<string> >(pref,word_vec));
			}
			
			
		}

		for(int j=word.length()-1; j>=0;j--){//create suffix map, key: suffix, value: words end with that prefix
			//suffix
			string suf = word.substr(j);
			map<string,vector<string> >::iterator it = suffix_map.find(suf);
			
			if(it!=suffix_map.end()){//suffix already in the map, add to the vector
				vector<string> word_vec = it->second;
				word_vec.push_back(word);
				it->second = word_vec;
			}
			else{//not in the map, insert the new pair to the map
				vector<string> word_vec;
				word_vec.push_back(word);
				suffix_map.insert(pair<string,vector<string> >(suf,word_vec));
			}

		}
		

	}
	

}

string serverB::search(string target){//function for search
	map<string,string>::iterator it = words.find(target);
	string result;
	if(it!=words.end()){
		//cout<<it->first<<" : "<<it->second<<endl;
		set_result(true);
		result = it->first + ":" + it->second;

	}
	else{
		set_result(false);
		result = "none";
	}
	return result;
	
}

vector<string> serverB::prefix(string target){//function for prefix
	map<string,vector<string> >::iterator it = prefix_map.find(target);
	if(it!=prefix_map.end()){
		set_result(true);
		return it->second;

	}
	else{
		vector<string> result;
		result.push_back("none");
		set_result(false);
		return result;
	}
}

vector<string> serverB::suffix(string target){//function for suffix
	map<string,vector<string> >::iterator it = suffix_map.find(target);
	if(it!=suffix_map.end()){
		set_result(true);
		return it->second;
		
	}
	else{
		vector<string> result;
		result.push_back("none");
		set_result(false);
		return result;
	}
}

string serverB::one_edit(string target){//function to find similar words which has only one letter different
	one_edit_num = 0;
	string temp = target;
	string result = "none";
	//for loop to replace the first letter
	for(int k=0;k<26;k++){
		temp = target;
		char c = 'A'+k;
		string s(1,c);
		temp.replace(0,1,s);
		if(temp.compare(target)!=0){
			
			if(words.find(temp)!=words.end()){//find similar word
				result = temp;
				one_edit_num++;
			}
				
			
		}
	}
	//for loop to replace the rest letters
	for(int i=1;i<target.length();i++){
		for(int j=0;j<26;j++){
			temp = target;
			char c = 'a'+j;
			string s(1,c);
			temp.replace(i,1,s);
			if(temp.compare(target)!=0){

				if(words.find(temp)!=words.end()){//find similar word
					result = temp;
					one_edit_num++;
				}
				
			}
		}
	}
	return result;
}

bool serverB::find_result(){//return true for find a result for search, prefix or suffix
	return _result;
}

void serverB::set_result(bool result){//set whether find result
	_result = result;
}

//uses code from Beej's Guide to Network Programming.
void *get_in_addr(struct sockaddr *sa)//get address info
{
	if (sa->sa_family == AF_INET) {
		return &(((struct sockaddr_in*)sa)->sin_addr);
	}
	return &(((struct sockaddr_in6*)sa)->sin6_addr);
}

int main(int argc, char* argv[]){
	serverB server_b;//create an object
	cout<<"The server B is up and running using UDP on port 22991"<<endl;

	//receive message from AWS
	//uses code from Beej's Guide to Network Programming.
	int sockfd;
	struct addrinfo hints, *servinfo, *p;
	int rv;
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
		return 1;
	}
	// loop through all the results and bind to the first we can
	for(p = servinfo; p != NULL; p = p->ai_next) {
		if ((sockfd = socket(p->ai_family, p->ai_socktype,
				p->ai_protocol)) == -1) {
			perror("listener: socket");
			continue;
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
		return 2;
	}

	freeaddrinfo(servinfo);

	

	while(1){
		//printf("listener: waiting to recvfrom...\n");
		addr_len = sizeof their_addr;

		if ((numbytes = recvfrom(sockfd, buf, MAXBUFLEN-1 , 0,
			(struct sockaddr *)&their_addr, &addr_len)) == -1) {
			perror("recvfrom");
			exit(1);
		}

		// printf("listener: got packet from %s\n",
		// 	inet_ntop(their_addr.ss_family,
		// 		get_in_addr((struct sockaddr *)&their_addr),
		// 		s, sizeof s));
		//printf("listener: packet is %d bytes long\n", numbytes);
		buf[numbytes] = '\0';
		//printf("listener: packet contains \"%s\"\n", buf);
		

		int sockfd2;
		struct addrinfo hints2, *servinfo2, *p2;
		int rv2;
		int numbytes2;

		memset(&hints2, 0, sizeof hints2);
		hints2.ai_family = AF_UNSPEC;
		hints2.ai_socktype = SOCK_DGRAM;

		if ((rv2 = getaddrinfo("127.0.0.1", PORT_UDP_aws, &hints2, &servinfo2)) != 0) {
			fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv2));
			return 1;
		}

		for(p2 = servinfo2; p2 != NULL; p2 = p2->ai_next) {
			if ((sockfd2 = socket(p2->ai_family, p2->ai_socktype,
					p2->ai_protocol)) == -1) {
				perror("talker: socket");
				continue;
			}
			break;
		}
		if (p2 == NULL) {
			fprintf(stderr, "talker: failed to create socket\n");
			return 2;
		}

		string msg(buf);
		int pos = msg.find_first_of(":");
		string function = msg.substr(0,pos);
		string input = msg.substr(pos+1);
		cout<<"The server B received input <"<<input<<"> and operation <"<<function<<">"<<endl;


		//print out required information
		if(function=="search"){
			string message = server_b.search(input);
			message = "search:"+message;
			string one = server_b.one_edit(input);
			//cout<<"one edit: "<<one<<endl;
			if(one!="none"){//found similar words
				one = server_b.search(one);
				
			}

			if(server_b.find_result()){//found match
				cout<<"The Server B has found <1> match and <"<<server_b.one_edit_num<<"> similar words"<<endl;
			}
			else{//no match
				cout<<"The Server B has found <0> match and <"<<server_b.one_edit_num<<"> similar words"<<endl;
			}
			message = message+":"+one;
			

			char *msg = new char[message.length()+1];
			strcpy(msg,message.c_str());
			//send to AWS through UDP
			//uses code from Beej's Guide to Network Programming.
			if ((numbytes2 = sendto(sockfd2, msg, strlen(msg), 0,
					p2->ai_addr, p2->ai_addrlen)) == -1) {
				perror("talker: sendto");
				exit(1);
			}
		}
		else if(function=="prefix"){
			vector<string> result = server_b.prefix(input);

			string message;
			if(server_b.find_result()){
				message = "prefix:";
				for(int i=0;i<result.size();i++){
					message = message + result[i]+" ";
					//cout<<result[i]<<endl;
				}
				cout<<"The server B has found <"<<result.size()<<"> matches"<<endl;
			}
			else{
				message = "none"; 
				cout<<"The server B has found <0> matches"<<endl;
			}

			char *msg = new char[message.length()+1];
			strcpy(msg,message.c_str());
			//send to AWS through UDP
			//uses code from Beej's Guide to Network Programming.
			if ((numbytes2 = sendto(sockfd2, msg, strlen(msg), 0,
					p2->ai_addr, p2->ai_addrlen)) == -1) {
				perror("talker: sendto");
				exit(1);
			}

		}
		else if(function=="suffix"){

			vector<string> result = server_b.suffix(input);

			string message;
			if(server_b.find_result()){
				message = "suffix:";
				for(int i=0;i<result.size();i++){
					message = message + result[i]+" ";
					//cout<<result[i]<<endl;
				}
				cout<<"The server B has found <"<<result.size()<<"> matches"<<endl;
			}
			else{
				message = "none"; 
				cout<<"The server B has found <0> matches"<<endl;
			}

			char *msg = new char[message.length()+1];
			strcpy(msg,message.c_str());
			//send to AWS through UDP
			//uses code from Beej's Guide to Network Programming.
			if ((numbytes2 = sendto(sockfd2, msg, strlen(msg), 0,
					p2->ai_addr, p2->ai_addrlen)) == -1) {
				perror("talker: sendto");
				exit(1);
			}

		}

		cout<<"The server B finished sending the output to AWS"<<endl;

		freeaddrinfo(servinfo2);
		//printf("talker: sent %d bytes to %s\n", numbytes2, "serverB");
		close(sockfd2);

	}
	
	//cout<<"one_edit: "<<server_b.one_edit("Heel")<<endl;
	//close(sockfd);


	return 0;


}