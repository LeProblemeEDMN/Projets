import numpy as np

def fonction(x,args):
    return x.dot(args[0].dot(x))

def sousGradient(x,args):
    return 2*args[0].dot(x)

def proximal(y,gamma,args):
    return np.linalg.solve(2*gamma*args[0]+np.eye(len(y)),y)
