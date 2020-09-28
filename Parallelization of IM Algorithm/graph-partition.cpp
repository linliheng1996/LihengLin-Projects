#include "readfile.h"
#include "kempe.h"
#include <unordered_map>
#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <string>
#include <algorithm>
#include <unistd.h>
#include <queue>
#include <vector>



int main(int argc, char** argv) {

    int thread_num, rank;
    MPI_Init(&argc,&argv);
    MPI_Status status;
    MPI_Comm_size(MPI_COMM_WORLD,&thread_num);
    MPI_Comm_rank(MPI_COMM_WORLD,&rank); 

    int block_size = NODE_NUM/thread_num;
    int begin = rank*block_size+1;
    int end = (rank+1)*block_size;
    if(rank==thread_num-1){
    	end = NODE_NUM;
    }

    int max_node = NODE_NUM;//defined in readfile.h

  	std::vector<std::unordered_map<Node, float> >& graph = readfile_range(begin, end);
    std::unordered_set<Node>& empty_nodes = get_empty_nodes();
    std::unordered_set<Node> seed;

	if(argc != 3) {
		printf("Wrong parameter counts. Expecting two parameters: maximum seed set size and sample times.");
		exit(1);
	}

    int max_seed_size = atoi(argv[1]);
    int sample_times = atoi(argv[2]);
    //printf("rank %d: checkpoint 1\n", rank);

	struct timespec start, stop; 
	double time;
	if(rank == 0) {

		// get start time 
		if( clock_gettime(CLOCK_REALTIME, &start) == -1) { perror("clock gettime");}
	}

    while(seed.size() < max_seed_size){
		//printf("Seed size %lu =========\n", seed.size());
    	float max_influence = 0;
    	int maximized_node = -1;
    	for(Node candidate = 1; candidate <= max_node; candidate++){
    		int total_influence = 0;
    		//printf("rank %d: checkpoint 2\n", rank);
    		if(empty_nodes.find(candidate) == empty_nodes.end() && seed.find(candidate) == seed.end()) {	
    			seed.insert(candidate);
    			for(int s = sample_times;s>0;s--){
		    		int infected_count = 0;

				    MPI_Request send_request[thread_num];
					
					std::unordered_set<Node> infected;

					
					
					std::queue<Node> infecting_nodes;
					std::unordered_set<Node> already_sent;
					for(auto& i : seed){
						if(i>=begin && i<=end){
							
							infecting_nodes.push(i);
							infected.insert(i);
						}
					}


					int flag = 1;
					while(flag){
						int sent_num=0;
				    	int total_sent=0;
				    	int buffer_pos = 0;
				    	std::vector<std::unordered_set<int> > send_nodes;
				    	for(int i=0;i<thread_num;i++){
				        	std::unordered_set<int> temp;
				        	send_nodes.push_back(temp);
				        }
						while(!infecting_nodes.empty()){
							Node current_infecting_node = infecting_nodes.front();
							//printf("rank %d current node: %d\n", rank, current_infecting_node);
					        infecting_nodes.pop();
					        std::unordered_map<Node, float>& edges = graph[current_infecting_node-begin+1];
					        //for(int i=0;i<thread_num;i++){
					        //	send_nodes[i].clear();
					        //}

					        for(auto it = edges.begin(); it != edges.end(); ++it) {
					        	//if target is in one thread's own node range
					        	if(it->first >= begin && it->first <= end){
					        		//printf("rank=%d it_first: %d\n", rank, it->first);
					        		if(infected.find(it->first) == infected.end() && getRandomNumber() >= it->second) {
						                infected.insert(it->first);
						                infecting_nodes.push(it->first);
						            }
					        	}
					            else if(already_sent.find(it->first) == already_sent.end()){
					            	int t = (it->first - 1)/block_size;
					            	//printf("t: %d \n", t);
					        		if(it->first>block_size*thread_num) t = thread_num-1;
					            	send_nodes[t].insert(it->first);
					            	already_sent.insert(it->first);
					            	sent_num++;
					            }
					        }
					        
					        //printf("checkpoint 4\n");

						}
						//printf("checkpoint 3\n");
						//printf("rank= %d sent_num:%d \n",rank,sent_num);
						int* arr[thread_num];
						for(int n=0;n<thread_num;n++){
				        	if(n!=rank){
				        		//printf("rank= %d n: %d\n",rank,n);
				        		int vec_size = send_nodes[n].size();
				        		//if(rank==0) {printf("rank= %d size: %d\n",rank,vec_size);}
				        		if(vec_size!=0){
				        			//vec_size /= 4;
				        			arr[n] = (int*)malloc(sizeof(int) * vec_size);
						        	auto it = send_nodes[n].begin();
									for (int i = 0; i < vec_size; ++i){
										arr[n][i] = *it;
										//printf("rank= %d i=%d value=%d\n", rank, i, arr[n][i]);
										it++;
									}
									
									//printf("n: %d\n",n);
						    		MPI_Isend(arr[n],vec_size,MPI_INT,n,1,MPI_COMM_WORLD,&send_request[n]);
						    		//MPI_Send(arr,vec_size,MPI_INT,n,1,MPI_COMM_WORLD);
				        		}
								else {
									arr[n] = nullptr;
									int dummy = 1;
									int NOTHING_TAG = 10;
									//printf("rank %d Dummy!!!!!!!!\n", rank);
									MPI_Isend(&dummy, 1, MPI_INT, n, NOTHING_TAG, MPI_COMM_WORLD, &send_request[n]);
								}
				        	}
							else {
								arr[n] = nullptr;
							}
				        }
						//printf("rank %d: ==============\n",rank);
						MPI_Barrier(MPI_COMM_WORLD);
						//printf("rank= %d checkpoint 5\n",rank);
						MPI_Allreduce(&sent_num, &total_sent,1,MPI_INT,MPI_SUM,MPI_COMM_WORLD);
						//printf("rank= %d total_sent:%d \n",rank,total_sent);
						if(total_sent>0){
							for(int i=0;i<thread_num;++i){
								if(i!=rank){
									MPI_Probe(i, MPI_ANY_TAG, MPI_COMM_WORLD, &status);
									int curr_size;
									MPI_Get_count(&status, MPI_INT, &curr_size);
									//printf("rank= %d curr size=%d tag %d\n",rank, curr_size, status.MPI_TAG);

									if(status.MPI_TAG != 10 && curr_size > 0) {
										int recv_node[curr_size];
										//printf("rank= %d checkpoint 6\n",rank);
										//printf("i: %d\n",i);
										MPI_Recv(recv_node, curr_size, MPI_INT, i, 1, MPI_COMM_WORLD, &status);
										//printf("rank %d status.MPI_ERROR: %d\n", rank, status.MPI_ERROR);
										//printf("rank= %d checkpoint 7\n",rank);
										for(int j=0;j<curr_size;j++){
											infecting_nodes.push(recv_node[j]);
											infected.insert(recv_node[j]);
											//printf("rank= %d j: %d receive= %d\n",rank,j, recv_node[j]);
										}
									}
									else {
										int dummy;
										MPI_Recv(&dummy, 1, MPI_INT, i, 10, MPI_COMM_WORLD, &status);
									}
								}
							}
							
						}
						else{
							flag = 0;
							int dummy;
							for(int i = 0; i < thread_num; ++i) {
								if(i != rank)
									MPI_Recv(&dummy, 1, MPI_INT, i, 10, MPI_COMM_WORLD, &status);
							}
						}
						MPI_Barrier(MPI_COMM_WORLD);
						//printf("rank= %d checkpoint 8\n",rank);
						
						for(int i = 0; i < thread_num; ++i) {
							if(arr[i] != nullptr)
								free(arr[i]);
						}
					}
					//after simulation
					//calculate total infected number
					infected_count = infected.size();
					
					for(auto it = infected.begin(); it != infected.end(); ++it) {
						//printf("rank %d, infected: %d\n", rank, *it);
					}

					int influence;
					MPI_Allreduce(&infected_count, &influence,1,MPI_INT,MPI_SUM,MPI_COMM_WORLD);
					
					total_influence += influence;
				}//***** end for sample
				float avg_influence = total_influence / sample_times;
				//if(rank==0) printf("node= %d average influence: %f \n", candidate, avg_influence);
				if(avg_influence > max_influence) {
					max_influence = avg_influence;
	                maximized_node = candidate;
				}
				/*
				else if(avg_influence == max_influence){
					// Arbitrary tie break
	                if(getRandomNumber() > 0.5) {
	                    max_influence = avg_influence;
	                    maximized_node = candidate;
	                }
				}*/
				seed.erase(candidate);
			}//***** end if
    	}//***** end for candidate
    	seed.insert(maximized_node);
    	printf("max node: %d max influence: %f \n", maximized_node, max_influence);
    }//***** end while
    
	if(rank == 0) {
		if( clock_gettime( CLOCK_REALTIME, &stop) == -1 ) { perror("clock gettime");}		
		time = (stop.tv_sec - start.tv_sec)+ (double)(stop.tv_nsec - start.tv_nsec)/1e9;
		
		printf("Execution time = %f sec\n", time);
	}
	MPI_Finalize();
	return 0;
}