
#include "kempe.h"
#include <time.h>
#include <stdlib.h>
#include <queue>
#include <iostream>
using std::cout;
using std::endl;

// Return a random number in [0, 1]
float getRandomNumber() {
    //srand(time(NULL));
    return (float)rand() / RAND_MAX;
    
}

int infect(std::vector<std::unordered_map<Node, float> >& graph, std::unordered_set<Node>& seed) {



    std::unordered_set<Node> infected = seed;


    std::queue<Node> infecting_nodes;

    for(auto it = infected.begin(); it != infected.end(); ++it) {
        infecting_nodes.push(*it);
    }

    

    while(!infecting_nodes.empty()) {
        Node current_infecting_node = infecting_nodes.front();
        infecting_nodes.pop();
        //std::cout << current_infecting_node << std::endl;
        std::unordered_map<Node, float>& edges = graph[current_infecting_node];
        for(auto it = edges.begin(); it != edges.end(); ++it) {
            //std::cout << "Here!" << std::endl;
            // The head of the edge is not infected and activation succeeds
            if(infected.find(it->first) == infected.end() && getRandomNumber() >= it->second) {
                infected.insert(it->first);
                infecting_nodes.push(it->first);
            }
        }
    }

    /*
    if(seed.size() == 1) {
        std::cout << "Seed:" << std::endl;
        for(auto it = seed.begin(); it != seed.end(); ++it) {
            std::cout << *it << std::endl;
        } 
        std::cout << "Infected:" << std::endl;
        for(auto it : infected) {
            std::cout << it << " ";
        }
        std::cout << std::endl << std::endl;
    }
    */
    return infected.size();
}

float sample(std::vector<std::unordered_map<Node, float> > graph, std::unordered_set<Node>& seed, int times) {
    // Simulate
    int influence = 0;
    for(int iterate_time = 0; iterate_time < times; iterate_time++) {
        influence += infect(graph, seed);
    }


    // return the number of active nodes
    return ((float)influence) / times;
}

/**
 * 
 */
std::pair<Node, float> select_maximize_node(std::vector<std::unordered_map<Node, float> > graph, std::unordered_set<Node>& seed, std::unordered_set<Node>& empty_nodes, Node min_node, Node max_node, int sample_times) {

    //int sample_times = 1; // To be determined

    float max_influence = 0;
    Node maximized_node = -1;
    // for each node not in the seed set nor in the empty set
    for(Node candidate = min_node; candidate <= max_node; candidate++) {
        if(empty_nodes.find(candidate) == empty_nodes.end() && seed.find(candidate) == seed.end()) {
            seed.insert(candidate);
            // Simulate activation for multiple times
            float influence = sample(graph, seed, sample_times);
            // Update if influcence is larger
            if(influence > max_influence) {
                max_influence = influence;
                maximized_node = candidate;
            }
            else if(influence == max_influence) {
                // Arbitrary tie break
                if(getRandomNumber() > 0.5) {
                    max_influence = influence;
                    maximized_node = candidate;
                }
            }
            seed.erase(candidate);
        }
        
    }
    // Return the node with maximized influence
    std::cout << "global max node = " << maximized_node << ", max influence = " << max_influence << endl;
    return {maximized_node, max_influence};
}

std::unordered_set<Node> greedy_maximize_influence(
    std::vector<std::unordered_map<Node, float> >& graph,
    std::unordered_set<Node>& empty_nodes,
    Node max_node,
    int size,
    int sample_times) {
    Node min_node = 1;
    std::unordered_set<Node> seed;
    while(seed.size() < size) {
        printf("==== Round %lu starts ====\n", seed.size());
        std::pair<Node, float> node_with_max_influence = select_maximize_node(graph, seed, empty_nodes, min_node, max_node, sample_times);
        printf("==== Round %lu ends ====\n", seed.size());
        seed.insert(node_with_max_influence.first);

    }
    return seed;
}

