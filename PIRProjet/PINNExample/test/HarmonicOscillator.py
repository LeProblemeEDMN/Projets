import numpy as np
import torch
import math
def w0():
    return 6.28
def E0():
    return 0.5*w0()**2
def data(nSteps,tMin, tMax):
    """
                    Predict the evolution of the model with a forward Euler algorithm
                    :param nSteps: number of steps in the discretion
                    :param tMin: Beginning of the interval
                    :param tMax: End of the interval
                    :return: the time discretion and the solution at these points.
                    """
    w0=2*math.pi
    tStep = (tMax - tMin) / nSteps  # Time step ....................
    tPoints = np.arange(tMin, tMax, tStep)

    y=np.cos(tPoints*w0)

    return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y).view(-1, 1)

def d_out():
    """
    :return: the dimension of the problem
    """
    return 1

def losses():
    return [F,Energy]

def F(x,y):
    dx=torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]
    dx2 = torch.autograd.grad(dx, x, torch.ones_like(y), create_graph=True)[0]

    return (dx2+w0()**2*y)** 2

def Energy(x,y):
    dx = torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]
    return (0.5 * (dx ** 2 + w0() ** 2 * y ** 2) - E0())** 2