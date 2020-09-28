#pragma once

#include <unordered_set>
#include <vector>
#include <unordered_map>
#include <utility>
typedef int Node;

float getRandomNumber();

std::unordered_set<Node> greedy_maximize_influence(
    std::vector<std::unordered_map<Node, float> >& graph,
    std::unordered_set<Node>& empty_nodes,
    Node max_node,
    int size,
    int sample_times);

std::pair<Node, float> select_maximize_node(std::vector<std::unordered_map<Node, float> > graph, std::unordered_set<Node>& seed, std::unordered_set<Node>& empty_nodes, Node min_node, Node max_node, int sample_times);
