#!/bin/bash
#SBATCH --ntasks-per-node=1
#SBATCH --nodes=1
#SBATCH --time=16:00:00
#SBATCH --output=serialjob.out
#SBATCH --error=serialjob.err
#SBATCH --export=NONE

source /usr/usc/openmpi/default/setup.sh

srun --ntasks=${SLURM_NTASKS} ./serial > serial-output1.txt
