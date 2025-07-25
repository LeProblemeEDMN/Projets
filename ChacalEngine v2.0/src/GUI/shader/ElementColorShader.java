package GUI.shader;

import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderProgram;

public class ElementColorShader extends ShaderProgram{
	private static final String VERTEX_FILE = "/GUI/shader/vertexGUI.txt";
	private static final String FRAGMENT_FILE = "/GUI/shader/fragmentGUI.txt";
	public ShaderAttrib scale=new ShaderAttrib("scale", this);
	public ShaderAttrib location=new ShaderAttrib("location", this);
	public ShaderAttrib color=new ShaderAttrib("color", this);
	public ShaderAttrib useText=new ShaderAttrib("useText", this);
	public ShaderAttrib textureGUI=new ShaderAttrib("textureGUI", this);
	public ShaderAttrib alpha=new ShaderAttrib("alpha", this);

	public ElementColorShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
	}

	public void init(){
		start();
		alpha.loadFloat(1);
		textureGUI.loadInt(0);
		stop();
	}




	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
}
