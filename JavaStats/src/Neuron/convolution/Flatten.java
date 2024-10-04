package Neuron.convolution;

import Neuron.Dense;
import Neuron.Layer;
import matrix.Matrix;
import matrix.Vector;

public class Flatten extends Layer {
    public int flattenSize;

    @Override
    public void init(Layer layer) {
        super.init(layer);
        if(layer instanceof Convolution) {
            Convolution convolution = (Convolution) layer;
            flattenSize = convolution.getSx() * convolution.getSy() * convolution.getNumberFilters();
        }
        if(layer instanceof MaxPooling) {
            MaxPooling convolution = (MaxPooling) layer;
            flattenSize = convolution.getWidth() * convolution.getHeight() * convolution.getNumberFilter();
        }
        setErrorGradient(new Vector(flattenSize));
    }

    @Override
    public void forwardPropagation() {
        Matrix[] inputs = getPreviousLayer().getOutConv();
        int sizeMat = inputs[0].getCol() * inputs[0].getLin();
        int sCol = inputs[0].getCol();
        Vector result = new Vector(flattenSize);

        for (int i = 0; i < inputs.length; i++) {
            Matrix mat = inputs[i];
            for (int j = 0; j < mat.getLin(); j++) {
                for (int k = 0; k < mat.getCol(); k++) {
                    result.setValue(i * sizeMat + j * sCol + k,0, mat.getValue(j, k));
                }
            }
        }
        setOut(result);
    }

    @Override
    public Matrix[] backwardPropagation(Matrix[] errorGradientNext) {
        Matrix error = errorGradientNext[0];
        Matrix[] result = new Matrix[getPreviousLayer().getOutConv().length];
        int sizeMat = getPreviousLayer().getOut().getLin() * getPreviousLayer().getOut().getCol();
        int sCol = getPreviousLayer().getOut().getCol();
        for (int i = 0; i < result.length; i++) {
            Matrix mat = new Matrix(getPreviousLayer().getOut().getCol(), getPreviousLayer().getOut().getCol());
            for (int j = 0; j < mat.getCol(); j++) {
                for (int k = 0; k < mat.getLin(); k++) {
                    mat.setValue(j,k, error.getValue(i*sizeMat + j * sCol + k, 0));
                }
            }
            result[i] = mat;
        }

        return result;
    }

    @Override
    public void updateLayer(float lr) {

    }

    @Override
    public String save() {
        return "FLATTEN";
    }


}
