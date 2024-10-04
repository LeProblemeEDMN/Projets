package Neuron.costFunction;


import matrix.Vector;

public class MSE extends  CostFunction{
    public static final MSE MSE = new MSE();

    public MSE() {
        super("MSE");
    }

    @Override
    public float loss(Vector y_pred, Vector y_real) {
        float cost = 0;
        for (int i = 0; i < y_pred.getLin(); i++) {
            cost += (float)Math.pow(y_pred.getValue(i,0) - y_real.getValue(i,0), 2) / y_pred.getLin();
        }
        return cost*0.5f;
    }

    @Override
    public Vector gradientLoss(Vector y_pred, Vector y_real) {
        Vector gradient = new Vector(y_pred.getLin());
        for (int i = 0; i < y_pred.getLin(); i++) {
            gradient.setValue(i, 0, y_pred.getValue(i, 0) - y_real.getValue(i, 0));
        }
        return gradient;
    }
}
