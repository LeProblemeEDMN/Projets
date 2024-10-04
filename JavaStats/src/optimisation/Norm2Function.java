package optimisation;

public class Norm2Function extends Function{

    public Norm2Function() {
        this.nbVar=2;
    }

    @Override
    public double compute(double[] X) {
        return -Math.sqrt((1+X[0])*(1+X[0])+X[1]*X[1]);
    }
}
