import sys

# setting path
import matplotlib.pyplot as plt
import numpy as np

def profile_performance(tab,labels,title):
    """
    fonction générant un profil de performance
    tab: tableau des données
    label: nom des différents algorithmes à comparer
    title: nom de la variable analysé (temps, itération...)
    """
    mini=np.min(tab,axis=1)
    for i in range(tab.shape[1]):
        v=tab[:,i]/mini
        v=np.sort(v)
        plt.plot(v,np.arange(tab.shape[0])/tab.shape[0],label=labels[i])
    plt.legend(fontsize=12)
    plt.ylabel("Pourcentage",size=12)
    plt.xlabel("Ecart au meilleur",size=12)
    plt.title("Profil de performance en "+title,size=14)
    plt.show()


listeN=np.array([1,2,3,4,6,8,10,13,16])
indices=np.array([0,1,2,3,4,6,8])
#génère les noms des algorithmes
names=[]
for i in listeN[indices]:
    names.append("iDINAM"+str(i)+"-split")
    
#charge les données
resultats=np.load("resultatsComparaison.npy")
timeTab=resultats[:,indices*3]
timeTab=timeTab[np.min(timeTab,axis=1)>0.15]
iteTab=resultats[:,indices*3+1]
preciTab=resultats[:,indices*3+2]


profile_performance(timeTab,names,"Temps")
profile_performance(iteTab,names,"Itérations")
profile_performance(iteTab,names,"Précision")