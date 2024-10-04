import numpy as np
import torch
import math

def y0():
    return np.array([1,2])
def b():
    return np.array([0.5,1])
def A():
    return np.array([1.5,1])
def data(nSteps, tMin, tMax):
    tStep = (tMax - tMin) / nSteps  # Time step ....................
    tPoints = np.arange(tMin, tMax, tStep)

    # set initial conditions on the state variables ....................
    y = np.zeros(nSteps)
    y=(y0()[0]+b()[0]/A()[0])*np.exp(tPoints*A()[0])-b()[0]/A()[0]

    return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y).view(-1, d_out())

def d_out():
    return 1


def losses():
    return [F]


def F(x, y):
    dx = torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]

    return (dx - A()[0]*y-b()[0]) ** 2
