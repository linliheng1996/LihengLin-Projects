#!/bin/bash
#SBATCH --ntasks-per-node=1
#SBATCH --nodes=4
#SBATCH --time=00:30:00
#SBATCH --output=gp.out
#SBATCH --error=gp.err
#SBATCH --export=NONE

source /usr/usc/openmpi/default/setup.sh

srun --ntasks=${SLURM_NTASKS} --mpi=pmi2 ./graph-partition
