#include "readfile.h"
#include "kempe.h"
#include <unordered_map>
#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <string>
#include <time.h>

int main(int argc, char** argv) {

    int thread_num, rank;
    MPI_Init(&argc,&argv);
    MPI_Status status;
    MPI_Comm_size(MPI_COMM_WORLD,&thread_num);
    MPI_Comm_rank(MPI_COMM_WORLD,&rank);

    std::vector<std::unordered_map<Node, float> >& graph = readfile();
    int max_node = NODE_NUM;
    std::unordered_set<Node>& empty_nodes = get_empty_nodes();
	if(argc != 3) {
		perror("Wrong parameter count. Expecting two parameters: Maximum seed size to be selected and sample times for each set\n");
		exit(1);
	}
    int max_seed_size = atoi(argv[1]);
    int sample_times = atoi(argv[2]);
	std::unordered_set<Node> seed;

	struct timespec start, stop; 
    double time;
    
    if(rank == 0){
	    // get start time 
    	if( clock_gettime(CLOCK_REALTIME, &start) == -1) { perror("clock gettime");}
    }

	while(seed.size() < max_seed_size){

		int curr_seed_size = 0; 
		if(rank == 0){
        	printf("==== Round %lu starts ====\n", seed.size());
			curr_seed_size = seed.size();
		}

		// broadcast seed size to each slave thread
		MPI_Bcast(&curr_seed_size, 1, MPI_INT, 0, MPI_COMM_WORLD);

		//printf("rank = %i, seed size = %i\n", rank, curr_seed_size);
		
		// broacast seed set to each slave thread
		int array[curr_seed_size];
		
		if (rank == 0){
			auto it = seed.begin();
			for (int i = 0; i < curr_seed_size; ++i){
				array[i] = *it;
				it++;
			}
		}
		MPI_Bcast(array, curr_seed_size, MPI_INT, 0, MPI_COMM_WORLD);

		//for (int i = 0; i < curr_seed_size; ++i){
		//	printf("rank = %i, seed[%i] = %i\n", rank, i, array[i]);
		//}

		// calculate block size for each thread
		int block_size = 0;
		int block_start_index[thread_num];
		int block_end_index[thread_num];
		if (rank == 0){
			block_size = max_node / thread_num;
			for (int i = 0; i < thread_num; ++i){
				block_start_index[i] = (i * block_size);
				block_end_index[i] = ((i+1) * block_size) - 1;
			}
			block_end_index[thread_num-1] = max_node;
		}

		// scatter start and end index array to each slave
		int block_start = 0;
		int block_end = 0;
		MPI_Scatter(block_start_index, 1, MPI_INT, &block_start, 1, MPI_INT, 0, MPI_COMM_WORLD);
		MPI_Scatter(block_end_index, 1, MPI_INT, &block_end, 1, MPI_INT, 0, MPI_COMM_WORLD);
		//printf("rank = %i, block start = %i, block end = %i\n", rank, block_start, block_end);
		
		
		// convert seed array to seed unordered set
		std::unordered_set<Node> curr_seed;
		for (int j = 0; j < curr_seed_size; ++j){
			curr_seed.insert(array[j]);
		}
		std::pair<Node, float> node_with_max_influence = select_maximize_node(graph, curr_seed, empty_nodes, block_start, block_end, sample_times);
		//printf("rank = %i max influence node = %i, max influence = %f\n", rank, node_with_max_influence.first, node_with_max_influence.second);



		int max_node_array[thread_num];
		float max_inf_array[thread_num];
		// gather max back to master
		printf("rank = %i, gather max starts\n", rank);
		MPI_Gather(&(node_with_max_influence.first), 1, MPI_INT, max_node_array, 1, MPI_INT, 0, MPI_COMM_WORLD);
		MPI_Gather(&(node_with_max_influence.second), 1, MPI_FLOAT, max_inf_array, 1, MPI_FLOAT, 0, MPI_COMM_WORLD);
		printf("rank = %i, gather max end\n", rank);

		if (rank == 0){
			printf("max node in each block:");
			for(int i = 0; i < thread_num; ++i){
				printf("%i  %f  ", max_node_array[i], max_inf_array[i]);
			}
			printf("\n");

			// compare max's and find global max
			int curr_max = max_node_array[0];
			float curr_max_inf = max_inf_array[0];
			for(int i = 0; i < thread_num; ++i){
				if (curr_max_inf < max_inf_array[i]){
					curr_max_inf = max_inf_array[i];
					curr_max = max_node_array[i];
				}
			}
			printf("global max node = %i with influence = %f\n", curr_max, curr_max_inf);

        	printf("==== Round %lu ends ====\n", seed.size());
			// add new global max node into seed
			seed.insert(curr_max);
		}
		else{
			if (curr_seed_size == max_seed_size-1){
				break;
			}
		}

	}


	if (rank == 0){
        // get end time
    	if( clock_gettime( CLOCK_REALTIME, &stop) == -1 ) { perror("clock gettime");}		
		time = (stop.tv_sec - start.tv_sec)+ (double)(stop.tv_nsec - start.tv_nsec)/1e9;
        
		printf("done!\n");
		printf("seed: ");
		for(auto i : seed){
			printf("%i ", i);
		}
		printf("\n");
         // print execution time
    	printf("Execution time = %f sec\n", time);
	}
	MPI_Finalize();
	return 0;
}
