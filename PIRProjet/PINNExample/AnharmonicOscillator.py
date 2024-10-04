import numpy as np
import torch
def w0():
    #Pulsation of the pendulum
    return 15.5
def E0():
    #Initial energy
    return w0()**2*1.5**4/4
def data(nSteps, tMin, tMax):
    """
            Predict the evolution of the model with a forward RK2 algorithm
            :param nSteps: number of steps in the discretion
            :param tMin: Beginning of the interval
            :param tMax: End of the interval
            :return: the time discretion and the solution at these points.
            """
    tStep = (tMax - tMin) / nSteps  # Time step ....................
    tPoints = np.arange(tMin, tMax, tStep)

    # set initial conditions on the state variables ....................
    y = np.zeros((nSteps, 2))
    y[0, 0] = 1.5
    y[0, 1] = 0
    # solve for the time evolution
    # for t in tPoints[1:]:
    for i in range(1, nSteps):
        t = tPoints[i]
        tdemi = t - tStep / 2.

        ydemi = y[i - 1] + tStep / 2. * np.array([y[i - 1, 1], -w0()**2 * y[i-1,0]**3])
        y[i] = y[i - 1] + tStep * np.array([ydemi[1], -w0()**2 * ydemi[0]**3])

    return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y[:,0]).view(-1, d_out())

def d_out():
    """
    :return: losses of the model
    """
    return 1

def losses():
    """
    :return: losses of the model
    """
    return [F,Energy]

def F(x,y,args):
    """
    Compute the loss of the conservation of energy criterion.

    :param X: A tensor of the collocations points (size (n;1))
    :param y: A tensor of the prediction of the network at the collocation points
    :param args: Not required
    :return: A tensor of the loss for each collocation point
    """
    dx=torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]
    dx2 = torch.autograd.grad(dx, x, torch.ones_like(y), create_graph=True)[0]

    return (dx2+w0()**2*y**3)** 2

def Energy(x,y):
    """
        Compute the loss of the conservation of energy criterion.

        :param X: A tensor of the collocations points (size (n;1))
        :param y: A tensor of the prediction of the network at the collocation points
        :param args: Not required
        :return: A tensor of the loss for each collocation point
        """
    dx = torch.autograd.grad(y, x, torch.ones_like(y), create_graph=True)[0]
    return 0.1*(0.5 * dx ** 2 + w0() ** 2 * y ** 4/4 - E0())** 2