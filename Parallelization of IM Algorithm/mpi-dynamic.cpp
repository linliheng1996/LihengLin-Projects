#include "readfile.h"
#include "kempe.h"
#include <unordered_map>
#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <string>
#include <algorithm>
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
	if(argc != 4) {
		perror("Wrong parameter count. Expecting three parameters: Maximum seed size to be selected, sample times for each set, and block size\n");
		exit(1);
	}
    int max_seed_size = atoi(argv[1]);
    int sample_times = atoi(argv[2]);
	// Node node_with_max_influence; 
	std::unordered_set<Node> seed;

	// Producer
	if(rank == 0) {
		struct timespec start, stop; 
    	double time;

		if( clock_gettime(CLOCK_REALTIME, &start) == -1) { perror("clock gettime");}
		
		MPI_Request send_request[thread_num];
		MPI_Request receive_request[thread_num];
		while(seed.size()<max_seed_size){
			int test_number = NODE_NUM;// number of nodes to be distributed
			
        	printf("==== Round %lu starts ====\n", seed.size());
			// convert seed from set to array
			int curr_seed_size = seed.size();
			int seed_array[curr_seed_size];
			auto it = seed.begin();
			for (int i = 0; i < curr_seed_size; ++i){
				seed_array[i] = *it;
				it++;
			}
			// send seed to every slaves
			for(int i = 1; i < thread_num; ++i){
				MPI_Send(seed_array, seed.size(), MPI_INT, i, 1, MPI_COMM_WORLD);//?????blocking or non-blocking
			}
			// array store the starting and ending position of the block to be calculated
			int node_block[thread_num][2];
			int block_size = atoi(argv[3]);
			int current_node = 1;//position of the next node to be send
			int curr_max_node = -1;
			float curr_max_value = -1;
			// send nodes to slaves for calculation
			for(int i = 1; i < thread_num; ++i) {
				// calculate starting and ending position
				node_block[i][0] = current_node;
				node_block[i][1] = current_node + block_size - 1;
				current_node = current_node + block_size;
				//MPI_Isend(&test_number, 1, MPI_INT, i, 1, MPI_COMM_WORLD, &send_request[i]);
				MPI_Isend(node_block[i], 2, MPI_INT, i, 1, MPI_COMM_WORLD, &send_request[i]);				
				//test_number++;
			}
			float node_and_value[thread_num][2];
			for(int i = 1; i < thread_num; ++i) {
				MPI_Irecv(node_and_value[i], 2, MPI_FLOAT, i, 0, MPI_COMM_WORLD, &receive_request[i]);
			}


			while(current_node <= test_number){
				int i = 0;
				MPI_Waitany(thread_num - 1, receive_request + 1, &i, MPI_STATUS_IGNORE);

				node_block[i+1][0] = current_node;
				node_block[i+1][1] = std::min(current_node + block_size - 1, test_number);
				current_node = current_node + block_size;

				Node node_result = (int)node_and_value[i+1][0];
				float value_result = node_and_value[i+1][1];
				printf("master receive: %d\n",node_result);
				if(value_result > curr_max_value){
					curr_max_value = value_result;
					curr_max_node = node_result;
				}
				// index is 1 off the start
				MPI_Isend(node_block[i+1], 2, MPI_INT, i + 1, 1, MPI_COMM_WORLD, &send_request[i + 1]);
				MPI_Irecv(node_and_value[i+1], 2, MPI_FLOAT, i + 1, 0, MPI_COMM_WORLD, &receive_request[i + 1]);
				printf("Send: rank = %d number = %d to %d\n", rank, node_block[i+1][0], node_block[i+1][1]);
				//test_number++;
				
			}
			/*
			int dum;
			MPI_Waitany(thread_num - 1, receive_request + 1, &dum, MPI_STATUS_IGNORE);
			Node node_result2 = (int)node_and_value[0];
			float value_result2 = node_and_value[1];
			printf("master receive: %d %f\n",node_result2, value_result2);
			if(value_result2 > curr_max_value){
				curr_max_value = value_result2;
				curr_max_node = node_result2;
			}

			printf("global max node = %i with influence = %f\n", curr_max_node, curr_max_value);
			*/
			// add new global max node into seed
			//seed.insert(curr_max_node);

			// No more to send
			int dummy = -1;
			for(int i = 1; i < thread_num; ++i) {
				//printf("Wait: rank = %d waiting for rank = %d\n", rank, i);
				MPI_Wait(&receive_request[i], MPI_STATUS_IGNORE); // Possible bug

				Node node_result2 = (int)node_and_value[i][0];
				float value_result2 = node_and_value[i][1];
				printf("master receive: %d %f\n",node_result2, value_result2);
				if(value_result2 > curr_max_value){
					curr_max_value = value_result2;
					curr_max_node = node_result2;
				}



				MPI_Send(&dummy, 1, MPI_INT, i, 0, MPI_COMM_WORLD);// &send_request[i]);
				//printf("Exit: rank = %d sent to rank = %d\n", rank, i);
			}

			printf("global max node = %i with influence = %f\n", curr_max_node, curr_max_value);

			printf("==== Round %lu ends ====\n", seed.size());
			seed.insert(curr_max_node);

			//printf("Exit: rank = %d exits\n", rank);
		}

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

		int dummy = -1;
		for(int i = 1; i < thread_num; ++i) {
			printf("Wait: rank = %d waiting for rank = %d\n", rank, i);
			MPI_Wait(&receive_request[i], MPI_STATUS_IGNORE); // Possible bug
			MPI_Send(&dummy, 1, MPI_INT, i, 0, MPI_COMM_WORLD);// &send_request[i]);
			printf("Exit: rank = %d sent to rank = %d\n", rank, i);
		}
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
			// receive seed and store into seed_buf
			int seed_buf[current_seed_size];
			MPI_Recv(seed_buf, current_seed_size, MPI_INT, 0, MPI_ANY_TAG, MPI_COMM_WORLD, &status);

			// if master has no more to send
			if(status.MPI_TAG == 0) {
				break;
			}

			// convert seed array to seed unordered set
			std::unordered_set<Node> curr_seed;
			for (int j = 0; j < current_seed_size; ++j){
				curr_seed.insert(seed_buf[j]);
			}
			while(true){
				// receive the begin and end of the block
				int node_block[2];
				MPI_Recv(node_block, 2, MPI_INT, 0, MPI_ANY_TAG, MPI_COMM_WORLD, &status);

				// if master has no more to send
				if(status.MPI_TAG == 0) {
					break;
				}
				printf("rank = %d calculate %d to %d ", rank, node_block[0], node_block[1]);
				std::pair<Node, float> node_with_max_influence = select_maximize_node(graph, curr_seed, empty_nodes, node_block[0], node_block[1], sample_times);
				printf("rank = %i max influence node = %i, max influence = %f\n", rank, node_with_max_influence.first, node_with_max_influence.second);

				// put the node number with max influence and its value into an array
				float node_and_value[2];
				node_and_value[0] = (float) node_with_max_influence.first;
				node_and_value[1] = node_with_max_influence.second;
				// send the node number and value array to master
				MPI_Send(node_and_value, 2, MPI_FLOAT, 0, 0, MPI_COMM_WORLD);

			}
			

			// printf("rank = %d receiving\n", rank);
			// for(int i = 0; i < current_seed_size; ++i) {
			// 	printf("%d ", seed_buf[i]);
			// }
			// printf("\n");
	
			// int sleeptime = rand() % 10;
			// printf("Sleep: rank = %d for %d seconds\n", rank, sleeptime);
			// sleep(sleeptime);


			// MPI_Send(&okay_signal, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
			// printf("rank = %d sent okay\n", rank);
		}
		printf("rank = %d exits\n", rank);
	}

	MPI_Finalize();
	return 0;
}
