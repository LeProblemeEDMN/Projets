import numpy as np

def fonction(x,args):
    return np.max(np.abs(x))

def sousGradient(x,args):
    y=np.zeros(len(x))
    y[np.argmax(np.abs(x))]=1
    return y

def proximalRN(x,gamma,args):
    y=np.copy(x)
    i=np.argmax(np.abs(x))
    y[i]=[0,x[i]-np.sign(x[i])*gamma][abs(x[i])>gamma]
    return y