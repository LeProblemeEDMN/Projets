package Neuron.GA;

import Neuron.Dense;
import Neuron.Layer;
import Neuron.NNetwork;
import matrix.Matrix;

import java.util.Random;

public class GeneticAlgorithm {
    private Population lastPopulation, currentPopulation;
    private NNetwork[] lastNetwork, currentNetwork;
    private int popSize;
    private Random random =new Random();
    private float probabilityMutation =0;

    public boolean fightFightness=false;

    public GeneticAlgorithm(NNetwork[] networks,Population a,Population b,float prob){
        currentNetwork = networks;
        lastNetwork = networks;
        lastPopulation = a;
        currentPopulation = b;
        lastPopulation.setFitness(lastNetwork);
        currentPopulation.setFitness(currentNetwork);
        probabilityMutation = prob;
    }

    public void newGeneration(){
        NNetwork[] newNet = new NNetwork[currentNetwork.length];
        int[][] parents = new int[currentNetwork.length][2];
        for (int i = 0; i < currentNetwork.length; i++) {
            NNetwork childN = currentPopulation.getNNetwork();
            parents[i][0] = currentPopulation.secondId;//idParent();
            parents[i][1] = currentPopulation.idBestId;//idParent();
            NNetwork parent1 = currentNetwork[parents[i][0]];
            NNetwork parent2 = currentNetwork[parents[i][1]];
            //load the weight of the layers
            for (int j = 0; j < childN.getLayers().size(); j++) {
                if(childN.getLayers().get(j) instanceof Dense){
                    Dense newDense = (Dense)childN.getLayers().get(j);
                    Dense parentDense1 = (Dense)parent1.getLayers().get(j);
                    Dense parentDense2 = (Dense)parent2.getLayers().get(j);

                    newDense.setWeights(mixMatrix(parentDense1.getWeights(),parentDense2.getWeights()));
                    newDense.setBias(mixMatrix(parentDense1.getBias(),parentDense2.getBias()));

                    mutateMatrix(newDense.getWeights());
                    mutateMatrix(newDense.getBias());
                }
            }
            newNet[i] = childN;
        }
        //switchPopulation;
        Population temp = currentPopulation;
        currentPopulation = lastPopulation;
        lastPopulation = currentPopulation;

        lastPopulation = currentPopulation;
        currentNetwork = newNet;

        if(!fightFightness)currentPopulation.setFitness(currentNetwork);
        else currentPopulation.setFitnessWithFight(currentNetwork);
        //remove the network with -20% off fitness
        for (int i = 0; i < currentNetwork.length; i++) {
            if(currentPopulation.fitness[i] < Math.min(lastPopulation.fitness[parents[i][0]], lastPopulation.fitness[parents[i][0]]) * 0.5f){
                currentNetwork[i] = lastNetwork[parents[i][random.nextInt(2)]];
            }
        }
        System.out.println(currentPopulation.bestFitness+" mean:"+currentPopulation.mean);
    }
    private void mutateMatrix(Matrix p1){
        for (int i = 0; i < p1.getLin(); i++) {
            for (int j = 0; j < p1.getCol(); j++) {
                if(random.nextFloat()<probabilityMutation){
                    float v = (4*random.nextFloat()-2)*Math.abs(p1.getValue(i,j));
                    p1.setValue(i, j, v);
                }
            }
        }

    }

    private Matrix mixMatrix(Matrix p1, Matrix p2){
        Matrix child = new Matrix(p1.getLin(), p1.getCol());
        for (int i = 0; i < p1.getLin(); i++) {
            for (int j = 0; j < p1.getCol(); j++) {
                child.setValue(i, j, random.nextInt(2)==0? p1.getValue(i,j): p2.getValue(i,j));
            }
        }
        return child;
    }

    public int idParent(){
        float rdm = random.nextFloat();
        int i=0;
        while (rdm>=currentPopulation.percents[i])i++;
        return i;
    }
}
