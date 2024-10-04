package Neuron.GA;

import Neuron.NNetwork;

import java.util.Arrays;

public abstract  class Population {
    public float[] fitness;
    public float[] percents;
    public float bestFitness = -999999;
    public int idBestId,secondId;
    public float mean;

    public abstract NNetwork getNNetwork();


    protected abstract float calculateFitnessnetwork(NNetwork network);
    protected abstract float[] calculateFitnessnetwork(NNetwork a, NNetwork b);

    public void setFitness(NNetwork[] population){
        bestFitness = -999999;
        fitness = new float[population.length];
        percents = new float[population.length];

        float totalfitness = 0;

        for (int i = 0; i < population.length; i++){
            fitness[i] = calculateFitnessnetwork(population[i]);
            //System.out.println();
            //System.out.println();
            totalfitness +=Math.max(0,fitness[i]);
            bestFitness = Math.max(bestFitness, fitness[i]);
        }
        percents[0] = fitness[0]/totalfitness;
        for (int i = 1; i < population.length - 1; i++){
            percents[i] = percents[i-1] + Math.max(0,fitness[i])/totalfitness;
        }
        float secondBest =-9999;

        for (int i = 1; i < population.length - 1; i++){
            if(fitness[i] ==bestFitness)idBestId=i;
            else if(secondBest<fitness[i]){
                secondId=i;
                secondBest=fitness[i];
            }
        }

        percents[population.length - 1] = 1;
        mean = totalfitness/population.length;
        System.out.println(Arrays.toString(fitness));
    }

    public void setFitnessWithFight(NNetwork[] population){

        bestFitness = -999999;
        fitness = new float[population.length];
        percents = new float[population.length];

        float totalfitness = 0;

        for (int i = 0; i < population.length; i+=2){
            float[] arr_fit = calculateFitnessnetwork(population[i], population[i + 1]);
            fitness[i] = arr_fit[0];
            fitness[i+1] = arr_fit[1];

            totalfitness +=fitness[i] + fitness[i+1];
            bestFitness = Math.max(bestFitness, Math.max(fitness[i], fitness[i + 1]));
        }
        percents[0] = fitness[0]/totalfitness;
        for (int i = 1; i < population.length - 1; i++){
            percents[i] = percents[i-1] + fitness[i]/totalfitness;
        }
        percents[population.length - 1] = 1;
        mean = totalfitness/population.length;
    }
}
