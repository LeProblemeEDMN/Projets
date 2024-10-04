package utils;

import Matrix.Matrix;

import java.util.HashMap;

public class DCTMatrix {
    public static HashMap<Integer,Matrix>maps=new HashMap<Integer, Matrix>();
    public static Matrix get_DCT_Matrix(int N){
        if(maps.containsKey(N)){
            //return create_DCT_matrix(N);
            return maps.get(N);
        }else{
           Matrix M =create_DCT_matrix(N);
           maps.put(N,M);
           return M;
        }
    }

    public static Matrix create_DCT_matrix(int N){
        Matrix D=new Matrix(N,N);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                D.set_value(i,j,Math.cos(Math.PI/N*i*(j+0.5))*(i==0?0.5:1));
            }
        }
        D.mul(2.0/N);
        return D;
    }
}
