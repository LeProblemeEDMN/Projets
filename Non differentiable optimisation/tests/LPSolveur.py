import numpy as np
def solve(c,A:np.array,b):
    """
    D contrainte egualite
    A contrainte inegualite
    """
    n=len(c)+A.shape[0]
    m=A.shape[0]
    constraintMatrix=np.zeros((m,n))
    constraintMatrix[0:A.shape[0],0:len(c)]=A
    constraintMatrix[0:A.shape[0],len(c):]= np.eye(A.shape[0])

    constraintVector=np.zeros(m)
    constraintVector[:A.shape[0]]=b

    cost=np.zeros(n)
    cost[:len(c)]=c

    #debut de l'algo
    x=np.zeros(n)
    x[len(c):]=b
    
    epsilon=np.arange(A.shape[0])+len(c)

    ameliore=True
    valeurx = cost.dot(x)
    bv=0
    while(ameliore):
        bestX=0
        bestValue=-np.infty
        inv=(-1,-1)
        for i in range(len(c)):
            if(cost[i] > 0):
                v_max = np.Infinity
                jm=-1
                for j in range(A.shape[0]):
                    v=x[epsilon[j]]/(constraintMatrix[j,i]+10**-25)
                    
                    if(v>=0 and v<v_max):
                        jm=j
                        v_max=v
                        
                nx=np.copy(x)
                nx[i]=v_max
                
                nx[epsilon]=x[epsilon]+constraintMatrix[:,i]*(x[i]-nx[i])
            elif(cost[i] < 0):
                v_min = 0
                jm=-1
                for j in range(A.shape[0]):
                    v=-x[epsilon[j]]/(constraintMatrix[j,i]+10**-25)
                    if(v>=0 and v>v_min):
                        jm=j
                        v_min=v
                nx=np.copy(x)
                nx[i]=v_min
                nx[epsilon]=x[epsilon]+constraintMatrix[:,i]*(x[i]-nx[i])
            else:
                continue
            eval=cost.dot(nx)+bv
            #print(i,eval)
            if(eval>bestValue):
                bestValue=eval
                bestX=nx
                inv=(i,jm)
        
        ameliore=False
        #print(valeurx,bestValue,bestX)
        if(valeurx<bestValue):
            ameliore=True
            valeurx=bestValue
            x=bestX
            epsilon[inv[1]]=inv[0]
            nc=cost-constraintMatrix[inv[1],inv[0]]/cost[inv[0]]*constraintMatrix[inv[1],:]
            bv+=cost.dot(x)-nc.dot(x)
            cost=nc
        

    return x,valeurx

"""
dim=100
ctr=20
#A=np.random.random((ctr,dim))*10+0.1
#b=np.random.random(ctr)*25+1
#c=np.random.random(dim)*20-10
A=np.array([[1,0],[0,1]])
b=np.array([1,1])
c=np.ones(2)
print(solve(c,A,b))"""
    


    

