#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <sys/time.h> // needed for timing
#include "OptimProblem.h"
#include "Simplex.h"
#include "BranchAndBound.h"
double get_time(void);
void add_constraints(int id,int n_var,double sign,struct OptimProblem* problem,double x0,double y0,double x1,double y1);
void test1();
void testKS(int N,int capacity);
void testPlanning(int N);
//pre optim genre le truc qui modif les ctr pr quelles soient plus ressérées  chvatal-gomory


void solvePDH(double* p,int* d,int* h,int N){
    int* index= malloc(sizeof(int )*N);
    int late=0;
    for (int i = 0; i < N; ++i) {
        index[i]=-1;
        late+=d[i];
    }
    double totalCost=0;
    for (int i = N-1; i >=0 ; --i) {
        int best=-1;
        double best_cost=9999999;
        for (int j = 0; j < N; ++j) {
            double c=p[j]* fmax(0,late-d[j]-h[j]);
            if(best_cost>c && index[j]<0){
                best_cost=c;
                best=j;
            }
        }
        late-=d[best];
        totalCost+=best_cost;
        index[best]=i;
    }
    printf("Cost:%f\n",totalCost);
    for (int i = 0; i < N; ++i) {
        printf("%d:%d ",i,index[i]);
    }
    printf("\n");
}

double continous_knapSack(int capacity, int* weights, double* values, int n) {
    double utility=0;
    double c=capacity;
    while (c>0){
        int besstid=0;
        double ratio=values[0]/weights[0];
        for (int i = 1; i < n; ++i) {
            double r=values[i]/weights[i];
            if(r>ratio){
                ratio=r;
                besstid=i;
            }
        }

        double  amount= fmin(1,(double)c/weights[besstid]);

        c-=amount*weights[besstid];
        utility+=amount*values[besstid];
        values[besstid]*=-1;
    }
    for (int i = 0; i < n; ++i) {
        if(values[i]<0)values[i]*=-1;
    }
    return utility;
}

double knapSack(int capacity, int* weights, double* values, int n) {
    double* dp= malloc(sizeof(double)*(capacity+1));

    // Initialiser le tableau dp à 0
    for (int i = 0; i <= capacity; i++) {
        dp[i] = 0;
    }

    // Remplir le tableau dp en utilisant la programmation dynamique
    for (int i = 0; i < n; i++) {
        for (int w = capacity; w >= weights[i]; w--) {
            dp[w] = fmax(dp[w], dp[w - weights[i]] + values[i]);
        }
    }

    return dp[capacity];
}

int main(int argc, char **argv) {
    //todo stabilite parallelisme ajout liste
    //todo modif resultat pr enlever variables supplementaire
    const double t_begin = get_time();
    //test1();
    testKS(atoi(argv[1]),atoi(argv[2]));
   // testPlanning(atoi(argv[1]));


    const double t_end = get_time();
    printf("Time:%f\n",t_end-t_begin);
    //test minimisation:

/*int N=atoi(argv[1]);
    double* A= malloc(sizeof(double )*N*N);
    double* b= malloc(sizeof(double )*N*N);
    double* x= malloc(sizeof(double )*N*N);
    for (int i = 0; i < N; ++i) {
        b[i]=(double)(rand()%500000+1)/5000-50;
        for (int j = 0; j < N; ++j) {
            A[i+j*N]=(double)(rand()%500000+1)/5000-50;
        }
    }
    gauss_elimination(N,A,b,x);
    for (int i = 0; i < N; ++i) {
        printf("%f ",b[i]);
    }
    printf("\n");
    matrix_product(N,A,x,b);
    for (int i = 0; i < N; ++i) {
        printf("%f ",b[i]);
    }
    printf("\n");*/
    return 0;
}



void testKS(int N,int capacity){
    int* w= malloc(sizeof(int)*N);
    double* u= malloc(sizeof(double )*N);


    for (int i = 0; i < N; ++i) {
        w[i]=rand()%20+1;
        u[i]=(double)(rand()%50000+1)/5000+1;
    }
    double c= knapSack(capacity,w,u,N);
    double c_c= continous_knapSack(capacity,w,u,N);
    printf("Solution: %f  continuous:%f\n",c,c_c);

    double * w_d= malloc(sizeof(double )*N);
    for (int j = 0; j < N; ++j)
        w_d[j]=w[j];

    struct OptimProblem pb;
    init_problem(&pb);

    add_variable(&pb,N,DISCRETE_VARIABLE,0,1);
    for (int j = 0; j < N; ++j)
        pb.c[j]=u[j];

    add_inferior_constraint(&pb,w_d,capacity);

    struct SimplexResult result;
    struct OptimProblem copy_pb;
    copy_and_augment_size(&pb,&copy_pb,0,0);
    //print_problem(&pb);
    //print_problem(&copy_pb);

    //solve(&pb,&result);
    int ite=solveBranchAndBound(&pb,&result,MAX_ITERATION);
    printf("Iteration: %d\n",ite);
    //printSR(&result);
    double  v=0;
    double c_v=0;
   for (int i = 0; i < N; ++i) {
        c_v+=u[i]*result.solution[i];
        v+=w[i]*result.solution[i];
    }
    printf("%f %f %d\n",v,c_v,capacity);
    struct SimplexResult result2;
    solve(&copy_pb,&result2);
    //printSR(&result2);
    /*for (int i = 0; i < copy_pb.n_var-copy_pb.n_ctr; ++i) {
        //printf("%f ",result2.solution[i]);
        if(result2.solution[i]>0){
            printf("%d %f %f\n",i,result2.solution[i],u[i]/w[i]);
        }
    }*/
    //printf("\n");
    v=0;
    c_v=0;
    for (int i = 0; i < N; ++i) {
        c_v+=u[i]*result2.solution[i];
        v+=w[i]*result2.solution[i];
    }
    printf("%f %f %d\n",v,c_v,capacity);
    free(result.solution);

    free_optimProblem(&pb);


}

void test1(){
    struct OptimProblem pb;
    init_problem(&pb);
    add_variable(&pb,2,DISCRETE_VARIABLE,0,5.3);
    add_variable(&pb,1,DISCRETE_VARIABLE,1,2);
    for (int i = 0; i < 1; ++i) {
        //add_variable(&pb,1,DISCRETE_VARIABLE,(double)(rand()%50000+1)/10000,(double)(rand()%50000+1)/10000+0.01+100);
        //pb.c[2+i]=(double)(rand()%50000+1)/10000-2.5;
    }
    pb.c[0]=1;
    pb.c[1]=1;
    //pb.c[2]=1;

   // add_constraints(0,num_variables,1,&pb,0,3,4,4);
    //add_constraints(1,num_variables,-1,&pb,3,0,4,4);
   // add_constraints(2,num_variables,-1,&pb,0,3,3,0);

    print_problem(&pb);

    struct SimplexResult result;
    //int ite=solveBranchAndBound(&pb,&result);
    //printf("Iteration: %d\n",ite);
    solve(&pb,&result);
    printSR(&result);
    free(result.solution);
    free_optimProblem(&pb);
}

//y-ax+e=b <-> y-ax<b si < et signe 1
void add_constraints(int id,int n_var,double sign,struct OptimProblem* problem,double x0,double y0,double x1,double y1){
    double pente=(y1-y0)/(x1-x0);
    double origin=y0-pente*x0;
    double ctr[]={-pente,1};
    if(sign==1){
        add_inferior_constraint(problem,ctr,origin);
    }else{
        add_superior_constraint(problem,ctr,origin);
    }
}
double get_time(void)
{
    struct timeval tv;
    gettimeofday(&tv, NULL);

    return (tv.tv_sec) + 1.0e-6 * tv.tv_usec;
}

void testPlanning(int N){
    double* p= malloc(sizeof(double)*(N));
    int* h= malloc(sizeof(int )*N);
    int* d= malloc(sizeof(int )*N);

    for (int i = 0; i < N; ++i) {
        p[i]=(double)(rand()%50000+1)/10000+0.01;
        d[i]=rand()%10+2;
        h[i]=rand()%(3*N)+N/2;
        printf("(%f,%d,%d)\n",p[i],d[i],h[i]);
    }
    struct OptimProblem pb;
    init_problem(&pb);

    add_variable(&pb,N,CONTINOUS_VARIABLE,0,0);//late var
    add_variable(&pb,N*N,DISCRETE_VARIABLE,0,1);// i+j*N =1 si i passe avant j
    //ctr for late
    for (int i = 0; i < N; ++i) {
        double* ctr= calloc((N+N*N),sizeof(double));
        for (int j = 0; j < N; ++j){
            if(i==j)continue;
            ctr[N+j+i*N]=-d[j];

        }
        ctr[i]=1;
        add_superior_constraint(&pb,ctr,-h[i]);
        /*double* ctr2= calloc((N+N*N),sizeof(double));
        ctr2[i]=1;
        add_superior_constraint(&pb,ctr2,0);*/
    }
    //print_problem(&pb);
    //setup cost
    for (int j = 0; j < N; ++j)
        pb.c[j]=-p[j];

    //
    for (int i = 0; i < N; ++i) {
        for (int j = 0; j < N; ++j) {
            if(i==j)continue;
            double* ctr= calloc((N+N*N),sizeof(double));
            ctr[N+i+j*N]=1;
            ctr[N+j+i*N]=1;
            add_equality_constraint(&pb,ctr,1);
        }
    }
    for (int i = 0; i < N; ++i) {
        for (int j = 0; j < N; ++j) {
            if(i==j)continue;
            for (int k = 0; k < N; ++k) {
                if(i==k || j==k) continue;
                double* ctr= calloc((N+N*N),sizeof(double));
                ctr[N+i+j*N]=1;
                ctr[N+j+k*N]=1;
                ctr[N+i+k*N]=-1;
                add_inferior_constraint(&pb,ctr,1);
            }
        }
    }

    double* x= calloc((N+N*N),sizeof(double));
    for (int i = 0; i < N; ++i)x[i]=999;
    for (int i = 0; i < N; ++i) {
        for (int j = 0; j < i; ++j) {
            x[N+i+j*N]=1;
        }

    }

    struct SimplexResult result;
    //solve(&pb,&result);
    //determine_feasibility(&pb,&result);
    //print_problem(&pb);

    int ite=1;
    ite=solveBranchAndBound(&pb,&result,MAX_ITERATION);
    printf("FIN\n");
    //printSR(&result);

    solvePDH(p,d,h,N);

    printf("feasability %d\n",test_point_feasible(&pb,x));
    printf("Status %d:\n",result.state);
    printf("Iteration: %d Cost: %f\n",ite,result.cost);
    printf("Retard:");

    for (int i = 0; i < N; ++i) {
        printf("%f ",result.solution[i]);
    }
    printf("\n");


    printf("\n Y=\n");
    for (int i = 0; i < N; ++i) {
        double count=0;
        for (int j = 0; j < N; ++j) {
            count+=result.solution[N+j+i*N];
            printf("%f ",result.solution[N+j+i*N]);
        }
        printf("(%d:%f) \n",i,count);
    }
    printf("\n");
}



