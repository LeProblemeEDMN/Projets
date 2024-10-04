package PostProcessing.gaussianBlur;

import ShaderEngine.ShaderProgram;

public class HorizontalBlurShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/PostProcessing/gaussianBlur/horizontalBlurVertex.txt";
	private static final String FRAGMENT_FILE = "/PostProcessing/gaussianBlur/blurFragment.txt";
	
	private int location_targetWidth;
	
	protected HorizontalBlurShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
	}

	protected void loadTargetWidth(float width){
		super.loadFloat(location_targetWidth, width);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_targetWidth = super.getUniformlocation("targetWidth");
	}


	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
}
