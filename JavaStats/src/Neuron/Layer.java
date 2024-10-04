package Neuron;

import datas_structures.Range;
import matrix.Matrix;
import matrix.Vector;

import java.nio.FloatBuffer;
import java.util.Random;

public abstract class Layer {
    private Matrix[] out = new Matrix[1];
    private FloatBuffer[] outBuffer = new FloatBuffer[1];
    private Matrix[] errorGradient = new Matrix[1];

    protected boolean iniated = false;
    private Layer previousLayer;

    public abstract void forwardPropagation();

    public abstract Matrix[] backwardPropagation(Matrix[] errorGradientNext);

    public abstract void updateLayer(float lr);

    public void init(Layer layer){
        previousLayer = layer;
    }

    public Layer getPreviousLayer() {
        return previousLayer;
    }

    public Matrix getOut() {
        return out[0];
    }

    public Matrix getErrorGradient() {
        return errorGradient[0];
    }

    public Matrix[] getErrorGradientArray() {
        return errorGradient;
    }

    public void setOut(Matrix out) {
        this.out[0] = out;
    }

    public void setErrorGradient(Matrix errorGradient) {
        this.errorGradient[0] = errorGradient;
    }

    public void setErrorGradient(Matrix[] errorGradient) {
        this.errorGradient = errorGradient;
    }

    public void setPreviousLayer(Layer previousLayer) {
        this.previousLayer = previousLayer;
    }

    private static Random random = new Random();

    public static Matrix randomMatrix(int l ,int c){
        Matrix rdmMat = new Matrix(l,c);
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < c; j++) {
                int nega = 2* random.nextInt(2) - 1;
                rdmMat.setValue(i, j, nega * (0.05f + random.nextFloat()));
            }
        }
        return rdmMat;
    }
    public Matrix[] getOutConv(){
        return out;
    }

    public void setOut(Matrix[] out) {
        this.out = out;
    }

    public abstract String save();

    public FloatBuffer[] getOutBuffer() {
        return outBuffer;
    }

    public void setOutBuffer(FloatBuffer[] outBuffer) {
        this.outBuffer = outBuffer;
    }
    public void setOutBuffer(FloatBuffer outBuffer) {
        this.outBuffer[0] = outBuffer;
    }
}
