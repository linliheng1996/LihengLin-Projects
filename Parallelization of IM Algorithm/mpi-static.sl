#!/bin/bash
#SBATCH --ntasks-per-node=1
#SBATCH --nodes=4
#SBATCH --time=04::00
#SBATCH --output=mpijob-static.out
#SBATCH --error=mpijob-static.err
#SBATCH --export=NONE

source /usr/usc/openmpi/default/setup.sh

srun --ntasks=${SLURM_NTASKS} --mpi=pmi2 ./mpi-static > mpi-static-output1.txt



