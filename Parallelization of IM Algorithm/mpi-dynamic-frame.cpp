#include "readfile.h"
#include "kempe.h"
#include <unordered_map>
#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <string>

#include <unistd.h>



int main(int argc, char** argv) {

    int thread_num, rank;
    MPI_Init(&argc,&argv);
    MPI_Status status;
    MPI_Comm_size(MPI_COMM_WORLD,&thread_num);
    MPI_Comm_rank(MPI_COMM_WORLD,&rank);


    std::vector<std::unordered_map<Node, float> >& graph = readfile();
    int max_node = NODE_NUM;
    std::unordered_set<Node>& empty_nodes = get_empty_nodes();
    int max_seed_size = 4;
    int sample_times = 1;
	// Node node_with_max_influence; 
	std::unordered_set<Node> seed;

	// Producer
	if(rank == 0) {
		int test_number = 0;
		MPI_Request send_request[thread_num];
		MPI_Request receive_request[thread_num];
		for(int i = 1; i < thread_num; ++i) {
			
			MPI_Isend(&test_number, 1, MPI_INT, i, 1, MPI_COMM_WORLD, &send_request[i]);
			
			test_number++;
		}
		int tmp;
		for(int i = 1; i < thread_num; ++i) {
			MPI_Irecv(&tmp, 1, MPI_INT, i, 0, MPI_COMM_WORLD, &receive_request[i]);
		}


		while(test_number < 20) {
			int i = 0;
			int flag = 0;


			//while(flag == 0 && test_number < 10) {

				//while(flag == 0)
					// Only test 1, 2, ..., max_rank; not test 0 itself
					//MPI_Testany(thread_num - 1, receive_request + 1, &i, &flag, MPI_STATUS_IGNORE);
				MPI_Waitany(thread_num - 1, receive_request + 1, &i, MPI_STATUS_IGNORE);
				//if(flag) {

					// index is 1 off the start
					MPI_Isend(&test_number, 1, MPI_INT, i + 1, 1, MPI_COMM_WORLD, &send_request[i + 1]);
					MPI_Irecv(&tmp, 1, MPI_INT, i + 1, 0, MPI_COMM_WORLD, &receive_request[i + 1]);
					printf("Send: rank = %d number = %d\n", rank, test_number);
					test_number++;
					//break;
				//}
			//}
		}

		// No more to send
		int dummy = -1;
		for(int i = 1; i < thread_num; ++i) {
			printf("Wait: rank = %d waiting for rank = %d\n", rank, i);
			MPI_Wait(&receive_request[i], MPI_STATUS_IGNORE); // Possible bug
			MPI_Send(&dummy, 1, MPI_INT, i, 0, MPI_COMM_WORLD);// &send_request[i]);
			printf("Exit: rank = %d sent to rank = %d\n", rank, i);
		}
		printf("Exit: rank = %d exits\n", rank);
	}

	// Consumer
	else {
		MPI_Status status;
		int okay_signal = 0;
		srand(rank);
		while(true) {
			// Probe size of next message
			MPI_Probe(0, MPI_ANY_TAG, MPI_COMM_WORLD, &status);

			int current_seed_size;
			MPI_Get_count(&status, MPI_INT, &current_seed_size);

			int seed_buf[current_seed_size];
			MPI_Recv(seed_buf, current_seed_size, MPI_INT, 0, MPI_ANY_TAG, MPI_COMM_WORLD, &status);

			if(status.MPI_TAG == 0) {
				// if master has no more to send
				
				break;
			}
			printf("rank = %d receiving\n", rank);
			for(int i = 0; i < current_seed_size; ++i) {
				printf("%d ", seed_buf[i]);
			}
			printf("\n");
	
			int sleeptime = rand() % 10;
			printf("Sleep: rank = %d for %d seconds\n", rank, sleeptime);
			sleep(sleeptime);
			/*
			int sum = 0;
			for(int j = 0; j < 1000; ++j) {
				if(j % 2 == 0) {
					sum += j;
				}
				else {
					sum += 2 * j + 1;
				}
			}*/

			MPI_Send(&okay_signal, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
			printf("rank = %d sent okay\n", rank);
		}
		printf("rank = %d exits\n", rank);
	}




	/*
	while(seed.size() < max_seed_size){

		int curr_seed_size = 0; 
		if(rank == 0){
			curr_seed_size = seed.size();
		}

		// broadcast seed size to each slave thread
		MPI_Bcast(&curr_seed_size, 1, MPI_INT, 0, MPI_COMM_WORLD);

		printf("rank = %i, seed size = %i\n", rank, curr_seed_size);
		
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

		for (int i = 0; i < curr_seed_size; ++i){
			printf("rank = %i, seed[%i] = %i\n", rank, i, array[i]);
		}

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
		printf("rank = %i, block start = %i, block end = %i\n", rank, block_start, block_end);
		
		
		// convert seed array to seed unordered set
		std::unordered_set<Node> curr_seed;
		for (int j = 0; j < curr_seed_size; ++j){
			curr_seed.insert(array[j]);
		}
		std::pair<Node, float> node_with_max_influence = select_maximize_node(graph, curr_seed, empty_nodes, block_start, block_end);
		printf("rank = %i max influence node = %i, max influence = %f\n", rank, node_with_max_influence.first, node_with_max_influence.second);



		int max_node_array[thread_num];
		float max_inf_array[thread_num];
		// gather max back to master
		MPI_Gather(&(node_with_max_influence.first), 1, MPI_INT, max_node_array, 1, MPI_INT, 0, MPI_COMM_WORLD);
		MPI_Gather(&(node_with_max_influence.second), 1, MPI_FLOAT, max_inf_array, 1, MPI_FLOAT, 0, MPI_COMM_WORLD);

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
		printf("done!\n");
		printf("seed: ");
		for(auto i : seed){
			printf("%i ", i);
		}
		printf("\n");
	}*/
	MPI_Finalize();
	return 0;
}
