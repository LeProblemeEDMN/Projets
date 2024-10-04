package Entity.Light;

import Loader.CubeMap.CubeMap;
import toolbox.maths.Vector3;

public class PointLight extends Light{
	
	private CubeMap shadowMap;
	public PointLight(Vector3 color, Vector3 attenuation, Vector3 position,float far) {
		super(color, attenuation, position,far);
	}
	public CubeMap getShadowMap() {
		return shadowMap;
	}
	public void setShadowMap(CubeMap shadowMap) {
		this.shadowMap = shadowMap;
	}
}
