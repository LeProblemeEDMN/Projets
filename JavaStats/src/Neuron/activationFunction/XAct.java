package Neuron.activationFunction;

public class XAct extends ActivFunction{
    public static final XAct X_ACT = new XAct();

    public XAct() {
        super("XACT");
    }

    @Override
    public float activation(float x) {
        return x;
    }

    @Override
    public float dActivation(float x) {
        return 1;
    }
}
