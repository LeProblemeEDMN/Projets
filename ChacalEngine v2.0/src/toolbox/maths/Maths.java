package toolbox.maths;


import org.joml.*;


import entity.Camera;

import java.lang.Math;


public class Maths {
	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.identity();
		matrix.translate(translation.x,translation.y,0);
		matrix.scale(scale.x, scale.y, 1f);
		return matrix;
	}
	
	public static Matrix4f createTransfromationMatrix(Vector3f translation,float rx,float ry,float rz,Vector3 scale) {
		Matrix4f matrix4f=new Matrix4f();
		//matrix4f.identity();
		matrix4f.translate(translation);
		if(Math.abs(rx)>0.01f)
			matrix4f.rotate((float)Math.toRadians(rx),1, 0, 0);
		if(Math.abs(ry)>0.01f)
			matrix4f.rotate((float)Math.toRadians(ry), 0, 1, 0);
		if(Math.abs(rz)>0.01f)
			matrix4f.rotate((float)Math.toRadians(rz),0, 0,1);
		if(Math.abs(scale.x-1)>0.01f || Math.abs(scale.y-1)>0.01f || Math.abs(scale.z-1)>0.01f)
			matrix4f.scale(scale.x, scale.y, scale.z);
		return matrix4f;
	}
	public static Matrix4f rotation(Vector3 translation,float rx,float ry,float rz,float scale){
		rx=(float) Math.toRadians(rx)*0.5f;
		ry=(float) Math.toRadians(ry)*0.5f;
		rz=(float) Math.toRadians(rz)*0.5f;
		float s=(float) Math.sqrt(scale);
		//quaternion de la rotation rz.
		float w2=(float) Math.cos(rz)*s;
		float z2=(float) Math.sin(rz)*s;
		//quaternion de al rotation rx et ry
		float w1 =(float)(Math.cos(rx)*Math.cos(ry));
		float x1 =(float)(Math.sin(rx)*Math.cos(ry));
		float y1 =(float)(Math.cos(rx)*Math.sin(ry));
		float z1 =(float)(Math.sin(rx)*Math.sin(ry));
		//quaternion des trois rotations scale par scale.
		float w = w1*w2-z1*z2;
		float x = x1 * w2 + y1 * z2;
		float y = -x1 * z2 + y1 * w2;
		float z = w1 * z2 + z1 * w2;
		float xq=2*x*x;
		float yq=2*y*y;
		float zq=2*z*z;
		//crée la matrice a partir des quaternions
		Matrix4f transfo=new Matrix4f();
		transfo.m00(scale - yq - zq);
		transfo.m10(2*x*y - 2*z*w);
		transfo.m20(2*x*z + 2*y*w);

		transfo.m01(2*x*y + 2*z*w);
		transfo.m11(scale - xq - zq);
		transfo.m21(2*y*z - 2*x*w);

		transfo.m02(2*x*z - 2*y*w);
		transfo.m12(2*y*z + 2*x*w);
		transfo.m22(scale - xq - yq);

		transfo.m30(translation.x);
		transfo.m31(translation.y);
		transfo.m32(translation.z);
		return transfo;
	}
	public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
		viewMatrix.rotate((float)Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0))
				.rotate((float)Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0));
		// Then do the translation
		viewMatrix.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);
		//viewMatrix.invert();
        return viewMatrix;
    }
	public static Matrix4f createViewMatrix(Vector3 position,float pitch,float yaw,float roll) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.identity();
		viewMatrix.rotate((float)Math.toRadians(pitch), new Vector3f(1, 0, 0))
				.rotate((float)Math.toRadians(yaw), new Vector3f(0, 1, 0))
				.rotate((float)Math.toRadians(roll), new Vector3f(0, 0, 1));
		// Then do the translation
		viewMatrix.translate(-position.x, -position.y, -position.z);
		//viewMatrix.invert();
		return viewMatrix;
	}
	public static Matrix4f createViewMatrix2(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.identity();
		viewMatrix.translate(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);
		viewMatrix.rotate((float)Math.toRadians(-camera.getYaw()), new Vector3f(0, 1, 0)).rotate((float)Math.toRadians(-camera.getPitch()), new Vector3f(1, 0, 0));
		// Then do the translation


		return viewMatrix;
	}
	public static Matrix4f createViewMatrixParams(Vector3f translation,float rx,float ry,float rz) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.identity();
		viewMatrix.rotate((float)Math.toRadians(rx), new Vector3f(1, 0, 0))
				.rotate((float)Math.toRadians(ry), new Vector3f(0, 1, 0));
		// Then do the translation
		viewMatrix.translate(-translation.x, -translation.y, -translation.z);
		//viewMatrix.invert();
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

		matrix3f.identity();
		matrix3f.rotate((float)Math.toRadians(y),0,1,0);
		matrix3f.rotate((float)Math.toRadians(x), 1,0,0);
		Vector4f vector4f=matrix3f.transform( new Vector4f(vector3f.x, vector3f.y, vector3f.z, 1));
	//	System.out.println(vector4f);
		return new Vector3f(-vector4f.x, -vector4f.y, vector4f.z);
	}
	public static Vector3f rotateVectorAAbb(float x,float y,Vector3f vector3f) {

		Matrix4f matrix3f=new Matrix4f();

		matrix3f.identity();
		matrix3f.rotate((float)Math.toRadians(y),0,1,0);
		matrix3f.rotate((float)Math.toRadians(x), 1,0,0);
		
		Vector4f vector4f=matrix3f.transform( new Vector4f(vector3f.x, vector3f.y, vector3f.z, 1));
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
