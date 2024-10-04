import numpy as np
import torch
from FFracDerivative import FFracDerivative
from PINNExample.AngelovaModel import d_out
from PINNExample.AngelovaModel import Angelova
import PINN

# Parameters
NSteps = 2000
t0 = 0
tMax = 40
size = 1
alpha = 0.9999
CInit = np.array([0.4, 0.5])

# Initialization of the Angelova Model
model = Angelova(1, 1, 1, 5, 1, 10, 1, 10, 10, 10, 2)  # tho0,q,Gin,tho,a0,a,b,d,e,h,NH
X_tensor, Y_tensor = model.data(NSteps, t0, tMax)

# Creation of the data points
X = X_tensor.detach().numpy()
Y = Y_tensor.detach().numpy()
x_data = X[0:2:10]
y_data = Y[0:2:10]
print("Number of data:", len(x_data))
x_tensor = torch.Tensor(x_data).view(-1, 1).requires_grad_(True)
y_tensor = torch.Tensor(y_data).view(-1, d_out())

# Initialization of the PINN
PINN = PINN.PINN(1, d_out(), 48, 3)
PINN.load_state_dict(torch.load("res/ModelAngelova2")) # load the parameters

# Initialization of the collocation points
x_physics = torch.linspace(0, tMax, 600 * size + 1).view(-1, 1).requires_grad_(True)

# Training of the PINN
PINN.train_PINN(x_tensor, y_tensor, physicsObjectives=model.losses(), coeffDatas=1, coeffObjectives=100, epochs=5001,
                plotLoss=False, x_physics=x_physics, physicsLossArgs=None, saveFile="res/ModelAngelova2")
torch.save(PINN.state_dict(), "res/ModelAngelova2")# save the parameters after the training

PINN.plotPINN(X_tensor, Y_tensor, names=["I(t)", "G(t)"])

#Compute the physics loss of the model
yh = PINN(x_physics)
physics = model.F_retard(x_physics, yh, None)
print("LOSS", torch.mean(physics))

print(PINN.evaluatePrecision(X_tensor[1500:], Y_tensor[1500:]))
