import numpy as np


def fonction_diff(x,args):
    return np.linalg.norm(x-args)**2

def gradient(x,args):
    return 2*(x-args)

def prox_grad(x,gamma,args):
    #print(args)
    #print(gamma)
    return (gamma*args+x)/(1+2*gamma)