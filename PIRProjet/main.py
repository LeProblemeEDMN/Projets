import numpy as np
import torch
from FFracDerivative import FFracDerivative
from PINNExample.test.VDP import d_out
from PINNExample.test.VDP import F,data
#from PINNExample.test.DelayTest1 import b_value,tho_value
import PINN

# Parameters
NSteps = 3000
t0 = 0
tMax = 4
size = 1
alpha = 0.2

# Initialization of the DC model
# Creation of the data points
X_tensor, Y_tensor = data(NSteps, t0, tMax)
X = X_tensor.detach().numpy()
Y = Y_tensor.detach().numpy()
x_data = X[0:2500:1001]
y_data = Y[0:2500:1001]
print("Number of data:", len(x_data))
x_tensor = torch.Tensor(x_data).view(-1, 1).requires_grad_(True)
y_tensor = torch.Tensor(y_data).view(-1, d_out())

# Initialization of the PINN
PINN = PINN.PINN(1, d_out(), 32, 2)
PINN.load_state_dict(torch.load("res/Simple2D"))  # load the parameters

N_p=399


# Initialization of the fast fractional derivative
x_physics = torch.linspace(t0, tMax, N_p * size + 1).view(-1, 1).requires_grad_(True)

ffd = FFracDerivative(x_physics, alpha, size)

# Initialization of the constants for the delayed version of the model
pas = tMax / (N_p * size + 1)
tho = 0.1
#longueur_retard=int(tho_value()/pas)
#print(tho_value()/pas)
#a = b_value() / (1 - tho)
#preSol=np.arange(-longueur_retard,0)
#0.001197 0.00147 0.000905 0.000575 0.000316 0.00212 0.0008783 0.00387 0.000230 0.00657 0.001542 0.000171 0.00319 0.00111
#0.002 0.00326 0.00202 0.00127 0.000724 0.00474 0.00195 0.00468 0.0005 0.0145 0.00309 0.000219 0.00703 0.00246
#pre_value=torch.Tensor(a*preSol*pas).view(-1, d_out())
#print(pre_value)
# Training of the PINN
PINN.train_PINN(x_tensor, y_tensor, physicsObjectives=[F], coeffDatas=13000, coeffObjectives=1000,
                epochs=5000, plotLoss=False, x_physics=x_physics,
                physicsLossArgs=[ffd, size, Y[0]])#[None,longueur_retard,pre_value ,alpha]
# [ffd,size] parameters for the fractionnal derivatives version of the model
# [-0.005, int(2 / pas), torch.Tensor(CInit)] for the delayed version of the model
torch.save(PINN.state_dict(), "res/Simple2D")  # save the parameters after the training

print(X.shape)
PINN.plotPINN(X_tensor, Y_tensor, names=["", "N(t)"])

print(PINN.evaluatePrecision(X_tensor, Y_tensor))
