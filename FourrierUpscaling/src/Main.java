import Matrix.Matrix;
import MatrixException.ArithmeticMatrixException;
import hyperVector.HyperVecHelper;
import hyperVector.HyperVector;
import upscaling.Upscaler;
import utils.DCT;
import utils.DCT2dim;
import utils.DCT3dim;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void print_mat(Matrix m){
        for (int i = 0; i < m.get_column(); i++) {
            System.out.println(Arrays.toString(m.values[i]));
        }
    }
    public static void main(String[] args ) throws IOException, ArithmeticMatrixException {
        double[]array={0,  0,  0,  0,  0,  0,  0,  0,
                50, 50, 50, 50, 50, 50, 50, 50,
                10, 10, 20, 30, 30, 20, 10, 10,
                5,  5, 10, 25, 25, 10,  5,  5,
                0,  0,  0, 20, 20,  0,  0,  0,
                5, -5,-10,  0,  0,-10, -5,  5,
                5, 10, 10,-20,-20, 10, 10,  5,
                0,  0,  0,  0,  0,  0,  0,  0};
        Matrix data=new Matrix(8,8);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                data.set_value(i,j,array[i*8+j]);
            }
        }
        print_mat(data);
        DCT2dim dct=new DCT2dim(8);
        dct.computeCoeffs(data.values);
        int error=0;
        for (int i = 0; i < 8; i++) {
            System.out.println();
            for (int j = 0; j < 8; j++) {
                int v=(int)Math.round(dct.getDCTValue(i,j));
                error+=(int)Math.abs(v-data.get_value(i,j));
                System.out.print(v+" ");
            }
        }
        System.out.println(error);
        Upscaler.upscale("res/orange.jpg","res/civ6_gath_upscale2.png",8,4,2);
        /*
        DCT3dim dim3=new DCT3dim(2);
        double[][][] X={{{2,8},{3,2}},{{0,1},{1,0}}};

        dim3.computeCoeffs(X);
        System.out.println(dim3.coeffs_to_HV());

        long t=System.currentTimeMillis();

        System.out.println((System.currentTimeMillis()-t)/1000);*/
    }
}
