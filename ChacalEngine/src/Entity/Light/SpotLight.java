package Entity.Light;

import RenderEngine.Fbo;
import org.lwjgl.Sys;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import toolbox.maths.Maths;
import toolbox.maths.Vector3;

public class SpotLight extends Light {
	private Vector3 rotation,directionVector;
	private float angle;
	public Fbo fbo;
	private Matrix4f toShadowMapSpace=new Matrix4f();
	private Matrix4f viewMatrix=new Matrix4f();
	private Matrix4f projectionMatrix=new Matrix4f();
	private int shadowMap;

	public SpotLight(Vector3 color, Vector3 attenuation, Vector3 position, Vector3 rotation, float angle,float far) {
		super(color, attenuation, position,far);
		this.rotation = rotation;
		this.angle = angle;
		directionVector=new Vector3(Maths.rotateVector(rotation.x, rotation.y, new Vector3f(0, 0, -1)));
		System.out.println("v " +directionVector);
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
	public void setShadowMap(int shadowMap) {
		this.shadowMap = shadowMap;
	}

	long lasttime,newtime;
	private Vector3 newAtt=new Vector3(),lastAtt=new Vector3();
    public Vector3 getAttenuationCurrent() {
		if(System.currentTimeMillis()>newtime){
			lasttime=newtime;
			newtime= System.currentTimeMillis()+500+(int)(Math.random()*500);
			lastAtt=newAtt;
			newAtt=new Vector3(1,(0.5f+Math.random())*getAttenuation().y,(0.5f+Math.random())*getAttenuation().z);
		}
		float delta=(float)(System.currentTimeMillis()-lasttime)/(float)(newtime-lasttime);
		return lastAtt.getAdd(newAtt.getSub(lastAtt).mul(delta));
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
