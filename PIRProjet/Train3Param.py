from PINNExample.test.HarmonicOscillator2D import *
from PINNExample.DCModel import DC
import PINN
import random

"""
This script train a PINN to predict the value of a DC model where the parameters I,C(0) and lambda are not specified during the training but when computing the solution.
This allows a single network to make accurate prediction for a wide range of parameters of the DC model.
"""

def createDataSetFull(size,nInd,path,NSteps=3000,t0=0,tMax=40,nbBegin=10):
    """
    Create a dataset for the training and save it in files. The input is of shape (n;4). An collocation point take the
    form of [time, C(0), I,lambda].
    :param size: Number of collocation points
    :param nInd: For each set of (C(0),I,lambda) it will generate nInd collocation point by generating random t.
    :param path: path of the directory where the files will be saved.
    :param NSteps: Number of steps used to approximate the exact solutions of the DC model.
    :param t0: Lower bound of the time interval
    :param tMax: Upper bound of the time interval
    :param nbBegin: Number of points where t=0. Used to make sure the prediction for t=0 equals the given initial condition.

    """
    id=0
    X = np.zeros((int(size/nInd)*nInd+nbBegin,4))
    Y = np.zeros((int(size/nInd)*nInd+nbBegin, 2))
    #create data points for t>0
    for i in range(int(size/nInd)):
        p=random.random()*0.45
        I=random.random()*0.04
        lamb = random.random() * 1
        DCModel = DC(np.array([p,0.5]), I, 0.02, lamb, 0.5, 0.05, 0.05)  # en millions
        x,y=DCModel.data(NSteps,t0,tMax,toTensor=False)
        for i in range(nInd):
            choice = random.randint(0, NSteps)
            X[id]=np.array([x[choice],p,I,lamb])
            Y[id]=y[choice]
            id+=1

    #create data points for t=0
    for i in range(nbBegin):
        p = random.random() * 0.45
        I = random.random() * 0.04
        lamb = random.random() * 1
        X[id] = np.array([0, p,I,lamb])
        Y[id] = np.array([p,0.5])
        id+=1
    np.save(path+"inputSizeFull",X)
    np.save(path + "outputSizeFull", Y)

# Parameters
NSteps = 3000
t0 = 0
tMax = 40
path="res/"
Cinit=0.05
lamb=0.98
I=0.005

#create dataset
createDataSetFull(1400,2,path,nbBegin=600)
print("DATASET CREATED")

# Initialization of the DC model
DCModel=DC(np.array([Cinit,0.5]),I,0.02,lamb,0.5,0.05,0.05)#en millions

# Creation of the data points
X=np.load(path+"inputSizeFull.npy")
Y=np.load(path + "outputSizeFull.npy")
x_tensor=torch.Tensor(X).view(-1, 4).requires_grad_(True)
y_tensor=torch.Tensor(Y).view(-1, 2)

#Creation of the collocation points
X_physics=np.zeros((len(X)*3,4))
X_physics[:,0]=t0+(tMax-t0)*np.random.random(len(X_physics))
X_physics[:,1]=0.45*np.random.random(len(X_physics))
X_physics[:,2]=0.04*np.random.random(len(X_physics))
X_physics[:,3]=1*np.random.random(len(X_physics))
x_physics=torch.Tensor(X_physics).view(-1, 4).requires_grad_(True)

# Initialization of the PINN
PINN=PINN.PINN(4,d_out(),32,10)
PINN.load_state_dict(torch.load("res/MultipleV2"))

#Training
#PINN.train_PINN(x_tensor,y_tensor,physicsObjectives=[DCModel.F_full],wd=1,wl=10,epochs=10001,plotLoss=True, x_physics=x_physics,saveFile="res/MultipleV2")
torch.save(PINN.state_dict(), "res/MultipleV2")

#Plot the result
dX,dY=DCModel.data(NSteps,t0,tMax,toTensor=False)
X_data=np.zeros((len(dX),4))
X_data[:,0]=dX
X_data[:,1]=Cinit
X_data[:,2]=I
X_data[:,3]=lamb
x_data=torch.Tensor(X_data).view(-1, 4).requires_grad_(True)
y_data=torch.Tensor(dY).view(-1, 2)
PINN.plotPINN(x_data,y_data,dim=2,dimX=0)
print(PINN.evaluatePrecision(x_data,y_data,dim=2))