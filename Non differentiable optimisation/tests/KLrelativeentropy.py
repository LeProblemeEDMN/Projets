import numpy as np

def fonction_diff(x,args):
    return np.sum(x*np.log(x/args)+args-x)

def gradient(x,args):
    return np.log(x/args)