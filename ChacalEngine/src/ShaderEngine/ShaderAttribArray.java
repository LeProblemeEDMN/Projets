package ShaderEngine;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import toolbox.maths.Vector3;

public class ShaderAttribArray {
	private String[] path;
	private ShaderProgram shaderProgram;
	private int[] location;
	private int numberAttrib;
	
	public ShaderAttribArray(String nameLeft,String nameRight,int number,ShaderProgram program) {
		this.path =new String[number];
		this.location=new int[number];
		for (int i = 0; i < number; i++) {
			this.path[i]=nameLeft+i+nameRight;
		}
		numberAttrib=number;
		this.shaderProgram=program;
		shaderProgram.addShaderAttribArray(this);
		
	}
	
	public String[] getPath() {
		return path;
	}
	public String getPath(int id) {
		return path[id];
	}
	
	public void setPath(String[] path) {
		this.path = path;
	}

	public int[] getLocation() {
		return location;
	}

	public void setLocation(int[] location) {
		this.location = location;
	}
	public void setLocation(int location,int id) {
		this.location[id] = location;
	}
	public int getNumberAttrib() {
		return numberAttrib;
	}

	public void setNumberAttrib(int numberAttrib) {
		this.numberAttrib = numberAttrib;
	}

	public void loadMatrix4f(Matrix4f mat,int id) {
		shaderProgram.loadMatrix(location[id], mat);
	}
	public void loadVector4f(Vector4f vec,int id) {
		shaderProgram.loadVector(location[id], vec);
	}
	public void loadVector3(Vector3 vec,int id) {
		shaderProgram.loadVector(location[id], vec);
	}
	public void loadVector2D(Vector2f vec,int id) {
		shaderProgram.load2DVector(location[id], vec);
	}
	public void loadInt(int value,int id) {
		shaderProgram.loadInt(location[id], value);
	}
	public void loadFloat(float value,int id) {
		shaderProgram.loadFloat(location[id], value);
	}
	public void loadBoolean(boolean value,int id) {
		shaderProgram.loadBoolean(location[id], value);
	}
}
