import numpy as np


def fonction_diff(x,args):
    return np.arange(1,len(x)+1).dot(x**2)+np.sum(x)**2

def gradient(x,args):
    
    return 2*np.arange(1,len(x)+1)*x+x*(np.sum(x)+x)