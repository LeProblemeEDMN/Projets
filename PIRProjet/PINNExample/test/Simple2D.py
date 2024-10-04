import numpy as np
import torch
import math

def y0():
    return np.array([1,2])
def b():
    return np.array([3,1])
def A():
    return np.array([1.5,1])
def data(nSteps, tMin, tMax):
    tStep = (tMax - tMin) / nSteps  # Time step ....................
    tPoints = np.arange(tMin, tMax, tStep)

    # set initial conditions on the state variables ....................
    y = np.zeros((nSteps, 2))
    y[:,0]=(y0()[0]+b()[0]/A()[0])*np.exp(tPoints*A()[0])-b()[0]/A()[0]
    y[:, 1] = (y0()[1]+b()[1]/A()[1]) * np.exp(tPoints * A()[1])-b()[1]/A()[1]

    return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y).view(-1, d_out())

def d_out():
    return 2


def losses():
    return [F]


def F(x, y):
    dx = torch.autograd.grad(y[:, 0], x, torch.ones_like(y[:, 0]), create_graph=True)[0]
    dy = torch.autograd.grad(y[:, 1], x, torch.ones_like(y[:, 1]), create_graph=True)[0]

    return (dx - A()[0]*y[:, 0]-b()[0]) ** 2 + (dy - A()[1]*y[:, 1]-b()[1]) ** 2
