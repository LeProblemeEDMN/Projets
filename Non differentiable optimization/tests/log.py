import numpy as np
import math
def fonction(x,args):
    return -np.sum(np.dot(np.log(x),args))

def sousGradient(x,args):
    return -args/x

def proximal(x,gamma,args):
    #print(gamma,args,x)
    return (x+math.sqrt(x**2+4*args[0]*gamma))/2

def proximalRN(x,gamma,args):
    y=np.zeros(len(x))
    for i in range(len(y)):
        
        y[i]=proximal(x[i],gamma,np.array([args[i]]))
    return y