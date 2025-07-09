import numpy as np
import matplotlib.pyplot as plt
from tests.valeurAbsolue import *
from tests.norme2Ab import *
from iDINAMsolveur import *
import time
def toValue(x,f,b,af,ag):
    y=np.zeros(len(x))
    for i in range(len(x)):
        y[i]=f(x[i],af)+b(x[i],ag)
    return y
def toValueNorme(x,f,b,af,ag):
    y=np.zeros(len(x))
    for i in range(len(x)):
        y[i]=np.linalg.norm(f(x[i],af)+b(x[i],ag))
    return y
h=0.1
dim=5
bb=0.1
bf=0.05
gammabf=3
N=1500
matSDP=(np.random.random((dim,dim))*2-1)*0.5
matSDP=np.transpose(matSDP).dot(matSDP)+np.eye(dim)
argsF=[0.1,matSDP,np.random.random(dim)]
argsB=0.1+np.random.random(dim)*4

gamma=gammabf/bf
x0=np.ones(dim)

xIte=iDINAM2Solveur(gradient,proximalRN,x0,h=h,bb=bb,bf=bf,gamma=gamma,Nmax=N,tol=10**-20,returnAll=True,argsB=argsB,argsF=argsF)
vNorme=toValueNorme(xIte,gradient,sousGradient,argsF,argsB)
plt.plot(vNorme)
plt.title("Norme de la fonction objectif en fonction")
plt.show()

