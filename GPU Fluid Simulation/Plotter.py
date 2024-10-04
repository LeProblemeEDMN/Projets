import numpy as np
import matplotlib.pyplot as plt
import os
import cv2

path=r"//wsl.localhost/Ubuntu/home/baptiste/results/"
for i in ["0000010.asc"]:
    data = np.loadtxt(path + i)
    plt.hexbin(data[:, 0], data[:, 1], C=data[:, 5], gridsize=70, cmap='viridis')
    plt.colorbar(label='Pression')
    plt.title(i)
    plt.show()

# Chemin vers le dossier contenant les fichiers .asc
folder_path = path
output_video = 'output_video.mp4'
# Liste des fichiers .asc dans le dossier
asc_files = sorted([f for f in os.listdir(folder_path) if f.endswith('.asc')])

print(len(asc_files))
# Paramètres pour la vidéo
fps = 10
frame_size = (480, 480)

# Créer l'objet VideoWriter pour écrire la vidéo
fourcc = cv2.VideoWriter_fourcc(*'mp4v')
video_writer = cv2.VideoWriter(output_video, fourcc, fps, frame_size)

# Parcourir chaque fichier .asc, créer la heatmap et l'ajouter à la vidéo
for asc_file in asc_files:
    # Charger les données du fichier .asc
    data = np.loadtxt(os.path.join(folder_path, asc_file))
    x = data[:, 0]
    y = data[:, 1]
    pression =data[:, 5]#np.minimum(2,data[:, 5])

    # Créer une heatmap de la pression
    plt.figure(figsize=(8, 6))
    plt.hexbin(x, y, C=pression, gridsize=110, cmap='viridis')
    plt.colorbar(label='Pression')
    plt.xlabel('X')
    plt.ylabel('Y')
    plt.title('Heatmap de la pression')
    plt.savefig('temp_heatmap.png')  # Enregistrer la heatmap comme une image temporaire
    plt.close()

    # Lire l'image de la heatmap
    heatmap_img = cv2.imread('temp_heatmap.png')
    # Redimensionner l'image si nécessaire
    if heatmap_img.shape[:2] != frame_size:
        heatmap_img = cv2.resize(heatmap_img, frame_size)

    # Écrire l'image de la heatmap dans la vidéo
    video_writer.write(heatmap_img)

# Libérer la ressource VideoWriter
video_writer.release()

print(f'La vidéo a été créée : {output_video}')