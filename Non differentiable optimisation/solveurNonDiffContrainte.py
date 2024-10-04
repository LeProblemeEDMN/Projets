import numpy as np
from solveurNonDifferentiable import *

def solveurMultContraintes(proxR,x0,treshold,dict_ctr,pas_lambda=0.05,L=50,R=None,argsR=None):
    """
    """

def solveurIneq(proxR,x0,iterMax,treshold,ctr,grad_ctr,pas_lambda=0.05,L=50,R=None,warning=True,argsR=None,argsContrainte=None,seuil=0):
    """
    Résout min R(x) sous la contrainte ctr(x)<=0 (une unique contrainte)
    avec une contrainte et coercive différentiable en utilisant la méthode d'uzawa avec FISTA pour les minimisation 
    Retourne x la solution obtenue le nombre d'itération et les valeurs de la fonction si arretAvecValeurs=True 
    Arguments:
        proxR: l'opérateur prox de la fonction R (de la forme ...(x, gamma, arguments))
        iterMax: itérations maximale au cas ou l'algorithme ne converge pas
        treshold: précision de la condition d'arrêt
        ctr: fonction pour évaluer la contrainte
        grad_ctr: gradient de la contrainte
        pas_lambda (optionnel): pas de la mise à jour des contraintes
        L (optionnel): majorant de la constante de Lipschitz du gradient de F
        R (optionnel): fonction pour évaluer R (de la forme ...(x, arguments))
        warning (optionnel):afficher un message d'erreur si il n'y a pas eu convergence de la méthode
        argsR (optionnel): arguments suplémentaires pour R et proxR
        argsContrainte (optionnel): arguments suplémentaires pour la contrainte
        seuil (optionnel): permet de remplacer ctr(x)<=0 par ctr(x)-seuil<=0
    """
    #initialisation
    iter=0
    x=x0
    y=x0
    t=1
    lambdaContrainte=0

    passe=True#variable de la condition d'arrêt
    lC=[0]#valeurs de la fonction contrainte pour chaque itération
    erreurFISTA=False #garde trace de si il a déja été indiqué une non convergence
    while(iter<iterMax and passe):
        iter+=1
        #minimise le lagrangien pour une valeur de lambdaContrainte avec FISTA
        nx,it=solveurFISTA(proxR,grad_ctr,x0=x,iterMax=500,treshold=treshold,L=L,warning=False,argsR=argsR,argsF=argsContrainte)
        #calcule la valeur de la contrainte
        ctr_value=ctr(nx,argsContrainte)-seuil
        #affiche une erreur en cas de non convergence de FISTA
        if(it==500 and warning and not erreurFISTA):
            erreurFISTA=True
            print("FISTA n'as pas convergé dans solveurIneq")
        #update lambda
        lambdaContrainte=max(0,lambdaContrainte+pas_lambda*ctr_value)
        lC.append(lambdaContrainte)
        passe=np.linalg.norm(nx-x)>treshold or ctr_value>treshold
        #deuxième condition d'arrêt si solution valide sans contrainte
        if(lambdaContrainte==0 and ctr_value<=0):
            break
        x=nx

    #Erreur en cas de non convergence
    if iter>=iterMax and warning:
        print("La méthode du multiplicateur n'as pas convergé en",iterMax,"étapes")
        if(R is not None):
            print("    f(x)=",R(x,argsR)," contrainte=",ctr_value)
    return x,lambdaContrainte,iter,lC

def solveurMultiplicateur(proxR,x0,iterMax,treshold,A,b,pas_mu=0.05,L=50,R=None,warning=True,args=None):
    """
    Résout min R(x) sous la contrainte Ax=b
    en utilisant la méthode d'uzawa avec FISTA pour les minimisation 
    Retourne x la solution obtenue le nombre d'itération et les valeurs de la fonction si arretAvecValeurs=True 
    Arguments:
        proxR: l'opérateur prox de la fonction R (de la forme ...(x, gamma, arguments))
        iterMax: itérations maximale au cas ou l'algorithme ne converge pas
        treshold: précision de la condition d'arrêt
        A: une matrice
        b: un vecteur
        pas_mu (optionnel): pas de la mise à jour des contraintes
        L (optionnel): majorant de la constante de Lipschitz du gradient de F
        R (optionnel): fonction pour évaluer R (de la forme ...(x, arguments))
        warning (optionnel):afficher un message d'erreur si il n'y a pas eu convergence de la méthode
        args (optionnel): arguments suplémentaires pour R et proxR
        """
    #initialisation
    iter=0
    x=x0
    passe=True#variable de la condition d'arrêt
    v=np.zeros(len(x0))

    erreurFISTA=False #garde trace de si il a déja été indiqué une non convergence
    
    while(iter<iterMax and passe):
        iter+=1
        #gradient de la norme du lagrangien augmente
        grad_dot_product=lambda x,ag:pas_mu*np.transpose(A).dot(A.dot(x)-b+v/pas_mu)
        #minimise le lagrangien pour ce v
        nx,it=solveurFISTA(proxR,grad_dot_product,x0=x,iterMax=500,treshold=0.0001,L=L,warning=False,argsR=args)
        #affiche une erreur en cas de non convergence de FISTA
        if(it==500 and warning and not erreurFISTA):
            erreurFISTA=True
            print("FISTA n'as pas convergé dans solveurIneq")
        
        #valeur de la contrainte
        ctr=A.dot(nx)-b
        #update v
        v+=pas_mu*ctr
        #condition d'arrêt
        passe=np.linalg.norm(nx-x)>treshold or np.linalg.norm(ctr)>0.001
        x=nx

    #Erreur en cas de non convergence
    if iter>=iterMax and warning:
        print("La méthode du multiplicateur n'as pas convergé en",iterMax,"étapes")
        if(R is not None):
            print("    f(x)=",R(x,args)," contrainte=",np.linalg.norm(ctr))
    
    return x,iter



#JAMAIS TESTE
def solveurSeparable1D(proxR,x0,iterMax,treshold,A,b,pas_mu=0.05,L=50,R=None,warning=True,args=None):
    iter=0
    x=x0
    y=x0
    t=1

    passe=True

    l=len(proxR)
    v=np.zeros(l)
    
    while(iter<iterMax and passe):
        iter+=1
        nx=np.zeros(l)
        for i in range(l):
            grad_dot_product=lambda x,ag:A[:,i].dot(v)
            nx[i],it=solveurGradientProximal(proxR[i],grad_dot_product,x0=x[i],iterMax=500,treshold=0.0001,gamma=6/L,warning=False,args=args)
            print(it,grad_dot_product(0,0))
        ctr=A.dot(nx)-b
        v+=pas_mu*ctr
        print(iter,nx,np.linalg.norm(ctr))
        print()
        passe=np.linalg.norm(nx-x)>treshold or np.linalg.norm(ctr)>0.001
        x=nx

    #Erreur en cas de non convergence
    if iter>=iterMax and warning:
        print("La méthode de montée de gradient n'as pas convergé en",iterMax,"étapes")
        if(R is not None):
            print("    f(x)=",R(x,args)," contrainte=",np.linalg.norm(ctr))
    print("test ctr=",np.linalg.norm(ctr))
    return x,iter



