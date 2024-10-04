cmake_minimum_required(VERSION 3.26)
project(HPC_Project C)

set(CMAKE_C_STANDARD 11)

add_executable(HPC_Project main.c
        OptimProblem.c
        OptimProblem.h
        Simplex.c
        Simplex.h
)
