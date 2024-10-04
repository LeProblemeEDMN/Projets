import matplotlib.pyplot as plt
import numpy as np

from FFracDerivative import FFracDerivative
from PINNExample.test.DelayTest1 import *
import PINN

X1=np.array([0.001197, 0.00147, 0.000905, 0.000575, 0.000316, 0.00212, 0.0008783, 0.00387, 0.000230, 0.00657, 0.001542, 0.000171, 0.00319, 0.00111])
#0.002 0.00326 0.00202 0.00127 0.000724 0.00474 0.00195 0.00468 0.0005 0.0145 0.00309 0.000219 0.00703 0.00246
print(math.sqrt(np.var(X1)),np.median(X1))
print(len(X1))
print(np.max(X1)/np.min(X1))
plt.boxplot(X1)
plt.show()

#P92 et 103 normal puis avec derive partielles
#ajouter x'=F(t,x)+alpha*x(t-tho)
X=np.array([50,100,200,400,1000,2000])
Y=np.array([5.32,3.22,1.16,0.668,0.639,0.101])*0.01
Y2=np.array([10.1,6.32,2.37,1.31,1.25,0.223])*0.01
Y=np.array([71.75,75.45,86.26,90.11,119.72,145.48])
plt.plot(np.log(X),np.log(Y))
plt.show()
#print(PINN.evaluatePrecision(X_tensor[1500:],Y_tensor[1500:],dim=d_out()))
#torch.save(PINN.state_dict(), "res/Simple2D")