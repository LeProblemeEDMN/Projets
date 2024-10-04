package RenderEngine.gui;

import org.lwjgl.util.vector.Matrix4f;

import ShaderEngine.ShaderProgram;


public class GuiShader extends ShaderProgram{
	
	private static final String VERTEX_FILE = "/RenderEngine/gui/guiVertexShader.txt";
	private static final String FRAGMENT_FILE = "/RenderEngine/gui/guiFragmentShader.txt";
	
	private int location_transformationMatrix;

	public GuiShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
	}
	
	public void loadTransformation(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformlocation("transformationMatrix");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	
	

}
