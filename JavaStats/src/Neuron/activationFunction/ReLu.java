package Neuron.activationFunction;

public class ReLu extends  ActivFunction{
    public static final ReLu RELU = new ReLu();

    public ReLu() {
        super("RELU");
    }

    @Override
    public float activation(float x) {
        return Math.max(0, x);
    }

    @Override
    public float dActivation(float x) {
        return x > 0? 1 : 0;
    }
}
