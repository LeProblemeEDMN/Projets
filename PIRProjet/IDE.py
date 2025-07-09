import math
import numpy as np
import torch

import FractionnalDerivative
from FFracDerivativeOld import FFracDerivativeOld
from PINNExample.test.testFracT2 import d_out,data,F
import PINN
import matplotlib.pyplot as plt
# Parameters
NSteps = 3000
t0 = 0
tMax = 1
size = 1
alpha = 0.95
N=2*size+1
x_physics = torch.linspace(0, tMax, N).view(-1, 1).requires_grad_(True)

ffd_old = FFracDerivativeOld(x_physics, alpha, size)
res=ffd_old.computeCaputoDerivative(x_physics,torch.pow(x_physics,2))
print(res/x_physics[1:])

