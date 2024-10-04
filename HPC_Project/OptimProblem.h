#ifndef OPTIM_PROBLEM_H
#define OPTIM_PROBLEM_H

#define MAX_CONSTRAINTS 16384
#define CONTINOUS_VARIABLE 0
#define DISCRETE_VARIABLE 1


struct OptimProblem{
    unsigned int n_var;
    unsigned int n_ctr;
    unsigned int* var_types;//0 if continuous or 1+i+j*MAX_CONSTRAINTS where i is the id of the min ctr and j the id of the max ctr
    double* c;
    double* b;
    double* A;
    double dual;
};
void init_problem(struct OptimProblem* problem);
void add_inferior_constraint(struct OptimProblem* problem,double* coeff_variables,double treshold);
void augment_size(struct OptimProblem* problem,int upgrade_var,int upgrade_ctr);
void add_equality_constraint(struct OptimProblem* problem,double* coeff_variables,double treshold);
void add_superior_constraint(struct OptimProblem* problem,double* coeff_variables,double treshold);
void copy_and_augment_size(struct OptimProblem* initial_problem,struct OptimProblem* new_problem,int nb_var,int nb_ctr);
void print_problem(struct OptimProblem* problem);
void free_optimProblem(struct OptimProblem* problem);
void add_variable(struct OptimProblem* problem,int number_var,unsigned int type,double minRange,double maxRange);

#endif /* OPTIM_PROBLEM_H */