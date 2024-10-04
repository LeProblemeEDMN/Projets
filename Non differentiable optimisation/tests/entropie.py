import numpy as np
import time
def fonction(x,args):
    return np.sum(x*np.log(x))

def sousGradient(x,args):
    return 1+np.log(x)

def proximal(y,gamma,args):
    min=0
    max=np.max([y+5,300])
    ite=0
    while(abs(max-min)>0.001 and ite<420):
        mid=(max+min)/2
        v_mid=-9999
        ite+=1
        if(mid>0):
            v_mid=gamma*(np.log(mid)+1)+mid-y
        if(v_mid<0):
            min=mid
        else:
            max=mid
    
    return (max+min)/2

def proximalRN(x,gamma,args):
    y=np.zeros(len(x))
    for i in range(len(y)):
        
        y[i]=proximal(x[i],gamma,args[i])
    return y