import numpy as np
import matplotlib.pyplot as plt
import imageio
import os
from joblib import Parallel, delayed
Nb_file=150

# Dossier pour stocker les images
os.makedirs('frames', exist_ok=True)


# Générer les images pour le GIF
def process(t):
    plt.figure()
    data = np.loadtxt("t/result_temperature_" + str(t) + ".txt")

    data = data.reshape((200, 200, 2))
    fig = plt.figure(figsize=(20, 10))
    plt.subplot(1, 2, 1)
    plt.imshow(np.minimum(10,data[::-1, :, 0]), cmap='hot', interpolation='bicubic')
    plt.colorbar(label='Temperature')
    plt.subplot(1, 2, 2)
    plt.imshow(data[::-1, :, 1], cmap='hot', interpolation='bicubic')
    plt.colorbar(label='Density')
    plt.tight_layout()
    plt.savefig(f'frames/frame_{t:07d}.png')
    plt.close()
    return True


results = Parallel(n_jobs=8)(delayed(process)(i) for i in range(Nb_file))

# Créer le GIF
images = []
for t in range(Nb_file):
    filename = f'frames/frame_{t:07d}.png'
    images.append(imageio.imread(filename))
print(len(images))
imageio.mimsave('trajectoires.gif', images, duration=0.05, loop=0)

