#!/bin/bash
#SBATCH --ntasks-per-node=1
#SBATCH --nodes=4
#SBATCH --time=04:00:00
#SBATCH --output=mpijob-dynamic.out
#SBATCH --error=mpijob-dynamic.err
#SBATCH --export=NONE

source /usr/usc/openmpi/default/setup.sh

srun --ntasks=${SLURM_NTASKS} --mpi=pmi2 ./mpi-dynamic 10 10 10 > mpi-dynamic-output.txt



