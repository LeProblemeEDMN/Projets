package rendering.shadow.EntityShadow;

import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderProgram;

public class EntityShadowShader extends ShaderProgram{
	public static int NUMBER_LIGHT=10;
	public static String VERTEX_FILE= "/rendering/shadow/EntityShadow/vertex.glsl";
	public static String FRAGMENT_FILE= "/rendering/shadow/EntityShadow/fragment.glsl";

	public ShaderAttrib VPMatrix=new ShaderAttrib("VPMatrix", this);
		
	public EntityShadowShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
	}

	@Override
	protected void bindAttributes() {
	     super.bindAttribute(0, "position");
	     //super.bindAttribute(1, "modelMatrix");
	}
}

