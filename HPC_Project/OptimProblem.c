//
// Created by bapti on 04/03/2024.
//
#include <stdio.h>
#include <stdlib.h>
#include "OptimProblem.h"


void init_problem(struct OptimProblem* problem){
    problem->n_var=0;
    problem->n_ctr=0;
    problem->A=NULL;
    problem->b=NULL;
    problem->c=NULL;
    problem->var_types=NULL;
}

void add_variable(struct OptimProblem* problem,int number_var,unsigned int type,double minRange,double maxRange){
    /*Add number_var identical variables on the range [minRange maxRange]
     * problem: the problem on which we want to add the variables
     * number_var: the number of variables we want to add.
     * type: CONTINOUS_VARIABLE (0) or DISCRETE_VARIABLE (1)
     * minRange: the min of this variable (put a value <=0 if no minimum)
     * maxRange: the max of this variable (put a value <=0 if no maximum)
     */

    int old_var=problem->n_var;
    int ptr_ctr= problem->n_ctr;
    int min=minRange>0;
    int max=maxRange>0;
    augment_size(problem,number_var,number_var*(min+max));

    int index_ecart=problem->n_var-problem->n_ctr;
    for (int i = 0; i < number_var; ++i) {
        if(type==DISCRETE_VARIABLE)
            problem->var_types[old_var+i]=1;

        if(min){
            if(type==DISCRETE_VARIABLE)
                problem->var_types[old_var+i]+=1+ptr_ctr;//encode the min constraints
            problem->A[ptr_ctr*problem->n_var+old_var+i]=1;
            problem->A[ptr_ctr*problem->n_var+(index_ecart+ptr_ctr)]=-1;
            problem->b[ptr_ctr]=minRange;
            ptr_ctr++;
        }
        if(max){
            if(type==DISCRETE_VARIABLE)
                problem->var_types[old_var+i]+=(1+ptr_ctr)*MAX_CONSTRAINTS;//encode the max constraints
            problem->A[ptr_ctr*problem->n_var+old_var+i]=1;
            problem->A[ptr_ctr*problem->n_var+(index_ecart+ptr_ctr)]=1;
            problem->b[ptr_ctr]=maxRange;
            ptr_ctr++;
        }

    }
}

void add_equality_constraint(struct OptimProblem* problem,double* coeff_variables,double treshold){
    /*add an equality constraints (on the form a1*x1+a2*x2+...+an*xn=b)
     * problem: the porblem on which we want to add a constraints
     * coeff_variables the coefficients (a1,..,an)
     * treshold: the value b
    */
    augment_size(problem,0,2);
    //loop over all the interesting variables
    for (int i = 0; i < problem->n_var-problem->n_ctr; ++i) {
        problem->A[(problem->n_ctr-2)*problem->n_var+i]=coeff_variables[i];
        problem->A[(problem->n_ctr-1)*problem->n_var+i]=coeff_variables[i];
    }
    problem->b[problem->n_ctr-2]=treshold;
    problem->b[problem->n_ctr-1]=treshold;
    problem->A[(problem->n_ctr-2)*problem->n_var+problem->n_var-2]=-1;
    problem->A[(problem->n_ctr-1)*problem->n_var+problem->n_var-1]=1;
}
void add_inferior_constraint(struct OptimProblem* problem,double* coeff_variables,double treshold){
    /*add an inequality constraints (on the form a1*x1+a2*x2+...+an*xn<=b)
     * problem: the porblem on which we want to add a constraints
     * coeff_variables the coefficients (a1,..,an)
     * treshold: the value b
    */
    augment_size(problem,0,1);
    //loop over all the interesting variables
    for (int i = 0; i < problem->n_var-problem->n_ctr; ++i) {
        problem->A[(problem->n_ctr-1)*problem->n_var+i]=coeff_variables[i];
    }
    problem->b[problem->n_ctr-1]=treshold;
    problem->A[(problem->n_ctr-1)*problem->n_var+problem->n_var-1]=1;
}

void add_superior_constraint(struct OptimProblem* problem,double* coeff_variables,double treshold){
    /*add an i,equality constraints (on the form a1*x1+a2*x2+...+an*xn>=b)
     * problem: the porblem on which we want to add a constraints
     * coeff_variables the coefficients (a1,..,an)
     * treshold: the value b
    */
    augment_size(problem,0,1);
    //loop over all the interesting variables
    for (int i = 0; i < problem->n_var-problem->n_ctr; ++i) {
        problem->A[(problem->n_ctr-1)*problem->n_var+i]=coeff_variables[i];
    }
    problem->b[problem->n_ctr-1]=treshold;
    problem->A[(problem->n_ctr-1)*problem->n_var+problem->n_var-1]=-1;
}

void copy_and_augment_size(struct OptimProblem* initial_problem,struct OptimProblem* new_problem,int nb_var,int nb_ctr){
    /*Copy the initial problem and augment his size
     * initial_problem: the problem we want tot copy
     * new_problem: the new problem (not initialized)
     * nb_var: the number of new variables
     *nb_ctr: the number of new cosntraints
    */
    //new size
    int new_var=initial_problem->n_var+nb_var+nb_ctr;
    int new_ctr=initial_problem->n_ctr+nb_ctr;
    new_problem->n_var=new_var;
    new_problem->n_ctr=new_ctr;

    //allocate new memory
    new_problem->c=calloc(new_var , sizeof(double));
    new_problem->var_types=calloc(new_var , sizeof(unsigned int));
    new_problem->b=calloc(new_ctr , sizeof(double));
    new_problem->A=calloc(new_ctr*new_var , sizeof(double));
    if(new_problem->c==NULL ||new_problem->b==NULL || new_problem->A==NULL){
        printf("Error allocation memory augment size");
        exit(0);
    }
    //copy c by parts because we want the epsilon to be at the end
    for (int i = 0; i < initial_problem->n_var-initial_problem->n_ctr; ++i) {
        new_problem->c[i]=initial_problem->c[i];
        new_problem->var_types[i]=initial_problem->var_types[i];
    }
    for (int i = 0; i < initial_problem->n_ctr; ++i) {
        new_problem->c[new_var-new_ctr+i]=initial_problem->c[initial_problem->n_var-initial_problem->n_ctr+i];
        new_problem->var_types[new_var-new_ctr+i]=initial_problem->var_types[initial_problem->n_var-initial_problem->n_ctr+i];
    }
    //copy b
    for (int i = 0; i < initial_problem->n_ctr; ++i) {
        new_problem->b[i]=initial_problem->b[i];
    }

    //copy values of A
    for (int j = 0; j < initial_problem->n_ctr; ++j){
        for (int i = 0; i < initial_problem->n_var-initial_problem->n_ctr; ++i) {
            new_problem->A[j*new_var+i]=initial_problem->A[j*initial_problem->n_var+i];
        }
        for (int i = 0; i < initial_problem->n_ctr; ++i) {
            new_problem->A[j*new_var+(new_var-new_ctr+i)]=initial_problem->A[j*initial_problem->n_var+(initial_problem->n_var-initial_problem->n_ctr)+i];
        }
    }
}

void augment_size(struct OptimProblem* problem,int upgrade_var,int upgrade_ctr){
    /*Augment the number of row and columns the initial problem
 * problem: the problem we want to augment
 * upgrade_var: the number of new variables
 *upgrade_ctr: the number of new cosntraints
*/
    //new size
    int new_var=problem->n_var+upgrade_var+upgrade_ctr;
    int new_ctr=problem->n_ctr+upgrade_ctr;

    //allocate new memory
    double* c=calloc(new_var , sizeof(double));
    unsigned int* var_types=calloc(new_var , sizeof(unsigned int));
    if(problem->n_var>0) {
        if(problem->n_ctr!=new_ctr)problem->b = realloc(problem->b, new_ctr * sizeof(double));
    }else
        problem->b=calloc(new_ctr , sizeof(double));
    double* A=calloc(new_ctr*new_var , sizeof(double));

    if(c==NULL ||problem->b==NULL || A==NULL){
        printf("Error allocation memory augment size");
        exit(0);
    }

    for (int i = 0; i < problem->n_var-problem->n_ctr; ++i) {
        c[i]=problem->c[i];
        var_types[i]=problem->var_types[i];
    }
    for (int i = 0; i < problem->n_ctr; ++i) {
        c[new_var-new_ctr+i]=problem->c[problem->n_var-problem->n_ctr+i];
        var_types[new_var-new_ctr+i]=problem->var_types[problem->n_var-problem->n_ctr+i];
    }

    //copy values of A
    for (int j = 0; j < problem->n_ctr; ++j){
        for (int i = 0; i < problem->n_var-problem->n_ctr; ++i) {
            A[j*new_var+i]=problem->A[j*problem->n_var+i];
        }
        for (int i = 0; i < problem->n_ctr; ++i) {
            A[j*new_var+(new_var-new_ctr+i)]=problem->A[j*problem->n_var+(problem->n_var-problem->n_ctr)+i];
        }
    }
    //modify the problem
    free(problem->A);
    free(problem->c);
    free(problem->var_types);
    problem->c=c;
    problem->var_types=var_types;
    problem->n_var=new_var;
    problem->A=A;
    problem->n_ctr=new_ctr;
}


void free_optimProblem(struct OptimProblem* problem){
    //avoid memory leak
    free(problem->A);
    free(problem->b);
    free(problem->c);
    free(problem->var_types);
}

void print_problem(struct OptimProblem* problem){
    printf("c=(");
    for (int i = 0; i < problem->n_var-1; ++i) printf("%f, ",problem->c[i]);
    printf("%20.2f)\n",problem->c[problem->n_var-1]);
    if(problem->n_ctr==0)return;


    for (int j = 0; j < problem->n_ctr; ++j) {
        for (int i = 0; i < problem->n_var; ++i){
            printf("%3.2f ",problem->A[j*problem->n_var+i]);
        }
        printf("=%f\n",problem->b[j]);
    }
}