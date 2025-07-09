import numpy as np

def fonction(x,args):
    return np.sum(np.abs(x-args[0]))

def sousGradient(x,args):
    y=np.zeros(len(x))
    for i in range(len(y)):
        y[i]= [-1,1][x[i]>args[i]]
    return y

def proximal(x,gamma,args):
    return [0,x-np.sign(x-args[0])*gamma][abs(x+args[0])>gamma]
    
def proximalRN(x,gamma,args):
    y=np.zeros(len(x))
    for i in range(len(y)):
        
        y[i]=proximal(x[i],gamma,np.array([args[i]]))
    return y
