package utils;

public class DCT {
    public int n;
    public double[] coeffs;
    public double pulsation,T;

    public DCT(int n) {
        this.n = n;
        coeffs=new double[n+1];
    }

    public void computeCoeffs(double[] values){
        pulsation = 2*Math.PI/(double)values.length;
        T=values.length;
        double dx = 1.0/(double)values.length;

        coeffs[0]=0;
        for (int i = 0; i < values.length; i++) {
            coeffs[0] += dx*values[i];
        }
        coeffs[0]/=n;

        for (int i = 1; i <= n; i++) {
            coeffs[i]=0;
            for (int j = 0; j < values.length; j++) {
                coeffs[i] += dx*values[j]*FastCos.cos(pulsation*i*j);
            }
            coeffs[i]/=n;
            coeffs[i]*=2;
        }
    }

    public double getDCTValue(double x){
        double v=coeffs[0];
        for (int j = 1; j <= n; j++) {
             v += coeffs[j]*FastCos.cos(pulsation*j*x);
        }
        return v;
    }

}
