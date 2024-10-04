package Matrix;

import MatrixException.ArithmeticMatrixException;

import java.util.Arrays;

public class Matrix {
    public double[][] values;
    private int r,c;

    public Matrix(double[][] values) {
        this.values = values;
        r = values.length;
        c = values[0].length;
    }

    public Matrix(int r, int c) {
        this.r = r;
        this.c = c;
        values = new double[r][c];
    }

    public double[][] get_values(){
        return values;
    }

    public double get_value(int r, int c){
        return values[r][c];
    }

    public void set_value(int r, int c, double v){
        values[r][c] = v;
    }

    public void add(Matrix b) throws ArithmeticMatrixException {
        if(b.get_column()!=this.get_column() || b.get_row() != this.get_row()) throw new ArithmeticMatrixException("Matrix of size "+this.get_row()+","+this.get_column()+" and size "+b.get_row()+","+b.get_column()+" can't be added");
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                values[i][j] += b.get_value(i,j);
            }
        }
    }

    public void sub(Matrix b) throws ArithmeticMatrixException {
        if(b.get_column()!=this.get_column() || b.get_row() != this.get_row()) throw new ArithmeticMatrixException("Matrix of size "+this.get_row()+","+this.get_column()+" and size "+b.get_row()+","+b.get_column()+" can't be substracted");
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                values[i][j] -= b.get_value(i,j);
            }
        }
    }

    public void mul(double b){
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                values[i][j] *= b;
            }
        }
    }

    private double[][] compute_mul(Matrix b) throws ArithmeticMatrixException{
        if(b.get_row()!=this.get_column()) throw new ArithmeticMatrixException("Matrix of size "+this.get_row()+","+this.get_column()+" and size "+b.get_row()+","+b.get_column()+" can't be multiplied");

        double[][] copy = new double[r][b.get_column()];
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < b.get_column(); j++) {
                for (int k = 0; k < c; k++) {
                    copy[i][j] += this.get_value(i,k) * b.get_value(k,j);
                }
            }
        }
        return copy;
    }

    public void mul(Matrix b) throws ArithmeticMatrixException {
        values = compute_mul(b);
        c = b.get_column();
    }

    public Matrix get_mul(Matrix b) throws ArithmeticMatrixException {
        return new Matrix(compute_mul(b));
    }

    public Matrix get_mul(double b)  {
        Matrix a = duplicate();
        a.mul(b);
        return a;
    }

    public Matrix get_add(Matrix b) throws ArithmeticMatrixException {
        Matrix a = duplicate();
        a.add(b);
        return a;
    }

    public Matrix get_sub(Matrix b) throws ArithmeticMatrixException {
        Matrix a = duplicate();
        a.sub(b);
        return a;
    }

    public Matrix transpose(){
        Matrix T=new Matrix(c,r);
        for (int i = 0; i < r; i++) {
            for (int j = 0; j < c; j++) {
                T.set_value(j,i,get_value(i,j));
            }
        }
        return T;
    }

    public Matrix duplicate(){
        double[][] copy = new double[r][c];
        for (int i = 0; i < r; i++) {
            copy[i] = Arrays.copyOf(values[i],c);
        }
        return new Matrix(copy);
    }



    public int get_row() {
        return r;
    }

    public int get_column() {
        return c;
    }
}
