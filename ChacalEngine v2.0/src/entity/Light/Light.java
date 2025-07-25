package entity.Light;



import toolbox.maths.MathHelper;
import toolbox.maths.Vector3;

public class Light {
	public static float MAX_ATTENUATION=5;
	//parameters of the light color of the light,
	// attenuation coefficients: attenuation=a.x+a.y*d+a.z*d².
	private Vector3 color,attenuation,position;
	//maximum range of the light. If the object is further away dont use this light
	private float maxRange=1000000;
	
	public Light(Vector3 color, Vector3 attenuation, Vector3 position) {
		super();
		this.color = color;
		this.attenuation = attenuation;
		this.position = position;
		//compute the maxRange by finding where attenuation=MAX_ATTENUATION.
		Vector3 v=MathHelper.getRacines(attenuation.z, attenuation.y, attenuation.x-MAX_ATTENUATION);

		if(v.x>0) {
			this.maxRange=v.z;
		}
		this.maxRange=Math.max(1000,this.maxRange);
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
