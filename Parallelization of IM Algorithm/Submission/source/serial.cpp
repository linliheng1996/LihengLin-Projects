#include "readfile.h"
#include "kempe.h"
#include <unordered_map>
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <iostream>
using std::cout;
using std::endl;

int main(int argc, char** argv) {
    std::vector<std::unordered_map<Node, float> >& graph = readfile();
    int max_node = NODE_NUM;
    if(argc != 3) {
      perror("Wrong parameter count. Expecting two parameters: maximum seed size and sample times.\n");
      exit(1);
    }
    int sample_times = atoi(argv[2]);
    std::unordered_set<Node>& empty_nodes = get_empty_nodes();
    int seed_size = atoi(argv[1]);
    
    // time
    struct timespec start, stop; 
    double time;
    // get start time 
    if( clock_gettime(CLOCK_REALTIME, &start) == -1) { perror("clock gettime");}

    std::unordered_set<Node> maximized_set = greedy_maximize_influence(graph, empty_nodes, max_node, seed_size, sample_times);
    
    // get end time
    if( clock_gettime( CLOCK_REALTIME, &stop) == -1 ) { perror("clock gettime");}		
		time = (stop.tv_sec - start.tv_sec)+ (double)(stop.tv_nsec - start.tv_nsec)/1e9;
    
    cout << "Seed: ";
    for (auto i : maximized_set){
        cout << i << " ";
    }
    cout << endl;
    // print execution time
    printf("Execution time = %f sec\n", time);
    return 0;
}
