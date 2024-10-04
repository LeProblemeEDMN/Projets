//
// Created by bapti on 05/03/2024.
//
#ifndef SIMPLEX_H
#define SIMPLEX_H
#include "OptimProblem.h"

#define EPSILON 0.000000001
#define MAX_ITERATION 20000

#define OPTIMAL_SOLUTION 1
#define INFEASIBLE 2
#define SUBOPTIMAL_SOLUTION 3
#define TIME_LIMIT 4
#define UNBOUNDED 5

struct SimplexResult{
    unsigned short state;
    unsigned int size_solution;
    double cost;
    double* solution;
};
void solve(struct OptimProblem* problem,struct SimplexResult* result);
void solve_treshold(struct OptimProblem* problem,struct SimplexResult* result,double earlyQuit);
void determine_feasibility(struct OptimProblem* problem,struct SimplexResult* result);
int test_point_feasible(struct OptimProblem* problem,double* x);
void simplex(struct SimplexResult* result,double* c,double* A,double* b,double* x,int n_var,int n_constraints,double earlyQuit,int maxIteration);
void printSR(struct SimplexResult* result);
void matrix_product(int n, double* A, double* b, double* y);
void gauss_elimination(int n, double* A, double* b, double* x);

#endif /* SIMPLEX_H */