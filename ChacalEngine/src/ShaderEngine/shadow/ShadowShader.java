package ShaderEngine.shadow;

import org.lwjgl.util.vector.Matrix4f;

import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderProgram;



public class ShadowShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "/ShaderEngine/shadow/shadowVertexShader.txt";
	private static final String FRAGMENT_FILE = "/ShaderEngine/shadow/shadowFragmentShader.txt";
	
	public ShaderAttrib mvpMatrix=new ShaderAttrib("mvpMatrix", this);
	public ShaderAttrib texture=new ShaderAttrib("modelTexture", this);
	public ShaderAttrib far=new ShaderAttrib("far", this);
	public ShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
		start();
		texture.loadInt(0);
		stop();
	}

	@Override
	public void bindAttributes() {
		super.bindAttribute(0, "in_position");
		super.bindAttribute(1, "in_textureCoords");
	}

}
