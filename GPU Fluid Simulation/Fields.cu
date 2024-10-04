//
// Created by bapti on 19/04/2024.
//
#include <stdio.h>
#include <stdlib.h>
#include "Fields.h"
#include "derivative.h"



void laplacien(struct Field2D* vectors,struct FieldScalar* result){
    for (int j = vectors->ghost_zone; j < vectors->Ny-vectors->ghost_zone; ++j) {
        for (int i = vectors->ghost_zone; i < vectors->Nx-vectors->ghost_zone; ++i) {
            result->v[INDEX(i,j,vectors->Nx)]= SD_2O_CEN(vectors->cx,INDEX(i,j,vectors->Nx),1,vectors->dx)+
                    SD_2O_CEN(vectors->cy,INDEX(i,j,vectors->Nx),vectors->Nx,vectors->dy);
        }
    }
}

void gradient(double* value,struct Field2D* result){
    for (int j = result->ghost_zone; j < result->Ny-result->ghost_zone; ++j) {
        for (int i = result->ghost_zone; i < result->Nx-result->ghost_zone; ++i) {
            result->cx[INDEX(i,j,result->Nx)]= FD_2O_CEN(value,INDEX(i,j,result->Nx),1,result->dx);
            result->cy[INDEX(i,j,result->Nx)]= FD_2O_CEN(value,INDEX(i,j,result->Nx),result->Nx,result->dy);
        }
    }


    
}

void divergence(struct Field2D* vectors,struct FieldScalar* result){
    //interior
    for (int j = vectors->ghost_zone; j < vectors->Ny-vectors->ghost_zone; ++j){
        for (int i = vectors->ghost_zone; i < vectors->Nx-vectors->ghost_zone; ++i) {
            result->v[INDEX(i,j,vectors->Nx)]= FD_2O_CEN(vectors->cx,INDEX(i,j,vectors->Nx),1,vectors->dx)+FD_2O_CEN(vectors->cy,INDEX(i,j,vectors->Nx),result->Nx,vectors->dy);
        }
    }
}

void initField2D(struct Field2D *field2D, int Nx, int Ny, int ghost, double dx, double dy) {
    // Allouer de la mémoire sur le GPU pour cx et cy
    CUDA_CHECK(cudaMallocManaged(&(field2D->cx), Nx * Ny * sizeof(double)));

    CUDA_CHECK(cudaMallocManaged(&(field2D->cy), Nx * Ny * sizeof(double)));

    if (field2D->cx == NULL || field2D->cy == NULL) {
        printf("Error dynamic allocation 2D field\n");
        exit(-1);
    }
    // Initialiser la mémoire allouée à zéro
    CUDA_CHECK(cudaMemset(field2D->cx, 0, Nx * Ny * sizeof(double)));
    CUDA_CHECK(cudaMemset(field2D->cy, 0, Nx * Ny * sizeof(double)));

    // Initialiser les autres champs de la structure
    field2D->Nx = Nx;
    field2D->Ny = Ny;
    field2D->dx = dx;
    field2D->dy = dy;
    field2D->ghost_zone = ghost;
}


void initFieldScalar(struct FieldScalar* fieldScalar,int Nx,int Ny,int ghost, double dx,double dy){
    fieldScalar->Nx=Nx;
    fieldScalar->Ny=Ny;
    fieldScalar->dx=dx;
    fieldScalar->dy=dy;
    fieldScalar->ghost_zone=ghost;
    int size=Nx*Ny;
    CUDA_CHECK(cudaMallocManaged(&(fieldScalar->v), Nx * Ny * sizeof(double)));
    if(fieldScalar->v==NULL){
        printf("Error dynamic allocation scalar field\n");
        exit(-1);
    }
    CUDA_CHECK(cudaMemset(fieldScalar->v, 0, Nx * Ny * sizeof(double)));
}
void freeField3D(struct Field2D* field2D){
    cudaFree(field2D->cx);
    cudaFree(field2D->cy);
}
void freeFieldScalar(struct FieldScalar* fieldScalar){
    cudaFree(fieldScalar->v);
}