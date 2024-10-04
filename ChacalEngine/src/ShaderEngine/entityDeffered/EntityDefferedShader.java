package ShaderEngine.entityDeffered;

import Main.MainRender;
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderProgram;
import org.lwjgl.util.vector.Matrix4f;

public class EntityDefferedShader extends ShaderProgram{
	public static int NUMBER_LIGHT=10;
	public static String VERTEX_FILE= "/ShaderEngine/entityDeffered/vertex.glsl";
	public static String FRAGMENT_FILE= "/ShaderEngine/entityDeffered/fragment.glsl";

	public ShaderAttrib transformationMatrix=new ShaderAttrib("transformationMatrix", this);
	public ShaderAttrib projectionMatrix=new ShaderAttrib("projectionMatrix", this);
	public ShaderAttrib viewMatrix=new ShaderAttrib("viewMatrix", this);
	public ShaderAttrib invViewMatrix=new ShaderAttrib("invViewMatrix", this);

	public ShaderAttrib useFakeLightning=new ShaderAttrib("useFakeLightning", this);

	public ShaderAttrib reflectivity=new ShaderAttrib("reflectivity", this);
	public ShaderAttrib shineDamper=new ShaderAttrib("shineDamper", this);
	public ShaderAttrib modelTexture=new ShaderAttrib("modelTexture", this);
	public ShaderAttrib entityCube=new ShaderAttrib("environmentMap", this);
	public ShaderAttrib materialValue=new ShaderAttrib("materialValue", this);
	public ShaderAttrib camera=new ShaderAttrib("camera", this);
	
	public EntityDefferedShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
		start();
		init();
		modelTexture.loadInt(0);
		entityCube.loadInt(1);
		stop();
	}
	
	public void init() {
		projectionMatrix.loadMatrix4f(MainRender.CAMERA.getProjectionMatrix());
	}
	
	public void loadViewMatrix(Matrix4f view) {
		viewMatrix.loadMatrix4f(view);
		invViewMatrix.loadMatrix4f(Matrix4f.invert(view, null));
		camera.loadVector3(MainRender.CAMERA.getPosition());
	}

	@Override
	protected void bindAttributes() {
	     super.bindAttribute(0, "position");
	     super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}
}
