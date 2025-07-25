package ShaderEngine;

import org.joml.*;
import toolbox.maths.Vector3;

public class ShaderAttribArray {
	/*Object of a uniform variable
	 * Allow to simply change the value of the elements of the uniform variable
	 * */
	private String[] path;
	private ShaderProgram shaderProgram;
	private ShaderProgram.UNIFORM_TYPE attrib_type;
	private int[] location;
	private int numberAttrib;
	public ShaderAttribArray(String nameLeft,String nameRight,int number,ShaderProgram program){
		this(nameLeft,nameRight,number,program, ShaderProgram.UNIFORM_TYPE.UNDEFINED);
	}
	public ShaderAttribArray(String nameLeft,String nameRight,int number,ShaderProgram program,ShaderProgram.UNIFORM_TYPE attribute_type) {
		this.path =new String[number];
		this.location=new int[number];
		this.attrib_type=attribute_type;
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
	//Functions to change the value of the variable. The shader must be turned on first
	public void loadMatrix4f(Matrix4f mat,int id) {
		if(attrib_type== ShaderProgram.UNIFORM_TYPE.UNDEFINED || attrib_type== ShaderProgram.UNIFORM_TYPE.MAT4)
			shaderProgram.loadMatrix(location[id], mat);
		else throw new RuntimeException("The uniform variable array is not of the right type");
	}
	public void loadVector4f(Vector4f vec,int id) {
		if(attrib_type== ShaderProgram.UNIFORM_TYPE.UNDEFINED || attrib_type== ShaderProgram.UNIFORM_TYPE.VEC4)
			shaderProgram.loadVector(location[id], vec);
		else throw new RuntimeException("The uniform variable array is not of the right type");
	}
	public void loadVector3(Vector3 vec,int id) {
		if(attrib_type== ShaderProgram.UNIFORM_TYPE.UNDEFINED || attrib_type== ShaderProgram.UNIFORM_TYPE.VEC3)
			shaderProgram.loadVector(location[id], vec);
		else throw new RuntimeException("The uniform variable array is not of the right type");
	}
	public void loadVector2D(Vector2f vec,int id) {
		if(attrib_type== ShaderProgram.UNIFORM_TYPE.UNDEFINED || attrib_type== ShaderProgram.UNIFORM_TYPE.VEC2)
			shaderProgram.load2DVector(location[id], vec);
		else throw new RuntimeException("The uniform variable array is not of the right type");
	}
	public void loadInt(int value,int id) {
		if(attrib_type== ShaderProgram.UNIFORM_TYPE.UNDEFINED || attrib_type== ShaderProgram.UNIFORM_TYPE.INT
			|| attrib_type== ShaderProgram.UNIFORM_TYPE.TEXTURE|| attrib_type== ShaderProgram.UNIFORM_TYPE.MULTISAMPLED_TEXTURE)
			shaderProgram.loadInt(location[id], value);
		else throw new RuntimeException("The uniform variable array is not of the right type");
	}
	public void loadFloat(float value,int id) {
		if(attrib_type== ShaderProgram.UNIFORM_TYPE.UNDEFINED || attrib_type== ShaderProgram.UNIFORM_TYPE.FLOAT)
			shaderProgram.loadFloat(location[id], value);
		else throw new RuntimeException("The uniform variable array is not of the right type");
	}
	public void loadBoolean(boolean value,int id) {
		if(attrib_type== ShaderProgram.UNIFORM_TYPE.UNDEFINED || attrib_type== ShaderProgram.UNIFORM_TYPE.FLOAT)
			shaderProgram.loadBoolean(location[id], value);
		else throw new RuntimeException("The uniform variable array is not of the right type");
	}
}
