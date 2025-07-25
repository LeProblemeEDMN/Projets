package GUI.font.shader;

import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderProgram;

public class FontShader extends ShaderProgram{
	private static final String VERTEX_FILE = "/GUI/font/shader/vertexGUI.txt";
	private static final String FRAGMENT_FILE = "/GUI/font/shader/fragmentGUI.txt";
	public ShaderAttrib fontTexture=new ShaderAttrib("fontTexture", this);
	public ShaderAttrib color=new ShaderAttrib("color", this);

	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
	}

	public void init(){
		start();
		fontTexture.loadInt(1);
		stop();
	}




	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
}
