package Neuron.activationFunction;

public class Sigmoid extends ActivFunction{
    public static final Sigmoid SIGMOID = new Sigmoid();

    public Sigmoid() {
        super("SIGMOID");
    }

    @Override
    public float activation(float x) {
        return 1/(float)(1+Math.exp(-x));
    }

    @Override
    public float dActivation(float x) {
        float a =activation(x);
        return a * (1 - a);
    }
}
