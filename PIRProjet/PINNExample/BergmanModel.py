import numpy as np
import torch


def d_out():
    return 3


class BergModel:
    """
    This class represent the original minimal model of Bergman that is modeling the evolution of the glucose and insulin
    for a healthy subject.
    """

    def __init__(self, P1, P2, P3, Gb, Ib, gamma, Gth, kf, Ra):
        self.P1 = P1
        self.P2 = P2
        self.P3 = P3
        self.Gb = Gb
        self.Ib = Ib
        self.gamma = gamma
        self.Gth = Gth
        self.kf = kf
        self.Ra = Ra

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

        # set initial conditions on the state variables ....................
        y = np.zeros((nSteps, 3))  # C,N
        # y[0]=np.array([0,3.2,4.3])
        # solve for the time evolution
        for i in range(1, nSteps):
            t = tPoints[i]
            old = y[i - 1]
            dG = -(self.P1 + old[1]) * old[0] + self.P1 * self.Gb + self.Ra
            dX = -self.P2 * old[1] + self.P3 * (old[2] - self.Ib)
            dI = self.gamma * max(0, old[0] - self.Gth) * t - self.kf * (old[2] - self.Ib)
            y[i] = old + tStep * np.array([dG, dX, dI])
        if toTensor:
            return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y).view(-1, self.d_out())
        return tPoints, y

    def d_out(self):
        """
        :return: the dimension of the problem
        """
        return 3

    def F(self, x, y, args):
        """
                Compute the loss of the governing equation of the minimal model of Bergmann.

                :param X: A tensor of the collocations points (size (n;1))
                :param y: A tensor of the prediction of the network at the collocation points
                :param args: A list with  the initial condition at the third place of the list.
                :return: A tensor of the loss for each collocation point
                """
        G = y[:, 0].view(-1, 1)
        X = y[:, 1].view(-1, 1)
        I = y[:, 2].view(-1, 1)

        dG = torch.autograd.grad(G, x, torch.ones_like(G), create_graph=True, allow_unused=True)[0][:, 0].view(-1, 1)
        dX = torch.autograd.grad(X, x, torch.ones_like(X), create_graph=True)[0][:, 0].view(-1, 1)
        dI = torch.autograd.grad(I, x, torch.ones_like(I), create_graph=True)[0][:, 0].view(-1, 1)

        # Set the origin of the curve exactly at the initial condition to allow a better convergence
        G = G - G[0, 0] + args[2][0]
        X = X - X[0, 0] + args[2][1]
        I = I - I[0, 0] + args[2][2]

        finalTensor = ((dG - (-(self.P1 + X) * G + self.P1 * self.Gb + self.Ra)) ** 2 + (
                dX - (-self.P2 * X + self.P3 * (I - self.Ib))) ** 2 +
                       (dI - (self.gamma * torch.maximum(torch.zeros_like(G), G - self.Gth) * x - self.kf * (
                                   I - self.Ib))) ** 2)

        return finalTensor
