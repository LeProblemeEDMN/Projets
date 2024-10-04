package Neuron;

import Neuron.convolution.Convolution;
import Neuron.convolution.Flatten;
import Neuron.convolution.MaxPooling;
import Neuron.costFunction.CostFunction;
import matrix.Matrix;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NNetwork {


    private List<Layer> layers = new ArrayList<>();
    private FirstLayer inputLayer;

    private int[] inputsize;

    private Optimizer optimizer;

    public NNetwork() {
        inputLayer = new FirstLayer();
        add(inputLayer);
    }

    public void add(Layer layer) {
        layers.add(layer);
    }

    public void compile(CostFunction costFunction, int... inputsize) {
        this.inputsize = inputsize;
        optimizer = new Optimizer(this, costFunction);
        inputLayer.setSizeInput(inputsize);

        for (int i = 1; i < layers.size(); i++) {
            layers.get(i).init(layers.get(i - 1));
        }
    }



    public Matrix predict(Matrix[] input) {
        inputLayer.setOut(input);

        for (int i = 1; i < layers.size(); i++) {
            layers.get(i).forwardPropagation();
        }
        return layers.get(layers.size() - 1).getOut();
    }

    public void save(String dirFile){
        try {
            BufferedWriter writer=new BufferedWriter(new FileWriter(dirFile));
            for (int i = 0; i < inputsize.length; i++)
                writer.write(Integer.toString(inputsize[i]) +" ");

            writer.newLine();
            writer.write(optimizer.getCostFunction().getName());
            writer.newLine();
            writer.write(Integer.toString(layers.size()-1));
            for (int i = 1; i < layers.size(); i++) {
                writer.newLine();
                writer.write(layers.get(i).save());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static NNetwork readNetwork(String filePath){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String[] sizeLine = reader.readLine().split(" ");
            int[] inputSize= new int[sizeLine.length-1];
            for (int i = 0; i < inputSize.length; i++) inputSize[i] = Integer.parseInt(sizeLine[i]);
            String cost =reader.readLine();
            CostFunction costFunction = null;

            for (int i = 0; i < CostFunction.COST_FUNCTIONS.length; i++){
                if(cost.equals(CostFunction.COST_FUNCTIONS[i].getName()))costFunction = CostFunction.COST_FUNCTIONS[i];
            }

            int layerSize = Integer.parseInt(reader.readLine());

            NNetwork network = new NNetwork();
            for (int i = 0; i < layerSize; i++) {
                String line = reader.readLine();
                if(line.contains("DENSE"))network.add(Dense.createDense(line));
                if(line.contains("FLATTEN"))network.add(new Flatten());
                if(line.contains("CONVOLUTION"))network.add(Convolution.createConvo(line));
                if(line.contains("MAXPOOLING"))network.add(MaxPooling.createMP(line));
            }
            network.compile(costFunction,inputSize);
            return network;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Layer> getLayers() {
        return layers;
    }
    public Layer getLastLayer(){
        return layers.get(layers.size() - 1);
    }

    public Optimizer getOptimizer() {
        return optimizer;
    }
}
