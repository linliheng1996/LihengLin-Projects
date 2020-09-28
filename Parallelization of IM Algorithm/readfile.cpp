#include "readfile.h"
#include "helpers.h"

#include <iostream>
#include <fstream>
#include <vector>
#include <string>



std::unordered_set<Node> empty_set;
std::vector<std::unordered_map<Node, float> > node_vec;

std::vector<std::unordered_map<Node, float>>& readfile(){
	node_vec = std::vector<std::unordered_map<Node, float>>(NODE_NUM + 1);
	std::ifstream file(FILE_NAME);

	std::string line = "";
	std::unordered_set<Node> exist_set;
	
	if(file.fail()){
		std::cout<<"Error opening file"<<std::endl;
	}
	while(getline(file, line)){
		Edge edge = parse_edge(line);
		int source = edge.source;
		exist_set.insert(source);
		int target = edge.target;
		exist_set.insert(target);
		float weight = edge.weight;
		node_vec[source][target] = weight;
	}

	file.close();

	// calculate empty set
	for(int i = 1; i <= NODE_NUM; ++i){
		auto it = exist_set.find(i);
		if (it == exist_set.end()){
			empty_set.insert(i);
		}
	}
	return node_vec;
}

std::vector<std::unordered_map<Node, float> >& readfile_range(int begin, int end){
	int size = end - begin + 1;
	node_vec = std::vector<std::unordered_map<Node, float>>(size + 1);
	std::ifstream file(FILE_NAME);

	std::string line = "";
	std::unordered_set<Node> exist_set;
	

	if(file.fail()){
		std::cout<<"Error opening file"<<std::endl;
	}
	while(getline(file, line)){
		Edge edge = parse_edge(line);
		int source = edge.source;
		exist_set.insert(source);
		int target = edge.target;
		exist_set.insert(target);
		float weight = edge.weight;
		if(source >= begin && source <= end){
			node_vec[source-begin+1][target] = weight;
		}
	}

	file.close();

	// calculate empty set
	for(int i = 1; i <= NODE_NUM; ++i){
		auto it = exist_set.find(i);
		if (it == exist_set.end()){
			empty_set.insert(i);
		}
	}
	return node_vec;
}


std::unordered_set<Node>& get_empty_nodes() {
	return empty_set;
}