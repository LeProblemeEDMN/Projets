import numpy as np
import torch
import PINN
import matplotlib.pyplot as plt

"""
This script plot the results of a network for different parameters in order to compare their results.
"""

listePoids = ["res/a005","res/a02","res/tau2","res/a06","res/a2"]  # path of the different parameters files.
nomPoids = ["c=0.05","c=0.2","c=1","c=0.6","c=2"]  # names of the parameters to be shown in the graph.
tMax = 10
tStep = 3000
dim = 4  # size of the output
dimNames = ["G", "X","I","U"]  # names of the dimension
phasePortrait = True  # if we want a phase portrait

# Initialize the time discretion
X = np.linspace(0, tMax, tStep)
Xt = torch.Tensor(X).view(-1, 1).requires_grad_(True)

# compute the predictions for the different parameters.
PINN = PINN.PINN(1, dim, 48, 3)
y = np.zeros((len(listePoids), tStep, dim))
for i in range(len(listePoids)):
    PINN.load_state_dict(torch.load(listePoids[i]))
    y[i] = PINN(Xt).detach().numpy()

# Plot the solution in function of time.
for i in range(len(listePoids)):
    for d in range(dim):
        plt.plot(X, y[i, :, d], label=dimNames[d] + ":" + nomPoids[i])
plt.xlabel("Time", size=14)
plt.legend(fontsize=14)
plt.show()

# Plot the phase portrait.
if (phasePortrait):
    for i in range(len(listePoids)):
        plt.plot(y[i, :, 0], y[i, :, 2], label=nomPoids[i])
    plt.xlabel(dimNames[0], size=14)
    plt.ylabel(dimNames[2], size=14)
    plt.legend(fontsize=14)
    plt.show()
