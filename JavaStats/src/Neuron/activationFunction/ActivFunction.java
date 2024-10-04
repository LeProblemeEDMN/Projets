package Neuron.activationFunction;

import Neuron.costFunction.CostFunction;
import Neuron.costFunction.MBCE;
import Neuron.costFunction.MSE;
import matrix.Matrix;
import matrix.Vector;
import stats.ACPVisualiser;

public abstract class ActivFunction {

    public static final ActivFunction[] ACTIVAITON_FUNCTIONS = {new XAct(), new ReLu(),new Sigmoid()};

    private final String name;
    public abstract float activation(float x);
    public abstract float dActivation(float x);
    public abstract float dActivation2(float x);
    public ActivFunction(String name) {
        this.name = name;
    }

    public Matrix actLayer(Matrix sum){
        Matrix act = new Matrix(sum.getLin(),sum.getCol());
        for (int i = 0; i < sum.getLin(); i++) {
            for (int j = 0; j < sum.getCol(); j++) {
                act.setValue(i,j, activation(sum.getValue(i,j)));
            }

        }
        return act;
    }

    public Matrix dActLayer(Matrix sum){
        Matrix act = new Matrix(sum.getLin(),sum.getCol());
        for (int i = 0; i < sum.getLin(); i++) {
            for (int j = 0; j < sum.getCol(); j++) {
                act.setValue(i, j, dActivation(sum.getValue(i, j)));
            }
        }
        return act;
    }

    public Matrix dActLayer2(Matrix sum){
        Matrix act = new Matrix(sum.getLin(),sum.getCol());
        for (int i = 0; i < sum.getLin(); i++) {
            for (int j = 0; j < sum.getCol(); j++) {
                act.setValue(i, j, dActivation2(sum.getValue(i, j)));
            }
        }
        return act;
    }

    public String getName() {
        return name;
    }
}
