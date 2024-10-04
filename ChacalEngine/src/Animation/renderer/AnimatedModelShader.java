package Animation.renderer;

import org.lwjgl.Sys;
import org.lwjgl.util.vector.Matrix4f;

import Loader.Loader;
import Loader.Texture.MyFile;
import Main.MainRender;
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderAttribArray;
import ShaderEngine.ShaderProgram;

public class AnimatedModelShader extends ShaderProgram {

	private static final int MAX_JOINTS = 50;// max number of joints in a skeleton
	private static final int DIFFUSE_TEX_UNIT = 0;

	private static final String VERTEX_SHADER ="/Animation/renderer/animatedEntityVertex.glsl";
	private static final String FRAGMENT_SHADER = "/Animation/renderer/animatedEntityFragment.glsl";

	//protected UniformMatrix projectionViewMatrix = new UniformMatrix("projectionViewMatrix");
	//protected UniformVec3 lightDirection = new UniformVec3("lightDirection");
	//protected UniformMat4Array jointTransforms = new UniformMat4Array("jointTransforms", MAX_JOINTS);
	//private UniformSampler diffuseMap = new UniformSampler("diffuseMap");
	public ShaderAttrib projectionViewMatrix=new ShaderAttrib("projectionViewMatrix", this);
	public ShaderAttrib t=new ShaderAttrib("test", this);
	
	public ShaderAttrib lightDirection=new ShaderAttrib("lightDirection", this);
	public ShaderAttrib diffuseMap=new ShaderAttrib("diffuseMap", this);
	public ShaderAttribArray jointTransforms=new ShaderAttribArray("jointTransforms[", "]", MAX_JOINTS, this);
	public ShaderAttrib position=new ShaderAttrib("position", this);
	
	/**
	 * Creates the shader program for the {@link AnimatedModelRenderer} by
	 * loading up the vertex and fragment shader code files. It also gets the
	 * location of all the specified uniform variables, and also indicates that
	 * the diffuse texture will be sampled from texture unit 0.
	 */
	public AnimatedModelShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
		getAllUniformLocations();
		connectTextureUnits();
		
	}

	/**
	 * Indicates which texture unit the diffuse texture should be sampled from.
	 */
	private void connectTextureUnits() {
		super.start();
		diffuseMap.loadInt(DIFFUSE_TEX_UNIT);
		super.stop();
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "in_position");
		bindAttribute(1, "in_textureCoords");
		bindAttribute(2, "in_normal");
		bindAttribute(3, "in_jointIndices");
		bindAttribute(4, "in_weight");
		
	}
	public void loadJoints( Matrix4f[] j) {
		Matrix4f vp=Matrix4f.mul(MainRender.CAMERA.getProjectionMatrix(), MainRender.CAMERA.getViewMatrix(), null);
		for (int i = 0; i < j.length; i++) {
		
			jointTransforms.loadMatrix4f(j[i], i);
		}
		//t.loadInt((int)((System.currentTimeMillis()-n)/1000)-2);
		//System.out.println((int)((System.currentTimeMillis()-n)/1000)-2);
		
	}
}
