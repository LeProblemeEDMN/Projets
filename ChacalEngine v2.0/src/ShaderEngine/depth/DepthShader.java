package ShaderEngine.depth;

import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderProgram;
import main.Pipeline;

public class DepthShader extends ShaderProgram{
	/*charge une depthmap dans le depthBuffer. Permet de discard automatiquement tout les triangles non rendu si
	une depth map est fournie.

	 */
	public static String VERTEX="/ShaderEngine/depth/vertexShader.txt";
	public static String FRAGMENT="/ShaderEngine/depth/fragmentShader.txt";

	public ShaderAttrib depthMap=new ShaderAttrib("depthMap", this);


	
	public DepthShader() {
		super(VERTEX, FRAGMENT);
		getAllUniformLocations();
	}

	public void init() {
		start();
		depthMap.loadInt(0);
		stop();
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(5, "transformationMatrix");
	}
}
