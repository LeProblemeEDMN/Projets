package toolbox;


import main.DisplayManager;
import org.joml.*;

import entity.Camera;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

public class MousePicker {
	private Vector3 currentRay=new Vector3(0,0,-1);//initial guess

	private Matrix4f invProjectionMatrix;
	private Matrix4f invViewMatrix;
	private Camera camera;
	
	public MousePicker(Camera camera,Matrix4f projection) {
		super();
		this.camera = camera;
		this.invProjectionMatrix=new Matrix4f(projection).invert();
		
	}
	
	public void update() {
		invViewMatrix=Maths.createViewMatrix(camera).invert();
		currentRay=calculateMouseRay();
		
	}
	
	private Vector3 calculateMouseRay() {
		Vector2f normalizedCoord=getNormalizedDeviceCoord();
		Vector4f clipCoord=new Vector4f(normalizedCoord.x, normalizedCoord.y, -1, 1);
		Vector4f eyeCoord=toEyeSpace(clipCoord);
		//System.out.println(eyeCoord);
		return toWorldCoords(eyeCoord);
		
	}
	private Vector4f toEyeSpace(Vector4f clipCoord) {
		Vector4f eyeCoord=invProjectionMatrix.transform(clipCoord);
		return new Vector4f(eyeCoord.x, eyeCoord.y, -1, 0);
	}
	private Vector3 toWorldCoords(Vector4f eyeSpace) {
		Vector4f rayWorld=invViewMatrix.transform(eyeSpace);
		Vector3 ray=new Vector3(rayWorld.x, rayWorld.y, rayWorld.z);
		ray.normalize();
		return ray;
		
		
	}
	
	private Vector2f getNormalizedDeviceCoord() {
		double[] xpos = new double[1];
		double[] ypos = new double[1];
		glfwGetCursorPos(DisplayManager.window, xpos, ypos);
		float x=(float) xpos[0];
		float y=(float) ypos[0];
		x=(2*x)/ DisplayManager.getWidth()-1;
		y=-(2*y)/ DisplayManager.getHeight()+1;
		return new Vector2f(x,y);
		
	}
	
	public Vector3 getCurrentRay() {
		return currentRay;
	}
	public void resize(){
		this.invProjectionMatrix=new Matrix4f(camera.getProjectionMatrix()).invert();
	}
}
