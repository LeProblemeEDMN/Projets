

#ifndef HPC_PROJECT_BRANCHANDBOUND_H
#define HPC_PROJECT_BRANCHANDBOUND_H
#include "OptimProblem.h"
#include "Simplex.h"
#define INCREMENT_STACK 256

int solveBranchAndBound(struct OptimProblem* problem,struct SimplexResult* result,int max_iteration);
#endif //HPC_PROJECT_BRANCHANDBOUND_H
