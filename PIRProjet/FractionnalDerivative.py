import math

import torch

def computeCaputoDerivative(x,y,alpha):
    """
    This function estimate the Caputo derivative at each point.

    :param x:  A tensor (size (n;1)) of the time discretion.
    :param y: A tensor (size (n;1)) of the function estimation.
    :param alpha: The order of the derivative
    :return: A tensor (size (n-1;1))
    """
    m=math.ceil(alpha)

    # compute the m-th derivative of y(x)
    dx=torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]
    for i in range(1,m):
        dx = torch.autograd.grad(dx.view(-1,1), x, torch.ones_like(dx.view(-1,1)), create_graph=True)[0]
    # compute an integration matrix M such as the Caputo derivative estimation (with rectangle) is Mdx
    # M is diagonal inferior and M(i,j) is the integral of (t(i)-s)^(m-1-alpha) between t(j) and t(j+1). (t(k) is k-th value of X the time discretion)
    xt = torch.clone(x).view(1,-1)
    M = torch.clamp_min(x[1:]-xt,0)
    M = torch.pow(M,m-alpha)/(m-alpha)#now M(i,j) is the integral of (t(i)-s)^(m-1-alpha) between 0 and t(j+1). (t(k) is k-th value of X the time discretion)
    M =M[:,:-1]-M[:,1:]
    return 0.5/math.gamma(m-alpha)*torch.matmul(M,(dx[:-1]+dx[1:]))#use the method of the trapeze to approximate the integral.
