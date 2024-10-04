package PostProcessing.DLSS;

import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderAttribArray;
import ShaderEngine.ShaderProgram;

public class DLSSShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/PostProcessing/DLSS/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "/PostProcessing/DLSS/combineFragment.txt";
	
	private int location_colourTexture;

	public ShaderAttrib width=new ShaderAttrib("width",this);
	public ShaderAttrib height=new ShaderAttrib("height",this);
	public ShaderAttrib size=new ShaderAttrib("size",this);
	public ShaderAttrib halfsize=new ShaderAttrib("halfsize",this);

	public ShaderAttribArray coeffRed=new ShaderAttribArray("coeffRed[","]",4*DLSS.SIZE*DLSS.SIZE,this);
	public ShaderAttribArray coeffGreen=new ShaderAttribArray("coeffGreen[","]",4*DLSS.SIZE*DLSS.SIZE,this);
	public ShaderAttribArray coeffBlue=new ShaderAttribArray("coeffBlue[","]",4*DLSS.SIZE*DLSS.SIZE,this);

	public DLSSShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
	}
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		location_colourTexture = super.getUniformlocation("colourTexture");
	}

	protected void connectTextureUnits(){
		super.loadInt(location_colourTexture, 0);
		
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	

}
