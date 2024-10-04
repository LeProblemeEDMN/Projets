import torch
from FFracDerivative import FFracDerivative
from PINNExample.BergmanModel import d_out
from PINNExample.BergmanModel import BergModel
import PINN

# Parameters
NSteps = 30000
t0 = 0
tMax = 5
size = 1
alpha = 0.2

# Initialization of the minimal model of Bergman
model = BergModel(1, 1, 1, 2, 10, 1, 10, 1, 10)
X_tensor, Y_tensor = model.data(NSteps, t0, tMax)

# Creation of the data points
X = X_tensor.detach().numpy()
Y = Y_tensor.detach().numpy()
x_data = X[0:20:10]
y_data = Y[0:20:10]
print("Number of data:", len(x_data))
x_tensor = torch.Tensor(x_data).view(-1, 1).requires_grad_(True)
y_tensor = torch.Tensor(y_data).view(-1, d_out())

# Initialization of the PINN
PINN = PINN.PINN(1, d_out(), 48, 3)
PINN.load_state_dict(torch.load("res/BergMan")) # load the parameters

# Initialization of the fast fractional derivative
x_physics = torch.linspace(0, tMax, 300 * size + 1).view(-1, 1).requires_grad_(True)
ffd = FFracDerivative(x_physics, alpha, size)
pas = tMax / (600 * size + 1)

# Training of the PINN
PINN.train_PINN(x_tensor, y_tensor, physicsObjectives=[model.F], coeffDatas=0.1, coeffObjectives=1000, epochs=20001,
                plotLoss=False, x_physics=x_physics, physicsLossArgs=[ffd, size, Y[0]])
torch.save(PINN.state_dict(), "res/BergMan") # save the parameters after the training

PINN.plotPINN(X_tensor, Y_tensor, names=["G(t)", "X(t)", "I(t)"])

#Compute the physics loss of the model
yh = PINN(x_physics)
physics = model.F(x_physics, yh, [ffd, size, Y[0]])
print("LOSS", torch.mean(physics))
