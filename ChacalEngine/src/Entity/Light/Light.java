package Entity.Light;



import toolbox.maths.MathHelper;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

public class Light {
	public static float MAX_ATTENUATION=5;
	private Vector3 color,attenuation,position;
	private float maxRange=1000000;
	
	public Light(Vector3 color, Vector3 attenuation, Vector3 position,float far) {
		super();
		this.color = color;
		this.attenuation = attenuation;
		this.position = position;
		Vector3 v=MathHelper.getRacines(attenuation.z, attenuation.y, attenuation.x-MAX_ATTENUATION);
		if(v.x>0) {
			this.maxRange=v.z;
		}
		//this.maxRange=far;
	}

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	public Vector3 getColor() {
		return color;
	}

	public void setColor(Vector3 color) {
		this.color = color;
	}

	public Vector3 getAttenuation() {
		return attenuation;
	}

	public void setAttenuation(Vector3 attenuation) {
		this.attenuation = attenuation;
	}
	public float getMaxRange() {
		return maxRange;
	}
	public void setMaxRange(float maxRange) {
		this.maxRange = maxRange;
	}
	
	
}
