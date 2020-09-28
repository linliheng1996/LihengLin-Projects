#! /bin/bash
#salloc --nodes=4 --ntasks-per-node=1 --time=01:00:00
#source /usr/usc/openmpi/default/setup.sh
#make clean
#make serial
#echo "serial 1st run"
#./serial > serial1.txt
echo "serial 2nd run"
./serial > serial2.txt
#echo "serial 3rd run"
#./serial > serial3.txt
#echo "serial 4th run"
#./serial > serial4.txt
#echo "serial 5th run"
#./serial > serial5.txt
#echo "mpi-static 1st run"
#mpirun -np 4 ./mpi-static > static1.txt
#echo "mpi-static 2nd run"
#mpirun -np 4 ./mpi-static > static2.txt
#echo "mpi-static 3rd run"
#mpirun -np 4 ./mpi-static > static3.txt
#echo "mpi-static 4th run"
#mpirun -np 4 ./mpi-static > static4.txt
#echo "mpi-static 5th run"
#mpirun -np 4 ./mpi-static > static5.txt
echo "done...."
exit
