import numpy as np
import torch
from FFracDerivative import FFracDerivative
from FFracDerivativeOld import FFracDerivativeOld
from PINNExample.test.testFracT2 import d_out,data,F
import PINN
import matplotlib.pyplot as plt
# Parameters
NSteps = 3000
t0 = 0
tMax = 2
size = 2
NP=20
alpha = 0.95

# Creation of the data points
X_tensor, Y_tensor = data(alpha,NSteps, t0, tMax)

X = X_tensor.detach().numpy()
Y = Y_tensor.detach().numpy()
x_data = X[0:1000000:10000]
y_data = Y[0:1000000:10000]
print("Number of data:", len(x_data))
x_tensor = torch.Tensor(x_data).view(-1, 1).requires_grad_(True)
y_tensor = torch.Tensor(y_data).view(-1, d_out())
for alpha in [0.6,0.75,0.9,0.95]:
    # Initialization of the PINN
    PINN_new = PINN.PINN(1, d_out(), 32, 2)
    #PINN_new.load_state_dict(torch.load("res/SimpleT2"))
    # Initialization of the fast fractional derivative
    N=NP * size + 1
    x_physics = torch.linspace(0, tMax, N).view(-1, 1).requires_grad_(True)
    ffd = FFracDerivative(x_physics, alpha, size)
    # Initialization of the constants for the delayed version of the model
    pas = tMax / (400 * size + 1)

    # Training of the PINN
    PINN_new.train_PINN(x_tensor, y_tensor, physicsObjectives=[F], coeffDatas=10, coeffObjectives=10,epochs=5001, plotLoss=False, x_physics=x_physics,physicsLossArgs=[ffd, size, Y[0],alpha])
    #torch.save(PINN_new.state_dict(), "res/SimpleT2")
    PINN_old = PINN.PINN(1, d_out(), 32, 2)
    #PINN_old.load_state_dict(torch.load("res/SimpleT2_old"))
    # Initialization of the fast fractional derivative
    N=NP * size + 1
    x_physics = torch.linspace(0, tMax, N).view(-1, 1).requires_grad_(True)
    ffd_old = FFracDerivativeOld(x_physics, alpha, size)
    # Initialization of the constants for the delayed version of the model
    pas = tMax / (400 * size + 1)

    # Training of the PINN
    PINN_old.train_PINN(x_tensor, y_tensor, physicsObjectives=[F], coeffDatas=10, coeffObjectives=10,epochs=5001, plotLoss=False, x_physics=x_physics,physicsLossArgs=[ffd_old, size, Y[0],alpha])
    # [ffd,size] parameters for the fractionnal derivatives version of the model
    # [-0.005, int(2 / pas), torch.Tensor(CInit)] for the delayed version of the model
    #torch.save(PINN_old.state_dict(), "res/SimpleT2_old")  # save the parameters after the training
    #plt.plot(eul_x.detach().numpy(),eul_y.detach().numpy(),color="purple")

    x_physics = torch.linspace(0, tMax, NSteps).view(-1, 1).requires_grad_(True)
    y_pred=PINN_new(x_physics)
    #plt.plot(x_physics.detach(),y_pred.detach(),label=r"mPINN,$\alpha$="+str(alpha))
    y_pred_old=PINN_old(x_physics)
    plt.plot(x_physics.detach(),y_pred_old.detach(),label=r"PINN,$\alpha$="+str(alpha))
    print(PINN_new.evaluatePrecision(X_tensor, Y_tensor))
    print(PINN_old.evaluatePrecision(X_tensor, Y_tensor))
plt.plot(X,Y,label="Exact Solution")
plt.xlabel("t")
plt.ylabel("y(t)")
plt.legend()
plt.savefig("Solution t2_PINN.png")
#plt.show()
#PINN.plotPINN(X_tensor, Y_tensor, names=["VDP(t)", "N(t)"])
X_tensor=X_tensor[50:]
Y_tensor=Y_tensor[50:]

