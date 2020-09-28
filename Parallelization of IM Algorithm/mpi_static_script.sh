#! /bin/bash
salloc --nodes=4 --ntasks-per-node=1 --time=01:00:00
echo "sourcing"
source /usr/usc/openmpi/default/setup.sh
#make clean
#make mpi-static
echo "mpi-static 1st run"
mpirun -np 4 ./mpi-static > static-test.txt
echo "done...."
exit
