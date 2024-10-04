import math

import torch
import numpy
def computeCaputoDerivative(x,y,alpha):
    m=math.ceil(alpha)
    h=x[1:]-x[:-1]
    dx = torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]
    for i in range(1, m):
        dx = torch.autograd.grad(dx.view(-1, 1), x, torch.ones_like(dx.view(-1, 1)), create_graph=True)[0]

    xt = torch.clone(x).view(1,-1)
    matrix = torch.clamp_min(x[1:]-xt[:,:-1],10**-9)
    mask=torch.where(matrix<9*10**-8)
    matrix = torch.pow(matrix,m-alpha-1)
    matrix[mask]=0

    #print(matrix)
    return 1/math.gamma(m-alpha)*torch.matmul(matrix,h*dx[:-1])
def computeCaputoDerivative2(x,y,alpha):
    m=math.ceil(alpha)

    dx = torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]
    for i in range(1, m):
        dx = torch.autograd.grad(dx.view(-1, 1), x, torch.ones_like(dx.view(-1, 1)), create_graph=True)[0]

    xt = torch.clone(x).view(1,-1)
    matrix = torch.clamp_min(x[1:]-xt,0)

    matrix = torch.pow(matrix,m-alpha)/(m-alpha)

    matrix=matrix[:,:-1]-matrix[:,1:]
    return 1/math.gamma(m-alpha)*torch.matmul(matrix,dx[:-1])


def computeCaputoDerivativeLastMultipleParam(x,y,alpha,tDerivative):
    m=math.ceil(alpha)

    dx = torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0][:,0]
    for i in range(1, m):
        dx = torch.autograd.grad(dx.view(-1, 1), x, torch.ones_like(dx.view(-1, 1)), create_graph=True)[0][:,0]

    matrix = torch.clamp_min(tDerivative-x[:,0],0)

    matrix = torch.pow(matrix,m-alpha)/(m-alpha)

    matrix=matrix[:-1]-matrix[1:]
    return 1/math.gamma(m-alpha)*torch.sum(matrix*dx[:-1])

def computeCaputoDerivativeFL(x,dx,alpha,tDerivative):
    m=math.ceil(alpha)

    matrix = torch.clamp_min(tDerivative-x[:,0],0)

    matrix = torch.pow(matrix,m-alpha)/(m-alpha)

    matrix=matrix[:-1]-matrix[1:]
    return 1/math.gamma(m-alpha)*torch.sum(matrix*dx[:-1])



def computeCaputoDerivative3(x,y,alpha):
    m=math.ceil(alpha)
    dx=torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]
    for i in range(1,m):
        dx = torch.autograd.grad(dx.view(-1,1), x, torch.ones_like(dx.view(-1,1)), create_graph=True)[0]
    xt = torch.clone(x).view(1,-1)
    matrix = torch.clamp_min(x[1:]-xt,0)
    matrix = torch.pow(matrix,m-alpha)/(m-alpha)
    matrix=matrix[:,:-1]-matrix[:,1:]
    return 0.5/math.gamma(m-alpha)*torch.matmul(matrix,(dx[:-1]+dx[1:]))

def computeCaputoDerivative3s(x,y,alpha,size):
    m=math.ceil(alpha)
    dx=torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]
    for i in range(1,m):
        dx = torch.autograd.grad(dx.view(-1,1), x, torch.ones_like(dx.view(-1,1)), create_graph=True)[0]
    xt = torch.clone(x).view(1,-1)
    xr=x[1:].view(-1,size)[:,size-1].view(-1,1)
    matrix = torch.clamp_min(xr-xt,0)
    matrix = torch.pow(matrix,m-alpha)/(m-alpha)
    matrix=matrix[:,:-1]-matrix[:,1:]
    return 0.5/math.gamma(m-alpha)*torch.matmul(matrix,(dx[:-1]+dx[1:]))