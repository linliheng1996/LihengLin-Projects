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
#include <fstream>
#include <string>
#include <cstdlib>
#include <map>
#include <vector>

using namespace std;

class serverA{

	public:

		serverA();
		void getWords();
		string search(string target);
		vector<string> prefix(string target);
		vector<string> suffix(string target);
		bool find_result();
		void set_result(bool result);
		string one_edit(string target);
		int one_edit_num;

	private:
		map<string, vector<string> > prefix_map;
		map<string, string> words;
		map<string, vector<string> > suffix_map;		
		bool _result;
};