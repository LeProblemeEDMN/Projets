package PostProcessing.gaussianBlur;

import ShaderEngine.ShaderProgram;

public class VerticalBlurShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/PostProcessing/gaussianBlur/verticalBlurVertex.txt";
	private static final String FRAGMENT_FILE = "/PostProcessing/gaussianBlur/blurFragment.txt";
	
	private int location_targetHeight;
	
	protected VerticalBlurShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
	}
	
	protected void loadTargetHeight(float height){
		super.loadFloat(location_targetHeight, height);
	}

	@Override
	protected void getAllUniformLocations() {	
		location_targetHeight = super.getUniformlocation("targetHeight");
	
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
}
