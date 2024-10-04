package toolbox;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import Entity.Camera;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

public class MousePicker {
	private Vector3 currentRay;
	
	private Matrix4f invProjectionMatrix;
	private Matrix4f invViewMatrix;
	private Camera camera;
	
	public MousePicker(Camera camera,Matrix4f projection) {
		super();
		this.camera = camera;
		this.invProjectionMatrix=Matrix4f.invert(projection, null);
		
	}
	
	public void update() {
		invViewMatrix=Matrix4f.invert(Maths.createViewMatrix(camera), null);
		currentRay=calculateMouseRay();
		
	}
	
	private Vector3 calculateMouseRay() {
		Vector2f normalizedCoord=getNormalizedDeviceCoord();
		Vector4f clipCoord=new Vector4f(normalizedCoord.x, normalizedCoord.y, -1, 1);
		Vector4f eyeCoord=toEyeSpace(clipCoord);
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
	
	private Vector2f getNormalizedDeviceCoord() {
		float x=Mouse.getX();
		float y=Mouse.getY();
		x=(2*x)/Display.getWidth()-1;
		y=(2*y)/Display.getHeight()-1;
		return new Vector2f(x,y);
		
	}
	
	public Vector3 getCurrentRay() {
		return currentRay;
	}
	
}
