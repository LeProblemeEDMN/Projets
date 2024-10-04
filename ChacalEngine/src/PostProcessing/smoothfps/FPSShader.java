package PostProcessing.smoothfps;

import ShaderEngine.ShaderProgram;
import org.lwjgl.util.vector.Vector2f;

public class FPSShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/PostProcessing/smoothfps/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "/PostProcessing/smoothfps/combineFragment.txt";
	
	private int location_colourTexture;
	private int location_colourTexture2;
	private int location_delta;

	public FPSShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_colourTexture = super.getUniformlocation("colourTexture");
		location_colourTexture2=super.getUniformlocation("colourTexture2");
		location_delta=super.getUniformlocation("delta");
	}
	
	protected void connectTextureUnits(){
		super.loadInt(location_colourTexture, 0);
		super.loadInt(location_colourTexture2, 1);
		
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	public void loadDelta(float exp) {
		super.loadFloat(location_delta, exp);
		
	}
	

}
