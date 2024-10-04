import math

import torch
import numpy

class FFracDerivative():
    """
    This class is used to make fast approximation of the Caputo Derivative while training the PINN.
    In order to do that it precomputes the integration matrix.
    """
    def __init__(self,x,alpha,size):
        """
        Compute the integration matrix. This precomputation allow to gain a lot of time when computing the Caputo derivative.

        :param x: A tensor (size (size*k+1;1)) of the time discretion.
        :param alpha: The order of the derivative
        :param size: the number of points between two approximation of the Caputo derivative
            1 means the Caputo derivative will be approximated at each point, 2 at one points every two points...
        """
        m=math.ceil(alpha)
        # compute an integration matrix M such as the Caputo derivative estimation (with rectangle) is Mdx
        # M is diagonal inferior and M(i,j) is the integral of (t(i)-s)^(m-1-alpha) between t(j) and t(j+1). (t(k) is k-th value of X the time discretion)
        xt = torch.clone(x).view(1, -1)
        xr = x[1:].view(-1, size)[:, size - 1].view(-1, 1)
        matrix = torch.clamp_min(xr - xt, 0)
        matrix = torch.pow(matrix, m - alpha) / (m - alpha)#now M(i,j) is the integral of (t(i)-s)^(m-1-alpha) between 0 and t(j+1). (t(k) is k-th value of X the time discretion)
        matrix = 0.5 / math.gamma(m - alpha) *(matrix[:, :-1] - matrix[:, 1:])

        self.integrationMatrix=matrix.detach().numpy()
        self.alpha=alpha
        self.shape=torch.ones_like(x)

    def computeCaputoDerivative(self,x,y):
        """
        Compute the Caputo derivative.

        :param x: A tensor of the time discretion. It must be the same size and values that the one used to precompute
            the integration matrix.
        :param y: A tensor (size (n;1)) of the function estimation.
        :return: A tensor (size (n-1;1))
        """
        m = math.ceil(self.alpha)
        # compute the m-th derivative of y(x)
        dx = torch.autograd.grad(y, x, self.shape, create_graph=True)[0]
        for i in range(1, m):
            dx = torch.autograd.grad(dx, x, torch.ones_like(dx.view(-1,1)), create_graph=True)[0]

        intmat = torch.tensor(self.integrationMatrix)
        return torch.matmul(intmat, (dx[:-1] + dx[1:]))#use the method of the trapeze to approximate the integral.