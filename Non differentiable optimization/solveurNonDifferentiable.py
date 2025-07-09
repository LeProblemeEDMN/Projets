import numpy as np
import math
""" ########################################################################
ALGORITHME FISTA
############################################################################
""" 
def solveurFISTA(proxR,gradF,x0,iterMax,treshold,L=50,R=None,F=None,arretAvecValeurs=False,warning=True,argsR=None,argsF=None,restart=False):
    """
    Résout min R(x)+F(x)  avec F différentiable en utilisant la méthode FISTA 
    Retourne x la solution obtenue le nombre d'itération et les valeurs de la fonction si arretAvecValeurs=True 
    Arguments:
        proxR: l'opérater prox de la fonction R (de la forme ...(x, gamma, arguments))
        gradF: gradient de F (de la forme ...(x, arguments))
        iterMax: itérations maximale au cas ou l'algorithme ne converge pas
        treshold: précision de la condition d'arrêt
        restart (optionnel): utiliser FISTA avec redémarrage
        L (optionnel): majorant de la constante de Lipschitz du gradient de F
        R (optionnel): fonction pour évaluer R (de la forme ...(x, arguments))
        F (optionnel): fonction pour évaluer F (de la forme ...(x, arguments))
        arretAvecValeurs (optionnel): si R et F sont renseigné utilise la convergence de R+F comme condition d'arrêt à la place d ela convergence des itéré
        warning (optionnel):afficher un message d'erreur si il n'y a pas eu convergence de la méthode
        argsR (optionnel): arguments suplémentaires pour R et proxR
        argsF (optionnel): arguments suplémentaires pour F et gradF
    """
    #initialisation
    iter=0
    x=x0
    y=x0
    t=1
    arretAvecValeurs=arretAvecValeurs and R is not None and F is not None
    
    if(arretAvecValeurs):
        ancienneEval=R(x0,argsR)+F(x0,argsF)

    passe=True#variable de la condition d'arrêt
    value=[]#valeurs de la fonction objectif pour chaque itération

    while(iter<iterMax and passe):
        iter+=1
        #descente de gradient
        z=x-gradF(y,argsF)/L
        #proximal
        nx=proxR(z,1/L,argsR)
        #calcul de tk
        nt=(1+math.sqrt(1+4*t*t))/2
        #restart de FISTA
        if(restart and (nx-z).dot(nx-x)<0):
            nt=1
            nx=z

        y=nx+(t-1)/nt*(nx-x)
        #condition d'arret sur la distance
        if(arretAvecValeurs):
            eval=R(nx,argsR)+F(nx,argsF)
            passe=abs(eval-ancienneEval)>treshold
            ancienneEval=eval
            value.append(eval)
            
        else:
            passe=np.linalg.norm(nx-x)>treshold
        x=nx
        t=nt
    
    #Erreur en cas de non convergence
    if iter>=iterMax and warning:
        print("La méthode FISTA n'as pas convergé en",iterMax,"étapes")
        if(R is not None and F is not None):
            print("    f(x)=",R(x,argsR)+F(x,argsF))
    if(arretAvecValeurs):
        return x,iter,value
    return x,iter
""" ########################################################################
ALGORITHME DU SOUS GRADIENT
############################################################################
""" 
def solveurSousGradient(sgf,x0,iterMax,treshold,L=5,reductionPasAngle=0.8,R=None,arretAvecValeurs=False,warning=True,args=None):
    """
    Résout min R(x)  avec F différentiable en utilisant la méthode de descente du sosu-gradient
    Retourne x la solution obtenue le nombre d'itération et les valeurs de la fonction si arretAvecValeurs=True 
    Arguments:
        proxR: l'opérater prox de la fonction R (de la forme ...(x, gamma, arguments))
        iterMax: itérations maximale au cas ou l'algorithme ne converge pas
        treshold: précision de la condition d'arrêt
        L (optionnel): majorant de la constante de Lipschitz du gradient de F
        arretAvecValeurs (optionnel): si R et F sont renseigné utilise la convergence de R+F comme condition d'arrêt à la place d ela convergence des itéré
        warning (optionnel):afficher un message d'erreur si il n'y a pas eu convergence de la méthode
        args (optionnel): arguments suplémentaires pour R et proxR
        reductionPasAngle (optionnel): paramètre permettant d'accélere la convegence (=1 pour algorithme de base)
    """
    iter=0
    #on peut avoir l'evolution des valeurs en conditions d'arrêt que si la fct pr evaluer est donnée
    arretAvecValeurs=arretAvecValeurs and R is not None
    x=x0
    if(arretAvecValeurs):
        ancienneEval=R(x0,args)
    sens=None
    passe=True#varaible de la condition d'arrêt
    value=[]#valeurs de la fonction objectif pour chaque itération
    while(iter<iterMax and passe):
        iter+=1
        #calcule et normalise un sous gradient
        ssDif=sgf(x,args)
        ssDif=ssDif/(np.linalg.norm(ssDif)+10**-15)
        #descente
        nx=x-L/np.sqrt(iter)*ssDif

        #cherche le sens de descente et si il a trop varie(pres du min local) baisse le pas
        newSens=(nx-x)/np.linalg.norm(nx-x)
        if(sens is not None) and np.dot(newSens,sens)>0.5:
            L*=reductionPasAngle
        sens=newSens

        #condition d'arret sur la distance
        if(arretAvecValeurs):
            eval=R(nx,args)
            passe=np.abs(eval-ancienneEval)>treshold
            ancienneEval=eval
            value.append(eval)
        else:
            passe=np.linalg.norm(nx-x)>treshold
        x=nx

    #Erreur en cas de non convergence
    if iter>=iterMax and warning:
        print("La méthode du sous gradient n'as pas convergé en",iterMax,"étapes")
        if(R is not None):
            print("    f(x)=",R(x,args))
    if(arretAvecValeurs):
        return x,iter,value
    return x,iter

""" ########################################################################
ALGORITHME DU GRADIENT PROXIMAL
############################################################################
""" 
def solveurGradientProximal(proxR,gradF,x0,iterMax,treshold,gamma=0.5,R=None,F=None,arretAvecValeurs=False,warning=True,argsR=None,argsF=None):
    """
    Résout min R(x)+F(x)  avec F différentiable en utilisant la méthode du gradient proximal 
    Retourne x la solution obtenue le nombre d'itération et les valeurs de la fonction si arretAvecValeurs=True 
    Arguments:
        proxR: l'opérater prox de la fonction R (de la forme ...(x, gamma, arguments))
        gradF: gradient de F (de la forme ...(x, arguments))
        iterMax: itérations maximale au cas ou l'algorithme ne converge pas
        treshold: précision de la condition d'arrêt
        gamma (optionnel): constante (idéalement >1/L ou L est le majorant de la constante de Lipschitz du gradient de F)
        R (optionnel): fonction pour évaluer R (de la forme ...(x, arguments))
        F (optionnel): fonction pour évaluer F (de la forme ...(x, arguments))
        arretAvecValeurs (optionnel): si R et F sont renseigné utilise la convergence de R+F comme condition d'arrêt à la place d ela convergence des itéré
        warning (optionnel):afficher un message d'erreur si il n'y a pas eu convergence de la méthode
        argsR (optionnel): arguments suplémentaires pour R et proxR
        argsF (optionnel): arguments suplémentaires pour F et gradF
    """
    #initialisation
    iter=0
    x=x0

    arretAvecValeurs=arretAvecValeurs and R is not None and F is not None
    
    if(arretAvecValeurs):
        ancienneEval=R(x0,argsR)+F(x0,argsF)
    
    passe=True#varaible de la condition d'arrêt
    value=[]#valeurs de la fonction objectif pour chaque itération

    while(iter<iterMax and passe):
        iter+=1
        #descente de gradient
        y=x-gamma*gradF(x,argsF)
        #proximal
        nx=proxR(y,gamma,argsR)

        #condition d'arret sur la distance de la fonction objectif 
        if(arretAvecValeurs):
            eval=R(nx,argsR)+F(nx,argsF)
            passe=abs(eval-ancienneEval)>treshold
            ancienneEval=eval
            value.append(eval)
        else:
            #condition d'arret sur la distance des itéré
            passe=np.linalg.norm(nx-x)>treshold

        x=nx
    
    #Erreur en cas de non convergence
    if iter>=iterMax and warning:
        print("La méthode du gradient proximal n'as pas convergé en",iterMax,"étapes")
        if(R is not None and F is not None):
            print("    f(x)=",R(x,argsR)+F(x,argsF))
    if(arretAvecValeurs):
        return x,iter,value
    return x,iter

def fonction_nulle(x,*args):
    return 0

def gradient_nul(x,*args):
    try:
        return np.zeros(len(x))
    except:
        return 0