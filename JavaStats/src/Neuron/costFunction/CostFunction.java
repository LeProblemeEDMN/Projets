package Neuron.costFunction;


import matrix.Vector;

public abstract class CostFunction {
    public static final CostFunction[] COST_FUNCTIONS = {new MBCE(), new MSE()};

    protected CostFunction(String name) {
        this.name = name;
    }

    public abstract float loss(Vector y_pred, Vector y_real);
    public abstract Vector gradientLoss(Vector y_pred, Vector y_real);
    private final String name;

    public String getName() {
        return name;
    }

}
