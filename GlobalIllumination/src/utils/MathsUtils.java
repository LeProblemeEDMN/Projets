package utils;

import entity.Droite;
import entity.Vector3;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.util.Random;

public class MathsUtils {
    private static Random RDM = new Random();


    public static Vector3 randomVector(float scale){
        Vector3 a = new Vector3(RDM.nextFloat(),RDM.nextFloat(),RDM.nextFloat());
        a.mul(scale/a.length());
        return a;
    }

    public static Droite newRay(Vector3 ori, Vector3 dir){
        return new Droite(ori.getAdd(dir.getMul(0.001f)),new Vector3(dir));
    }

    public static Vector3 reflect(Vector3 I,Vector3 N){
        float dot = I.dotProduct(N);
        return I.sub(N.getMul(2*dot));
    }

    public static Vector3[] createOrthonormalBasis(Vector3 A,Vector3 B){
        A = A.getNormalize();
        float dot = A.dotProduct(B);
        B=B.getSub(A.getMul(dot));
        B.normalize();
        Vector3 C = A.crossProduct(B);
        return new Vector3[]{A,B,C};
    }
    public static Vector3 changeBasis(Vector3[] basis,Vector3 vector){
        return basis[0].getMul(vector.x).add(basis[1].getMul(vector.y)).add(basis[2].getMul(vector.z));
    }
    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        Matrix4f matrix4f=new Matrix4f();
        matrix4f.setIdentity();
        Matrix4f.translate(translation,matrix4f,matrix4f);
        Matrix4f.rotate((float)Math.toRadians(rx), new Vector3f(1, 0, 0),matrix4f,matrix4f);
        Matrix4f.rotate((float)Math.toRadians(ry), new Vector3f(0, 1, 0),matrix4f,matrix4f);
        Matrix4f.rotate((float)Math.toRadians(rz), new Vector3f(0, 0,1),matrix4f,matrix4f);
        Matrix4f.scale(new Vector3f(scale, scale, scale),matrix4f,matrix4f);
        return matrix4f;
    }
}
