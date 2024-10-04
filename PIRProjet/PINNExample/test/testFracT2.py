import numpy as np
import torch
import math
import FractionnalDerivative


def d_out():
    return 1
def losses():
    return [F]

def data(alpha,nSteps, tMin, tMax,toTensor=True):
        tStep = (tMax - tMin) / nSteps  # Time step ....................
        tPoints = np.arange(tMin, tMax, tStep)

        # set initial conditions on the state variables ....................
        y = np.zeros(nSteps)#C,N
        y[0] = 0
        # solve for the time evolution
        for i in range(1, nSteps):
            t = tPoints[i]
            y[i]=t*t
        if toTensor:
            return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y).view(-1, d_out())
        return tPoints,y

def F(x, y,args):

    dx = args[0].computeCaputoDerivative(x,y)

    y1 = y[1:].view(-1, args[1])[:, args[1]-1].view(-1, 1)
    t = x[1:].view(-1, args[1])[:, args[1] - 1].view(-1, 1)
    return (dx-(-y1+t*t+2*torch.pow(t,2-args[3])/math.gamma(3-args[3])))**2
