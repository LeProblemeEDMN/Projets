package stats;

import Jama.EigenvalueDecomposition;
import matrix.*;

public class ACP {

    private SquareMatrix weightMatrix =null;
    private SquareMatrix eigenVectorMatrix, invEigenVectorMatrix = null;
    private float[] percentageVarianceExplained;
    /*
        x tableau de matrices de la taille n * k avec n nb ind et k nb de variables
        inverse permet d'ecahnger les k et n (si sortie de normalizer par exemple)
     */
    public void fit(float[][] x,boolean inverse){
        Matrix X = new Matrix(x.length, x[0].length);
        X.setValues(x);

        if(inverse)X = MatrixHelper.transposeMatrix(X,null);

        //multiplie par al matrice des poids
        Matrix XW =null;

        if(weightMatrix != null){
            if(weightMatrix.getLin() != X.getCol()){
                System.err.println("The weight and individu matrix dont have the same numbers of lines and rows.");
            }
            XW = MatrixHelper.multiplyMatrix(X,weightMatrix, null);
        }else {
            XW = MatrixHelper.multiplyMatrix(X, 1/(float)X.getCol(), null);
        }

        //crée la matrices des corrélations C.
        Matrix M = MatrixHelper.multiplyMatrix(MatrixHelper.transposeMatrix(X,null),XW,null);
        SquareMatrix C = MatrixHelper.convertToSquare(M);
        float maxiC = 0;
        for (int i = 0; i < C.getCol(); i++) {
            for (int j = 0; j < C.getLin(); j++) {
                maxiC = Math.max(Math.abs(C.getValue(j,i)),maxiC);
            }
        }
        C = MatrixHelper.convertToSquare(MatrixHelper.multiplyMatrix(C, 1/maxiC, null));

        Jama.Matrix jamaC = new Jama.Matrix(C.getLin(), C.getCol());
        for (int i = 0; i < C.getLin(); i++) {
            for (int j = 0; j < C.getCol(); j++) {
                jamaC.set(i,j ,C.getValue(i, j));
            }
        }
        EigenvalueDecomposition e =jamaC.eig();

        eigenVectorMatrix = new SquareMatrix(C.getCol());
        for (int i = 0; i < C.getLin(); i++) {
            for (int j = 0; j < C.getCol(); j++) {
                eigenVectorMatrix.setValue(i,j ,(float)e.getV().get(i, j));
            }
        }

        invEigenVectorMatrix = MatrixHelper.inverseMatrix(eigenVectorMatrix, null);
        percentageVarianceExplained = new float[eigenVectorMatrix.getCol()];
        float totalEigenValue = 0;
        for (int i = 0; i < eigenVectorMatrix.getCol(); i++) totalEigenValue += Math.abs(e.getD().get(i, i));

        for (int i = 0; i < eigenVectorMatrix.getCol(); i++) percentageVarianceExplained[i] += Math.abs(e.getD().get(i, i))/totalEigenValue;


    }

    public Vector transform(Vector vector){
        return MatrixHelper.transform(invEigenVectorMatrix, vector, null);
    }

    public Matrix transform(Matrix ind){
        return MatrixHelper.multiplyMatrix(invEigenVectorMatrix, ind, null);
    }

    public Matrix transform(float[][] ind){
        Matrix indMatrix = new Matrix(ind.length, ind[0].length);
        indMatrix.setValues(ind);
        return MatrixHelper.multiplyMatrix(invEigenVectorMatrix, indMatrix, null);
    }

    public float[] getPercentageVarianceExplained() {
        return percentageVarianceExplained;
    }

    public SquareMatrix getEigenVectorMatrix() {
        return eigenVectorMatrix;
    }

    public SquareMatrix getInvEigenVectorMatrix() {
        return invEigenVectorMatrix;
    }
}
