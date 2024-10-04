package Entity;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;

import Loader.ConfigLoader;
import RenderEngine.DisplayManager;
import toolbox.InputManager;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

public class Camera {
	public static float FOV,NEAR_PLANE,FAR_PLANE;
	private Vector3 position=new Vector3(-70, 0, 0);
	protected Vector3 rotation=new Vector3(0, 0, 0);
	
	protected Matrix4f viewMatrix=new Matrix4f();
	protected Matrix4f projectionMatrix=new Matrix4f();
	
	public Camera(ConfigLoader configLoader) {
		FOV=configLoader.getFloatParameter("fov");
		FAR_PLANE=configLoader.getFloatParameter("far");
		NEAR_PLANE=configLoader.getFloatParameter("near");
		createProjectionMatrix();
		System.out.println(projectionMatrix);
		System.out.println(Matrix4f.invert(projectionMatrix,null));
	}
	public Camera() {
	}
	
	//float speed=30*DisplayManager.getFrameTimeSecond();
	
	public void update() {
		viewMatrix=Maths.createViewMatrix(this);
		
		if(InputManager.escape.isPressed())Mouse.setGrabbed(false);
		if(InputManager.grab.isPressed()) Mouse.setGrabbed(true);
		if(Mouse.isGrabbed()) {
			this.rotation.x=this.rotation.x-Mouse.getDY();
			this.rotation.y=this.rotation.y+Mouse.getDX();
			if(this.rotation.x>90) this.rotation.x=90;	
			if(this.rotation.x<-90) this.rotation.x=-90;
			float vit=80*DisplayManager.getFrameTimeSecond();
			if(InputManager.up.isPressed()) {
				position.add(getForward(-vit));
			}
			if(InputManager.down.isPressed()) {
				position.add(getForward(vit));
			}
			if(InputManager.right.isPressed()) {
				position.add(getRight(vit));
			}
			if(InputManager.left.isPressed()) {
				position.add(getRight(-vit));
			}
			if(InputManager.jump.isPressed()) {
				position.y+=vit;
			}
			if(InputManager.sneak.isPressed()) {
				position.y-=vit;
			}
	
		}
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
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
    }
    
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}
	public Matrix4f getProjectionViewMatrix() {
		return Matrix4f.mul(projectionMatrix,viewMatrix,null);
	}
}
