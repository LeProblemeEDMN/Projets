//
// Created by bapti on 08/03/2024.
//

#include <omp.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "BranchAndBound.h"


#define THREAD_PER_ITERATION 1

int solveBranchAndBound(struct OptimProblem* problem,struct SimplexResult* result,int max_iteration){
    result->cost=-999999;
    result->state=INFEASIBLE;
    int max_stack_size=INCREMENT_STACK;
    struct OptimProblem* stack= malloc(sizeof(struct OptimProblem)*max_stack_size);
    if(stack==NULL){
        printf("Branch and Bound: Error allocation stack");
        return -1;
    }
    struct OptimProblem copy_pb;
    copy_and_augment_size(problem,&copy_pb,0,0);
    copy_pb.dual=9999999;
    stack[0]=copy_pb;

    int size_stack=1;

    int ite=0;


    omp_set_num_threads(THREAD_PER_ITERATION);

    int maxlength=0;

    FILE *fptr;
    fptr = fopen("dualPrimal.asc", "w");//C:/Users/bapti/PycharmProjects/IDE/res/

    int threadsRunned=0;

    while(size_stack>0 && ite<max_iteration){

        double high_dual=-999999;
        double low_dual=999999;

#pragma omp parallel
    for (int i = 0; i < THREAD_PER_ITERATION; ++i) {

        struct OptimProblem current;
        int passe=0;
        int init_size=size_stack;
#pragma omp critical
        {
            threadsRunned++;
            if (size_stack > 0) {

                for (int j = 0; j < size_stack; ++j) {
                    high_dual= fmax(stack[j].dual,high_dual);
                    low_dual= fmin(stack[j].dual,low_dual);
                }
                fprintf(fptr, "%d %20.16e %20.16e %20.16e %d\n",ite,high_dual,low_dual,result->cost,init_size);

                ite++;
                size_stack--;
                passe=1;
                current = stack[size_stack];
                printf("stack size %d\n",size_stack);
            }
            maxlength=fmax(maxlength,size_stack);

        }

        if (passe==0)continue;

        if(current.dual<result->cost){
            free_optimProblem(&current);
            continue;
        }

        struct OptimProblem current_solve;
        copy_and_augment_size(&current, &current_solve, 0, 0);//copy so solving the

        struct SimplexResult current_result;
        solve_treshold(&current_solve, &current_result, 999999);//result->cost

        free_optimProblem(&current_solve);
        //check optimality
        if (current_result.state == TIME_LIMIT) {
            printf("Branch and bound: simplex reached time limit! (to implement)");
            continue;
        } else if (current_result.state == INFEASIBLE || (current_result.cost < result->cost ) || test_point_feasible(problem,current_result.solution)==0) {

            free_optimProblem(&current);
            free(current_result.solution);

            continue;
        }

        int id_most_frac = -1;
        double most_frac = 0.0;
        for (int i = 0; i < current.n_var - current.n_ctr; ++i) {
            double frac = fabs(round(current_result.solution[i]) - current_result.solution[i]);
            if (current.var_types[i] > 0 && frac > most_frac) {
                id_most_frac = i;
                most_frac = frac;
            }
        }
        if(current_result.cost>current.dual){
            printf("Error more constraints higher dual:%f %f\n",current_result.cost,current.dual);
        }
        if (id_most_frac == -1 || most_frac < EPSILON) {

            //free old optimum result
            if (result->state != INFEASIBLE)free(result->solution);
            //modify optim
            result->solution = current_result.solution;
            result->cost = current_result.cost;
            result->state = SUBOPTIMAL_SOLUTION;
            result->size_solution = current_result.size_solution;
            result->size_solution=problem->n_var;

        } else {
            //create subproblems
            int min_ctr = current.var_types[id_most_frac] % MAX_CONSTRAINTS - 2;
            int max_ctr = current.var_types[id_most_frac] / MAX_CONSTRAINTS - 1;
            struct OptimProblem current_min;

            copy_and_augment_size(&current, &current_min, 0, fmax(-min_ctr, 0));
            //just modify
            if (min_ctr >= 0) {
                current_min.b[min_ctr] = fmax(current_min.b[min_ctr], ceil(current_result.solution[id_most_frac]));
            } else {
                current_min.var_types[id_most_frac] += 1 + current_min.n_ctr;
                current_min.b[current_min.n_ctr - 1] = ceil(current_result.solution[id_most_frac]);
                current_min.A[(current_min.n_ctr - 1) * current_min.n_var + id_most_frac] = 1;
                current_min.A[(current_min.n_ctr) * current_min.n_var - 1] = -1;

            }

            augment_size(&current, 0, fmax(-max_ctr, 0));

            if (max_ctr >= 0) {

                current.b[max_ctr] = fmin(current.b[max_ctr], floor(current_result.solution[id_most_frac]));
            } else {
                current.var_types[id_most_frac] += current.n_ctr * MAX_CONSTRAINTS;
                current.b[current.n_ctr - 1] = floor(current_result.solution[id_most_frac]);
                current.A[(current.n_ctr - 1) * current.n_var + id_most_frac] = 1;
                current.A[(current.n_ctr - 1) * current.n_var + current.n_var - 1] = 1;
            }
            current.dual=current_result.cost;
            current_min.dual=current_result.cost;
#pragma omp critical
            {
                //allocation for the two nodes in the same time
                if (size_stack + 1 >= max_stack_size) {
                    stack = realloc(stack, sizeof(struct OptimProblem) * (max_stack_size + INCREMENT_STACK));
                    if (stack == NULL) {
                        printf("Branch and Bound: Error reallocation stack");
                        //return -1;
                    }
                    max_stack_size += INCREMENT_STACK;
                }
                size_stack += 2;
                stack[size_stack - 1] = current;
                stack[size_stack - 2] = current_min;

            }
            //free result
            free(current_result.solution);
        }

    }

    }
    printf("Max stack size:%d\n",maxlength);
    printf("Threads runed:%d Percentage:%f\n",threadsRunned,(double)ite/threadsRunned);
    if(size_stack==0)result->state=OPTIMAL_SOLUTION;
    else result->state=TIME_LIMIT;
    fclose(fptr);
    return ite;
}
