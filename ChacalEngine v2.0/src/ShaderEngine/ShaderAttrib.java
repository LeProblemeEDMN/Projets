package ShaderEngine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import toolbox.maths.Vector3;


public class ShaderAttrib {

	/*Object of a uniform variable
	* Allow to simply change the valeu of this uniform variable
	* */
	//name
	private String path;
	private ShaderProgram.UNIFORM_TYPE attrib_type;
	private ShaderProgram shaderProgram;
	private int location;
	public ShaderAttrib(String name,ShaderProgram program) {
		this(name,program, ShaderProgram.UNIFORM_TYPE.UNDEFINED);
	}
	public ShaderAttrib(String name,ShaderProgram program,ShaderProgram.UNIFORM_TYPE attribute_type) {
		this.path =name;
		this.shaderProgram=program;
		shaderProgram.addShaderAttrib(this);
		this.attrib_type=attribute_type;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public int getLocation() {
		return location;
	}
	public String getPath() {
		return path;
	}
	//Functions to change the value of the variable. The shader must be turned on first

	public void loadMatrix4f(Matrix4f mat) {
		if(attrib_type== ShaderProgram.UNIFORM_TYPE.UNDEFINED || attrib_type== ShaderProgram.UNIFORM_TYPE.MAT4)
			shaderProgram.loadMatrix(location, mat);
		else throw new RuntimeException("The uniform variable is not of the right type");
	}
	public void loadVector4f(Vector4f vec) {
		if(attrib_type== ShaderProgram.UNIFORM_TYPE.UNDEFINED || attrib_type== ShaderProgram.UNIFORM_TYPE.VEC4)
			shaderProgram.loadVector(location, vec);
		else throw new RuntimeException("The uniform variable is not of the right type");
	}
	public void loadVector3(Vector3 vec) {
		if(attrib_type== ShaderProgram.UNIFORM_TYPE.UNDEFINED || attrib_type== ShaderProgram.UNIFORM_TYPE.VEC3)
			shaderProgram.loadVector(location, vec);
		else throw new RuntimeException("The uniform variable is not of the right type");
	}
	public void loadVector2D(Vector2f vec) {
		if(attrib_type== ShaderProgram.UNIFORM_TYPE.UNDEFINED || attrib_type== ShaderProgram.UNIFORM_TYPE.VEC2)
			shaderProgram.load2DVector(location, vec);
		else throw new RuntimeException("The uniform variable is not of the right type");
	}
	public void loadInt(int value) {
		if(attrib_type== ShaderProgram.UNIFORM_TYPE.UNDEFINED || attrib_type== ShaderProgram.UNIFORM_TYPE.INT
			|| attrib_type== ShaderProgram.UNIFORM_TYPE.TEXTURE || attrib_type== ShaderProgram.UNIFORM_TYPE.MULTISAMPLED_TEXTURE)
			shaderProgram.loadInt(location, value);
		else throw new RuntimeException("The uniform variable is not of the right type");
	}
	public void loadFloat(float value) {
		if(attrib_type== ShaderProgram.UNIFORM_TYPE.UNDEFINED || attrib_type== ShaderProgram.UNIFORM_TYPE.FLOAT)
			shaderProgram.loadFloat(location, value);
		else throw new RuntimeException("The uniform variable is not of the right type");
	}
	public void loadBoolean(boolean value) {
		if(attrib_type== ShaderProgram.UNIFORM_TYPE.UNDEFINED || attrib_type== ShaderProgram.UNIFORM_TYPE.FLOAT)
			shaderProgram.loadBoolean(location, value);
		else throw new RuntimeException("The uniform variable is not of the right type");
	}
}
