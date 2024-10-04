import numpy as np
import torch


def d_out():
    return 4


class ControlModel:
    """
        This class represent the model of Bergman that is modeling the evolution of the glucose and insulin
        for a subject with type 1 diabetes.
    """

    def __init__(self, P1, P2, P3, Gb, Ib, Bf, kf, ks, u):
        self.P1 = P1
        self.P2 = P2
        self.P3 = P3
        self.Gb = Gb
        self.Ib = Ib
        self.Bf = Bf
        self.kf = kf
        self.ks = ks
        self.u = u

    def data(self, nSteps, tMin, tMax, Ra_function, toTensor=True):
        """
                Predict the evolution of the model with a forward Euler algorithm
                :param nSteps: number of steps in the discretion
                :param tMin: Beginning of the interval
                :param tMax: End of the interval
                :param Ra_function: A function that takes an array of time as input and return an array of the glucose flow at each moment.
                :param toTensor: True if you want to obtain the result in pytorch tensors.
                :return: the time discretion and the solution at these points.
                """
        # Initialisation
        tStep = (tMax - tMin) / nSteps
        tPoints = np.arange(tMin, tMax, tStep)

        # set initial conditions on the state variables ....................
        y = np.zeros((nSteps, 4))  # C,N
        y[0] = np.array([1, 3, 5, 0.5])
        Ra = Ra_function(tPoints)
        # solve for the time evolution
        for i in range(1, nSteps):
            t = tPoints[i]
            old = y[i - 1]

            dG = -(self.P1 + old[1]) * old[0] + self.P1 * self.Gb + Ra[i]
            dX = -self.P2 * old[1] + self.P3 * (old[2] - self.Ib)
            dI = self.Bf * old[3] - self.kf * (old[2] - self.Ib)
            dU = -self.ks * old[3] + self.u
            y[i] = old + tStep * np.array([dG, dX, dI, dU])
        if toTensor:
            return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y).view(-1, self.d_out())
        return tPoints, y

    def d_out(self):
        """
        :return: the dimension of the problem
        """
        return 4

    def F(self, x, y, args):
        """
        Compute the loss of the governing equation of the model of Bergmann.

        :param X: A tensor of the collocations points (size (n;1))
        :param y: A tensor of the prediction of the network at the collocation points
        :param args: A list with  the initial condition at the third place of the list and at the fourth place the function Ra(Tensor X) that compute the glucose flow.
        :return: A tensor of the loss for each collocation point
        """

        G = y[:, 0].view(-1, 1)
        X = y[:, 1].view(-1, 1)
        I = y[:, 2].view(-1, 1)
        U = y[:, 3].view(-1, 1)

        dG = torch.autograd.grad(G, x, torch.ones_like(G), create_graph=True, allow_unused=True)[0][:, 0].view(-1, 1)
        dX = torch.autograd.grad(X, x, torch.ones_like(X), create_graph=True)[0][:, 0].view(-1, 1)
        dI = torch.autograd.grad(I, x, torch.ones_like(I), create_graph=True)[0][:, 0].view(-1, 1)
        dU = torch.autograd.grad(U, x, torch.ones_like(U), create_graph=True)[0][:, 0].view(-1, 1)

        # Set the origin of the curve exactly at the initial condition to allow a better convergence
        G = G - G[0, 0] + args[2][0]
        X = X - X[0, 0] + args[2][1]
        I = I - I[0, 0] + args[2][2]
        U = U - U[0, 0] + args[2][3]
        Ra = args[3](x)

        finalTensor = ((dG - (-(self.P1 + X) * G + self.P1 * self.Gb + Ra)) ** 2 + (
                dX - (-self.P2 * X + self.P3 * (I - self.Ib))) ** 2 +
                       (dI - (self.Bf * U - self.kf * (I - self.Ib))) ** 2 +
                       (dU - (-self.ks * U + self.u)) ** 2)

        return finalTensor

    def F_caputo(self, x, y, args):
        """
        Compute the loss of the governing equation of the model of Bergmann with the Caputo derivative.

        :param X: A tensor of the collocations points (size (n;1))
        :param y: A tensor of the prediction of the network at the collocation points
        :param args: A list where the first element is a FFracDerivative object and the second the number
            of computation points per collocation points and the initial condition at the third place of the list and at
            the fourth place the function Ra(Tensor X) that compute the glucose flow.
        :return: A tensor of the loss for each collocation point
        """
        G = y[:, 0].view(-1, 1)
        X = y[:, 1].view(-1, 1)
        I = y[:, 2].view(-1, 1)
        U = y[:, 3].view(-1, 1)

        dG = args[0].computeCaputoDerivative(x, G)
        dX = args[0].computeCaputoDerivative(x, X)
        dI = args[0].computeCaputoDerivative(x, I)
        dU = args[0].computeCaputoDerivative(x, U)

        # Set the origin of the curve exactly at the initial condition to allow a better convergence
        U = U[1:].view(-1, args[1])[:, args[1] - 1].view(-1, 1) - G[0, 0] + args[2][0]
        I = I[1:].view(-1, args[1])[:, args[1] - 1].view(-1, 1) - X[0, 0] + args[2][1]
        X = X[1:].view(-1, args[1])[:, args[1] - 1].view(-1, 1) - I[0, 0] + args[2][2]
        G = G[1:].view(-1, args[1])[:, args[1] - 1].view(-1, 1) - U[0, 0] + args[2][3]

        x = x[1:].view(-1, args[1])[:, args[1] - 1].view(-1, 1)
        Ra = args[3](x)

        finalTensor = ((dG - (-(self.P1 + X) * G + self.P1 * self.Gb + Ra)) ** 2 + (
                dX - (-self.P2 * X + self.P3 * (I - self.Ib))) ** 2 +
                       (dI - (self.Bf * U - self.kf * (I - self.Ib))) ** 2 +
                       (dU - (-self.ks * U + self.u)) ** 2)

        return finalTensor

    def F_delay(self, x, y, args):
        """
        Compute the loss of the governing equation of the model of Bergmann with the delay.

        :param X: A tensor of the collocations points (size (n;1))
        :param y: A tensor of the prediction of the network at the collocation points
        :param args: A list with  the initial condition at the third place of the list and at the fourth place the function Ra(Tensor X) that compute the glucose flow.
        :return: A tensor of the loss for each collocation point
        """

        G = y[:, 0].view(-1, 1)
        X = y[:, 1].view(-1, 1)
        I = y[:, 2].view(-1, 1)
        U = y[:, 3].view(-1, 1)

        dG = torch.autograd.grad(G, x, torch.ones_like(G), create_graph=True, allow_unused=True)[0][:, 0].view(-1, 1)
        dX = torch.autograd.grad(X, x, torch.ones_like(X), create_graph=True)[0][:, 0].view(-1, 1)
        dI = torch.autograd.grad(I, x, torch.ones_like(I), create_graph=True)[0][:, 0].view(-1, 1)
        dU = torch.autograd.grad(U, x, torch.ones_like(U), create_graph=True)[0][:, 0].view(-1, 1)

        # Set the origin of the curve exactly at the initial condition to allow a better convergence
        G = G - G[0, 0] + args[2][0]
        X = X - X[0, 0] + args[2][1]
        I = I - I[0, 0] + args[2][2]
        U = U - U[0, 0] + args[2][3]
        Ra = args[3](x)

        yr = torch.ones_like(y)

        yr[:args[1]] =args[4] #torch.matmul(torch.exp(args[5]*torch.arange(-args[1],0,1)).view(-1,1),args[4].view(1,4))
        yr[args[1]:] = y[:(-args[1])]-y[0]+args[4]


        finalTensor = ((dG - (-(self.P1 + X) * G + self.P1 * self.Gb + Ra)+args[0]*yr[:,0].view(-1, 1)) ** 2 + (
                dX - (-self.P2 * X + self.P3 * (I - self.Ib))+args[0]*yr[:,1].view(-1, 1)) ** 2 +
                       (dI - (self.Bf * U - self.kf * (I - self.Ib))+args[0]*yr[:,2].view(-1, 1)) ** 2 +
                       (dU - (-self.ks * U + self.u)+args[0]*yr[:,3].view(-1, 1)) ** 2)

        return finalTensor