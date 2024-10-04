//
// Created by bapti on 05/03/2024.
//

#include "Simplex.h"

#include <stdio.h>
#include <stdlib.h>
#include <math.h>

void solve(struct OptimProblem* problem,struct SimplexResult* result){
    /*Solve a linear optimisation problem (written in his standard form) with the simplex algorithm.
     * Determine the initial extreme point and solve the problem
     * input:
     * problem: OptimProblem representing a linear optimisation problem (written in his standard form)
     * result: a SimplexResult representing the results will be fill by this function
     */
    solve_treshold(problem,result,999999999.0);
}
//test if time limit reached
void solve_treshold(struct OptimProblem* problem,struct SimplexResult* result,double earlyQuit){
    /*Solve a linear optimisation problem (written in his standard form) with the simplex algorithm.
     * Determine the initial extreme point and solve the problem
     * input:
     * problem: OptimProblem representing a linear optimisation problem (written in his standard form)
     * result: a SimplexResult representing the results will be fill by this function
     * earlyQuit: if during the execution of the simple the objective is superior to earlyquit the algorithm stop
     */
    double* x= calloc(problem->n_var,sizeof(double));
    if(x==NULL ){
        printf("Error allocation memory solve");
        exit(0);
    }
    
    //test if the point (0,0,...) is feasible (and can be used as the initial extreme point
    if(1==test_point_feasible(problem,x)){
        for (int i = 0; i < problem->n_ctr; ++i) {
            double v=problem->b[i];
            for (int j = 0; j < problem->n_var-problem->n_ctr; ++j) {
                v-=problem->A[i*problem->n_var+j]*x[j];
            }
            x[problem->n_var-problem->n_ctr+i]=fabs(v);
        }
        result->state=OPTIMAL_SOLUTION;
        result->cost=0;
        result->solution=x;
        result->size_solution=problem->n_var;
        //otherwise use the determine feasibility to find the initial extreme point
    }else {
        determine_feasibility(problem, result);
        for (int i = 0; i < problem->n_var-problem->n_ctr; ++i)
            x[i]=result->solution[i];
        for (int i = 0; i < problem->n_ctr; ++i)
            x[problem->n_var-problem->n_ctr+i]=result->solution[result->size_solution-problem->n_ctr+i];
        free(result->solution);
        result->solution=x;
        result->size_solution=problem->n_var;

    }

    //test infeasibility
    if(result->state==INFEASIBLE){
        return;
    }
    if(result->state==TIME_LIMIT){
        printf("Time limit reached when computing the feasibility return");
        return;
    }
    //solve with simplex
    simplex(result,problem->c,problem->A,problem->b,result->solution,problem->n_var,problem->n_ctr,earlyQuit,MAX_ITERATION);

}

void determine_feasibility(struct OptimProblem* problem,struct SimplexResult* result){
    /*Determine if a linear optimisation problem (written in his standard form) is feasible with the simplex algorithm.
     * input:
     * problem: OptimProblem representing a linear optimisation problem (written in his standard form)
     * result: a SimplexResult representing the results will be fill by this function
     */
    //init x which is an extreme corner of this feasible problem
    double* x= calloc(problem->n_var+problem->n_ctr,sizeof(double));
    if(x==NULL ){
        printf("Error allocation memory test feasibility");
        exit(0);
    }
    //create the feasibility problem
    struct OptimProblem feasibility_pb;
    copy_and_augment_size(problem,&feasibility_pb,problem->n_ctr,0);

    //reset c with zeros everywhere except for the new variables where we put 1
    for (int i = 0; i < problem->n_var-problem->n_ctr; ++i)
        feasibility_pb.c[i]=0;

    for (int i = 0; i < feasibility_pb.n_ctr; ++i){
        feasibility_pb.c[feasibility_pb.n_var-feasibility_pb.n_ctr+i]=0;
    }

    for (int i = 0; i < problem->n_ctr; ++i)
        feasibility_pb.c[problem->n_var-problem->n_ctr+i]=-1;

    //modify A
    for (int i = 0; i < problem->n_ctr; ++i) {
        int sign=feasibility_pb.b[i]>=0?1:-1;
        feasibility_pb.A[i*feasibility_pb.n_var+(problem->n_var-problem->n_ctr+i)]=sign;
    }
    //init initial extreme point
    for (int i = 0; i < feasibility_pb.n_ctr; ++i) {
        x[problem->n_var-problem->n_ctr+i]= fabs(feasibility_pb.b[i]);
    }

    //simplex to determine feasibility
    simplex(result,feasibility_pb.c,feasibility_pb.A,feasibility_pb.b,x
            ,feasibility_pb.n_var,feasibility_pb.n_ctr,9999999,MAX_ITERATION);

    result->solution=x;


    if(result->cost<=-EPSILON && result->state!=TIME_LIMIT){
        result->state=INFEASIBLE;
    }
    result->size_solution=feasibility_pb.n_var;
    free_optimProblem(&feasibility_pb);
}

int test_point_feasible(struct OptimProblem* problem,double* x){
    /*Test if the point is feasible (assume that x as at least problem->n_var-problem->n_ctr variables)
     * return 1 if the point is feasible 0 otherwise
     */
    //test all the variables are positives
    for (int j = 0; j < problem->n_var-problem->n_ctr; ++j) {
        if(x[j]<0)return 0;
    }
    //test if the constraint can be respected
    for (int i = 0; i < problem->n_ctr; ++i) {
        double v=problem->b[i];
        for (int j = 0; j < problem->n_var-problem->n_ctr; ++j) {
            v-=problem->A[i*problem->n_var+j]*x[j];
        }

        if(v/problem->A[i*problem->n_var+problem->n_var-problem->n_ctr+i]<-EPSILON){
            //printf("ctr:%d ,%20.16e %20.16e %20.16e\n",i,v,problem->A[i*problem->n_var+problem->n_var-problem->n_ctr+i],v/problem->A[i*problem->n_var+problem->n_var-problem->n_ctr+i]);
            return 0;
        }
    }
    return 1;
}

void simplex(struct SimplexResult* result,double* c,double* A,double* b,double* x,int n_var,int n_constraints,double earlyQuit,int maxIteration){
    /*Solve a linear optimisation problem (written in his standard form) with the simplex algorithm.
     * A,b,c,x will be modified by the algorithm. The last n_constraints variable must be the slack variables
     * input:
     * c: the cost vector
     * A: the matrix of full rank encoding the equalities
     * b: the vector of the equalities values
     * x: an extreme point (corner of the feasible set)
     * n_var: the number of variables
     * n_constraints: the number of constraints
     *
     * output: fill the result
     */
    /*struct OptimProblem pb;
    pb.n_var=n_var;
    pb.n_ctr=n_constraints;
    pb.A=A;
    pb.b=b;
    pb.c=c;

    struct OptimProblem pb2;
    copy_and_augment_size(&pb,&pb2,0,0);
    printf("Initial feasability %d\n", test_point_feasible(&pb2,x));*/
    result->size_solution=n_var;
    double cost_offset=0;
    double cost=0;
    for (int i = 0; i < n_var; ++i) cost+=c[i]*x[i];


    int* visited =malloc(sizeof(int )*n_var);
    for (int i = 0; i < n_var; ++i) {
        visited[i]=0;
    }


    //ids of the \hline variable
    int* ecart= malloc(n_constraints*sizeof(int));
    if(ecart==NULL)printf("Error allocation memory");
    for (int i = 0; i < n_constraints; ++i) ecart[i]=n_var-n_constraints+i;



    int loop=0;
    int zeroIte=0;
    //end condition
    while(loop<maxIteration){
        loop++;

        int id_augmentation=-1;//id of the pivot variable
        int total_associated_ctr=-1;//associated variable
        double total_best_augmentation=0;
        double cost_upgrade=0;
        for (int i = 0; i < n_var; ++i) {
            int flag=0;
            for (int j = 0; j < n_constraints; ++j) {
                if(ecart[j]==i){
                    flag=1;
                    break;
                }
            }
            if(flag==1){
                continue;
            }


            if(c[i]>EPSILON){
                int associated_ctr=-1;//associated variable
                double best_augmentation=9999999;
                //find how much we can augment this variable and still respect the cosntraints
                for (int j = 0; j < n_constraints; ++j) {
                    //check if it is unbounded on this constraints
                    if(A[i+j*n_var]/A[ecart[j]+j*n_var]<=EPSILON){
                        if(best_augmentation>9999999)associated_ctr=j;
                        continue;
                    }
                    //max step according to this constraints
                    double  v=A[ecart[j]+j*n_var]*x[ecart[j]]/A[i+j*n_var];

                    if(best_augmentation-v>=-EPSILON){
                        best_augmentation=v;
                        associated_ctr=j;
                    }
                }


                if(best_augmentation>=9999999){
                    x[id_augmentation]=9999999;//unbounded
                    result->cost=999999;
                    result->solution=x;
                    result->state=UNBOUNDED;
                }

                if(cost_upgrade<c[i]*best_augmentation+EPSILON){
                    total_best_augmentation=best_augmentation;
                    total_associated_ctr=associated_ctr;
                    id_augmentation=i;
                    cost_upgrade=c[i]*best_augmentation;
                    //break;
                }

            }else if(c[i]<0 && x[i]>0 && visited[i]==0){

                int associated_ctr=-1;//associated variable
                double best_augmentation=0;
                //find how much we can reduce this variable and still respect the constraints
                for (int j = 0; j < n_constraints; ++j) {
                    //check if it is unbounded on this constraints
                    if(fabs(A[i+j*n_var])>EPSILON){
                        associated_ctr=j;
                    }
                }


                if(cost_upgrade<EPSILON){
                    //printf("change\n");
                    total_best_augmentation=best_augmentation;
                    total_associated_ctr=associated_ctr;
                    id_augmentation=i;
                    cost_upgrade=0;
                }
            }
        }

        int associated_ctr=total_associated_ctr;//associated variable
        double best_augmentation=total_best_augmentation;
        if(fabs(best_augmentation)<EPSILON ){
            zeroIte++;//if(c[id_augmentation]<EPSILON)
            best_augmentation=0;
        }
        //if(loop>maxIteration/2){
           // printf("ite:%d/%d/%d id_aug:%d ctr:%d c:%f aug:%f, cost:%f\n",loop,zeroIte,n_constraints,id_augmentation,associated_ctr,c[id_augmentation],best_augmentation,cost);
        //}

        if(fabs(best_augmentation)<EPSILON && (zeroIte>loop )){//||zeroIte>n_constraints+10
            break;//we can't move and we exit
        }

        if(c[id_augmentation]<0){
            visited[id_augmentation]=1;
        }

        if(id_augmentation<0){//End condition
            break;
        }


        if(associated_ctr>=0) {
            //change the value of the variable
           if(c[id_augmentation]>0 && best_augmentation>EPSILON) {
                x[id_augmentation] += best_augmentation;
                x[ecart[associated_ctr]] = 0;
           }
            /*for (int i = 0; i < n_var; ++i) {
                printf("%f ",x[i]);
            }
            printf("\n");*/
            //divide the associated constraints by the coefficient of the pivot variable
            b[associated_ctr] /= A[id_augmentation  + associated_ctr*n_var];

            for (int i = 0; i < n_var; ++i) {
                if (i != id_augmentation) {
                    A[i + associated_ctr*n_var] /= A[id_augmentation + associated_ctr*n_var];
                    /*if(A[i + associated_ctr*n_var]<-1000000){
                        printf("C:%f,%f,%f\n",A[i + associated_ctr*n_var],A[id_augmentation + associated_ctr*n_var],c[id_augmentation]);
                    }*/
                }
            }

            A[id_augmentation + associated_ctr*n_var] = 1;

            //update c by replacing the pivot variable by this expression derived with the associated constraints
            for (int i = 0; i < n_var; ++i) {

                if (i != id_augmentation) {
                    c[i] -= A[i + associated_ctr*n_var] * c[id_augmentation];
                    /*if(c[i]>1000000){
                        printf("C:%f,%f,%f\n",c[i],A[i + associated_ctr*n_var],c[id_augmentation]);
                    }*/
                }
            }

            cost_offset += b[associated_ctr] * c[id_augmentation];
            c[id_augmentation]=0;
            //update A and b by replacing the pivot variable by this expression derived with the associated constraints
            for (int j = 0; j < n_constraints; ++j) {
                if (j == associated_ctr)continue;
                for (int i = 0; i < n_var; ++i) {
                    if (i != id_augmentation) {
                        A[i + j*n_var] -=
                                A[i + associated_ctr*n_var] * A[id_augmentation + j*n_var];
                    }
                }
                b[j] -= A[id_augmentation + j*n_var] * b[associated_ctr];
                //printf("aug:%f\n",A[id_augmentation + j*n_var] * b[associated_ctr]);
                x[ecart[j]]-=A[id_augmentation+j*n_var]/A[ecart[j]+j*n_var]*best_augmentation;//A[id_augmentation + j*n_var] * b[associated_ctr];
                if(x[ecart[j]]<EPSILON && x[ecart[j]]>-EPSILON)x[ecart[j]]=0;
                if(x[ecart[j]]<0 && best_augmentation>0)printf("INF<0: %f %f %f %20.16e\n",x[ecart[j]],c[id_augmentation],A[id_augmentation+j*n_var]/A[ecart[j]+j*n_var]*best_augmentation,best_augmentation);
                A[id_augmentation + j*n_var] = 0;
            }
            /*for (int j = 0; j < n_constraints; ++j){
                for (int i = 0; i < n_var; ++i) {
                    printf("%f ",A[i+j*n_var]);
                }
                printf("= %f\n",b[j]);
            }*/
        }


        if(associated_ctr>=0)ecart[associated_ctr]=id_augmentation;

        //recompute the values of the slack
        /*for (int i = 0; i < n_constraints; ++i) {
            if(i==associated_ctr)continue;
            double ec=b[i];

            for (int j = 0; j < n_var; ++j) {
                ec-=A[j+i*n_var]*x[j];
            }
            if(ec/A[ecart[i]+i*n_var]!=0)
                printf("%f %f %f\n",x[ecart[i]],ec/A[ecart[i]+i*n_var],A[ecart[i]+i*n_var]);
            //x[ecart[i]]+=ec/A[ecart[i]+i*n_var];//update valeur ancien slack pr conserver

        }*/

        //new cost
        cost=cost_offset;
        for (int i = 0; i < n_var; ++i) cost+=c[i]*x[i];
        if(cost>=earlyQuit) break;

        /*struct OptimProblem pb;
        pb.n_var=n_var;
        pb.n_ctr=n_constraints;
        pb.A=A;
        pb.b=b;
        pb.c=c;
*/
        //printf("test:%d\n", test_point_feasible(&pb2,x));
       // if(test_point_feasible(&pb2,x)==0)break;
    }


    free(visited);
    result->cost=cost;
    result->solution=x;
    if(maxIteration==loop)
        result->state=TIME_LIMIT;
    else if(cost>=earlyQuit)
        result->state=SUBOPTIMAL_SOLUTION;
    else
        result->state=OPTIMAL_SOLUTION;
}

void printSR(struct SimplexResult* result){
    printf("Simplex result\n");
    if(result->state==INFEASIBLE){
        printf("State: Infeasible\n");
        return;
    }
    if(result->state==SUBOPTIMAL_SOLUTION)printf("  State: Suboptimal solution (objective complete), Cost %f\n",result->cost);
    if(result->state==TIME_LIMIT)printf("  State: Reached time limit, Cost %f\n",result->cost);
    if(result->state==UNBOUNDED)printf("  State: Unbounded, Cost %f\n",result->cost);
    if(result->state==OPTIMAL_SOLUTION)printf("  State: Optimal solution, Cost %f\n",result->cost);
    printf("  Solution:(");
    for (int i = 0; i < result->size_solution-1; ++i) {
        printf("%f, ",result->solution[i]);
    }
    printf("%f)\n",result->solution[result->size_solution-1]);
}

void gauss_elimination(int n, double* A, double* b, double* x) {
    // Forward elimination
    for (int k = 0; k < n - 1; k++) {
        // Find pivot row
        int max_row = k;
        double max_val = A[k + k * n];
        for (int i = k + 1; i < n; i++) {
            if (fabs(A[i + k * n]) > fabs(max_val)) {
                max_row = i;
                max_val = A[i + k * n];
            }
        }

        // Swap rows
        if (max_row != k) {
            for (int j = 0; j < n; j++) {
                double temp = A[k + j * n];
                A[k + j * n] = A[max_row + j * n];
                A[max_row + j * n] = temp;
            }
            double temp = b[k];
            b[k] = b[max_row];
            b[max_row] = temp;
        }

        // Eliminate elements below pivot
        for (int i = k + 1; i < n; i++) {
            double factor = A[i + k * n] / A[k + k * n];
            for (int j = k; j < n; j++) {
                A[i + j * n] -= factor * A[k + j * n];
            }
            b[i] -= factor * b[k];
        }
    }

    // Back substitution
    for (int i = n - 1; i >= 0; i--) {
        double sum = 0.0;
        for (int j = i + 1; j < n; j++) {
            sum += A[i + j * n] * x[j];
        }
        x[i] = (b[i] - sum) / A[i + i * n];
    }
}

void matrix_product(int n, double* A, double* b, double* y) {
    for (int i = 0; i < n; i++) {
        y[i] = 0.0; // Initialize x[i] to 0
        for (int j = 0; j < n; j++) {
            y[i] += A[i + j * n] * b[j];
        }
    }
}
