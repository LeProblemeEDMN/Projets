package PostProcessing.SSAO.normal;

import Main.MainRender;
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderProgram;

public class ShaderNormal extends ShaderProgram{
	public static String VERTEX="/PostProcessing/SSAO/normal/vertexShader.txt";
	public static String FRAGMENT="/PostProcessing/SSAO/normal/fragmentShader.txt";
	
	public ShaderAttrib transformationMatrix=new ShaderAttrib("transformationMatrix", this);
	public ShaderAttrib projectionMatrix=new ShaderAttrib("projectionMatrix", this);
	public ShaderAttrib viewMatrix=new ShaderAttrib("viewMatrix", this);
	
	
	public ShaderNormal() {
		super(VERTEX, FRAGMENT);
		getAllUniformLocations();
		start();
		//System.out.println(MainRender.CAMERA.getProjectionMatrix());
		projectionMatrix.loadMatrix4f(MainRender.CAMERA.getProjectionMatrix());
		stop();
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
		bindAttribute(2, "normal");
	}
}
