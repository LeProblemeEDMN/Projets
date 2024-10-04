import numpy as np
import matplotlib.pyplot as plt
import os
import cv2

path=r"//wsl.localhost/Ubuntu/home/baptiste/Fluid/results/"

Nx=500
Ny=500
img = cv2.imread('duck.png')
img = cv2.resize(img, (Nx,Ny))
gray_image = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
print(gray_image.shape)

result=np.zeros(gray_image.shape)
result[gray_image>0.8]=1
result=np.transpose(result[:,::-1])
plt.matshow(result)
plt.show()
print(result.shape)
result=np.flip(result,axis=0)
np.savetxt(r"simMask/duck500.txt",result.reshape(-1))

