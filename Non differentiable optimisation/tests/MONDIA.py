import numpy as np


def fonction_diff(x,args):
    return 25*np.sum((x[0]-x[1:]**2)**2)+np.sum(1-x)**2

def gradient(x,args):
    grad=np.zeros(len(x))
    grad[1:]=2*(x[1:]-1)+50*x[1:]*(x[1:]-x[0])
    grad[0]=50*np.sum(x[0]-x[1:]**2)+2*(x[0]-1)
    return grad

def prox_grad(x,gamma,args):
    #print(args)
    #print(gamma)
    return (gamma*args+x)/(1+2*gamma)