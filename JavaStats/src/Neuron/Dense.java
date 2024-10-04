package Neuron;

import Neuron.activationFunction.ActivFunction;
import Neuron.convolution.Flatten;
import matrix.Matrix;
import matrix.Vector;

import java.nio.FloatBuffer;

public class Dense extends Layer{
    private Matrix weights;
    private Matrix bias;
    private FloatBuffer weightsBuffer;
    private FloatBuffer biasBuffer;

    private int number_neurons;
    private ActivFunction actFunction;

    private Matrix sum;

    private Matrix gradientWeights;
    private Matrix gradientBias;

    public Dense(int number_neurons, ActivFunction actFunction){
        this.number_neurons = number_neurons;
        this.actFunction = actFunction;
    }

    @Override
    public void init(Layer layer) {
        super.init(layer);
        int size = 0;

        if(layer instanceof Dense){
            Dense dense = (Dense)layer;
            size = dense.getNumber_neurons();
        }
        if(layer instanceof FirstLayer){
            FirstLayer dense = (FirstLayer)layer;
            size = dense.getSizeInput();
        }

        if(layer instanceof Flatten){
            Flatten flatten = (Flatten)layer;
            size = flatten.flattenSize;
        }
        if(!iniated) {
            weights = Layer.randomMatrix(number_neurons, size);
            bias = Layer.randomMatrix(number_neurons, 1);
        }

        size = weights.getCol();
        sum = new Matrix(number_neurons, 1);
        setErrorGradient(new Matrix(number_neurons, 1));
        setOut(new Matrix(number_neurons, 1));


        gradientWeights = new Matrix(number_neurons, size);
        gradientBias = new Matrix(number_neurons, 1);
        iniated = true;
    }

    @Override
    public void forwardPropagation() {
            sum = weights.mul(getPreviousLayer().getOut()).add(bias);
            setOut(actFunction.actLayer(sum));
    }

    @Override
    public Matrix[] backwardPropagation(Matrix[] errorGradientNexts) {
        Matrix errorGradientNext = errorGradientNexts[0];
        Matrix dAct = actFunction.dActLayer(sum);
        for (int i = 0; i < errorGradientNext.getLin(); i++) {
            errorGradientNext.setValue(i, 0, errorGradientNext.getValue(i, 0) * dAct.getValue(i, 0));
        }
        setErrorGradient(errorGradientNext);
        //update le gradient des neurones du layer
        for (int i = 0; i < weights.getLin(); i++) {
            for (int j = 0; j < weights.getCol(); j++) {
                gradientWeights.setValue(i, j, gradientWeights.getValue(i, j) + errorGradientNext.getValue(i, 0) * getPreviousLayer().getOut().getValue(j,0));
            }
            gradientBias.setValue(i, 0, gradientBias.getValue(i, 0) + errorGradientNext.getValue(i, 0));
        }
        if(!(getPreviousLayer() instanceof FirstLayer)) {
            //prepare le gradient des sorties des neurone de la couche suivante.
            Matrix newErrG = new Vector(getPreviousLayer().getErrorGradient().getLin());

            for (int i = 0; i < getPreviousLayer().getErrorGradient().getLin(); i++) {
                float dValue = 0;
                for (int j = 0; j < getErrorGradient().getLin(); j++) {
                    dValue += errorGradientNext.getValue(j, 0) * weights.getValue(j, i);
                }
                newErrG.setValue(i, 0, dValue);
            }
            Matrix[]result = {newErrG};
            return result;
        }
        return null;
    }

    public void updateLayer(float lr){


        weights = weights.add(gradientWeights.mul(lr));
        bias = bias.add(gradientBias.mul(lr));

        gradientWeights = new Matrix(weights.getLin(), weights.getCol());
        gradientBias = new Matrix(bias.getLin(), bias.getCol());
    }

    public int getNumber_neurons() {
        return number_neurons;
    }

    public Matrix getWeights() {
        return weights;
    }

    public void setWeights(Matrix weights) {
        this.weights = weights;
    }

    public void setBias(Matrix bias) {
        this.bias = bias;
    }

    public Matrix getBias() {
        return bias;
    }

    @Override
    public String save() {
        String line ="DENSE "+actFunction.getName()+" "+number_neurons+" "+weights.getCol();
        for (int i = 0; i < weights.getLin(); i++) {
            for (int j = 0; j < weights.getCol(); j++) {
                line += " "+weights.getValue(i,j);
            }
        }
        for (int i = 0; i < bias.getLin(); i++) {
            line+=" "+bias.getValue(i,0);
        }
        return line;
    }
    public static Dense createDense(String line){
        String[] split = line.split(" ");

        ActivFunction actFunction = null;
        for (int i = 0; i < ActivFunction.ACTIVAITON_FUNCTIONS.length; i++)
            if(split[1].equals( ActivFunction.ACTIVAITON_FUNCTIONS[i].getName())) actFunction = ActivFunction.ACTIVAITON_FUNCTIONS[i];

        int number_neuron = Integer.parseInt(split[2]);
        Dense dense = new Dense(number_neuron,actFunction);
        dense.iniated = true;
        int weigths_col = Integer.parseInt(split[3]);

        Matrix weights = new Matrix(number_neuron, weigths_col);
        int compteur = 4;
        for (int i = 0; i < weights.getLin(); i++) {
            for (int j = 0; j < weights.getCol(); j++) {
                weights.setValue(i, j, Float.parseFloat(split[compteur]));
                compteur++;
            }
        }
        Matrix bias = new Matrix(number_neuron, 1);
        for (int i = 0; i < bias.getLin(); i++) {
            bias.setValue(i, 0, Float.parseFloat(split[compteur]));
            compteur++;
        }

        dense.setWeights(weights);
        dense.setBias(bias);
        return dense;
    }
}
