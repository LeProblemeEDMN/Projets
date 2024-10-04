import numpy as np
import torch
from FFracDerivative import FFracDerivative
from PINNExample.DCModel import d_out
from PINNExample.DCModel import DC
import PINN


# Parameters
NSteps = 3000
t0 = 0
tMax = 20
size = 1
alpha = 0.2

# Initialization of the DC model
CInit = np.array([0.1, 0.5])
DCModel = DC(CInit, 0.02, 0.02, 0.85, 0.5, 0.05, 0.05)  # en millions

# Creation of the data points
X_tensor, Y_tensor = DCModel.data(NSteps, t0, tMax)
X = X_tensor.detach().numpy()
Y = Y_tensor.detach().numpy()
x_data = X[0:100:110]
y_data = Y[0:100:110]
print("Number of data:", len(x_data))
x_tensor = torch.Tensor(x_data).view(-1, 1).requires_grad_(True)
y_tensor = torch.Tensor(y_data).view(-1, d_out())

# Initialization of the PINN
PINN = PINN.PINN(1, d_out(), 32, 2)
#PINN.load_state_dict(torch.load("res/Simple2D"))  # load the parameters

# Initialization of the fast fractional derivative
x_physics = torch.linspace(t0, tMax, 400 * size + 1).view(-1, 1).requires_grad_(True)
ffd = FFracDerivative(x_physics, alpha, size)
# Initialization of the constants for the delayed version of the model
pas = tMax / (400 * size + 1)
print(pas)
# Training of the PINN
PINN.train_PINN(x_tensor, y_tensor, physicsObjectives=[DCModel.F_retard], coeffDatas=1, coeffObjectives=4.5,
                epochs=2001, plotLoss=False, x_physics=x_physics,
                physicsLossArgs=[[1.5,-0.05], int(0.1 / pas), torch.Tensor(CInit)])
# [ffd,size] parameters for the fractionnal derivatives version of the model
# [-0.005, int(2 / pas), torch.Tensor(CInit)] for the delayed version of the model
torch.save(PINN.state_dict(), "res/Delay15_m05")  # save the parameters after the training

PINN.plotPINN(X_tensor, Y_tensor, names=["C(t)", "N(t)"])

print(PINN.evaluatePrecision(X_tensor, Y_tensor))
