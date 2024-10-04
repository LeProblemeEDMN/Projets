package ShaderEngine;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import toolbox.maths.Vector3;


public class ShaderAttrib {
	private String path;
	private ShaderProgram shaderProgram;
	private int location;
	public ShaderAttrib(String name,ShaderProgram program) {
		this.path =name;
		this.shaderProgram=program;
		shaderProgram.addShaderAttrib(this);
		
	}
	public void setLocation(int location) {
		this.location = location;
		//System.out.println(location+" "+path);
	}
	public int getLocation() {
		return location;
	}
	public String getPath() {
		return path;
	}
	public void loadMatrix4f(Matrix4f mat) {
		shaderProgram.loadMatrix(location, mat);
	}
	public void loadVector4f(Vector4f vec) {
		shaderProgram.loadVector(location, vec);
	}
	public void loadVector3(Vector3 vec) {
		shaderProgram.loadVector(location, vec);
	}
	public void loadVector2D(Vector2f vec) {
		shaderProgram.load2DVector(location, vec);
	}
	public void loadInt(int value) {
		shaderProgram.loadInt(location, value);
	}
	public void loadFloat(float value) {
		shaderProgram.loadFloat(location, value);
	}
	public void loadBoolean(boolean value) {
		shaderProgram.loadBoolean(location, value);
	}
}
