#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <sys/time.h> // needed for timing
#include "derivative.h"
#include "Fields.h"
#include <cuda_runtime.h>
#include <device_launch_parameters.h>
#include <cooperative_groups.h>
#define NX_VALUE 150
#define NY_VALUE 150
#define USUAL_VX_OUTPUT 12
#define USUAL_VX_INPUT 12 //
namespace cg = cooperative_groups;

typedef double gpu_fp;

double get_time(void);
void initFluid(struct Fluid *fluid, int Nx, int Ny,int ghost_zone,double dx,double dy);

struct Fluid{
    double density;
    double viscosity;
    double t;
    double dt;
    struct Field2D* speed;
    struct Field2D* old;
    struct FieldScalar* pressure;
    struct Field2D* temp2D;
    struct FieldScalar* tempScalar;
    struct FieldScalar* smoke;
    char* mask;
};

struct Params{
    struct Fluid* fluid;
    double time_tp;
    double dtp;
};

void output_gf(struct Fluid* fluid);
//use __shared for dot

__device__
void forces(double* dot_cx,double* dot_cy,double t,int i,int j){
    /*const int Nx=dot->Nx;
    const int Ny=dot->Ny;
    const double dx=dot->dx;
    const double dy=dot->dy;
     int index=INDEX_GPU(i,j,dot->Nx);
     */


    dot_cx[j]=0;
    dot_cy[j]=0;
}
__device__
void derivative_no_pressure(struct Fluid* fluid,double* dot_cx,double* dot_cy,double time_t,int i,int j){
    const int Nx=fluid->speed->Nx;
    //const int Ny=fluid->speed->Ny;
    const double dx=fluid->speed->dx;
    const double dy=fluid->speed->dy;
    //const int ghost_zone=fluid->speed->ghost_zone;
    forces(dot_cx,dot_cy,time_t,i,j);

    double viscOverDens=fluid->viscosity/fluid->density;
    int index=INDEX_GPU(i,j,Nx);
    double term_laplacien=viscOverDens*(SD_2O_CEN_GPU(fluid->speed->cx,index,1,dx)+
                                        SD_2O_CEN_GPU(fluid->speed->cy,index,Nx,dy));
    dot_cx[j]+= term_laplacien;
    dot_cy[j]+= term_laplacien;
}
__device__
void set_boundaries(struct Fluid* fluid,double time_t,int i,int j){
    const int Nx=fluid->speed->Nx;
    const int Ny=fluid->speed->Ny;
    const double dx=fluid->speed->dx;
    const double dy=fluid->speed->dy;

    //y=0 boundary
    if(j==0) {
        fluid->speed->cx[i] = 0;
        fluid->speed->cy[i] = 0;
    }
    //y=1 boundary
    if(j==Ny-1) {
        fluid->speed->cx[i + Nx * (Ny - 1)] = 0;
        fluid->speed->cy[i + Nx * (Ny - 1)] = 0;
    }

    if(i==0) {
        fluid->speed->cx[Nx*j]=USUAL_VX_INPUT;
        if(j>55)fluid->speed->cx[Nx*j]=-0;
        fluid->speed->cy[Nx*j]=0;
        fluid->smoke->v[Nx*j]=0;
       /* if(j>40+20* cos(0.25*time_t) && j<60+20* cos(0.25*time_t)){
            fluid->speed->cx[Nx*j]=12;
            fluid->smoke->v[Nx*j]=1;
        }*/
        if(j>30 && j<40 ){
            fluid->smoke->v[Nx*j]=1;
        }
    }

    if(i==Nx-1){
        fluid->speed->cx[Nx*j+Nx-1]=0;
        if(j>50)fluid->speed->cx[Nx*j+Nx-1]=-USUAL_VX_OUTPUT;
        fluid->speed->cy[Nx*j+Nx-1]=0;
        fluid->smoke->v[Nx*j+Nx-1]=0;
        if(j>70 && j<80){
            fluid->smoke->v[Nx*j+Nx-1]=1;
        }
    }


    int index = INDEX_GPU(i, j, Nx);
    if(fluid->mask[index]==0){
        fluid->speed->cx[index]=0;
        fluid->speed->cy[index]=0;
        fluid->smoke->v[index]=-1;
    }

}


__global__
void RK4(struct Fluid* fluid,double time_t,double dt){

    cooperative_groups::grid_group g = cooperative_groups::this_grid();
    dt= fluid->dt;
    time_t=fluid->t;
    __shared__ double shared_cx[NY_VALUE];
    __shared__ double shared_cy[NY_VALUE];
    __shared__ double old_cx[NY_VALUE];
    __shared__ double old_cy[NY_VALUE];
    __shared__ double dot_cx[NY_VALUE];
    __shared__ double dot_cy[NY_VALUE];
    int j =  threadIdx.x;
    //int i = blockIdx.x;

    const int Nx=fluid->speed->Nx;
    const int Ny=fluid->speed->Ny;
    const double dx=fluid->speed->dx;
    const double dy=fluid->speed->dy;
    const int ghost_zone=fluid->speed->ghost_zone;

    double hdt = 0.5 * dt;
    double dt_6 = dt / 6;
    double dt_3 = dt / 3;

    int rep=(Nx+gridDim.x-1)/gridDim.x;
    for(int id=0;id<rep;id++) {
        int i = blockIdx.x + id * gridDim.x;
        int index = INDEX_GPU(i, j, Nx);
        int mask = i >= ghost_zone && j >= ghost_zone && i < Nx - ghost_zone &&
                   j < Ny - ghost_zone;
        if (mask)mask = fluid->mask[index] != 0;

        if (i >= 0 && j >= 0 && i < Nx && j < Ny) {
            if (mask)derivative_no_pressure(fluid, dot_cx, dot_cy, time_t, i, j);
            else set_boundaries(fluid, time_t, i, j);
            old_cx[j] = fluid->speed->cx[index];
            old_cy[j] = fluid->speed->cy[index];

            shared_cx[j] = old_cx[j] + dt_6 * dot_cx[j];
            shared_cy[j] = old_cy[j] + dt_6 * dot_cy[j];
        }
        g.sync();

        if (mask) {
            fluid->speed->cx[index] += hdt * dot_cx[j];
            fluid->speed->cy[index] += hdt * dot_cy[j];
        }
        g.sync();

        if (i >= 0 && j >= 0 && i < Nx && j < Ny) {
            if (mask)derivative_no_pressure(fluid, dot_cx, dot_cy, time_t, i, j);
            else set_boundaries(fluid, time_t, i, j);
            shared_cx[j] += dt_3 * dot_cx[j];
            shared_cy[j] += dt_3 * dot_cy[j];
        }

        g.sync();

        if (mask) {
            fluid->speed->cx[index] += old_cx[j] + hdt * dot_cx[j];
            fluid->speed->cy[index] += old_cy[j] + hdt * dot_cy[j];
        }

        g.sync();

        if (i >= 0 && j >= 0 && i < Nx && j < Ny) {
            if (mask)derivative_no_pressure(fluid, dot_cx, dot_cy, time_t, i, j);
            else set_boundaries(fluid, time_t, i, j);
            shared_cx[j] += dt_3 * dot_cx[j];
            shared_cy[j] += dt_3 * dot_cy[j];
        }
        g.sync();

        if (mask) {

            fluid->speed->cx[index] += old_cx[j] + dt * dot_cx[j];
            fluid->speed->cy[index] += old_cy[j] + dt * dot_cy[j];
        }
        g.sync();

        if (i >= 0 && j >= 0 && i < Nx && j < Ny) {
            if (mask)derivative_no_pressure(fluid, dot_cx, dot_cy, time_t, i, j);
            else set_boundaries(fluid, time_t, i, j);
        }
        g.sync();

        if (i >= ghost_zone && j >= ghost_zone && i < Nx - ghost_zone && j < Ny - ghost_zone){
            if (mask) {
                fluid->speed->cx[index] = shared_cx[j] + dt_6 * dot_cx[j];
                fluid->speed->cy[index] = shared_cy[j] + dt_6 * dot_cy[j];

            }
            fluid->pressure->v[index] = 0;
        }
    }
}

void set_boundaries_CPU(struct Fluid* fluid,double time_t){
    const int Nx=fluid->speed->Nx;
    const int Ny=fluid->speed->Ny;
    const double dx=fluid->speed->dx;
    const double dy=fluid->speed->dy;
    const int ghost_zone=fluid->speed->ghost_zone;
    for (int i = 0; i < Nx; ++i) {
        //y=0 boundary
        fluid->speed->cx[i]=0;
        fluid->speed->cy[i]=0;
        //y=1 boundary
        fluid->speed->cx[i+Nx*(Ny-1)]=0;
        fluid->speed->cy[i+Nx*(Ny-1)]=0;
    }
    for (int j = 0; j < Ny; ++j) {
        //x=0 boundary
        fluid->speed->cx[Nx*j]=USUAL_VX_INPUT;
        if(j>55)fluid->speed->cx[Nx*j]=-0;
        fluid->speed->cy[Nx*j]=0;
        //x=1 boundary
        fluid->speed->cx[Nx*j+Nx-1]=0;
        if(j>55)fluid->speed->cx[Nx*j+Nx-1]=-USUAL_VX_OUTPUT;
        fluid->speed->cy[Nx*j+Nx-1]=0;
        fluid->smoke->v[Nx*j]=0;
        fluid->smoke->v[Nx*j+Nx-1]=0;
        if(j>30 && j<40){
            fluid->smoke->v[Nx*j]=1;

        }
        if(j>70 && j<80){
            fluid->smoke->v[Nx*j+Nx-1]=1;
        }
        /*if(j>40+20* cos(0.25*time_t) && j<60+20* cos(0.25*time_t)){
            fluid->speed->cx[Nx*j]=12;
            fluid->smoke->v[Nx*j]=1;
            //printf("%f %f\n",40+20* cos(0.5*time_t),time_t);
        }*/
    }
    for (int j = ghost_zone; j < Ny-ghost_zone; ++j) {
        for (int i = ghost_zone; i < Nx - ghost_zone; ++i) {
            int index = INDEX(i, j, Nx);
            if(fluid->mask[index]>0)continue;
            fluid->speed->cx[index]=0;
            fluid->speed->cy[index]=0;
            fluid->smoke->v[index]=-1;
        }
    }

}

void solve_pressure2(struct Fluid* fluid,double time_t,double dt){
    const int Nx=fluid->speed->Nx;
    const int Ny=fluid->speed->Ny;
    const double dx=fluid->speed->dx;
    const double dy=fluid->speed->dy;
    const int ghost_zone=fluid->speed->ghost_zone;
    divergence(fluid->speed,fluid->tempScalar);

    for (int ite = 0; ite < 10000; ++ite) {
        double error=0;
        double maxe=0;

        divergence(fluid->speed,fluid->tempScalar);
        for (int j = ghost_zone; j < Ny-ghost_zone; ++j) {
            for (int i = ghost_zone; i < Nx-ghost_zone; ++i) {
                int index=INDEX(i,j,Nx);
                if(fluid->mask[index]==0)continue;
                double value_e=fluid->tempScalar->v[index];
                error+=value_e*value_e;
                maxe= fmax(fabs(value_e),maxe);
            }
        }
        error/=(Nx-2)*(Ny-2);
        if(error<1e-4 ||ite>=1000){
            printf("Iteration: %d;Error:%20.16e Max:%20.16e\n",ite,error,maxe);
            break;
        }

        if(ite==0){
            printf("Initialisation Error:%20.16e Max:%20.16e\n",error,maxe);
        }

        for (int k = 0; k < 100; ++k) {
            for (int j = ghost_zone; j < Ny-ghost_zone; ++j) {
                for (int i = ghost_zone; i < Nx-ghost_zone; ++i) {
                    int index=INDEX(i,j,Nx);
                    if(fluid->mask[index]==0)continue;
                    double div=2*dx*FD_2O_CEN(fluid->speed->cx,INDEX(i,j,Nx),1,dx)+2*dy*FD_2O_CEN(fluid->speed->cy,INDEX(i,j,Nx),Nx,dy);
                    int count_neighs=4;
                    if(fluid->mask[INDEX(i-1,j,Nx)]==0)count_neighs--;
                    if(fluid->mask[INDEX(i+1,j,Nx)]==0)count_neighs--;
                    if(fluid->mask[INDEX(i,j-1,Nx)]==0)count_neighs--;
                    if(fluid->mask[INDEX(i,j+1,Nx)]==0)count_neighs--;

                    fluid->speed->cy[INDEX(i,j-1,Nx)]+=div/count_neighs*1.5;
                    fluid->speed->cy[INDEX(i,j+1,Nx)]-=div/count_neighs*1.5;
                    fluid->speed->cx[INDEX(i-1,j,Nx)]+=div/count_neighs*1.5;
                    fluid->speed->cx[INDEX(i+1,j,Nx)]-=div/count_neighs*1.5;
                    fluid->pressure->v[index]+=div/4*fluid->density*dx/dt;
                }
            }
            set_boundaries_CPU(fluid,time_t);
        }
    }

}

__global__
void printer(struct Fluid* fluid,double time_t,double dt) {
    printf("Enter\n");
}

__global__
void advect(struct Fluid* fluid,double time_t,double delta_t){
    cooperative_groups::grid_group g = cooperative_groups::this_grid();
    delta_t= fluid->dt;
    time_t=fluid->t;
    __shared__ double shared_cx[NY_VALUE];
    __shared__ double shared_cy[NY_VALUE];
    __shared__ double shared_v[NY_VALUE];
    int j =  threadIdx.x;

    const int Nx=fluid->speed->Nx;
    const int Ny=fluid->speed->Ny;
    const double dx=fluid->speed->dx;
    const double dy=fluid->speed->dy;
    const int ghost_zone=fluid->speed->ghost_zone;
    int rep=(Nx+gridDim.x-1)/gridDim.x;
    for(int id=0;id<rep;id++) {
        int i=blockIdx.x+id*gridDim.x;
        int index = INDEX_GPU(i, j, Nx);

        if (i >= ghost_zone && j >= ghost_zone && i < Nx - ghost_zone && j < Ny - ghost_zone)
            if (fluid->mask[index] != 0) {

                double newX = i - delta_t / dx * fluid->speed->cx[index];

                double newY = j - delta_t / dy * fluid->speed->cy[index];
                newX = fmax(0.0, fmin(0.0 + Nx - 1, newX));
                newY = fmax(0.0, fmin(0.0 + Ny - 1, newY));
                int nx = (int) newX;
                int ny = (int) newY;

                double difx = newX - nx;
                double dify = newY - ny;
                shared_cx[j] = (1 - difx) * (1 - dify) * fluid->speed->cx[INDEX_GPU(nx, ny, Nx)]
                               + (difx) * (1 - dify) * fluid->speed->cx[INDEX_GPU(nx + 1, ny, Nx)]
                               + (1 - difx) * (dify) * fluid->speed->cx[INDEX_GPU(nx, ny + 1, Nx)]
                               + (difx) * (dify) * fluid->speed->cx[INDEX_GPU(nx + 1, ny + 1, Nx)];
                shared_cy[j] = (1 - difx) * (1 - dify) * fluid->speed->cy[INDEX_GPU(nx, ny, Nx)]
                               + (difx) * (1 - dify) * fluid->speed->cy[INDEX_GPU(nx + 1, ny, Nx)]
                               + (1 - difx) * (dify) * fluid->speed->cy[INDEX_GPU(nx, ny + 1, Nx)]
                               + (difx) * (dify) * fluid->speed->cy[INDEX_GPU(nx + 1, ny + 1, Nx)];
                shared_v[j] =
                        (1 - difx) * (1 - dify) * fmax(fluid->smoke->v[INDEX_GPU(nx, ny, Nx)], 0.0)
                        + (difx) * (1 - dify) * fmax(fluid->smoke->v[INDEX_GPU(nx + 1, ny, Nx)], 0.0)
                        + (1 - difx) * (dify) * fmax(fluid->smoke->v[INDEX_GPU(nx, ny + 1, Nx)], 0.0)
                        + (difx) * (dify) * fmax(fluid->smoke->v[INDEX_GPU(nx + 1, ny + 1, Nx)], 0.0);
            }

        g.sync();

        if (i >= ghost_zone && j >= ghost_zone && i < Nx - ghost_zone && j < Ny - ghost_zone)
            if (fluid->mask[index] != 0) {
                fluid->speed->cx[index] = shared_cx[j];
                fluid->speed->cy[index] = shared_cy[j];
                fluid->smoke->v[index] = fluid->smoke->v[index] * 0.1 + 0.9 * shared_v[j];
            }
    }
}


int main(int argc, char **argv) {
    const int Nx=NX_VALUE;
    const int Ny=NY_VALUE;
    const int ghost_zone=1;
    const double dx=70.0/(Nx-1);
    const double dy=70.0/(Ny-1);

    double* dtp;
    CUDA_CHECK(cudaMallocManaged((void**)&dtp, sizeof(double )));
    *dtp=0.1;
    printf("DT:%f\n",*dtp);
    double dt=*dtp;
    struct Fluid* fluid;
    CUDA_CHECK(cudaMallocManaged(&fluid, sizeof(struct Fluid)));

    printf("Entraine\n");
    initFluid(fluid,Nx,Ny,ghost_zone,dx,dy);
    printf("Fin init\n");

    char* centered_mask;
    CUDA_CHECK(cudaMallocManaged(&(centered_mask), Nx * Ny * sizeof(char)));
    FILE *fichier;
    // Chemin du fichier à ouvrir en mode lecture ("r" pour read)
    const char *chemin_fichier = "simMask/duck110.txt";
    fichier = fopen(chemin_fichier, "r");
    // Vérifier si l'ouverture du fichier a réussi
    if (fichier == NULL) {
        fprintf(stderr, "Erreur lors de l'ouverture du fichier.\n");
        return 1; // Quitter le programme avec code d'erreur
    }

    for(int i =0;i<Nx;i++){
        for(int j =0;j<Ny;j++){
            fluid->mask[INDEX(i,j,Nx)]=1;
            centered_mask[INDEX(i,j,Nx)]=1;
        }
    }

    double value=0;
    int count=0;
    while (fscanf(fichier, "%lf", &value) == 1) {
        if(count>=Nx*Ny)break;
        int i=count/Nx;
        int j=count%Nx;

        centered_mask[count]=1;
        fluid->mask[count]=1;
       /* if(value<0.5 ){//&& i>5 && i<Nx-5 && j>5 && j<Ny-5){
            centered_mask[count]=0;
            fluid->mask[count]=0;
            fluid->smoke->v[count]=-1;
        }*/
        count++;
    }
    printf("Fin %d/%d\n",count,Nx*Ny);
    fclose(fichier);

    fluid->density=1;
    fluid->viscosity=0.001;



//------------------------------------------------------------------------

//------------------------------------------------------------------------

    int numBlocksPerSm = 0;
    // Number of threads my_kernel will be launched with
    int numThreads = Ny;
    cudaDeviceProp deviceProp;
    cudaGetDeviceProperties(&deviceProp, 0);
    cudaOccupancyMaxActiveBlocksPerMultiprocessor(&numBlocksPerSm, (void*) RK4, numThreads, 0);
    // launch
    double* time_p;
    CUDA_CHECK(cudaMallocManaged((void**)&time_p, sizeof(double )));
    *time_p=0;
    // Lancement du kernel avec cudaLaunchCooperativeKernel
    dim3 dimBlock(numThreads, 1, 1);
    dim3 dimGrid(deviceProp.multiProcessorCount*numBlocksPerSm, 1, 1);
    if (dimGrid.x > Nx)
    {
        dimGrid.x = Nx;
    }
    printf("numBlocks = %d, blockSize = %d, totalThreads=%d %d\n", dimGrid.x, dimBlock.x, dimGrid.x * dimBlock.x,deviceProp.multiProcessorCount);

    double  begin=get_time();
    output_gf(fluid);

    int iterations=0;

    while(*time_p<=100){
        fluid->dt=dt;
        fluid->t=*time_p;
        struct {
            struct Fluid* fluid;
            double t;
            double dt;
        } params = { fluid,*time_p,*dtp };


        void* kernelArgs[] = { (void*)&params };

        printf("Time:%f\n",*time_p);
        //dont remove necessary but idk why
        if(*time_p==0) {
            printer<<<1, 1>>>(fluid, *time_p, 0);
            CUDA_CHECK(cudaDeviceSynchronize());
        }

        CUDA_CHECK(cudaLaunchCooperativeKernel((void*) RK4, dimGrid, dimBlock, kernelArgs));
        CUDA_CHECK(cudaDeviceSynchronize());

        solve_pressure2(fluid,*time_p,dt);

       cudaLaunchCooperativeKernel((void*) advect, dimGrid, dimBlock, kernelArgs);
        CUDA_CHECK(cudaDeviceSynchronize());
        *time_p+=dt;
        iterations+=1;
        if(iterations%(10)==0){
            output_gf(fluid);
        }
    }

    printf("Fin time:%f Iteration:%d\n",get_time()-begin,iterations);

    freeFieldScalar(fluid->pressure);
    freeFieldScalar(fluid->tempScalar);
    freeField3D(fluid->speed);
    freeField3D(fluid->temp2D);
    freeField3D(fluid->old);
    freeFieldScalar(fluid->smoke);
    cudaFree(centered_mask);
    cudaFree(fluid->speed);
    cudaFree(fluid->temp2D);
    cudaFree(fluid->tempScalar);
    cudaFree(fluid->pressure);
    cudaFree(fluid->old);
    cudaFree(fluid->smoke);
    cudaFree(fluid->mask);
    return 0;
}

/*
        double error=99999;
        int ite=0;
        while(error>1e-5 && ite<100){
            ite++;
            error=0;

            cudaLaunchCooperativeKernel((void*) solve_pressure, dimGrid, dimBlock, kernelArgs);
            CUDA_CHECK(cudaDeviceSynchronize());
            double maxi=0;
            int im=0;int jm=0;
            for(int i =ghost_zone;i<Nx-ghost_zone;i++){
                for(int j =ghost_zone;j<Ny-ghost_zone;j++){
                    error+=fluid->tempScalar->v[INDEX(i,j,Nx)];
                    if(fluid->tempScalar->v[INDEX(i,j,Nx)]>maxi){
                        im=i;jm=j;
                        maxi=fluid->tempScalar->v[INDEX(i,j,Nx)];
                    }
                }
            }
            //printf("%f\n",fluid->speed->cx[INDEX(8,7,Nx)]);
            error/=(Nx-2)*(Ny-2);
            printf("Iteration: %d;Error:%20.16e %d %d %d %f\n",ite,error,im,jm, INDEX(im,jm,Nx),maxi);
        }
        printf("Iteration: %d;Error:%20.16e\n",ite,error);*/

void initFluid(struct Fluid *fluid, int Nx, int Ny,int ghost_zone,double dx,double dy) {

    // Allouer de la mémoire pour chaque structure et tableau sur le GPU
    CUDA_CHECK(cudaMallocManaged(&(fluid->speed), sizeof(struct Field2D)));
    CUDA_CHECK(cudaMallocManaged(&(fluid->temp2D), sizeof(struct Field2D)));
    CUDA_CHECK(cudaMallocManaged(&(fluid->old), sizeof(struct Field2D)));
    CUDA_CHECK(cudaMallocManaged(&(fluid->pressure), sizeof(struct FieldScalar)));
    CUDA_CHECK(cudaMallocManaged(&(fluid->tempScalar), sizeof(struct FieldScalar)));
    CUDA_CHECK(cudaMallocManaged(&(fluid->smoke), sizeof(struct FieldScalar)));
    CUDA_CHECK(cudaMallocManaged(&(fluid->mask), Nx * Ny * sizeof(char)));

    if (fluid->speed == NULL || fluid->temp2D == NULL ||
        fluid->old == NULL || fluid->pressure == NULL || fluid->tempScalar == NULL ||
        fluid->smoke == NULL || fluid->mask == NULL) {
        printf("Error dynamic allocation for fluid\n");
        exit(-1);
    }
    // Initialiser d'autres champs si nécessaire
    initField2D(fluid->speed,Nx,Ny,ghost_zone,dx,dy);
    initField2D(fluid->temp2D,Nx,Ny,ghost_zone,dx,dy);
    initField2D(fluid->old,Nx,Ny,ghost_zone,dx,dy);
    initFieldScalar(fluid->pressure,Nx,Ny,ghost_zone,dx,dy);
    initFieldScalar(fluid->tempScalar,Nx,Ny,ghost_zone,dx,dy);
    initFieldScalar(fluid->smoke,Nx,Ny,ghost_zone,dx,dy);
}

void output_gf(struct Fluid* fluid)
{

    static int counter = 0;
    const int Nx=fluid->speed->Nx;
    const int Ny=fluid->speed->Ny;
    const double dx=fluid->speed->dx;
    const double dy=fluid->speed->dy;

    char name_buff[1024];
    snprintf(name_buff, 1024, "../results/%07d.asc", counter);
    FILE *ofile = fopen(name_buff, "w");
    for (int j = 0; j < Ny; ++j)
    {
        for (int i = 0; i < Nx; ++i)
        {
            const int ij = INDEX(i, j, Nx);
            fprintf(ofile, "%20.16e %20.16e %20.16e %20.16e %20.16e %20.16e\n", i * dx,j * dy,
                    fluid->pressure->v[ij],fluid->speed->cx[ij],fluid->speed->cy[ij],fluid->smoke->v[ij]);
        }
    }
    ++counter;

    fclose(ofile);
    ofile = NULL;
}

double get_time(void)
{
    struct timeval tv;
    gettimeofday(&tv, NULL);

    return (tv.tv_sec) + 1.0e-6 * tv.tv_usec;
}

__global__
void solve_pressure(struct Fluid* fluid,double time_t,double dt){
    dt= fluid->dt;
    time_t=fluid->t;
    cooperative_groups::grid_group g = cooperative_groups::this_grid();

    int j =  threadIdx.x;
    const int Nx=fluid->speed->Nx;

    const int Ny=fluid->speed->Ny;
    const double dx=fluid->speed->dx;
    const double dy=fluid->speed->dy;
    const int ghost_zone=fluid->speed->ghost_zone;

    int rep=(Nx+gridDim.x-1)/gridDim.x;

    for (int k = 0; k < 250; ++k) {
        for(int id=0;id<rep;id++) {
            int i = blockIdx.x + id * gridDim.x;
            int index = INDEX_GPU(i, j, Nx);
            int mask = i >= ghost_zone && j >= ghost_zone && i < Nx - ghost_zone &&
                       j < Ny - ghost_zone;
            if(mask){
                mask=fluid->mask[index] != 0;
            }
            int count_neighs = 4;
            if (mask) {
                if (fluid->mask[INDEX_GPU(i - 1, j, Nx)] == 0 || i - 1 <= ghost_zone)count_neighs--;
                if (fluid->mask[INDEX_GPU(i + 1, j, Nx)] == 0 || i + 1 > Nx - ghost_zone)count_neighs--;
                if (fluid->mask[INDEX_GPU(i, j - 1, Nx)] == 0 || j - 1 <= ghost_zone)count_neighs--;
                if (fluid->mask[INDEX_GPU(i, j + 1, Nx)] == 0 || j + 1 > Ny - ghost_zone)count_neighs--;
            }
            double div = 0;

            if (mask) {
                div = 2 * dx * FD_2O_CEN_GPU(fluid->speed->cx, index, 1, dx) +
                      2 * dy * FD_2O_CEN_GPU(fluid->speed->cy, index, Nx, dy);
                fluid->pressure->v[index] = 0.5 * div;
                fluid->speed->cx[index] += 0.5 * (fluid->pressure->v[INDEX_GPU(i + 1, j, Nx)] -
                                                  fluid->pressure->v[INDEX_GPU(i - 1, j, Nx)]);
                fluid->speed->cy[index] += 0.5 * (fluid->pressure->v[INDEX_GPU(i, j + 1, Nx)] -
                                                  fluid->pressure->v[INDEX_GPU(i, j - 1, Nx)]);
                fluid->pressure->v[index] += div / 4 * fluid->density * dx / dt;
            } else {
                fluid->pressure->v[index] = 0;
                if (fluid->mask[index] != 0)set_boundaries(fluid, time_t, i, j);
            }
            g.sync();
        }
    }

    for(int id=0;id<rep;id++) {
        int i = blockIdx.x + id * gridDim.x;
        int index = INDEX_GPU(i, j, Nx);
        int mask = i >= ghost_zone && j >= ghost_zone && i < Nx - ghost_zone &&
                   j < Ny - ghost_zone;
        if(mask){
            mask=fluid->mask[index] != 0;
        }
        if (mask) {
            double div = FD_2O_CEN_GPU(fluid->speed->cx, index, 1, dx) + FD_2O_CEN_GPU(fluid->speed->cy, index, Nx, dy);
            fluid->tempScalar->v[index] = div * div;

        } else {
            if (fluid->mask[index] != 0)set_boundaries(fluid, time_t, i, j);
        }
    }
}



