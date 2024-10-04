import numpy as np
import torch
import math
def w0():
    return 3.14
def E0():
    return w0()**2*1.5**4/4
def eps():
    return 2

def data(nSteps, tMin, tMax):
    tStep = (tMax - tMin) / nSteps  # Time step ....................
    tPoints = np.arange(tMin, tMax, tStep)

    # set initial conditions on the state variables ....................
    y = np.zeros((nSteps, 2))
    y[0, 0] = 2
    y[0, 1] = 0
    # solve for the time evolution
    for i in range(1, nSteps):
        t = tPoints[i]
        tdemi = t - tStep / 2.

        ydemi = y[i - 1] + tStep / 2. * np.array([y[i - 1, 1], -w0()**2 * y[i-1,0]+eps()*w0()*(1-y[i-1,0]**2)*y[i-1,1]])

        y[i] = y[i - 1] + tStep * np.array([ydemi[1], -w0()**2 * ydemi[0]+eps()*w0()*(1-ydemi[0]**2)*ydemi[1]])

    return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y[:,0]).view(-1, d_out())

def d_out():
    return 1

def losses():
    return [F]

def F(x,y,args):
    dx=torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]
    dx2 = torch.autograd.grad(dx, x, torch.ones_like(dx), create_graph=True)[0]
    y=y-y[0]+args[2][0]
    return (dx2+w0()**2*y-eps()*w0()*(1-y**2)*dx)** 2+ dx[0]**2*5#data loss