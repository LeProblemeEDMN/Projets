package Neuron.costFunction;

import matrix.Vector;

public class MBCE extends  CostFunction{
    //binary cross entropy
    public static final MBCE MBCE = new MBCE();

    private static final float ln10 =1;// (float)Math.log(10);

    public MBCE() {
        super("MBCE");
    }

    @Override
    public float loss(Vector y_pred, Vector y_real) {
        float cost = 0;
        for (int i = 0; i < y_pred.getLin(); i++) {


            float p = Math.min(Math.max(y_pred.getValue(i,0),0.0001f),0.9999f);
            p= (float)Math.log(p/(1-p));
            cost+=(Math.max(0,p) - p * y_real.getValue(i,0) + Math.log(1 + Math.exp(-Math.abs(p))))/ y_pred.getLin();

          /*  if (0.99999 > y_pred.getValue(i, 0) && y_pred.getValue(i, 0) > 0.00001)
                cost -= (y_real.getValue(i, 0) * (float) Math.log(y_pred.getValue(i, 0)) + (1 - y_real.getValue(i, 0)) * (float) Math.log(1 - y_pred.getValue(i, 0))) / y_pred.getLin();
            else if (y_pred.getValue(i, 0) >= 0.0001)
                cost -= y_real.getValue(i, 0) * (float) Math.log(y_pred.getValue(i, 0)) / y_pred.getLin();
            else cost -= (1 - y_real.getValue(i, 0)) * (float) Math.log(1 - y_pred.getValue(i, 0)) / y_pred.getLin();*/
        }
        return cost;
    }

    @Override
    public Vector gradientLoss(Vector y_pred, Vector y_real) {
        Vector gradient = new Vector(y_pred.getLin());
        for (int i = 0; i < y_pred.getLin(); i++) {
            float value = -y_real.getValue(i,0) / (y_pred.getValue(i, 0))  + (1 - y_real.getValue(i,0)) / ((1 - y_pred.getValue(i, 0)));
            if (y_pred.getValue(i, 0) <= 0.0001) value = (1 - y_real.getValue(i,0)) / (1 - y_pred.getValue(i, 0));
            else if(0.9999 <= y_pred.getValue(i, 0)) value = -y_real.getValue(i,0) /  y_pred.getValue(i, 0);
            gradient.setValue(i, 0, value);
        }
        return gradient;
    }
}
