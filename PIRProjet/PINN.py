import torch
import torch.nn as nn
import numpy as np
import matplotlib.pyplot as plt
import time
import math
class PINN(nn.Module):
    """

    This class represent a Physic-Informed Neural Network which will used to predict the solution of an ODE.
    """

    def __init__(self, N_INPUT, N_OUTPUT, N_HIDDEN, N_LAYERS):
        """
        The network is composed of N_LAYERS hidden layers of N_HIDDEN neurons each with tanh activation function.
        The final layer is composed of N_INPUT neurons and didn't have any activation function.

        :param N_INPUT: Dimension of the input vector
        :param N_OUTPUT: Dimension of output vector
        :param N_HIDDEN: number of neuron per hidden layer (same for all layers)
        :param N_LAYERS: Number of hidden layers of the Network
        """
        super().__init__()
        activation = nn.Tanh
        self.fcs = nn.Sequential(*[
            nn.Linear(N_INPUT, N_HIDDEN),
            activation()])
        self.fch = nn.Sequential(*[
            nn.Sequential(*[
                nn.Linear(N_HIDDEN, N_HIDDEN),
                activation()]) for _ in range(N_LAYERS - 1)])
        self.fce = nn.Linear(N_HIDDEN, N_OUTPUT)
        self.N_OUTPUT=N_OUTPUT

    def train_PINN(self, data_X, data_Y, x_physics, physicsObjectives=None, coeffObjectives=1, coeffDatas=1, epochs=12000,
                   plotLoss=False, saveFile=None, physicsLossArgs=None):
        """
        This method train the PINN. It tries to minimize the sum of the MSE of the data and the loss of the physics objectives.

        :param data_X: A tensor (size (m;N_INPUT)) representing the different points where the MSE will be applied
        :param data_Y: A tensor (size (m;N_OUTPUT)) representing the exact values of the solution.
        :param x_physics: A tensor (size (n;N_INPUT)) representing the collocation points
        :param physicsObjectives: List of function (of the form F(x, PINN(x), physicsLossArgs))
            that will be evaluated at the collocations points to form the physics loss.
            These function can represent various functions such as Conservation of energy criterion,
             respect of the governing equation by the solution...
        :param coeffObjectives: A positive number used to weight the physics loss in the total loss
        :param coeffDatas: A positive number used to weight the data loss in the total loss
        :param epochs: The number of iteration of the algorithm.
        :param plotLoss: boolean saying if we want at the end of the training a graph representing the evolution of the loss.
        :param saveFile: A file path saying if where the network parameters need to be saved. None means that the parameters will not be saved.
        :param physicsLossArgs: Additional parameters for the physics loss (identical for all the losses)

        """
        #Initialization of the variables
        optimizer = torch.optim.Adam(self.parameters(), lr=5e-4)

        if plotLoss:
            lossHistory=np.zeros(epochs)

        debut=time.time()
        #main loop
        for i in range(epochs):
            #reset the optimizer
            optimizer.zero_grad()

            #Data loss
            yh = self(data_X)
            lossData = coeffDatas * torch.mean((yh - data_Y) ** 2)  # use mean squared error
            loss = lossData

            #Physics loss
            if physicsObjectives is not None:
                yh = self(x_physics)
                #loop over the different losses
                for L in physicsObjectives:
                    physics = L(x_physics, yh,physicsLossArgs)
                    lossPhysics = coeffObjectives * torch.mean(physics)
                    loss = lossPhysics + loss
            #update the parameters of the network
            loss.backward()
            optimizer.step()

            if plotLoss:
                lossHistory[i]=loss.item()

            #Every 1000 iterations shows message to inform about the progression and save the parameters
            if (i % 1000 == 0):
                if(saveFile is not None):
                    torch.save(self.state_dict(), saveFile)
                print("Epoch ",i,"/",epochs,": Total Loss:",loss.item(),", Data Loss:",lossData.item()," Time ellapsed:",str(math.ceil(100*(time.time()-debut))/100))
        #plot the loss evolution
        if plotLoss:
            plt.plot(np.log(lossHistory)/np.log(10))
            plt.title("Evolution of loss during the training",size=17)
            plt.xlabel("Epochs",size=14)
            plt.ylabel("Loss",size=14)
            plt.show()


    def plotPINN(self,X,Y,dimX=-1,names=None):
        """
        Plot the values of the solution and the prediction of the PINN on the same plot.

        :param X: A tensor (size (m;N_INPUT)) of the input data
        :param Y: A tensor (size (m;N_OUTPUT)) of the solutions
        :param dimX: Index of the input of the abscissa of the plot. -1 If N_OUTPUT=1.
        :param names: list of names of the different components of the vector

        """
        X_v=X.detach().numpy()
        if(dimX>=0):
            X_v=X_v[:,dimX]
        Y_v=Y.detach().numpy()
        S_v=self(X).detach().numpy()

        for d in range(self.N_OUTPUT):
            name=str(d)
            if names is not None:
                name=names[d]
            plt.plot(X_v,S_v[:,d],label="Prediction "+name)
            plt.plot(X_v,Y_v[:,d],linestyle="dashed",label="Solution "+name)


        plt.legend()
        #plt.title("Evolution of population size function of time",size=17)
        #plt.ylabel("Population size",size=14)
        plt.xlabel("Time",size=14)
        plt.show()

    def forward(self, x):
        """
        Predict the value with the PINN.

        :param x: A tensor (size (m;N_INPUT)) of the input data
        :return: A tensor (size (m;N_OUTPUT)) of the prediction
        """
        x = self.fcs(x)
        x = self.fch(x)
        x = self.fce(x)
        return x

    def evaluatePrecision(self,X,Y):
        """
               Plot the values of the solution and the prediction of the PINN on the same plot.

               :param X: A tensor (size (m;N_INPUT)) of the input data
               :param Y: A tensor (size (m;N_OUTPUT)) of the solutions
        """
        Y_v=Y.detach().numpy()
        S_v=self(X).detach().numpy()
        precision=np.zeros((self.N_OUTPUT,3))#mean median max
        for d in range(self.N_OUTPUT):
            delta=np.abs((S_v[:, d]-Y_v[:, d])/Y_v[:, d])
            precision[d,0] = np.mean(delta)*100
            precision[d, 1] = np.median(delta)*100
            precision[d, 2] = np.max(delta)*100
        print("Mean Median Max")
        print(np.round(precision,decimals=3))


        e2=np.sum(np.square(Y_v-S_v),axis=1)
        print("Error in L2:",math.sqrt(0.5*np.dot((X[1:].detach().numpy()-X[:-1].detach().numpy()).reshape(-1),e2[1:]+e2[:-1])))

        print("Error in infinite norme:", np.max(np.sum(np.abs(Y_v-S_v),axis=1)))

    def drawPhase(self,X):
        """
        Plot the phase diagramm.

        :param X: A tensor (size (m;N_INPUT)) of the input data
        """
        S_v = self(X).detach().numpy()
        plt.plot(S_v[:,0],S_v[:,1])
        plt.title("Phase Diagram")
        plt.xlabel("C(t)")
        plt.ylabel("N(t)")
        plt.show()
