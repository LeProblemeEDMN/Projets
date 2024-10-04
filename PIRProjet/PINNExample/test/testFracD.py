import numpy as np
import torch
import math
import FractionnalDerivative


def d_out():
    return 1
def losses():
    return [F]
def F(x, y,args):

    dx = args[0].computeCaputoDerivative(x,y)

    y1 = y[1:].view(-1, args[1])[:, args[1]-1].view(-1, 1)

    return (dx+y1)**2
