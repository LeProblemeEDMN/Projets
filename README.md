# Projects
This repository presents some of my projects that I realized on my free time for the last 10 years.

## ChacalEngine
ChacalEngine is a 3D game engine using OpenGL (using the LWJGL java library). This game engine mainly features:
-Phong reflection model
-Normal maps implementation
-Post processing pipeline
-Sun and point of light shadows
-Water, metal and glass reflections.

The game engine have been develloped in 2018 with the help of a tutorial made by Thin Matrix.

## Fourrier Upscaling
This project consisted to implement a image upscaler by using Fourrier series.

To use you should put your images in the input directory, use the JUpsacling.jar file and wait. The upscaled image can be found in the output directory.

## GlobalIllumination

This project is an implementation (not in realtime) of a RayTracer in java. Raytracer aims to generate photorealistics images.
To generate an image, multiple multiple rays needs to be computed per pixel (each ray yield a different color so multiple needs rays are launched and average to obtain the final pixel color). I implemented a an algorithm which reduce the number of ray launched by determining which pixel needs extra ray and which dont.

The rendering is slow but leads to photorealistics images.

## GPU fluid simulation

This incompressible fluid simulation is implemented in C and CUDA and solve Navier-Stokes equations. A dye is modeled which allow to render the evolution of this dye concentration.
The project use an image in black and white to model the environment. this image can be transformed with the script ImageToText.py into a .txt file that can be used with the main.cu file to compute the flow of water. Finally the Plotter.py script make a .gif whioch shows the flow of the water.

## HPC_Project
This project have been realised for the MATH751 class. This project implements a Simplex and a Branch and Bound algorithms. The goal of the project was to see if it was possible to use process multiple sub-problems in parallel. I have seen that it allowed to do process less nodes and thus became way faster to solve problems.

## JavaStats

This projects aimed to create a stats librairy. This librairy have:
-functionnal Neural networks (with Dense,Convolution,MaxPooling and Flatten layers)
-data frame implementation
-ACP implementation
-a graph visualizer including function plotting, histograms, boxplots...
-clustering algorithms

## Lattice Boltzmann

This projects model a gas using the Lattice Boltzmann Method (LBM). It model the movements of gas and the diffusion of temperature in the gas.
The project use an image in black and white to model the environment. this image can be transformed with the script ImageToText.py into a .txt file that can be used with the main.cu file to compute the simulation. Finally the plot_temperature.py script make a .gif which shows the gas movements and the temperature repartition.

## MCTS 
This project is an implementation of an IA in the Connect4 game. This IA uses a Monte-Carlo Tree Search (MCTS) to play.

## Non differentiable optimisation

This project is the result of my research internship at IRMAR. The report (in french) of this internship can be found in the directory. outperform the original versions in some cases (see the report for more detail).

## PIRProject

This project is the result of an Intorduction to research class. The report (in english) of this project can be found in the directory. 
The project consisted of evaluating the possiblity of performance of Physic Informed Neural Network to solve ODE and partial-derivative differential equations.
I modified the usual PINN algorithm to be able to efficiently solve the partial-derivative differential equations. I evaluated the PINNs on multiple examples.