import numpy as np
import torch
import math

def a():
    return -3103.0

def data(nSteps,tMin, tMax):
    tStep = (tMax - tMin) / nSteps  # Time step ....................
    tPoints = np.arange(tMin, tMax, tStep)
    y=np.cos(tPoints)+np.sin(tPoints)

    return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y).view(-1, 1)


def Euler(nSteps,tMin, tMax):
    tStep = (tMax - tMin) / nSteps  # Time step ....................
    tPoints = np.arange(tMin, tMax, tStep)
    y=np.zeros(nSteps)
    y[0]=1
    for i in range(1,nSteps):
        t=tPoints[i]
        y[i]=y[i-1]+tStep*(a()*y[i-1]+(1-a())*math.cos(t)-(1+a())*math.sin(t))
    r_x,r_y=data(nSteps,tMin,tMax)
    print("Erreur Euler:",np.mean(np.abs(y-r_y.detach().numpy())/r_y.detach().numpy()))
    return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y).view(-1, 1)

def d_out():
    return 1

def losses():
    return [F]

def F(x,y,args):
    dx = torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]
    y=y-y[0]+1
    return (dx -(a()*y+(1-a())*torch.cos(x)-(1+a())*torch.sin(x)))** 2