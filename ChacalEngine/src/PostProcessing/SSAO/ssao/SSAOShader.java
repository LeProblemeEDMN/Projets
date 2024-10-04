package PostProcessing.SSAO.ssao;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import Main.MainRender;
import PostProcessing.SSAO.SSAO;
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderAttribArray;
import ShaderEngine.ShaderProgram;
import toolbox.maths.Vector3;

public class SSAOShader extends ShaderProgram{
	public static String VERTEX="/PostProcessing/SSAO/ssao/vertexShader.txt";
	public static String FRAGMENT="/PostProcessing/SSAO/ssao/fragmentShader.txt";
	
	public ShaderAttrib posMap=new ShaderAttrib("gPositionMap", this);
	public ShaderAttrib nMap=new ShaderAttrib("normalMap", this);
	public ShaderAttrib noise=new ShaderAttrib("noiseTexture", this);
	public ShaderAttrib projectionMatrix=new ShaderAttrib("gProj", this);
	public ShaderAttrib viewMatrix=new ShaderAttrib("viewMatrix", this);
	public ShaderAttrib rad=new ShaderAttrib("gSampleRad", this);
	
	public ShaderAttribArray array=new ShaderAttribArray("gKernel[", "]", 128, this);
	public ShaderAttribArray samples=new ShaderAttribArray("samples[", "]", 16, this);
	public SSAOShader() {
		super(VERTEX, FRAGMENT);
		getAllUniformLocations();
		start();
		projectionMatrix.loadMatrix4f(MainRender.CAMERA.getProjectionMatrix());
		posMap.loadInt(0);
		nMap.loadInt(1);
		noise.loadInt(2);
		rad.loadFloat(1.5f);
		for(int i=0;i<127;i++) {
			array.loadVector3(new Vector3(Math.random()-0.5, Math.random()-0.5, Math.random()-0.5).normalize(), i);
		}
		List<Vector3f>vector3s=SSAO.getSSAOKernel();
		for (int i = 0; i < 16; i++) {
			samples.loadVector3(new Vector3(vector3s.get(i)), i);
		}
		stop();
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "textureCoords");
		
	}
}
