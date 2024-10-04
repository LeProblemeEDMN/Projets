from PINNExample.test.HarmonicOscillator2D import *
from PINNExample.DCModel import DC
import PINN
import random

#pk equa simple amrche pas
#pk 2dim marche pas

def createDataSet(size,nInd,path,NSteps=3000,t0=0,tMax=40,nbBegin=10):
    id=0
    X = np.zeros((int(size/nInd)*nInd+nbBegin,2))
    Y = np.zeros((int(size/nInd)*nInd+nbBegin, 2))
    for p in np.linspace(0,0.45,int(size/nInd)):
        DCModel = DC(np.array([p,0.5]), 0.02, 0.02, 0.85, 0.5, 0.05, 0.05)  # en millions
        x,y=DCModel.data(NSteps,t0,tMax,toTensor=False)
        for i in range(nInd):
            choice = random.randint(0, NSteps)
            X[id]=np.array([x[choice],p])
            Y[id]=y[choice]
            id+=1
    for p in np.linspace(0,0.45,nbBegin):
        X[id] = np.array([0, p])
        Y[id] = np.array([p,0.5])
        id += 1
    np.save(path+"inputSizeC",X)
    np.save(path + "outputSizeC", Y)


NSteps = 3000
t0 = 0
tMax = 40
path="res/"
#createDataSet(500,2,path,nbBegin=500)
print("DATASET CREATED")
Cinit=0.05
DCModel=DC(np.array([Cinit,0.5]),0.02,0.02,0.85,0.5,0.05,0.05)#en millions

X=np.load(path+"inputSizeC.npy")
Y=np.load(path + "outputSizeC.npy")
x_tensor=torch.Tensor(X).view(-1, 2).requires_grad_(True)
y_tensor=torch.Tensor(Y).view(-1, 2)

X_physics=np.zeros((len(X)*3,2))
X_physics[:,0]=t0+(tMax-t0)*np.random.random(len(X_physics))
X_physics[:,1]=0.45*np.random.random(len(X_physics))
x_physics=torch.Tensor(X_physics).view(-1, 2).requires_grad_(True)

PINN=PINN.PINN(2,d_out(),32,10)
PINN.load_state_dict(torch.load("res/MultipleV1"))
#PINN.train_PINN(x_tensor,y_tensor,physicsObjectives=[DCModel.F_multiple],wd=1,wl=5,epochs=10001,plotLoss=True, x_physics=x_physics,saveFile="res/MultipleV1")
torch.save(PINN.state_dict(), "res/MultipleV1")
dX,dY=DCModel.data(NSteps,t0,tMax,toTensor=False)
X_data=np.zeros((len(dX),2))
X_data[:,0]=dX
X_data[:,1]=Cinit
x_data=torch.Tensor(X_data).view(-1, 2).requires_grad_(True)
y_data=torch.Tensor(dY).view(-1, 2)
PINN.plotPINN(x_data,y_data,dim=2,dimX=0)
print(PINN.evaluatePrecision(x_data,y_data,dim=2))