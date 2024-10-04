package toolbox.maths;


import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Entity.Camera;



public class Maths {
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}
	
	public static Matrix4f createTransfromationMatrix(Vector3f translation,float rx,float ry,float rz,float scale) {
		Matrix4f matrix4f=new Matrix4f();
		matrix4f.setIdentity();
		Matrix4f.translate(translation,matrix4f,matrix4f);
		Matrix4f.rotate((float)Math.toRadians(rx), new Vector3f(1, 0, 0),matrix4f,matrix4f);
		Matrix4f.rotate((float)Math.toRadians(ry), new Vector3f(0, 1, 0),matrix4f,matrix4f);
		Matrix4f.rotate((float)Math.toRadians(rz), new Vector3f(0, 0,1),matrix4f,matrix4f);
		Matrix4f.scale(new Vector3f(scale, scale, scale),matrix4f,matrix4f);
		return matrix4f;
	}
	public static Matrix4f createTransfromationMatrix(Vector3f translation,float rx,float ry,float rz,float scaleX,float scaleY,float scaleZ) {
		Matrix4f matrix4f=new Matrix4f();
		matrix4f.setIdentity();

		Matrix4f.translate(translation,matrix4f,matrix4f);
	/*	matrix4f.m00=scaleX;
		matrix4f.m11 = scaleY;
		matrix4f.m22=scaleZ;*/
		Matrix4f.rotate((float)Math.toRadians(rz), new Vector3f(0, 0,1),matrix4f,matrix4f);
		Matrix4f.rotate((float)Math.toRadians(ry), new Vector3f(0, 1, 0),matrix4f,matrix4f);
		Matrix4f.rotate((float)Math.toRadians(rx), new Vector3f(1, 0, 0),matrix4f,matrix4f);
		matrix4f.m00*=scaleX;
		matrix4f.m01*=scaleX;
		matrix4f.m02*=scaleX;
		matrix4f.m10*=scaleY;
		matrix4f.m11*=scaleY;
		matrix4f.m12*=scaleY;
		matrix4f.m20*=scaleZ;
		matrix4f.m21*=scaleZ;
		matrix4f.m22*=scaleZ;
		return matrix4f;
	}
	public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0), viewMatrix,
                viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Vector3f cameraPos = camera.getPosition().getOglVec();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;
    }
	public static float barryCentric(Vector3 p1, Vector3 p2, Vector3 p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	public static Matrix3f createRotationMatrix(float rx,float ry,float rz) {
		double x=Math.toRadians(rx);
		double y=Math.toRadians(ry);
		double z=Math.toRadians(rz);
		Matrix3f r=new Matrix3f();
		r.m00=(float)(Math.cos(z)*Math.cos(y));
		r.m10=(float)(Math.cos(z)*Math.sin(y)*Math.sin(x)-Math.sin(z)*Math.cos(x));
		r.m20=(float)(Math.cos(z)*Math.sin(y)*Math.sin(x)+Math.sin(z)*Math.cos(x));
		
		r.m01=(float)(Math.sin(z)*Math.cos(y));
		r.m11=(float)(Math.sin(z)*Math.sin(y)*Math.sin(x)+Math.cos(z)*Math.cos(x));
		r.m21=(float)(Math.sin(z)*Math.sin(y)*Math.cos(x)-Math.cos(z)*Math.sin(x));
		
		r.m02=(float)(-Math.sin(y));
		r.m12=(float)(Math.cos(y)*Math.sin(x));
		r.m22=(float)(Math.cos(y)*Math.cos(x));
		return r;
	}
	public static Vector3f rotateVector(float x,float y,Vector3f vector3f) {
		
		Matrix4f matrix3f=new Matrix4f();

		matrix3f.setIdentity();
		Matrix4f.rotate((float)Math.toRadians(y), new Vector3f(0, 1, 0), matrix3f, matrix3f);
		Matrix4f.rotate((float)Math.toRadians(x), new Vector3f(1, 0, 0), matrix3f, matrix3f);
		
		Vector4f vector4f=Matrix4f.transform(matrix3f, new Vector4f(vector3f.x, vector3f.y, vector3f.z, 1), null);
	//	System.out.println(vector4f);
		return new Vector3f(-vector4f.x, -vector4f.y, vector4f.z);
	}
	public static Vector3f rotateVectorAAbb(float x,float y,Vector3f vector3f) {
		
		Matrix4f matrix3f=new Matrix4f();

		matrix3f.setIdentity();
		Matrix4f.rotate((float)Math.toRadians(y), new Vector3f(0, 1, 0), matrix3f, matrix3f);
		Matrix4f.rotate((float)Math.toRadians(x), new Vector3f(1, 0, 0), matrix3f, matrix3f);
		
		Vector4f vector4f=Matrix4f.transform(matrix3f, new Vector4f(vector3f.x, vector3f.y, vector3f.z, 1), null);
	//	System.out.println(vector4f);
		return new Vector3f(vector4f.x, vector4f.y, vector4f.z);
	}
	public static Vector3 lerp(Vector3 a,Vector3 b,float p){
		return a.getMul(p).getAdd(b.getMul(1-p));
	}
	public static Vector3 reflect(Vector3 I,Vector3 N,float absorptionCoeff){
		float coeff=-(1+absorptionCoeff)*N.dotProduct(I);

		return I.getAdd(N.getMul(coeff));
	}
}
