import numpy as np
import torch
import math


def w0():
    return 2

def E0():
    return w0()**2*(1.8**4/4-1.8**2/2)

def data(nSteps, tMin, tMax):
    tStep = (tMax - tMin) / nSteps  # Time step ....................
    tPoints = np.arange(tMin, tMax, tStep)

    # set initial conditions on the state variables ....................
    y = np.zeros((nSteps, 2))
    y[0, 0] = 1.8
    y[0, 1] = 0
    # solve for the time evolution
    for i in range(1, nSteps):
        t = tPoints[i]
        tdemi = t - tStep / 2.

        ydemi = y[i - 1] + tStep / 2. * np.array([w0() * y[i - 1, 1], -w0() * (y[i-1,0]**3-y[i-1,0])])
        y[i] = y[i - 1] + tStep * np.array([w0() * ydemi[1], -w0() * (ydemi[0]**3-ydemi[0])])

    return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y).view(-1, d_out())


def d_out():
    return 2


def losses():
    return [F,Energy]

def F(x, y):
    dx = torch.autograd.grad(y[:, 0].view(-1,1), x, torch.ones_like(y[:, 0].view(-1,1)), create_graph=True)[0]
    dy = torch.autograd.grad(y[:, 1].view(-1,1), x, torch.ones_like(y[:, 1].view(-1,1)), create_graph=True)[0]
    return (dx - w0() * y[:, 1]) ** 2 + (dy + w0() * (y[:, 0]**3-y[:, 0])) ** 2

def Energy(x,y):
    return 0.01*(w0()**2*(y[:,1].view(-1,1)**2/2+y[:,0].view(-1,1)**4/4-y[:,0].view(-1,1)**2/2)-E0())**2