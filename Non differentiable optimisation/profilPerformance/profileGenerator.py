import sys
 
# setting path
sys.path.append('../')
import numpy as np
from iDINAMsolveur import *
import tests.KLrelativeentropy
import tests.valeurAbsolue
import tests.DENNI
import tests.log
import tests.entropie
import tests.norme2Ab
import tests.norme2
import random
import time
#v1
#opti main pb1 gamma=1.6 h=1.3 bf=0.5 bb=0.1
#opti main pb3 gamma=2 h=0.6 bf=0.35 bb=0.75
#v2
#opti main pb1 gamma=1.5 h=1 bf=0.5 bb=0.05
#opti main pb2 gamma=2 h=1 bf=0.6 bb=0.05
#opti main pb3 gamma=2.1 h=1.1 bf=0.2 bb=0.75
testPerItem=40
listeTests=[]

for i in range(testPerItem):
    dim=random.randint(5,12)
    ab=np.random.random(dim)*4+0.1
    af=np.random.random(dim)*4+0.1
    test=[tests.norme2.gradient,tests.log.proximalRN,tests.log.sousGradient
          ,np.ones(dim),ab,af,10**-10,
          np.array([2.2,0.1,0.3,1.4])*0.6
          ,np.array([2.2,0.1,0.3,1.4])*0.6]
    listeTests.append(test)

for i in range(testPerItem):
    dim=random.randint(5,12)
    ab=np.random.random(dim)*4+0.1
    af=np.random.random(dim)*4+0.1
    test=[tests.DENNI.gradient,tests.log.proximalRN,tests.log.sousGradient
          ,np.ones(dim),ab,af,10**-10,
          np.array([1.6,1,0.2,1.8])*0.51
          ,np.array([1.6,1,0.2,1.8])*0.51]
    listeTests.append(test)

for i in range(testPerItem):
    dim=random.randint(5,12)
    ab=np.random.random(dim)*0.25+1
    matSDP=(np.random.random((dim,dim))*2-1)*1
    matSDP=np.transpose(matSDP).dot(matSDP)+np.eye(dim)*2
    af=[0.5,matSDP,np.random.random(dim)]
    test=[tests.norme2Ab.gradient,tests.valeurAbsolue.proximalRN,tests.valeurAbsolue.sousGradient
          ,np.ones(dim),ab,af,10**-10,
          np.array([0.5,0.05,0.15,2.5])*0.8
          ,np.array([0.5,0.05,0.15,2.5])*0.8]
    listeTests.append(test)

for i in range(testPerItem):
    dim=random.randint(5,15)
    ab=np.random.random(dim)*0+1
    af=np.random.random(dim)*0+1
    test=[tests.DENNI.gradient,tests.valeurAbsolue.proximalRN,tests.valeurAbsolue.sousGradient
          ,np.ones(dim),ab,af,10**-10,
          [1.9,0.2,0.15,2.5]
          ,[1.9,0.2,0.15,2.5]]
    listeTests.append(test)

for i in range(testPerItem):
    dim=random.randint(5,21)
    ab=np.random.random(dim)*0.4
    af=np.random.random(dim)*5+1
    test=[tests.KLrelativeentropy.gradient,tests.valeurAbsolue.proximalRN,tests.valeurAbsolue.sousGradient
          ,np.ones(dim),ab,af,10**-10,
          [2.5,0.01,0.21,1.44]
          ,[2.5,0.01,0.21,1.44]]
    listeTests.append(test)

for i in range(testPerItem):
    dim=random.randint(5,21)
    ab=np.random.random(dim)*0.4
    af=np.random.random(dim)*5+1
    test=[tests.norme2.gradient,tests.entropie.proximalRN,tests.entropie.sousGradient
          ,np.ones(dim),ab,af,4*10**-3,
          np.array([2.9,0.05,0.25,1.5])*0.6
          ,np.array([2.9,0.05,0.25,1.5])*0.6]
    listeTests.append(test)



N=600
print("Debut évaluation")
listeN=[1,2,3,4,6,8,10]
resultats=np.zeros((len(listeTests),len(listeN)*3))
iteMax=36
coeff=0.92

for idT in range(len(listeTests)):
    print("    Test:",idT,"/",len(listeTests))
    test = listeTests[idT]
    for idNAlgo in range(len(listeN)):
        NAlgo=listeN[idNAlgo]
        facteurCV=2
        v0=99999
        ite=0
        resultats[idT,idNAlgo*3+1]=999
        
        while((v0>test[6] or np.isnan(v0)) and ite<iteMax ):
            ite+=1
            deb=time.time()
            xid1=iDINAMNSolveur(test[0],test[1],NAlgo,test[3],h=test[7][0]*facteurCV,bb=test[7][1]*facteurCV,bf=test[7][2]*facteurCV,gamma=test[7][3]/test[7][2],Nmax=N,tol=10**-20,returnAll=True,argsB=test[4],argsF=test[5])
            tt=time.time()-deb

            idMin=len(xid1)-1
            v0=np.linalg.norm(test[0](xid1[idMin],test[5])+test[2](xid1[idMin],test[4]))
            v=v0
            while(idMin>1 and v<test[6]):
                idMin-=1
                v=np.linalg.norm(test[0](xid1[idMin],test[5])+test[2](xid1[idMin],test[4]))
            facteurCV*=coeff
            resultats[idT,idNAlgo*3+1]=idMin
            resultats[idT,idNAlgo*3]=tt
            resultats[idT,idNAlgo*3+2]=v0
    
        if(ite==iteMax):
            print("      L'itération ",idT," n'as pas convergée.")
print("Sauvegarde:")
np.save("iDINAM2/resultatsComparaisonN",resultats)
print(resultats)
