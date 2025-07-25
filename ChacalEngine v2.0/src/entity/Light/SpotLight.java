package entity.Light;



import org.joml.Matrix4f;
import org.joml.Vector3f;
import rendering.capture_object.Fbo;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

public class SpotLight extends Light {
	//raotation of the light and direction of the vector
	private Vector3 rotation,directionVector;
	private float angle;//in degree

	//shadow map parameters
	public Fbo fbo;
	private Matrix4f toShadowMapSpace=new Matrix4f();
	private Matrix4f viewMatrix=new Matrix4f();
	private Matrix4f projectionMatrix=new Matrix4f();


	public SpotLight(Vector3 color, Vector3 attenuation, Vector3 position, Vector3 rotation, float angle) {
		super(color, attenuation, position);
		this.rotation = rotation;
		this.angle = angle;//in degree
		directionVector=new Vector3(Maths.rotateVector(rotation.x, rotation.y, new Vector3f(0, 0, -1)));

	}
	public Vector3 getRotation() {
		return rotation;
	}
	public void setRotation(Vector3 rotation) {
		this.rotation = rotation;
		directionVector=new Vector3(Maths.rotateVector(rotation.x, rotation.y, new Vector3f(0, 0, -1)));
	}
	public Vector3 getDirectionVector() {
		return directionVector;
	}
	public void setDirectionVector(Vector3 directionVecotor) {
		this.directionVector = directionVecotor;
	}
	public float getAngle() {
		return angle;
	}
	public void setAngle(float angle) {
		this.angle = angle;
	}
	public Matrix4f getToShadowMapSpace() {
		return toShadowMapSpace;
	}
	public void setToShadowMapSpace(Matrix4f toShadowMapSpace) {
		this.toShadowMapSpace = toShadowMapSpace;
	}
	public int getShadowMap() {
		return fbo.getColourTexture();
	}

	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	public void setViewMatrix(Matrix4f viewMatrix) {
		this.viewMatrix = viewMatrix;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public void setProjectionMatrix(Matrix4f projectionMatrix) {
		this.projectionMatrix = projectionMatrix;
	}
}
