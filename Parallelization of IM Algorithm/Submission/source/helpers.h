#pragma once

#include <string>
#include <vector>

class Edge{
    public:
        Edge(int Source, int Target, float Weight):source(Source), target(Target), weight(Weight){}
        int source;
        int target;
        float weight;
};

// parse a line of a csv file and return a vector of strings
Edge parse_edge(const std::string& str)
{
    const char delim = ',';
	std::vector<std::string> retVal;
	
	size_t start = 0;
	size_t delimLoc = str.find_first_of(delim, start);
	while (delimLoc != std::string::npos)
	{
		retVal.emplace_back(str.substr(start, delimLoc - start));
		
		start = delimLoc + 1;
		delimLoc = str.find_first_of(delim, start);
	}
	
	retVal.emplace_back(str.substr(start));
    int source = std::stoi(retVal[0]);
    int target = std::stoi(retVal[1]);
    float weight = std::stof(retVal[2]);
	return Edge(source, target, weight);
}
