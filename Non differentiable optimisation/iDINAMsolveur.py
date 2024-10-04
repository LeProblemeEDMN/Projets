import numpy as np
import math
def iDINAMSolveur(grad_f,solveB,x0,h=0.3,bb=2,bf=0.6,gamma=None,Nmax=500,tol=10**-5,returnAll=False,argsF=None,argsB=None,M=1):
    """
    Résout min R(x)+F(x)  avec F différentiable en utilisant la méthode iDINAM-split
    Retourne x la solution obtenue le nombre d'itération et les valeurs de la fonction si arretAvecValeurs=True 
    Arguments:
        solveB: l'opérater prox de la fonction R (de la forme ...(x, gamma, arguments))
        grad_f: gradient de F (de la forme ...(x, arguments))
        Nmax: itérations maximale au cas ou l'algorithme ne converge pas
        tol: précision de la condition d'arrêt
        y0: valeur initiale
        h,bb,bf: paramètres de l'algorithme
        gamma: paramètre de l'algorithme choisis (valeur par défault gamma=1.1/bf )
        R (optionnel): fonction pour évaluer R (de la forme ...(x, arguments))
        F (optionnel): fonction pour évaluer F (de la forme ...(x, arguments))
        arretAvecValeurs (optionnel): si R et F sont renseigné utilise la convergence de R+F comme condition d'arrêt à la place d ela convergence des itéré
        warning (optionnel):afficher un message d'erreur si il n'y a pas eu convergence de la méthode
        argsR (optionnel): arguments suplémentaires pour R et proxR
        argsF (optionnel): arguments suplémentaires pour F et gradF
    """
    #calcul des différentes constantes
    alpha=1+bb/h
    if(gamma==None):
        gamma=1.1/bf

    ox=np.copy(x0)
    x=x0
    if returnAll:
        v=[x]
    passe=True
    ite=0
    while(passe):
        #calcul des itérés
        c=alpha/(1+gamma*h)
        eps=x +c*(x-ox)-c*h*h*M*grad_f(x+bf/h*(x-ox),argsF)
        nx=solveB(eps,c*h*h*M,argsB)/alpha+(alpha-1)/alpha*x
        if returnAll:
            v.append(nx)
        ox,x=x,nx
        ite+=1
        #condition d'arrêt
        passe=ite<Nmax and np.linalg.norm(x-ox)>tol and  np.linalg.norm(x)<10**15

    if(returnAll):
        return v
    return x

def iDINAM2Solveur(grad_f,solveB,y0,h=0.3,bb=2,bf=0.6,gamma=None,Nmax=500,tol=10**-5,returnAll=False,argsF=None,argsB=None,M=1):
    """
    Résout min R(x)+F(x)  avec F différentiable en utilisant la méthode iDINAM2-split
    Retourne x la solution obtenue le nombre d'itération et les valeurs de la fonction si arretAvecValeurs=True 
    Arguments:
        solveB: l'opérater prox de la fonction R (de la forme ...(x, gamma, arguments))
        grad_f: gradient de F (de la forme ...(x, arguments))
        Nmax: itérations maximale au cas ou l'algorithme ne converge pas
        tol: précision de la condition d'arrêt
        y0: valeur initiale
        h,bb,bf: paramètres de l'algorithme
        gamma: paramètre de l'algorithme choisis (valeur par défault gamma=1.1/bf )
        R (optionnel): fonction pour évaluer R (de la forme ...(x, arguments))
        F (optionnel): fonction pour évaluer F (de la forme ...(x, arguments))
        arretAvecValeurs (optionnel): si R et F sont renseigné utilise la convergence de R+F comme condition d'arrêt à la place d ela convergence des itéré
        warning (optionnel):afficher un message d'erreur si il n'y a pas eu convergence de la méthode
        argsR (optionnel): arguments suplémentaires pour R et proxR
        argsF (optionnel): arguments suplémentaires pour F et gradF
    """
    #calcul des différentes constantes
    delta=(h+bb)/h+bb**2/(2*h**2)
    deltaf=(h+bf)/h+bf**2/(2*h**2)
    alpha=delta/(gamma+1/h)
    mub=bb/h+(bb/h)**2
    muf=bf/h+(bf/h)**2
    
    if(gamma==None):
        gamma=1.1/bf
    
    y=np.copy(y0)
    ym1=np.copy(y0)
    ym2=y0
    if returnAll:
        v=[y]
    passe=True
    ite=0
    while(passe):
        #calcul des itérés
        z=y*(alpha*(gamma+2/h)-mub)+ym1*((bb/h)**2/2-alpha/h)-alpha*h*M*grad_f(y*deltaf-ym1*muf+(bf/h)**2/2*ym2,argsF)
        
        ny=solveB(z,alpha*h*M,argsB)/delta+y*mub/delta-ym1*(bb/h)**2/(2*delta)
       
        if returnAll:
            v.append(ny)
        ym2,ym1,y=ym1,y,ny
        ite+=1
        #condition d'arrêt
        passe=ite<Nmax and np.linalg.norm(y-ym1)>tol and  np.linalg.norm(y)<10**15
    if(returnAll):
        return v
    return y

def iDINAMNSolveur(grad_f,solveB,N,y0,h=0.3,bb=2,bf=0.6,gamma=None,Nmax=500,tol=10**-5,returnAll=False,argsF=None,argsB=None,M=1):
    """
    Résout min R(x)+F(x)  avec F différentiable en utilisant la méthode iDINAMN-split
    Retourne x la solution obtenue le nombre d'itération et les valeurs de la fonction si arretAvecValeurs=True 
    Arguments:
        solveB: l'opérater prox de la fonction R (de la forme ...(x, gamma, arguments))
        grad_f: gradient de F (de la forme ...(x, arguments))
        Nmax: itérations maximale au cas ou l'algorithme ne converge pas
        tol: précision de la condition d'arrêt
        N: choix de la version de iDINAMN utilisé
        y0: valeur initiale
        h,bb,bf: paramètres de l'algorithme
        gamma: paramètre de l'algorithme choisis (valeur par défault gamma=1.1/bf )
        R (optionnel): fonction pour évaluer R (de la forme ...(x, arguments))
        F (optionnel): fonction pour évaluer F (de la forme ...(x, arguments))
        arretAvecValeurs (optionnel): si R et F sont renseigné utilise la convergence de R+F comme condition d'arrêt à la place d ela convergence des itéré
        warning (optionnel):afficher un message d'erreur si il n'y a pas eu convergence de la méthode
        argsR (optionnel): arguments suplémentaires pour R et proxR
        argsF (optionnel): arguments suplémentaires pour F et gradF
    """
    #calcul des différentes constantes
    arraySum=bb/h/np.maximum(1,np.arange(N+1))
    arraySum=np.cumprod(arraySum)/bb*h
    qb=np.flip(np.cumsum(arraySum))*arraySum*np.power(-1,np.arange(N+1))
    arraySum=bf/h/np.maximum(1,np.arange(N+1))
    arraySum=np.cumprod(arraySum)/bf*h
    qf=np.flip(np.cumsum(arraySum))*arraySum*np.power(-1,np.arange(N+1))
    alpha=qb[0]/(1+gamma*h)
    if(gamma==None):
        gamma=1.1/bf
    oldY=np.transpose(np.repeat(y0,N+1).reshape(len(y0),N+1))
    if returnAll:
        v=[y0]
    passe=True
    ite=0
    while(passe):
        #calcul des itérés
        yk=qf.dot(oldY)
        
        uk=-alpha*h**2*M*grad_f(yk,argsF)+alpha*(2+gamma*h)*oldY[0]-alpha*oldY[1]+qb[1:].dot(oldY[:-1])
        
        ny=solveB(uk,h**2*alpha*M,argsB)-qb[1:].dot(oldY[:-1])
        ny/=qb[0]
        
        if returnAll:
            v.append(ny)
        ite+=1
        #condition d'arrêt
        passe=ite<Nmax and np.linalg.norm(ny-oldY[0])>tol and  np.linalg.norm(ny)<10**15
        oldY[1:]=oldY[:-1]
        oldY[0]=ny

    if(returnAll):
        return v
    return oldY[0]
    


def iDINAM2eSolveur(grad_f,solveB,y0,eps=0.0001,chi=1,h=0.3,bb=2,bf=0.6,gamma=None,Nmax=500,tol=10**-5,returnAll=False,argsF=None,argsB=None):
    """
    Résout min R(x)+F(x)  avec F différentiable en utilisant la méthode iDINAM2e-split
    En prenant chi=1 et eps=0 on obtient iDINAM2Solveur.
    Retourne x la solution obtenue le nombre d'itération et les valeurs de la fonction si arretAvecValeurs=True 
    Arguments:
        solveB: l'opérater prox de la fonction R (de la forme ...(x, gamma, arguments))
        grad_f: gradient de F (de la forme ...(x, arguments))
        Nmax: itérations maximale au cas ou l'algorithme ne converge pas
        tol: précision de la condition d'arrêt
        eps: paramètre de viscosité (devant la dérivée troisième dans iDINAM2e)
        chi: paramètre de la dérivée seconde (vaut 1 dans iDINAM2e)
        y0: valeur initiale
        h,bb,bf: paramètres de l'algorithme
        gamma: paramètre de l'algorithme choisis (valeur par défault gamma=1.1/bf )
        R (optionnel): fonction pour évaluer R (de la forme ...(x, arguments))
        F (optionnel): fonction pour évaluer F (de la forme ...(x, arguments))
        arretAvecValeurs (optionnel): si R et F sont renseigné utilise la convergence de R+F comme condition d'arrêt à la place d ela convergence des itéré
        warning (optionnel):afficher un message d'erreur si il n'y a pas eu convergence de la méthode
        argsR (optionnel): arguments suplémentaires pour R et proxR
        argsF (optionnel): arguments suplémentaires pour F et gradF
    """
    #calcul des différentes constantes
    delta=(h+bb)/h+bb**2/(2*h**2)
    deltaf=(h+bf)/h+bf**2/(2*h**2)
    alpha=delta/(gamma*h**2+chi*h+eps)
    mub=bb/h+(bb/h)**2
    muf=bf/h+(bf/h)**2
    if(gamma==None):
        gamma=1.1/bf
    
    y=np.copy(y0)
    ym1=np.copy(y0)
    ym2=y0
    if returnAll:
        v=[y]
    passe=True
    ite=0
    while(passe):
        #calcul des itérés
        z=y*(alpha*(3*eps+gamma*h**2+2*h*chi)-mub)+ym1*((bb/h)**2/2-alpha*h*chi-3*eps)+ym2*eps*alpha-alpha*h**3*grad_f(y*deltaf-ym1*muf+(bf/h)**2/2*ym2,argsF)
        
        ny=solveB(z,alpha*h**3,argsB)/delta+y*mub/delta-ym1*(bb/h)**2/(2*delta)

        if returnAll:
            v.append(ny)
        ym2,ym1,y=ym1,y,ny
        
        ite+=1
        #condition d'arrêt
        passe=ite<Nmax and np.linalg.norm(y-ym1)>tol
    
    if(returnAll):
        return v
    return y
