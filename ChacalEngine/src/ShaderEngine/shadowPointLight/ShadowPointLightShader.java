package ShaderEngine.shadowPointLight;

import org.lwjgl.util.vector.Matrix4f;

import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderProgram;



public class ShadowPointLightShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "/ShaderEngine/shadowPointLight/shadowVertexShader.txt";
	private static final String FRAGMENT_FILE = "/ShaderEngine/shadowPointLight/shadowFragmentShader.txt";
	
	public ShaderAttrib vpMatrix=new ShaderAttrib("vpMatrix", this);
	public ShaderAttrib modelMatrix=new ShaderAttrib("modelMatrix", this);
	public ShaderAttrib texture=new ShaderAttrib("modelTexture", this);
	public ShaderAttrib lightPos=new ShaderAttrib("lightPos", this);
	public ShaderAttrib far=new ShaderAttrib("far", this);
	public ShadowPointLightShader() {
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
