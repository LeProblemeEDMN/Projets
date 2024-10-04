package optimisation;

import java.util.Arrays;

public class GradOptimizer {
    //optimize sur Rn
    public static double[] maximise(Function f,double seuil){
        int s=f.nbVar;
        double[] X=new double[s];
        double lambda=0.1;
        double eps=lambda/100;
        double lengthD=100;

        while (lengthD>seuil*seuil){
            double[] dS=grad(X,f,eps);
            lengthD=0;
            for (int i = 0; i < f.nbVar; i++) {
                lengthD+=dS[i]*dS[i];
                X[i]+=lambda*dS[i];
            }

        }
        return X;
    }

    private static double[] grad(double[] X,Function f,double eps){
        double[] dX=new double[X.length];
        for (int i = 0; i < X.length; i++) {
            X[i]+=eps/2;
            double vp= f.compute(X);
            X[i]-=eps;
            double vm= f.compute(X);
            X[i]+=eps/2;
            dX[i]=(vp-vm)/eps;
        }
        return dX;
    }
}
