import numpy as np
import torch
import math
def w0():
    return 6.28
def E0():
    return 0.5*w0()**2
def data(nSteps,tMin, tMax):
    w0=2*math.pi
    tStep = (tMax - tMin) / nSteps  # Time step ....................
    tPoints = np.arange(tMin, tMax, tStep)
    y=np.zeros((nSteps,2))
    y[:,0]=np.cos(tPoints*w0)
    y[:,1] = np.cos(tPoints * w0)

    return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y).view(-1, d_out())

def d_out():
    """
    :return: the dimension of the problem
    """
    return 2

def losses():
    return [F_0]
def F_0(x,y):
    dx=torch.autograd.grad(y[:,0].view(-1,1), x, torch.ones_like(y[:,0].view(-1,1)), create_graph=True)[0]
    dx2 = torch.autograd.grad(dx, x, torch.ones_like(dx), create_graph=True)[0]
    dy = torch.autograd.grad(y[:,1].view(-1,1), x, torch.ones_like(y[:,1].view(-1,1)), create_graph=True)[0]
    dy2 = torch.autograd.grad(dy, x, torch.ones_like(dy), create_graph=True)[0]
    return (dx2+w0()**2*y[:,0].view(-1,1))** 2+(dy2+w0()**2*y[:,1].view(-1,1))** 2

def F(x,y):
    dx=torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]
    dx2 = torch.autograd.grad(dx, x, torch.ones_like(dx), create_graph=True)[0]

    return (dx2+w0()**2*y)** 2
def F_1(x,y):
    dx=torch.autograd.grad(y[:,0].view(-1,1), x, torch.ones_like(y[:,0].view(-1,1)), create_graph=True)[0]
    dx2 = torch.autograd.grad(dx, x, torch.ones_like(dx), create_graph=True)[0]

    return (dx2+w0()**2*y[:,0].view(-1,1))** 2
def F_2(x,y):
    dx=torch.autograd.grad(y[:,1].view(-1,1), x, torch.ones_like(y[:,1].view(-1,1)), create_graph=True)[0]
    dx2 = torch.autograd.grad(dx, x, torch.ones_like(dx), create_graph=True)[0]
    return (dx2+w0()**2*y[:,1].view(-1,1))** 2

def Energyd2(x,y):
    dx = torch.autograd.grad(y[:,0].view(-1,1), x, torch.ones_like(y[:,0].view(-1,1)), create_graph=True)[0]
    dy = torch.autograd.grad(y[:,1].view(-1,1), x, torch.ones_like(y[:,1].view(-1,1)), create_graph=True)[0]
    return (0.5 * (dx ** 2 + w0() ** 2 * y[:,0].view(-1,1) ** 2) - E0())** 2 +(0.5 * (dy ** 2 + w0() ** 2 * y[:,1].view(-1,1) ** 2) - E0())** 2

def Energyd0(x,y):
    dx = torch.autograd.grad(y[:,0], x, torch.ones_like(y[:,0]), create_graph=True)[0]
    return (0.5 * (dx ** 2 + w0() ** 2 * y[:,0] ** 2) - E0())** 2

def Energyd1(x,y):
    dx = torch.autograd.grad(y[:,1], x, torch.ones_like(y[:,1]), create_graph=True)[0]
    return (0.5 * (dx ** 2 + w0() ** 2 * y[:,1] ** 2) - E0())** 2

def Energy(x,y):
    dx = torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]
    return (0.5 * (dx ** 2 + w0() ** 2 * y ** 2) - E0())** 2
"""
def F(x,y):
    dx=torch.autograd.grad(y[:,0], x, torch.ones_like(y[:,0]), create_graph=True)[0]
    dx2 = torch.autograd.grad(dx, x, torch.ones_like(dx), create_graph=True)[0]
    dy = torch.autograd.grad(y[:,1], x, torch.ones_like(y[:,1]), create_graph=True)[0]
    dy2 = torch.autograd.grad(dy, x, torch.ones_like(dy), create_graph=True)[0]
    return (dx2+w0()**2*y[:,0])** 2+(dy2+w0()**2*y[:,1])** 2

def Energy(x,y):
    dx = torch.autograd.grad(y[:,0], x, torch.ones_like(y[:,0]), create_graph=True)[0]
    dy = torch.autograd.grad(y[:,1], x, torch.ones_like(y[:,1]), create_graph=True)[0]
    return (0.5 * (dx ** 2 + w0() ** 2 * y[:,0] ** 2) - E0())** 2 +(0.5 * (dy ** 2 + w0() ** 2 * y[:,1] ** 2) - E0())** 2
"""