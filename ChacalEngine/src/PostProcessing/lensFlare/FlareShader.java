package PostProcessing.lensFlare;

import org.lwjgl.util.vector.Vector4f;

import ShaderEngine.ShaderProgram;

/**
 * Sets up the shader program for the rendering the lens flare. It gets the
 * locations of the 3 uniform variables, links the "in_position" variable to
 * attribute 0 of the VAO, and connects the sampler uniform to texture unit 0.
 * 
 * @author Karl
 *
 */
public class FlareShader extends ShaderProgram {

	private static final String VERTEX_SHADER = "/PostProcessing/lensFlare/flareVertex.glsl";
	private static final String FRAGMENT_SHADER = "/PostProcessing/lensFlare/flareFragment.glsl";

	protected int brightness;
	protected int transform;

	private int flareTexture;

	public FlareShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
		getAllUniformLocations();
		connectTextureUnits();
	}
@Override
protected void getAllUniformLocations() {
	brightness=super.getUniformlocation("brightness");
	transform=super.getUniformlocation("transform");
	flareTexture=super.getUniformlocation("flareTexture");
}
	private void connectTextureUnits() {
		super.start();
		super.loadInt(flareTexture, 0);
		super.stop();
	}
public void loadBrightness(float v) {
	super.loadFloat(brightness, v);
}
public void loadTransform(Vector4f vec) {
	super.loadVector(transform, vec);
}
@Override
protected void bindAttributes() {
	// TODO Auto-generated method stub
	
}
}	
