package entity;

import main.ConfigLoader;
import main.DisplayManager;


import org.joml.Matrix4f;
import org.joml.Vector3f;
import toolbox.InputManager;
import toolbox.MouseBinding;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;
import static org.lwjgl.glfw.GLFW.*;
public class Camera {
	public static float FOV,NEAR_PLANE,FAR_PLANE;
	private Vector3 position=new Vector3(1, 1.8, 1);
	public Vector3 rotation=new Vector3(60, -40, 0);
	public Vector3 direction_view;//vecteur de la direction ou l'on regarde

	protected Matrix4f viewMatrix=new Matrix4f();
	protected Matrix4f projectionMatrix=new Matrix4f();
	public double[] lp=new double[16];
	public Camera(ConfigLoader configLoader) {
		FOV=configLoader.getFloatParameter("fov");
		FAR_PLANE=configLoader.getFloatParameter("far");
		NEAR_PLANE=configLoader.getFloatParameter("near");
		createProjectionMatrix();

	}
	public Camera() {
	}
	
	//float speed=30*DisplayManager.getFrameTimeSecond();
	
	public void update() {
		viewMatrix=Maths.createViewMatrix(this);
		computeDirection();
	}


	public void computeDirection(){
		direction_view=new Vector3(Maths.rotateVector(rotation.x,rotation.y,new Vector3f(0,0,-1)));
	}

	public boolean can_be_seen(Entity e){
		//test si l'objet englobe la camera ou si il est assez proche et bien dans le champ de vision
		float dx=e.position.x-this.position.x;
		float dy=e.position.y-this.position.y;
		float dz=e.position.z-this.position.z;
		return   (direction_view.x*dx+direction_view.y*dy+direction_view.z*dz>0 && dx*dx+dy*dy+dz*dz<e.renderingDistance*e.renderingDistance) || e.getAxisAlignedBB().isVecInside(this.position);
	}

	public Vector3 getForward(float speed) {
		Vector3 r=new Vector3();

		float cosY=(float)Math.cos(Math.toRadians(this.getYaw()+90));
		float sinY=(float)Math.sin(Math.toRadians(this.getYaw()+90));

		r.setX(cosY*speed);
		r.setZ(sinY*speed);
		
		return r;
	}
    
	public Vector3 getRight(float speed) {
		Vector3 r=new Vector3();	
		
		float cosY=(float)Math.cos(Math.toRadians(this.getYaw()));
		float sinY=(float)Math.sin(Math.toRadians(this.getYaw()));
		
		r.setX(cosY*speed);
		
		r.setZ(sinY*speed);
			
		return r;
	}
	
	public Vector3 getPosition() {
		return position;
	}
	public void setPosition(Vector3 position) {
		this.position = position;
	}
	public float getPitch() {
		return rotation.x;
	}
	public void setPitch(float pitch) {
		this.rotation.x = pitch;
	}
	public float getYaw() {
		return rotation.y;
	}
	public void setYaw(float yaw) {
		this.rotation.y= yaw;
	}
	public float getRoll() {
		return rotation.z;
	}
	public void setRoll(float roll) {
		this.rotation.z = roll;
	}
	public void invertPitch() {
		this.rotation.x= -this.rotation.x;
	}
	public void addY(float value) {
		this.position.y+=value;
	}
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

    public void createProjectionMatrix(){
    	projectionMatrix = new Matrix4f();
		int[] width = new int[1];
		int[] height = new int[1];
		glfwGetWindowSize(DisplayManager.window, width, height);
		float aspectRatio = (float)width[0] / (float) height[0];
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00( x_scale);
		projectionMatrix.m11(y_scale);
		projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustum_length));
		projectionMatrix.m23(-1);
		projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
		projectionMatrix.m33(0);

    }
    
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	public Matrix4f getProjectionViewMatrix() {
		return projectionMatrix.mul(projectionMatrix,viewMatrix);
	}
}
