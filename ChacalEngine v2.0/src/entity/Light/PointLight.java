package entity.Light;

import loading.CubeMap.CubeMap;
import toolbox.maths.Vector3;

public class PointLight extends Light{
	/*
	Point of light:
	TODO Point of light shadows
	 */

	public PointLight(Vector3 color, Vector3 attenuation, Vector3 position) {
		super(color, attenuation, position);
	}

}
