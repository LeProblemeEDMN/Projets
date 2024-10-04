import numpy as np
import torch



def d_out():
    return 2


class Angelova:
    """
    A model for the evolution of Insulin and GLucose in the blood.

    """

    def __init__(self, tho0, q, Gin, tho, a0, a, b, d, e, h, NH):
        self.tho0 = tho0
        self.q = q
        self.Gin = Gin
        self.tho = tho
        self.a0 = a0
        self.a = a
        self.b = b
        self.d = d
        self.e = e
        self.h = h
        self.NH = NH

    def data(self, nSteps, tMin, tMax, toTensor=True):
        """
        Predict the evolution of the model with a forward Euler algorithm
        :param nSteps: number of steps in the discretion
        :param tMin: Beginning of the interval
        :param tMax: End of the interval
        :param toTensor: True if you want to obtain the result in pytorch tensors.
        :return: the time discretion and the solution at these points.
        """
        # Initialisation
        tStep = (tMax - tMin) / nSteps
        tPoints = np.arange(tMin, tMax, tStep)

        # set initial conditions on the state variables
        y = np.zeros((nSteps, 2))  # I,G
        y[0, 0] = 0
        y[0, 1] = 0
        # solve for the time evolution
        rtd = int(self.tho / tStep)
        for i in range(1, nSteps):
            old = y[i - 1]
            oldH = y[i - 1] ** 2
            oldH = oldH / (1 + oldH)
            dI = self.f1(oldH[1]) - old[0] / self.tho0
            delay = 0
            if (i >= rtd):
                delay = y[i - rtd, 0]
            delay = delay ** 2 / (1 + delay ** 2)
            dG = self.Gin - self.f2(oldH[1]) - self.q * old[1] * self.f4(oldH[0]) + self.f5(delay)
            y[i] = old + tStep * np.array([dI, dG])

        if toTensor:
            return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y).view(-1, self.d_out())
        return tPoints, y

    def losses(self):
        """
        :return: losses of the model
        """
        return [self.F_retard]

    def d_out(self):
        """
        :return: the dimension of the problem
        """
        return 2

    def getRetardStep(self, x):
        """
        This function compute an integer representing the delay.
        :param x: A tensor of the collocations points (size (n;1))
        :return: An integer representing the delay.
        """
        return int(self.tho / (x[1] - x[0]).detach())

    def F_retard(self, x, y, args):
        """
        Compute the loss of the governing equation of the Angelova model.

        :param X: A tensor of the collocations points (size (n;1))
        :param y: A tensor of the prediction of the network at the collocation points
        :param args: Not required
        :return: A tensor of the loss for each collocation point
        """
        y1 = y[:, 0].view(-1, 1)
        y2 = y[:, 1].view(-1, 1)

        yr = torch.zeros_like(y)
        rtd = self.getRetardStep(x)
        yr[rtd:] = y[:(-rtd)]

        dx = torch.autograd.grad(y1, x, torch.ones_like(y1), create_graph=True, allow_unused=True)[0][:, 0].view(-1, 1)
        dy = torch.autograd.grad(y2, x, torch.ones_like(y2), create_graph=True)[0][:, 0].view(-1, 1)

        ig = self.H(y1)
        hg = self.H(y2)
        return (dx - (self.f1(hg) - y1 / self.tho0)) ** 2 + (
                dy - (self.Gin - self.f2(hg) - self.q * y2 * self.f4(ig) + self.f5(self.H(yr[:, 0].view(-1, 1))))) ** 2

    def H(self, u):
        """
        Compute the Hill function
        :param u: A tensor of the collocations points (size (n;1))
        :return: Hill(u)
        """
        up = torch.pow(u, self.NH)
        return up / (up + 1)

    def f1(self, h):
        """
                Compute the f1 function of the model
                :param h: A tensor of the Hill function evaluated on the collocations points (size (n;1))
                :return: f1(h)
                """
        return self.a0 + self.a * h

    def f2(self, h):
        """
        Compute the f2 function of the model
        :param h: A tensor of the Hill function evaluated on the collocations points (size (n;1))
        :return: f2(h)
        """
        return self.b * h

    def f3(self, h):
        """
                Compute the f3 function of the model
                :param h: A tensor of the Hill function evaluated on the collocations points (size (n;1))
                :return: f3(h)
                """
        return -self.q * h

    def f4(self, h):
        """
                Compute the f4 function of the model
                :param h: A tensor of the Hill function evaluated on the collocations points (size (n;1))
                :return: f4(h)
                """
        return self.d + self.e * h

    def f5(self, h2):
        """
                Compute the f5 function of the model
                :param h2: A tensor of the Hill function evaluated on the collocations points (size (n;1))
                :return: f5(h2)
                """
        return self.h * (1 - h2)
