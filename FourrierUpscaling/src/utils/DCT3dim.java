package utils;

import Matrix.Matrix;
import MatrixException.ArithmeticMatrixException;
import hyperVector.HyperVector;

public class DCT3dim {
    public int n;
    public double[][][] coeffs;
    public double pulsationX,TX,pulsationY,TY,TZ,pulsationZ;

    public DCT3dim(int n) {
        this.n = n;
        coeffs=new double[n][n][n];
    }

    public void computeCoeffs(double[][][] values) throws ArithmeticMatrixException {
        TX = values.length;
        TY = values[0].length;
        TZ = values[0][0].length;
        pulsationX = Math.PI / TX;
        pulsationY = Math.PI / TY;
        pulsationZ = Math.PI / TZ;
        //dx=dy=1
        //System.out.println(TX+" "+TY+" "+TZ+" "+n);
        //if(TX!=TY) {
            for (int i = 0; i < n; i++) {

                for (int k = 0; k < n; k++) {
                    for (int u = 0; u < n; u++) {
                        coeffs[i][k][u] = 0;

                        for (int j = 0; j < TX; j++) {
                            for (int l = 0; l < TY; l++) {
                                for (int m = 0; m < TZ; m++) {
                                    coeffs[i][k][u] += values[j][l][m] * FastCos.cos(pulsationX * i * (j + 0.5)) * FastCos.cos(pulsationY * k * (l + 0.5))* FastCos.cos(pulsationZ * u * (m + 0.5));

                                }
                            }

                        }
                        coeffs[i][k][u] /= TX * TY*TZ;

                        if (i != 0) coeffs[i][k][u] *= 2;
                        if (k != 0) coeffs[i][k][u] *= 2;
                        if (k != 0) coeffs[i][k][u] *= 2;
                    }
                }
            }
        //}

        //coeffs=DCTMatrix.get_DCT_Matrix((int)TX).get_mul(new Matrix(values).get_mul(DCTMatrix.get_DCT_Matrix((int)TY).transpose())).values;

    }
    public HyperVector coeffs_to_HV(){
        HyperVector vector = new HyperVector(new int[]{n,n,n});
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    vector.setValue((float)coeffs[i][j][k],i,j,k);
                }
            }
        }
        return vector;
    }
    /*public double getDCTValue(double x,double y) {
        double v = 0;
        for (int i = 0; i < coeffs.length; i++) {
            for (int k = 0; k < coeffs[0].length; k++) {
                v += coeffs[i][k] * FastCos.cos(pulsationX * i * x) * FastCos.cos(pulsationY * k * y);
            }
        }
        return v;
    }*/

}
