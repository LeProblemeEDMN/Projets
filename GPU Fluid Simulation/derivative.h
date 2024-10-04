//
// Created by bapti on 19/04/2024.
//

#ifndef FLUID_DERIVATIVE_H
#define FLUID_DERIVATIVE_H


//second order accurate first derivative
inline double FD_2O_CEN(const double *f, int i, int di, double h)
{
    const double oo2h = 1.0 / (2 * h);
    return (-f[-di + i] + f[di + i]) * oo2h;
}
__device__
inline double FD_2O_CEN_GPU(const double *f, int i, int di, double h)
{
    const double oo2h = 1.0 / (2 * h);
    return (-f[-di + i] + f[di + i]) * oo2h;
}

inline double FD_1O_EXCEN(const double *f, int i, int di, double h)
{
    const double oo2h = 1.0 / (h);
    return (-f[i] + f[di + i]) * oo2h;
}

inline double SD_2O_CEN(const double *f, int i, int di, double dx)
{
    const double oodx2 = 1.0 / dx * dx;
    return (-2 * f[i] + f[-di + i] + f[di + i]) * oodx2;
}
__device__
inline double SD_2O_CEN_GPU(const double *f, int i, int di, double dx)
{
    const double oodx2 = 1.0 / dx * dx;
    return (-2 * f[i] + f[-di + i] + f[di + i]) * oodx2;
}

#endif //FLUID_DERIVATIVE_H
