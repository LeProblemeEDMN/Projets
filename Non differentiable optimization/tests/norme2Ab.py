import numpy as np

def fonction_diff(x,args):
    return 1/(2*args[0])*np.linalg.norm(args[1].dot(x)-args[2])**2

def gradient(x,args):
    A=args[1]

    return (np.transpose(A).dot(A.dot(x))-np.transpose(A).dot(args[2]))/args[0]

def prox_grad(x,gamma,args):
    A=args[1]
    
    return np.linalg.solve(gamma/args[0]*np.matmul(np.transpose(A),A)+np.eye(len(x)),gamma/args[0]*np.transpose(A).dot(args[2])+x)