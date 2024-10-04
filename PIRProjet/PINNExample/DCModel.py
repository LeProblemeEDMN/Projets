import numpy as np
import torch


def d_out():
    return 2

class DC:
    """
    This class represent the DC model that is modeling the evolution of the number of patient with diabetes complications.
    """
    def __init__(self,DC0,I,mu,lamb,gamma,delta,v):
        self.init=DC0
        self.I=I
        self.mu=mu
        self.lamb=lamb
        self.gamma=gamma
        self.delta=delta
        self.v=v
        self.theta=gamma+mu+v+delta
    def data(self,nSteps, tMin, tMax,toTensor=True):
        """
                Predict the evolution of the model with a RK2 algorithm
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
        y = np.zeros((nSteps, 2))#C,N
        y[0, 0] = self.init[0]
        y[0,1] = self.init[1]

        # solve for the time evolution
        for i in range(1, nSteps):
            t = tPoints[i]

            ydemi = y[i - 1] + tStep / 2. * np.array([self.I-(self.lamb+self.theta)*y[i - 1,0]+self.lamb*y[i-1,1],
                                                      2*self.I-(self.v+self.delta)*y[i - 1,0]-self.mu*y[i-1,1]])
            y[i] = y[i - 1] + tStep * np.array([self.I-(self.lamb+self.theta)*ydemi[0]+self.lamb*ydemi[1],
                                                      2*self.I-(self.v+self.delta)*ydemi[0]-self.mu*ydemi[1]])
        if toTensor:
            return torch.Tensor(tPoints).view(-1, 1).requires_grad_(True), torch.Tensor(y).view(-1, self.d_out())
        return tPoints,y

    def losses(self):
        """
        :return: losses of the model
        """
        return [self.F_caputo]

    def d_out(self):
        """
        :return: the dimension of the problem
        """
        return 2

    def F_caputo(self,x, y,args):
        """
        Compute the loss of the governing equation of the DC model with the Caputo derivative.

        :param X: A tensor of the collocations points (size (n;1))
        :param y: A tensor of the prediction of the network at the collocation points
        :param args: A list where the first element is a FFracDerivative object and the second the number of computation points per collocation points
        :return: A tensor of the loss for each collocation point
        """
        y1=y[:, 0].view(-1,1)
        y2=y[:, 1].view(-1,1)

        dx = args[0].computeCaputoDerivative(x,y1)
        dy = args[0].computeCaputoDerivative(x, y2)
        y1 = y1[1:].view(-1, args[1])[:, args[1]-1].view(-1, 1)
        y2 = y2[1:].view(-1, args[1])[:, args[1]-1].view(-1, 1)

        return (dx - (self.I-(self.lamb+self.theta)*y1+self.lamb*y2)) ** 2 + (dy - (2*self.I-(self.v+self.delta)*y1-self.mu*y2)) ** 2

    def F_retard(self, x, y, args):
        """
        Compute the loss of the governing equation of the DC model (version delay differential equation i.e. we add the term a*y(t-tho)).

        :param X: A tensor of the collocations points (size (n;1))
        :param y: A tensor of the prediction of the network at the collocation points
        :param args: A list where the first element is the coefficient of the delay, the second is the step of the delay and the third the initialisation value for t-tho<0.
        :return: A tensor of the loss for each collocation point
        """
        y1 = y[:, 0].view(-1, 1)
        y2 = y[:, 1].view(-1, 1)
        yr=torch.ones_like(y)

        yr[:args[1]]=args[2]
        yr[args[1]:]=y[:(-args[1])]

        dx = torch.autograd.grad(y1, x, torch.ones_like(y1), create_graph=True, allow_unused=True)[0][:, 0].view(-1, 1)
        dy = torch.autograd.grad(y2, x, torch.ones_like(y2), create_graph=True)[0][:, 0].view(-1, 1)

        return (dx - (self.I - (self.lamb + self.theta) * y1 + self.lamb * y2+args[0][0]*yr[:,0].view(-1, 1))) ** 2 + (
                    dy - (2 * self.I - (self.v + self.delta) * y1 - self.mu * y2+args[0][1]*yr[:,1].view(-1, 1))) ** 2


    def F_simple(self,X, y, args):
        """
        Compute the loss of the governing equation of the DC model.

        :param X: A tensor of the collocations points (size (n;1))
        :param y: A tensor of the prediction of the network at the collocation points
        :param args: Not required
        :return: A tensor of the loss for each collocation point
        """
        y1=y[:, 0].view(-1,1)
        y2=y[:, 1].view(-1,1)
        dx = torch.autograd.grad(y1, X, torch.ones_like(y1), create_graph=True,allow_unused=True)[0].view(-1,1)
        dy = torch.autograd.grad(y2, X, torch.ones_like(y2), create_graph=True)[0].view(-1,1)

        return (dx - (self.I-(self.lamb+self.theta)*y1+self.lamb*y2)) ** 2 + (dy - (2*self.I-(self.v+self.delta)*y1-self.mu*y2)) ** 2

    def F_full(self,X, y, args):
        """
        Compute the loss of the governing equation of the DC model with the parameters C(0) lambda and I not specified.

                :param X: A tensor of the collocations points (size (n;4))
                :param y: A tensor of the prediction of the network at the collocation points
                :param args: Not required
                :return: A tensor of the loss for each collocation point
                """
        I = X[:, 2].view(-1, 1)
        lamb = X[:, 3].view(-1, 1)
        y1=y[:, 0].view(-1,1)
        y2=y[:, 1].view(-1,1)
        dx = torch.autograd.grad(y1, X, torch.ones_like(y1), create_graph=True,allow_unused=True)[0][:,0].view(-1,1)
        dy = torch.autograd.grad(y2, X, torch.ones_like(y2), create_graph=True)[0][:,0].view(-1,1)
        return (dx - (I-(lamb+self.theta)*y1+lamb*y2)) ** 2 + (dy - (2*I-(self.v+self.delta)*y1-self.mu*y2)) ** 2