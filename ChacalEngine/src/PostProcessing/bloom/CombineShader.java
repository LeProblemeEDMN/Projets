package PostProcessing.bloom;

import ShaderEngine.ShaderProgram;
import org.lwjgl.util.vector.Vector2f;

public class CombineShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/PostProcessing/bloom/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "/PostProcessing/bloom/combineFragment.txt";

	private int location_colourTexture;
	private int location_scatteringTexture;
	private int location_exposure;
	private int location_contrast;
	private int location_godRay;
	private int location_move;
	protected CombineShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_colourTexture = super.getUniformlocation("colourTexture");
		location_scatteringTexture = super.getUniformlocation("scatteringTexture");
		location_contrast=super.getUniformlocation("contrast");
		location_exposure=super.getUniformlocation("exposure");
		location_godRay=super.getUniformlocation("godRay");
		location_move=super.getUniformlocation("move");
	}
	
	protected void connectTextureUnits(){
		super.loadInt(location_colourTexture, 0);
		super.loadInt(location_godRay, 1);
		super.loadInt(location_scatteringTexture, 2);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	public void loadExposure(float exp) {
		super.loadFloat(location_exposure, exp);
		
	}
	
	public void loadContrast(float c) {
		super.loadFloat(location_contrast, c);
		
	}
	public void loadMove(Vector2f c) {
		super.load2DVector(location_move, c);

	}
}
