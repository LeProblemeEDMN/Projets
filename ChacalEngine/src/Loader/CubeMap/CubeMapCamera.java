package Loader.CubeMap;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Entity.Camera;
import toolbox.maths.Maths;

public class CubeMapCamera extends Camera {

	public static float NEAR = 0.1f;
	public static float FAR = 200f;
	public static final float FOV_CUBEMAP = 90;// don't change!
	public static final float ASPECT_RATIO = 1;

	private final Vector3f center;
	

	private Matrix4f pnMatrix = new Matrix4f();
	private Matrix4f vMatrix = new Matrix4f();
	private Matrix4f projectionViewMatrix = new Matrix4f();

	public CubeMapCamera(Vector3f center) {
		super();
		this.center = center;
		createProjectionMatrixCubeMap();
	}
	public float pitch,yaw;
	public void switchToFace(int faceIndex) {
		switch (faceIndex) {
		case 0:
			pitch = 0;
			yaw = 90;
			break;
		case 1:
			pitch = 0;
			yaw = -90;
			break;
		case 2:
			pitch = -90;
			yaw = 180;
			break;
		case 3:
			pitch = 90;
			yaw = 180;
			break;
		case 4:
			pitch = 0;
			yaw = 180;
			break;
		case 5:
			pitch = 0;
			yaw = 0;
			break;
		}
		//System.out.println("p="+pitch+" y="+yaw);
		updateViewMatrix();
		
	}


	public Matrix4f getViewMatrix() {
		return vMatrix;
	}



	public Matrix4f getProjectionMatrix() {
		return pnMatrix;
	}

	public Matrix4f getProjectionViewMatrix() {
		return projectionViewMatrix;
	}

	private void createProjectionMatrixCubeMap() {
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV_CUBEMAP / 2f))));
		float x_scale = y_scale / ASPECT_RATIO;
		float frustum_length = FAR - NEAR;

		pnMatrix.m00 = x_scale;
		pnMatrix.m11 = y_scale;
		pnMatrix.m22 = -((FAR + NEAR) / frustum_length);
		pnMatrix.m23 = -1;
		pnMatrix.m32 = -((2 * NEAR * FAR) / frustum_length);
		pnMatrix.m33 = 0;
	}

	private void updateViewMatrix() {
		vMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(180), new Vector3f(0, 0, 1), vMatrix, vMatrix);
		Matrix4f.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0), vMatrix, vMatrix);
		Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0), vMatrix, vMatrix);
		Vector3f negativeCameraPos = new Vector3f(-center.x, -center.y, -center.z);
		Matrix4f.translate(negativeCameraPos, vMatrix, vMatrix);

		Matrix4f.mul(pnMatrix, vMatrix, projectionViewMatrix);
	}
public Matrix4f getPnMatrix() {
	return pnMatrix;
}
public Matrix4f getvMatrix() {
	return vMatrix;
}
}
