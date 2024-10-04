import numpy as np
import torch
import math
import FractionnalDerivative
def losses():
    return [F_retard]

def d_out():
    return 1

def b_value():
    return 1


def tho_value():
    return 0.1

def data(alpha, nSteps, tMin, tMax, toTensor=True):
    tStep = (tMax - tMin) / nSteps  # Time step ....................
    tPoints = np.arange(tMin, tMax, tStep)
    b=b_value()
    tho=tho_value()
    a=b/(1-tho)
    # set initial conditions on the state variables ....................
    y = a*tPoints  # C,N

    if toTensor:
        return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y).view(-1, d_out())
    return tPoints, y

def F_retard(x, y, args):
    yr = torch.ones_like(y)

    yr[:args[1]] = args[2]

    yr[args[1]:] = y[:(-args[1])]

    dx = torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True, allow_unused=True)[0][:, 0].view(-1, 1)

    return (dx - (y-yr.view(-1, 1)+b_value())) ** 2