import numpy as np
import torch
import math

def data(nSteps,tMin, tMax):
    tStep = (tMax - tMin) / nSteps  # Time step ....................
    tPoints = np.arange(tMin, tMax, tStep)

    # set initial conditions on the state variables ....................
    y0 = 1.0
    y = [y0]

    # solve for the time evolution
    for t in tPoints[1:]:
        # for i in range(1,nSteps):
        tdemi = t - tStep / 2.
        ydemi = y[-1] + tStep / 2. * (- 0.1 * y[-1] + np.sin(np.pi * tdemi / 2))
        y.append(y[-1] + tStep * (- 0.1 * ydemi + np.sin(np.pi * t / 2)))

    # y=-2*np.cos(tPoints*math.pi/2)/math.pi-0.05*tPoints**2

    return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y).view(-1, 1)

def d_out():
    return 1

def losses():
    return [F]

def F(x,y):
    dx=torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]
    period=0.5*math.pi
    return (dx-torch.sin(x*period)+0.1*y)** 2