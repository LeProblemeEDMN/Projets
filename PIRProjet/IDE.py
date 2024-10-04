import math
import numpy as np
import torch

import FractionnalDerivative
from PINNExample.DCModel import d_out
from PINNExample.DCModel import DC
import PINN
import random

saveFile="res/alphaMultipleDim"

Cinit=0.1
lamb=0.85
I=0.02
NSteps=3000
t0=0
tMax=40

DCModel=DC(np.array([Cinit,0.5]),I,0.02,lamb,0.5,0.05,0.05)#en millions
X_tensor,Y_tensor=DCModel.data(NSteps,t0,tMax)
dX,dY=DCModel.data(NSteps,t0,tMax,toTensor=False)
X_data=np.zeros((len(dX),4))
X_data[:,0]=dX
X_data[:,1]=Cinit
X_data[:,2]=I
X_data[:,3]=lamb
x_data=torch.Tensor(X_data).view(-1, 4).requires_grad_(True)
y_data=torch.Tensor(dY).view(-1, 2)
PINN=PINN.PINN(4,d_out(),32,10)
PINN.load_state_dict(torch.load(saveFile))

PINN.plotPINN(x_data,y_data,dim=2,dimX=0)