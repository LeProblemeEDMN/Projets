import numpy as np
import torch
import math

def data(nSteps,tMin, tMax):
    tStep = (tMax - tMin) / nSteps  # Time step ....................
    tPoints = np.arange(tMin, tMax, tStep)
    y=np.sin(tPoints)

    return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y).view(-1, 1)

def d_out():
    return 1

def losses():
    return [F]

def F(x,y,args):
    dx = torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]
    return (dx - torch.cos(x))** 2