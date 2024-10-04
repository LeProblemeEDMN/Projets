package Neuron;

import matrix.Matrix;

public class FirstLayer extends Layer{

    private Matrix[] input;
    private int[] sizeInput;

    @Override
    public void forwardPropagation() {
        setOut(input);
    }

    @Override
    public Matrix[] backwardPropagation(Matrix[] errorGradientNext) {
        return null;
    }

    @Override
    public void updateLayer(float lr) {

    }

    public void setInput(Matrix[] input) {
        this.input = input;
    }

    public Matrix[] getInput() {
        return input;
    }

    public int getSizeInput() {
        return sizeInput[0];
    }
    public int[] getSizeInputArray() {
        return sizeInput;
    }

    public void setSizeInput(int sizeInput) {
        this.sizeInput = new int[1];
        this.sizeInput[0] = sizeInput;
    }
    public void setSizeInput(int[] sizeInput) {
        if(sizeInput.length ==3 )setOut(new Matrix[sizeInput[2]]);
        this.sizeInput = sizeInput;
    }

    @Override
    public String save() {
        return null;
    }
}
