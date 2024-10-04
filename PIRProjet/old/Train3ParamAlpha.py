import math
import time

import matplotlib.pyplot as plt
import numpy as np
import torch

import FractionnalDerivative
from PINNExample.DCModel import d_out
from PINNExample.DCModel import DC
import PINN
import random
alpha=0.4
nbBegin=0
id=0
tMax=40

#PARAM ENTRAINEMENT
wd=1;wl=10;
saveFile="res/alphaMultipleDim"
nbEpochs=0


X = np.zeros((nbBegin,4))
Y = np.zeros((nbBegin, 2))
for i in range(nbBegin):
    p = random.random() * 0.045
    I = random.random() * 0.04
    lamb = random.random() * 1
    X[id] = np.array([0, p,I,lamb])
    Y[id] = np.array([p,0.5])
    id+=1

Cinit=0.05
lamb=0.98
I=0.005

DCModel=DC(np.array([Cinit,0.5]),I,0.02,lamb,0.5,0.05,0.05)#en millions

x_tensor=torch.Tensor(X).view(-1, 4).requires_grad_(True)
y_tensor=torch.Tensor(Y).view(-1, 2)

nPointApproxDerivative=10
nbCollocation=nbBegin*4

X_colloc=[]
Y_colloc=[]
for i in range(nbCollocation):
    p = random.random() * 0.045
    I = random.random() * 0.04
    lamb = random.random() * 1
    t=random.random() * 40
    X=np.repeat([t,p,I,lamb],nPointApproxDerivative).reshape((4,nPointApproxDerivative)).transpose()
    X[:,0]=np.linspace(0,t,nPointApproxDerivative)
    X_colloc.append(torch.Tensor(X).view(-1, 4).requires_grad_(True))

print("FIN INIT DATA")
PINN=PINN.PINN(4,d_out(),32,10)
PINN.load_state_dict(torch.load(saveFile))

debut=time.time()
optimizer = torch.optim.Adam(PINN.parameters(), lr=5e-4)
for i in range(nbEpochs):
    optimizer.zero_grad()
    yh = PINN(x_tensor)
    lossData = wd * torch.mean((yh - y_tensor) ** 2)  # use mean squared error
    loss = lossData

    for j in range(nbCollocation):
        data=X_colloc[j]
        yh = PINN(data)
        t=data[-1,0]
        I=data[0,2]
        lamb=data[0,3]
        y1 = yh[:, 0].view(-1, 1)
        y2 = yh[:, 1].view(-1, 1)
        vy1 = yh[-1,0]
        vy2 = yh[-1,1]
        dy1=FractionnalDerivative.computeCaputoDerivativeLastMultipleParam(data,y1,alpha,t)
        dy2 = FractionnalDerivative.computeCaputoDerivativeLastMultipleParam(data, y2, alpha, t)
        loss=loss+wl *(dy1 - (I - (lamb + DCModel.theta) * vy1 + lamb * vy2)) ** 2/nbCollocation + wl *(
                dy2 - (2 * I - (DCModel.v + DCModel.delta) * vy1 - DCModel.mu * vy2)) ** 2/nbCollocation

    loss.backward()
    optimizer.step()

    if (i % 5 == 0):
        if (saveFile is not None):
            torch.save(PINN.state_dict(), saveFile)
        print("Epoch ", i, "/", nbEpochs, ": Total Loss:", loss.item(), ", Data Loss:", lossData.item()," Time ellapsed:",str(math.ceil(100*(time.time()-debut))/100))



#computeCaputoDerivativeLastMultipleParam