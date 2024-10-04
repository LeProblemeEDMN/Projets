package RenderEngine.sky;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Entity.Camera;

import Entity.Light.PointLight;
import Main.MainGame;
import Main.MainRender;
import ShaderEngine.ShaderProgram;
import toolbox.maths.Maths;

public class SkyboxShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/RenderEngine/sky/skyboxVertexShader.txt";
	private static final String FRAGMENT_FILE = "/RenderEngine/sky/skyboxFragmentShader.txt";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_fogColor;
	private int location_cm1;

	private int location_lightCoord;
	private int location_lightColor;
	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
		start();
		loadVector(location_fogColor, MainRender.SKY_COLOR);
		loadInt(location_cm1, 0);
		stop();
	}
	
	public void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadLight(PointLight sun){
		super.loadVector(location_lightCoord, sun.getPosition().getSub(MainRender.CAMERA.getPosition()));
		super.loadVector(location_lightColor, sun.getColor());
	}
	public void loadViewMatrix(Matrix4f matrix4f){
		Matrix4f matrix =new Matrix4f(matrix4f);
		matrix.m30=0;
		matrix.m31=0;
		matrix.m32=0;
		super.loadMatrix(location_viewMatrix, matrix);
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformlocation("projectionMatrix");
		location_viewMatrix = super.getUniformlocation("viewMatrix");
		location_fogColor = super.getUniformlocation("fogColor");
		location_cm1 = super.getUniformlocation("cubeMap");
		
		location_lightCoord = super.getUniformlocation("lightCoord");
		location_lightColor = super.getUniformlocation("lightColor");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
