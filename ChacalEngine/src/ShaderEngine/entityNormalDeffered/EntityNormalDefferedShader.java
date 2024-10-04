package ShaderEngine.entityNormalDeffered;

import Main.MainRender;
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderProgram;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import toolbox.maths.Vector3;

public class EntityNormalDefferedShader extends ShaderProgram{
	public static int NUMBER_LIGHT=10;
	public static String VERTEX_FILE= "/ShaderEngine/entityNormalDeffered/vertex.glsl";
	public static String FRAGMENT_FILE= "/ShaderEngine/entityNormalDeffered/fragment.glsl";

	public ShaderAttrib transformationMatrix=new ShaderAttrib("transformationMatrix", this);
	public ShaderAttrib projectionMatrix=new ShaderAttrib("projectionMatrix", this);
	public ShaderAttrib viewMatrix=new ShaderAttrib("viewMatrix", this);
	public ShaderAttrib invViewMatrixFragment=new ShaderAttrib("invViewMatrixFragment", this);
	public ShaderAttrib invViewMatrix=new ShaderAttrib("invViewMatrix", this);

	public ShaderAttrib useFakeLightning=new ShaderAttrib("useFakeLightning", this);

	public ShaderAttrib reflectivity=new ShaderAttrib("reflectivity", this);
	public ShaderAttrib shineDamper=new ShaderAttrib("shineDamper", this);
	public ShaderAttrib modelTexture=new ShaderAttrib("modelTexture", this);
	public ShaderAttrib normalTexture=new ShaderAttrib("normalTexture", this);
	public ShaderAttrib entityCube=new ShaderAttrib("environmentMap", this);
	public ShaderAttrib materialValue=new ShaderAttrib("materialValue", this);
	public ShaderAttrib camera=new ShaderAttrib("camera", this);
	public EntityNormalDefferedShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
		start();
		init();
		modelTexture.loadInt(0);
		entityCube.loadInt(1);
		normalTexture.loadInt(2);
		stop();
	}
	
	public void init() {
		projectionMatrix.loadMatrix4f(MainRender.CAMERA.getProjectionMatrix());
	}
	
	public void loadViewMatrix(Matrix4f view) {
		viewMatrix.loadMatrix4f(view);
		Matrix4f inv=Matrix4f.invert(view, null);
		invViewMatrix.loadMatrix4f(inv);
		invViewMatrixFragment.loadMatrix4f(inv);
		camera.loadVector3(MainRender.CAMERA.getPosition());
	}

	private Vector3 getEyeSpacePosition(Vector3 light, Matrix4f viewMatrix){
		Vector3f position = light.getOglVec();
		Vector4f eyeSpacePos = new Vector4f(position.x,position.y, position.z, 1f);
		Matrix4f.transform(viewMatrix, eyeSpacePos, eyeSpacePos);
		return new Vector3(eyeSpacePos.x,eyeSpacePos.y,eyeSpacePos.z);
	}

	@Override
	protected void bindAttributes() {
	     super.bindAttribute(0, "position");
	     super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "tangent");
	}
}
