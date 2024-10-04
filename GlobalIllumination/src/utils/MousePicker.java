package utils;

import entity.Vector3;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;


public class MousePicker {

	public Vector3 pos;
	private Matrix4f invProjectionMatrix;
	private Matrix4f invViewMatrix;
	
	public MousePicker(Vector3 pos, Vector3 look) {

		this.pos=pos;
		Vector3 lookAt=look.getSub(pos);

		lookAt.normalize();
		Vector2f vector2f=new Vector2f(-lookAt.z, lookAt.x);
		float l=vector2f.length();
		if(l==0)vector2f.x=1;
		vector2f=vector2f.normalise(null);
		float angle=(float)Math.toDegrees(Math.acos(vector2f.x));
		if(vector2f.y<0) {
			angle=-angle;
		}
		Vector2f vert=new Vector2f(l,lookAt.y);
		vert=vert.normalise(null);
		float angleVert=(float)Math.toDegrees(Math.acos(vert.x));
		if(vert.y>0) {
			angleVert=-angleVert;
		}
		this.invProjectionMatrix=Matrix4f.invert(createProjectionMatrix(), null);
		invViewMatrix=Matrix4f.invert(createViewMatrix(angleVert,angle,new Vector3f(pos.x,pos.y,pos.z)), null);

	}

	
	public Vector3 calculateMouseRay(float x,float y) {
		Vector4f clipCoord=new Vector4f(x, y, -1, 1);
		Vector4f eyeCoord=toEyeSpace(clipCoord);
		//System.out.println(x+" "+y+" "+toWorldCoords(eyeCoord));
		return toWorldCoords(eyeCoord);
		
	}
	private Vector4f toEyeSpace(Vector4f clipCoord) {
		Vector4f eyeCoord=Matrix4f.transform(invProjectionMatrix, clipCoord, null);
		return new Vector4f(eyeCoord.x, eyeCoord.y, -1, 0);
	}
	private Vector3 toWorldCoords(Vector4f eyeSpace) {
		Vector4f rayWorld=Matrix4f.transform(invViewMatrix, eyeSpace, null);
		Vector3 ray=new Vector3(rayWorld.x, rayWorld.y, rayWorld.z);
		ray.normalize();
		return ray;
		
		
	}

	 public Matrix4f createProjectionMatrix(){
		 float FAR_PLANE=1000;
		 float NEAR_PLANE=0.1f;
		 float FOV=20;
	    	Matrix4f projectionMatrix = new Matrix4f();
			float aspectRatio = (float) Constants.WIDTH / (float) Constants.HEIGHT;
			float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
			float x_scale = y_scale / aspectRatio;
			float frustum_length = FAR_PLANE - NEAR_PLANE;

			projectionMatrix.m00 = x_scale;
			projectionMatrix.m11 = y_scale;
			projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
			projectionMatrix.m23 = -1;
			projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
			projectionMatrix.m33 = 0;
			return projectionMatrix;
	    }
	 public static Matrix4f createViewMatrix(float pitch,float yaw,Vector3f pos) {
	        Matrix4f viewMatrix = new Matrix4f();
	        viewMatrix.setIdentity();
	        Matrix4f.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0), viewMatrix,
	                viewMatrix);
	        Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);

	        Vector3f cameraPos = pos;
	        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
	        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
	        return viewMatrix;
	    }
}
