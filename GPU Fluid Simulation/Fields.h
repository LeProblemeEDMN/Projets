//
// Created by bapti on 19/04/2024.
//

#ifndef FLUID_VECTORFIELD_H
#define FLUID_VECTORFIELD_H

typedef double gpu_fp;

#define CUDA_CHECK(t_)                                            \
  do {                                                            \
       const long int  _err = t_;                                 \
       if (_err != cudaSuccess  )                                 \
       {                                                          \
         fprintf(stderr, "cuda function call %s failed\n", #t_);  \
         exit(-1);                                                \
       }                                                          \
    } while(0)

struct Field2D{
    int Nx,Ny;
    double dx,dy;
    int ghost_zone;
    double* cx;
    double* cy;
};

struct FieldScalar{
    int Nx,Ny;
    double dx,dy;
    int ghost_zone;
    double* v;
};
__device__
inline int INDEX_GPU(int i, int j, int Nx)
{
    return i+j*Nx;
}
inline int INDEX(int i, int j, int Nx)
{
    return i+j*Nx;
}

void laplacien(struct Field2D* vectors,struct FieldScalar* result);
void initField2D(struct Field2D* field2D,int Nx,int Ny,int ghost, double dx,double dy);
void initFieldScalar(struct FieldScalar* fieldScalar,int Nx,int Ny,int ghost, double dx,double dy);
void freeField3D(struct Field2D* field2D);
void freeFieldScalar(struct FieldScalar* fieldScalar);
void gradient(double* value,struct Field2D* result);
void divergence(struct Field2D* vectors,struct FieldScalar* result);
#endif //FLUID_VECTORFIELD_H
