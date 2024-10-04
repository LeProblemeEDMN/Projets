package PostProcessing.SSAO.geometry;

import Main.MainRender;
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderProgram;

public class ShaderGeometry extends ShaderProgram{
	public static String VERTEX="/PostProcessing/SSAO/geometry/vertexShader.txt";
	public static String FRAGMENT="/PostProcessing/SSAO/geometry/fragmentShader.txt";
	
	public ShaderAttrib transformationMatrix=new ShaderAttrib("transformationMatrix", this);
	public ShaderAttrib projectionMatrix=new ShaderAttrib("projectionMatrix", this);
	public ShaderAttrib viewMatrix=new ShaderAttrib("viewMatrix", this);
	
	
	public ShaderGeometry() {
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
		
	}
}
