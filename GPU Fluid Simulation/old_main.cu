#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <sys/time.h> // needed for timing
#include "derivative.h"
#include "Fields.h"
#include <cuda_runtime.h>
#include <device_launch_parameters.h>
#include <cooperative_groups.h>

#define NX_VALUE 110
#define NY_VALUE 110
namespace cg = cooperative_groups;

typedef double gpu_fp;

double get_time(void);
void initFluid(struct Fluid *fluid, int Nx, int Ny,int ghost_zone,double dx,double dy);

/*
 * CFLAGS=-lm -I.
DEPS = derivative.h Fields.h
OBJ = main.o Fields.o

%.o: %.c $(DEPS)
	gcc -Wall -fopenmp -O3 --fast-math -c -o $@ $< $(CFLAGS)

Simul: $(OBJ)
	gcc -Wall -fopenmp -O3 --fast-math -o $@ $^ $(CFLAGS)
clean:
	-rm Simul:
 */
struct Fluid{
    double density;
    double viscosity;
    struct Field2D* speed;
    struct Field2D* old;
    struct FieldScalar* pressure;
    struct Field2D* dot;
    struct Field2D* temp2D;
    struct FieldScalar* tempScalar;
    struct FieldScalar* smoke;
    char* mask;
};
void output_gf(struct Fluid* fluid);
/*
__device__
void forces(struct Field2D* dot,double t,int i,int j){
    const int Nx=dot->Nx;
    const int Ny=dot->Ny;
    const double dx=dot->dx;
    const double dy=dot->dy;

    int index=INDEX_GPU(i,j,dot->Nx);
    dot->cx[index]=0;
    dot->cy[index]=0;


}
__device__
void derivative_no_pressure(struct Fluid* fluid,double time_t,int i,int j){
    const int Nx=fluid->speed->Nx;
    const int Ny=fluid->speed->Ny;
    const double dx=fluid->speed->dx;
    const double dy=fluid->speed->dy;
    const int ghost_zone=fluid->speed->ghost_zone;
    forces(fluid->dot,time_t,i,j);

    double viscOverDens=fluid->viscosity/fluid->density;
    int index=INDEX_GPU(i,j,Nx);
    double term_laplacien=viscOverDens*(SD_2O_CEN_GPU(fluid->speed->cx,index,1,dx)+
            SD_2O_CEN_GPU(fluid->speed->cy,index,Nx,dy));
    fluid->dot->cx[index]+= term_laplacien;
    fluid->dot->cy[index]+= term_laplacien;
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
        fluid->speed->cx[Nx*j]=0;
        fluid->speed->cy[Nx*j]=0;
        fluid->smoke->v[Nx*j]=0;
        if(j>80+40* cos(0.25*time_t) && j<100+40* cos(0.25*time_t)){
            fluid->speed->cx[Nx*j]=12;
            fluid->smoke->v[Nx*j]=1;
        }
    }
    if(i==Nx-1){
        fluid->speed->cx[Nx*j+Nx-1]=12*20.0/Ny;
        fluid->speed->cy[Nx*j+Nx-1]=0;
        fluid->smoke->v[Nx*j+Nx-1]=0;
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
    //__shared__ double shared_v[NY_VALUE];
    //__shared__ double shared_cx[NY_VALUE];
    //__shared__ double shared_cy[NY_VALUE];

    int j =  threadIdx.x;
    int i = blockIdx.x;

    const int Nx=fluid->speed->Nx;
    const int Ny=fluid->speed->Ny;
    const double dx=fluid->speed->dx;
    const double dy=fluid->speed->dy;
    const int ghost_zone=fluid->speed->ghost_zone;

    int index = INDEX_GPU(i, j, Nx);
    double hdt = 0.5 * dt;
    double dt_6 = dt / 6;
    double dt_3 = dt / 3;

    if (i >= 0 && j >=0 && i < Nx && j < Ny){
        if (fluid->mask[index] != 0 && i >= ghost_zone && j >=ghost_zone && i < Nx-ghost_zone && j < Ny-ghost_zone)derivative_no_pressure(fluid, time_t,i,j);
        else set_boundaries(fluid, time_t,i,j);
    }

    __syncthreads();
    if (i >= ghost_zone && j >=ghost_zone && i < Nx-ghost_zone && j < Ny-ghost_zone)
        if (fluid->mask[index] != 0) {
            fluid->old->cx[index] = fluid->speed->cx[index];
            fluid->old->cy[index] = fluid->speed->cy[index];

            fluid->temp2D->cx[index] = fluid->speed->cx[index] + dt_6 * fluid->dot->cx[index];
            fluid->temp2D->cy[index] = fluid->speed->cy[index] + dt_6 * fluid->dot->cy[index];

            fluid->speed->cx[index] += hdt * fluid->dot->cx[index];
            fluid->speed->cy[index] += hdt * fluid->dot->cy[index];
        }
    __syncthreads();
    if (i >= 0 && j >=0 && i < Nx && j < Ny){
        if (fluid->mask[index] != 0 && i >= ghost_zone && j >=ghost_zone && i < Nx-ghost_zone && j < Ny-ghost_zone)derivative_no_pressure(fluid, time_t,i,j);
        else set_boundaries(fluid, time_t,i,j);
    }
    __syncthreads();
    if (i >= ghost_zone && j >=ghost_zone && i < Nx-ghost_zone && j < Ny-ghost_zone)
        if (fluid->mask[index] != 0) {
            fluid->temp2D->cx[index] += dt_3 * fluid->dot->cx[index];
            fluid->temp2D->cy[index] += dt_3 * fluid->dot->cy[index];

            fluid->speed->cx[index] += fluid->old->cx[index] + hdt * fluid->dot->cx[index];
            fluid->speed->cy[index] += fluid->old->cy[index] + hdt * fluid->dot->cy[index];
        }
    __syncthreads();
    if (i >= 0 && j >=0 && i < Nx && j < Ny){
        if (fluid->mask[index] != 0 && i >= ghost_zone && j >=ghost_zone && i < Nx-ghost_zone && j < Ny-ghost_zone)derivative_no_pressure(fluid, time_t,i,j);
        else set_boundaries(fluid, time_t,i,j);
    }

    if (i >= ghost_zone && j >=ghost_zone && i < Nx-ghost_zone && j < Ny-ghost_zone)
        if (fluid->mask[index] != 0) {
            fluid->temp2D->cx[index] += dt_3 * fluid->dot->cx[index];
            fluid->temp2D->cy[index] += dt_3 * fluid->dot->cy[index];

            fluid->speed->cx[index] += fluid->old->cx[index] + dt * fluid->dot->cx[index];
            fluid->speed->cy[index] += fluid->old->cy[index] + dt * fluid->dot->cy[index];
        }
    __syncthreads();
    if (i >= 0 && j >=0 && i < Nx && j < Ny){
        if (fluid->mask[index] != 0 && i >= ghost_zone && j >=ghost_zone && i < Nx-ghost_zone && j < Ny-ghost_zone)derivative_no_pressure(fluid, time_t,i,j);
        else set_boundaries(fluid, time_t,i,j);
    }
    __syncthreads();
    if (i >= ghost_zone && j >=ghost_zone && i < Nx-ghost_zone && j < Ny-ghost_zone)
        if (fluid->mask[index] != 0) {
            fluid->speed->cx[index]=fluid->temp2D->cx[index]+dt_6* fluid->dot->cx[index];
            fluid->speed->cy[index]=fluid->temp2D->cy[index]+dt_6* fluid->dot->cy[index];
        }

}
*/
void forces(struct Field2D* dot,double t){
    for (int j = dot->ghost_zone; j < dot->Ny-dot->ghost_zone; ++j) {
        for (int i = dot->ghost_zone; i < dot->Nx-dot->ghost_zone; ++i) {
            int index=INDEX(i,j,dot->Nx);
            dot->cx[index]=0;
            dot->cy[index]=0;
            /* double espx=i-50;
              if(espx>0){
                  espx= fmax(0,espx-25);
              }
              espx=(espx*espx)*dot->dx*dot->dx;
              double espy=(j-30.5)*(j-30.5)*dot->dy*dot->dy;
              dot->cx[index]=  fmax(-6,-3/sqrt(espx+espy));

              espy=(j-30.5)*(j-80.5)*dot->dy*dot->dy;
              dot->cx[index]= fmax(-6,-3/sqrt(espx+espy));*/

        }
    }
}

void derivative_no_pressure(struct Fluid* fluid,double time_t){
    const int Nx=fluid->speed->Nx;
    const int Ny=fluid->speed->Ny;
    const double dx=fluid->speed->dx;
    const double dy=fluid->speed->dy;
    const int ghost_zone=fluid->speed->ghost_zone;
    forces(fluid->dot,time_t);

    double viscOverDens=fluid->viscosity/fluid->density;
    double max1=0;
    for (int j = ghost_zone; j < Ny-ghost_zone; ++j) {
        for (int i = ghost_zone; i < Nx-ghost_zone; ++i) {
            int index=INDEX(i,j,Nx);
            double term_laplacien=viscOverDens*(SD_2O_CEN(fluid->speed->cx,index,1,dx)+
                                                SD_2O_CEN(fluid->speed->cy,index,Nx,dy));
            max1=fmax(max1,fabs(term_laplacien));
            fluid->dot->cx[index]+= term_laplacien;
            fluid->dot->cy[index]+= term_laplacien;
            /* fluid->dot->cx[index]+= fluid->speed->cx[index]* FD_2O_CEN(fluid->speed->cx,index,1,dx)+
                     fluid->speed->cy[index]* FD_2O_CEN(fluid->speed->cy,index,1,dx);
             fluid->dot->cy[index]+= fluid->speed->cx[index]* FD_2O_CEN(fluid->speed->cx,index,Nx,dy)+
                                     fluid->speed->cy[index]* FD_2O_CEN(fluid->speed->cy,index,Nx,dy);
          */
        }
    }

}

void set_boundaries(struct Fluid* fluid,double time_t){
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
        fluid->speed->cx[Nx*j]=0;

        fluid->speed->cy[Nx*j]=0;
        //x=1 boundary
        fluid->speed->cx[Nx*j+Nx-1]=12*20.0/Ny;
        fluid->speed->cy[Nx*j+Nx-1]=0;
        fluid->smoke->v[Nx*j]=0;
        fluid->smoke->v[Nx*j+Nx-1]=0;
        if(j>40+20* cos(0.25*time_t) && j<60+20* cos(0.25*time_t)){
            fluid->speed->cx[Nx*j]=12;
            fluid->smoke->v[Nx*j]=1;
            //printf("%f %f\n",40+20* cos(0.5*time_t),time_t);
        }
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


void RK4(struct Fluid* fluid,double time_t,double dt){
    const int Nx=fluid->speed->Nx;
    const int Ny=fluid->speed->Ny;
    const int ghost_zone=fluid->speed->ghost_zone;

    double hdt=0.5*dt;
    double dt_6=dt/6;
    double dt_3=dt/3;
    derivative_no_pressure(fluid,time_t);
    set_boundaries(fluid,time_t);
    double vmax=0;
    for (int j = ghost_zone; j < Ny-ghost_zone; ++j){
        for (int i = ghost_zone; i < Nx-ghost_zone; ++i) {
            int index=INDEX(i,j,Nx);
            if(fluid->mask[index]==0)continue;
            fluid->old->cx[index]=fluid->speed->cx[index];
            fluid->old->cy[index]=fluid->speed->cy[index];

            fluid->temp2D->cx[index]=fluid->speed->cx[index]+dt_6* fluid->dot->cx[index];
            fluid->temp2D->cy[index]=fluid->speed->cy[index]+dt_6* fluid->dot->cy[index];

            fluid->speed->cx[index]+=hdt* fluid->dot->cx[index];
            fluid->speed->cy[index]+=hdt* fluid->dot->cy[index];
            vmax= fmax(fmax(fabs(fluid->speed->cx[index]),fabs(fluid->speed->cy[index])),vmax);
        }
    }

    vmax=0;
    derivative_no_pressure(fluid,time_t+hdt);
    set_boundaries(fluid,time_t+hdt);
    for (int j = ghost_zone; j < Ny-ghost_zone; ++j){
        for (int i = ghost_zone; i < Nx-ghost_zone; ++i) {
            int index=INDEX(i,j,Nx);
            if(fluid->mask[index]==0)continue;
            fluid->temp2D->cx[index]+=dt_3* fluid->dot->cx[index];
            fluid->temp2D->cy[index]+=dt_3* fluid->dot->cy[index];

            fluid->speed->cx[index]+=fluid->old->cx[index]+hdt* fluid->dot->cx[index];
            fluid->speed->cy[index]+=fluid->old->cy[index]+hdt* fluid->dot->cy[index];
        }
    }
    derivative_no_pressure(fluid,time_t+hdt);
    set_boundaries(fluid,time_t+hdt);

    for (int j = ghost_zone; j < Ny-ghost_zone; ++j){
        for (int i = ghost_zone; i < Nx-ghost_zone; ++i) {
            int index=INDEX(i,j,Nx);
            if(fluid->mask[index]==0)continue;
            fluid->temp2D->cx[index]+=dt_3* fluid->dot->cx[index];
            fluid->temp2D->cy[index]+=dt_3* fluid->dot->cy[index];

            fluid->speed->cx[index]+=fluid->old->cx[index]+dt* fluid->dot->cx[index];
            fluid->speed->cy[index]+=fluid->old->cy[index]+dt* fluid->dot->cy[index];
        }
    }

    derivative_no_pressure(fluid,time_t+dt);
    set_boundaries(fluid,time_t+dt);
    for (int j = ghost_zone; j < Ny-ghost_zone; ++j){
        for (int i = ghost_zone; i < Nx-ghost_zone; ++i) {
            int index=INDEX(i,j,Nx);
            if(fluid->mask[index]==0)continue;
            fluid->speed->cx[index]=fluid->temp2D->cx[index]+dt_6* fluid->dot->cx[index];
            fluid->speed->cy[index]=fluid->temp2D->cy[index]+dt_6* fluid->dot->cy[index];
            vmax= fmax(fmax(fabs(fluid->speed->cx[index]),fabs(fluid->speed->cy[index])),vmax);
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
        if(error<1e-5 ||ite>=1000){
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
            //set_boundaries(fluid,time_t);
        }
    }

}

__global__
void advect(struct Fluid* fluid,double delta_t){
    __shared__ double shared_v[NY_VALUE];
    __shared__ double shared_cx[NY_VALUE];
    __shared__ double shared_cy[NY_VALUE];

    int j =  threadIdx.x;
    int i_stride=gridDim.x;
    int i = blockIdx.x;
    //cg::grid_group grid = cg::this_grid();
    const int Nx=fluid->speed->Nx;
    const int Ny=fluid->speed->Ny;
    const double dx=fluid->speed->dx;
    const double dy=fluid->speed->dy;
    const int ghost_zone=fluid->speed->ghost_zone;

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
            shared_cx[index] = (1 - difx) * (1 - dify) * fluid->speed->cx[INDEX_GPU(nx, ny, Nx)]
                                       + (difx) * (1 - dify) * fluid->speed->cx[INDEX_GPU(nx + 1, ny, Nx)]
                                       + (1 - difx) * (dify) * fluid->speed->cx[INDEX_GPU(nx, ny + 1, Nx)]
                                       + (difx) * (dify) * fluid->speed->cx[INDEX_GPU(nx + 1, ny + 1, Nx)];
            shared_cy[index] = (1 - difx) * (1 - dify) * fluid->speed->cy[INDEX_GPU(nx, ny, Nx)]
                                       + (difx) * (1 - dify) * fluid->speed->cy[INDEX_GPU(nx + 1, ny, Nx)]
                                       + (1 - difx) * (dify) * fluid->speed->cy[INDEX_GPU(nx, ny + 1, Nx)]
                                       + (difx) * (dify) * fluid->speed->cy[INDEX_GPU(nx + 1, ny + 1, Nx)];
            shared_v[index] =
                    (1 - difx) * (1 - dify) * fmax(fluid->smoke->v[INDEX_GPU(nx, ny, Nx)], 0.0)
                    + (difx) * (1 - dify) * fmax(fluid->smoke->v[INDEX_GPU(nx + 1, ny, Nx)], 0.0)
                    + (1 - difx) * (dify) * fmax(fluid->smoke->v[INDEX_GPU(nx, ny + 1, Nx)], 0.0)
                    + (difx) * (dify) * fmax(fluid->smoke->v[INDEX_GPU(nx + 1, ny + 1, Nx)], 0.0);

        }

    __syncthreads();

    if (i >= ghost_zone && j >= ghost_zone && i < Nx - ghost_zone && j < Ny - ghost_zone)
        if (fluid->mask[index] != 0) {
            shared_cx[index] = fluid->temp2D->cx[index];
            shared_cy[index] = fluid->temp2D->cy[index];
            shared_v[index] = fluid->tempScalar->v[index];
        }

}


int main(int argc, char **argv) {
    const int Nx=NX_VALUE;
    const int Ny=NY_VALUE;
    const int ghost_zone=1;
    const double dx=70.0/(Nx-1);
    const double dy=70.0/(Ny-1);
    double dt=0.05;
    struct Fluid* fluid;
    CUDA_CHECK(cudaMallocManaged(&fluid, sizeof(struct Fluid)));
    printf("Entraine\n");
    initFluid(fluid,Nx,Ny,ghost_zone,dx,dy);
    printf("Fin init\n");

    char* centered_mask;
    CUDA_CHECK(cudaMallocManaged(&(centered_mask), Nx * Ny * sizeof(char)));
    FILE *fichier;
    // Chemin du fichier à ouvrir en mode lecture ("r" pour read)
    const char *chemin_fichier = "duck110.txt";
    fichier = fopen(chemin_fichier, "r");
    // Vérifier si l'ouverture du fichier a réussi
    if (fichier == NULL) {
        fprintf(stderr, "Erreur lors de l'ouverture du fichier.\n");
        return 1; // Quitter le programme avec code d'erreur
    }
    double value=0;
    int count=0;
    while (fscanf(fichier, "%lf", &value) == 1) {
        if(count>=Nx*Ny)break;
        int i=count/Nx;
        int j=count%Nx;

        centered_mask[count]=1;
        fluid->mask[count]=1;
        if(value<0.5 && i>5 && i<Nx-5 && j>5 && j<Ny-5){
            centered_mask[count]=0;
            fluid->mask[count]=0;
            fluid->smoke->v[count]=-1;
        }
        count++;
    }
    printf("Fin %d/%d\n",count,Nx*Ny);
    fclose(fichier);

    fluid->density=1;
    fluid->viscosity=0.001;


    int numBlocksPerSm = 0;
    // Number of threads my_kernel will be launched with
    int numThreads = Ny;
    cudaDeviceProp deviceProp;
    cudaGetDeviceProperties(&deviceProp, 0);
    cudaOccupancyMaxActiveBlocksPerMultiprocessor(&numBlocksPerSm, (void*)advect, numThreads, 0);
    // launch
    void *kernelArgs[] = { fluid };
    dim3 dimBlock(numThreads, 1, 1);
    dim3 dimGrid(deviceProp.multiProcessorCount*numBlocksPerSm, 1, 1);
    /*if (dimGrid.x > Nx)
    {
        dimGrid.x = Nx;
    }*/
    printf("numBlocks = %d, blockSize = %d, totalThreads=%d\n", dimGrid.x, dimBlock.x, dimGrid.x * dimBlock.x);

    double  begin=get_time();
    output_gf(fluid);
    double t=0;
    int iterations=0;
    while(t<=10){
        printf("Time:%f\n",t);
        RK4(fluid,t,dt);//<<<Nx, Ny>>>
        //CUDA_CHECK(cudaDeviceSynchronize());
        solve_pressure2(fluid,t,dt);

        advect <<<Nx, Ny>>> (fluid,dt);
        CUDA_CHECK(cudaDeviceSynchronize());
        t+=dt;
        iterations+=1;
        if(iterations%5==0){
            output_gf(fluid);
        }

    }

    printf("Fin time:%f Iteration:%d\n",get_time()-begin,iterations);

    freeFieldScalar(fluid->pressure);
    freeFieldScalar(fluid->tempScalar);
    freeField3D(fluid->speed);
    freeField3D(fluid->dot);
    freeField3D(fluid->temp2D);
    freeField3D(fluid->old);
    freeFieldScalar(fluid->smoke);
    cudaFree(centered_mask);
    cudaFree(fluid->speed);
    cudaFree(fluid->dot);
    cudaFree(fluid->temp2D);
    cudaFree(fluid->tempScalar);
    cudaFree(fluid->pressure);
    cudaFree(fluid->old);
    cudaFree(fluid->smoke);
    cudaFree(fluid->mask);
    return 0;
}



void initFluid(struct Fluid *fluid, int Nx, int Ny,int ghost_zone,double dx,double dy) {

    // Allouer de la mémoire pour chaque structure et tableau sur le GPU
    CUDA_CHECK(cudaMallocManaged(&(fluid->speed), sizeof(struct Field2D)));
    CUDA_CHECK(cudaMallocManaged(&(fluid->dot), sizeof(struct Field2D)));
    CUDA_CHECK(cudaMallocManaged(&(fluid->temp2D), sizeof(struct Field2D)));
    CUDA_CHECK(cudaMallocManaged(&(fluid->old), sizeof(struct Field2D)));
    CUDA_CHECK(cudaMallocManaged(&(fluid->pressure), sizeof(struct FieldScalar)));
    CUDA_CHECK(cudaMallocManaged(&(fluid->tempScalar), sizeof(struct FieldScalar)));
    CUDA_CHECK(cudaMallocManaged(&(fluid->smoke), sizeof(struct FieldScalar)));
    CUDA_CHECK(cudaMallocManaged(&(fluid->mask), Nx * Ny * sizeof(char)));

    if (fluid->speed == NULL || fluid->dot == NULL || fluid->temp2D == NULL ||
        fluid->old == NULL || fluid->pressure == NULL || fluid->tempScalar == NULL ||
        fluid->smoke == NULL || fluid->mask == NULL) {
        printf("Error dynamic allocation for fluid\n");
        exit(-1);
    }
    // Initialiser d'autres champs si nécessaire
    initField2D(fluid->speed,Nx,Ny,ghost_zone,dx,dy);
    initField2D(fluid->dot,Nx,Ny,ghost_zone,dx,dy);
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
    snprintf(name_buff, 1024, "results/%07d.asc", counter);
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



