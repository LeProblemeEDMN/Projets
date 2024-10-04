package utils;

import Matrix.Matrix;
import MatrixException.ArithmeticMatrixException;

public class DCT2dim {
    public int n;
    public double[][] coeffs;
    public double pulsationX,TX,pulsationY,TY;

    public DCT2dim(int n) {
        this.n = n;
        coeffs=new double[n][n];
    }

    public void computeCoeffs(double[][] values) throws ArithmeticMatrixException {
        TX = values.length;
        TY = values[0].length;
        pulsationX = Math.PI / TX;
        pulsationY = Math.PI / TY;
        //dx=dy=1
        /*if(TX!=TY){
            for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                coeffs[i][k] = 0;
                for (int j = 0; j < TX; j++) {
                    for (int l = 0; l < TY; l++) {
                        coeffs[i][k] += values[j][l] * FastCos.cos(pulsationX * i * (j+0.5))* FastCos.cos(pulsationY * k * (l+0.5));

                    }

                }
                coeffs[i][k] /= TX*TY;

                if(i!=0)coeffs[i][k] *= 2;
                if(k!=0)coeffs[i][k] *= 2;
            }
        }
        }else*/

        coeffs=DCTMatrix.get_DCT_Matrix((int)TX).get_mul(new Matrix(values).get_mul(DCTMatrix.get_DCT_Matrix((int)TY).transpose())).values;

    }

    public double getDCTValue(double x,double y) {
        double v = 0;
        for (int i = 0; i < coeffs.length; i++) {
            for (int k = 0; k < coeffs[0].length; k++) {
                v += coeffs[i][k] * FastCos.cos(pulsationX * i * x) * FastCos.cos(pulsationY * k * y);
            }
        }
        return v;
    }

}
