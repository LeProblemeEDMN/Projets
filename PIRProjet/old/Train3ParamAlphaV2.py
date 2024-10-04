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
nbBegin=80
id=0
tMax=40

#PARAM ENTRAINEMENT
wd=10;wl=wd*15;
saveFile="res/alphaMultipleDim"
nbEpochs=10000

X = np.zeros((nbBegin,4))
Y = np.zeros((nbBegin, 2))
for i in range(nbBegin):
    p = random.random() * 0.45
    I = random.random() * 0.04
    I = 0.02
    lamb = random.random() * 1
    lamb = 0.85
    X[id] = np.array([0, p,I,lamb])
    Y[id] = np.array([p,0.5])
    id+=1

Cinit=0.05
lamb=0.98
I=0.005

DCModel=DC(np.array([Cinit,0.5]),I,0.02,lamb,0.5,0.05,0.05)#en millions

x_tensor=torch.Tensor(X).view(-1, 4).requires_grad_(True)
y_tensor=torch.Tensor(Y).view(-1, 2)

nPointApproxDerivative=200
nbCollocation=nbBegin*5



X_colloc=np.zeros((nPointApproxDerivative*nbCollocation,4))
dataColloc=[]
for i in range(nbCollocation):
    p = random.random() * 0.045
    I = random.random() * 0.04
    I=0.02
    lamb = random.random() * 1
    lamb=0.85
    t=random.random() * 20
    #t=20
    dataColloc.append([t,I,lamb])

    X=np.repeat([t,p,I,lamb],nPointApproxDerivative).reshape((4,nPointApproxDerivative)).transpose()
    X[:,0]=np.linspace(0,t,nPointApproxDerivative)
    X_colloc[i*nPointApproxDerivative:(i+1)*nPointApproxDerivative]=X
X_colloc = torch.Tensor(X_colloc).view(-1, 4).requires_grad_(True)


print("FIN INIT DATA")
PINN=PINN.PINN(4,d_out(),32,10)
debut=time.time()
optimizer = torch.optim.Adam(PINN.parameters(), lr=5e-4)
PINN.load_state_dict(torch.load(saveFile))

for i in range(nbEpochs):
    optimizer.zero_grad()
    yh = PINN(x_tensor)
    lossData = wd * torch.mean((yh - y_tensor) ** 2)  # use mean squared error
    loss = lossData
    loss2=lossData

    yh = PINN(X_colloc)
    m=math.ceil(alpha)
    y1 = yh[:, 0].view(-1, 1)
    y2 = yh[:, 1].view(-1, 1)

    dx1 = torch.autograd.grad(y1, X_colloc, torch.ones_like(y1), create_graph=True)[0][:, 0]
    for j in range(1, m):
        dx1 = torch.autograd.grad(dx1.view(-1, 1), X_colloc, torch.ones_like(dx1.view(-1, 1)), create_graph=True)[0][:, 0]
    dx2 = torch.autograd.grad(y2, X_colloc, torch.ones_like(y1), create_graph=True)[0][:, 0]
    for j in range(1, m):
        dx2 = torch.autograd.grad(dx2.view(-1, 1), X_colloc, torch.ones_like(dx2.view(-1, 1)), create_graph=True)[0][:, 0]

    #fait une matrice ou chaque ligne correspond au point pour calculer l'approx de la derive partielle de la collocation
    matrix = X_colloc[:, 0].view(-1, nPointApproxDerivative)

    matrix = torch.matmul(matrix[:, -1].view(-1, 1), torch.ones(1, nPointApproxDerivative)) - matrix
    matrix = torch.pow(matrix, math.ceil(alpha) - alpha) / (math.ceil(alpha) - alpha)
    matrix = matrix[:,:-1] - matrix[:,1:]
    dx12 = dx1.view(-1, nPointApproxDerivative)
    dx22 = dx2.view(-1, nPointApproxDerivative)

    dy1= 0.5 / math.gamma(m - alpha) * torch.matmul(matrix * (dx12[:,:-1]+dx12[:,1:]),torch.ones(nPointApproxDerivative-1))
    dy2 = 0.5 / math.gamma(m - alpha) * torch.matmul(matrix * (dx22[:, :-1]+dx22[:,1:]), torch.ones(nPointApproxDerivative-1))
    I = X_colloc[:, 2].view(-1, nPointApproxDerivative)[:,0]
    lamb = X_colloc[:, 3].view(-1, nPointApproxDerivative)[:, 0]
    y12 = y1.view(-1, nPointApproxDerivative)[:, nPointApproxDerivative-1]
    y22 = y2.view(-1, nPointApproxDerivative)[:, nPointApproxDerivative - 1]
    loss=loss+wl*torch.mean((dy1 - (I - (lamb + DCModel.theta) * y12 + lamb * y22)) ** 2 + (
                dy2 - (2 * I - (DCModel.v + DCModel.delta) * y12 - DCModel.mu * y22)) ** 2)
    #print(((dy1 - (I - (lamb + DCModel.theta) * y12 + lamb * y22)) ** 2 + (
    #            dy2 - (2 * I - (DCModel.v + DCModel.delta) * y12 - DCModel.mu * y22)) ** 2)[:5])
    #print(loss2)
    #print(dy1)
    #sx=X_colloc[:nPointApproxDerivative].view(-1,4)
    #print(sx)
    #sy=PINN(sx)[:, 0].view(-1,1)
    #print(FractionnalDerivative.computeCaputoDerivativeLastMultipleParam(sx,sy,0.4,20))
    #print()
    """for j in range(nbCollocation):
        t = dataColloc[j][0]
        I = dataColloc[j][1]
        lamb = dataColloc[j][2]
        l=(j*nPointApproxDerivative)
        u=((j+1)*nPointApproxDerivative)
        vy1 = yh[u-1, 0]
        vy2 = yh[u-1, 1]

        nx=X_colloc[l:u].requires_grad_(True)
        ny=PINN(nx)
        ny1=ny[:,0].view(-1,1)
        dy12 = FractionnalDerivative.computeCaputoDerivativeLastMultipleParam(nx, ny1, alpha, t)
        
        dy1 = FractionnalDerivative.computeCaputoDerivativeFL(X_colloc[l:u], dx1[l:u], alpha, t)
        dy2 = FractionnalDerivative.computeCaputoDerivativeFL(X_colloc[l:u], dx2[l:u], alpha, t)
        #print((dy1 - (I - (lamb + DCModel.theta) * vy1 + lamb * vy2)) ** 2 + (
        #        dy2 - (2 * I - (DCModel.v + DCModel.delta) * vy1 - DCModel.mu * vy2)) ** 2)
        loss = loss + wl * (dy1 - (I - (lamb + DCModel.theta) * vy1 + lamb * vy2)) ** 2 / nbCollocation + wl * (
                dy2 - (2 * I - (DCModel.v + DCModel.delta) * vy1 - DCModel.mu * vy2)) ** 2 / nbCollocation
        print(dy1,dy12)"""
    #print(loss)
    #print()


    loss.backward()
    optimizer.step()
    if (i % 25 == 2):
        print("Epoch ", i, "/", nbEpochs, ": Total Loss:", loss.item(), ", Data Loss:", lossData.item(),
              " Time ellapsed:", str(math.ceil(100 * (time.time() - debut)) / 100))

    if (i % 25 == 1):
        if (saveFile is not None):
            torch.save(PINN.state_dict(), saveFile)
        X_colloc = np.zeros((nPointApproxDerivative * nbCollocation, 4))
        dataColloc = []
        for j in range(nbCollocation):
            p = random.random() * 0.045
            I = random.random() * 0.04
            I = 0.02
            lamb = random.random() * 1
            lamb = 0.85
            t = random.random() * 20
            # t=20
            dataColloc.append([t, I, lamb])

            X = np.repeat([t, p, I, lamb], nPointApproxDerivative).reshape((4, nPointApproxDerivative)).transpose()
            X[:, 0] = np.linspace(0, t, nPointApproxDerivative)
            X_colloc[j * nPointApproxDerivative:(j + 1) * nPointApproxDerivative] = X
        X_colloc = torch.Tensor(X_colloc).view(-1, 4).requires_grad_(True)
        print("Epoch ", i, "/", nbEpochs, ": Total Loss:", loss.item(), ", Data Loss:", lossData.item()," Time ellapsed:",str(math.ceil(100*(time.time()-debut))/100))

