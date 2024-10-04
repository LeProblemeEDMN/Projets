import math
import time

import matplotlib.pyplot as plt
import numpy as np
import torch
from FFracDerivative import FFracDerivative
import FractionnalDerivative
from PINNExample.ControlModel import d_out
from PINNExample.ControlModel import ControlModel
import PINN

#Glucose flow functions
def Ra_torch(x):
    Ra = torch.zeros_like(x)
    Ra[torch.logical_and(x > 3, x < 4)] = 50
    Ra[torch.logical_and(x > 6, x <= 7)] = 50
    return Ra


def Ra_numpy(x):
    Ra = np.zeros(len(x))
    Ra[np.logical_and(x > 3, x < 4)] = 50
    Ra[np.logical_and(x > 6, x < 7)] = 50
    return Ra

# Parameters
NSteps = 30000
t0 = 0
tMax = 10
size = 5
alpha = 0.4

# Initialization of the modified model of Bergman
model = ControlModel(5, 1, 1, 10, 8, 1, 1, 1, 1)
X_tensor, Y_tensor = model.data(NSteps, t0, tMax, Ra_numpy)

# Creation of the data points
X = X_tensor.detach().numpy()
Y = Y_tensor.detach().numpy()
x_data = X[0:201:1000]
y_data = Y[0:201:1000]
print("Number of data:", len(x_data))
x_tensor = torch.Tensor(x_data).view(-1, 1).requires_grad_(True)
y_tensor = torch.Tensor(y_data).view(-1, d_out())

# Initialization of the PINN
PINN = PINN.PINN(1, d_out(), 48, 3)
PINN.load_state_dict(torch.load("res/a2"))# load the parameters

# Initialization of the fast fractional derivative
N=100 * size + 1
x_physics = torch.linspace(0, tMax, 300 * size + 1).view(-1, 1).requires_grad_(True)
ffd = FFracDerivative(x_physics, alpha, size)

# Training of the PINN
pas=tMax/(N-1)
tau=2
print("Nb discret",int(tau/ pas),"tau:",tau*int(tau/ pas))
PINN.train_PINN(x_tensor, y_tensor, physicsObjectives=[model.F_delay], coeffDatas=4000, coeffObjectives=20000
                , epochs=10001,plotLoss=True, x_physics=x_physics,
                physicsLossArgs=[2, int(tau/ pas), Y[0],Ra_torch,torch.Tensor(Y[0]).view(-1, d_out()),tau])#[ffd, size, Y[0], Ra_torch]
torch.save(PINN.state_dict(), "res/a2")# save the parameters after the training

PINN.plotPINN(X_tensor, Y_tensor, names=["G(t)", "X(t)", "I(t)", "U(t)"])

#Compute the physics loss of the model
yh = PINN(x_physics)
physics = model.F(x_physics, yh, [ffd, size, Y[0], Ra_torch])
print("LOSS", torch.mean(physics))
