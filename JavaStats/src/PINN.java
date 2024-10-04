import Neuron.FirstLayer;
import Neuron.activationFunction.ActivFunction;
import Neuron.activationFunction.Sigmoid;
import Neuron.activationFunction.XAct;
import matrix.Matrix;
import matrix.Vector;

public class PINN {
    public Matrix bias;
    public Matrix weights;
    public ActivFunction activ=new XAct();
    public PINN(Matrix bias, Matrix weights) {
        this.bias = bias;
        this.weights = weights;
    }
    private Matrix sum,input;
    public Matrix forward(Matrix input){
        this.input=input;
        sum=weights.mul(input).add(bias);
        return activ.actLayer(sum);
    }
    private Matrix wd,bd,wcd,bcd;
    public Matrix[] backward(Matrix derivative,Matrix crossDerivative,Matrix computeCrossDerivative){
        Matrix errorGradientNext = derivative;
        //DErive première
        Matrix dAct = activ.dActLayer(sum);
        for (int i = 0; i < errorGradientNext.getLin(); i++) {
            errorGradientNext.setValue(i, 0, errorGradientNext.getValue(i, 0) * dAct.getValue(i, 0));
        }
        wd=new Matrix(weights.getLin(),weights.getCol());
        wcd=new Matrix(weights.getLin(),weights.getCol());
        bd=new Matrix(bias.getLin(),1);
        bcd=new Matrix(bias.getLin(),1);
        for (int i = 0; i < weights.getLin(); i++) {
            for (int j = 0; j < weights.getCol(); j++) {
                wd.setValue(i, j, errorGradientNext.getValue(i, 0) * input.getValue(j,0));
            }
            bd.setValue(i, 0, errorGradientNext.getValue(i, 0));
        }
        Matrix newErrG = new Vector(input.getLin());

        for (int i = 0; i < input.getLin(); i++) {
            float dValue = 0;
            for (int j = 0; j < input.getLin(); j++) {
                dValue += errorGradientNext.getValue(j, 0) * weights.getValue(j, i);
            }
            newErrG.setValue(i, 0, dValue);
        }



        Matrix dAct2 = activ.dActLayer2(sum);
        for (int i = 0; i < computeCrossDerivative.getLin(); i++) {
             crossDerivative.setValue(i, 0, crossDerivative.getValue(i, 0) * dAct2.getValue(i, 0));
        }
        //crossDerivative=crossDerivative.add()

        for (int i = 0; i < computeCrossDerivative.getLin(); i++) {
            computeCrossDerivative.setValue(i, 0, computeCrossDerivative.getValue(i, 0) * dAct2.getValue(i, 0));
        }

        }
    }
}
