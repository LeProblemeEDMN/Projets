package GPU;

import matrix.Matrix;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class GPUMatrix {
    protected static CLObject kernelMultiplication, kernelMulAdd;

    public static void init(){
        kernelMultiplication = new CLObject("src/GPU/matrixMul.cls","matrix_mul");
        kernelMultiplication.setDebug(false);

        kernelMulAdd = new CLObject("src/GPU/matrixMulAdd.cls","matrix_mul_add");
        kernelMulAdd.setDebug(false);
    }

    public static FloatBuffer matrixToBuffer(Matrix A){
        FloatBuffer a_values= BufferUtils.createFloatBuffer(A.getCol() * A.getLin());
        for (int i = 0; i < A.getLin(); i++) {
            a_values.put(A.getValues()[i]);
        }
        a_values.rewind();
        return a_values;
    }

    public static Matrix bufferToMatrix(FloatBuffer buffer, int l,int c){
        Matrix C = new Matrix(l,c);
        for (int i = 0; i < l; i++) {
            float[] line = new float[c];
            buffer.get(line, 0, c);
            C.getValues()[i] = line;
        }
        return C;
    }
    
    public static Matrix multiply(Matrix A, Matrix B){
        if(A.getCol()!=B.getLin()){
            System.err.println("A and B doesn't have the same size, multiplication of matrix impossible. return null");
            return null;
        }

        FloatBuffer a_values = matrixToBuffer(A);
        FloatBuffer b_values= matrixToBuffer(B);

        b_values.rewind();
        kernelMultiplication.setResult(A.getLin()*B.getCol(),8,2);
        kernelMultiplication.addEntry(a_values,0);
        kernelMultiplication.addEntry(b_values,1);
        kernelMultiplication.getMemoryInt().put(3,A.getLin());
        kernelMultiplication.getMemoryInt().put(4,B.getLin());
        kernelMultiplication.getMemoryInt().put(5,B.getCol());

        kernelMultiplication.execute_dim(A.getLin(),B.getCol());

        FloatBuffer output = kernelMultiplication.getWriteTo();
        Matrix C = bufferToMatrix(output,A.getLin(),B.getCol());
        return C;
    }

    public static Matrix multiplyAndAdd(Matrix A, Matrix B,Matrix C){
        if(A.getCol()!=B.getLin() || A.getLin()!=C.getLin() || B.getCol()!=C.getCol()){
            System.err.println("A and B and C doesn't have the same size, multiplication and add of matrix impossible. return null");
            return null;
        }

        FloatBuffer a_values = matrixToBuffer(A);
        FloatBuffer b_values= matrixToBuffer(B);
        FloatBuffer c_values= matrixToBuffer(C);

        kernelMulAdd.setResult(A.getLin()*B.getCol(),8,3);
        kernelMulAdd.addEntry(a_values,0);
        kernelMulAdd.addEntry(b_values,1);
        kernelMulAdd.addEntry(c_values,2);
        kernelMulAdd.getMemoryInt().put(4,A.getLin());
        kernelMulAdd.getMemoryInt().put(5,B.getLin());
        kernelMulAdd.getMemoryInt().put(6,B.getCol());

        kernelMulAdd.execute_dim(A.getLin(),B.getCol());

        FloatBuffer output = kernelMulAdd.getWriteTo();
        Matrix D = bufferToMatrix(output,A.getLin(),B.getCol());
        return D;
    }
}
