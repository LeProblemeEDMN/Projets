package ShaderEngine;

import main.MainLoop;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import toolbox.maths.Vector3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;


public abstract class ShaderProgram {
	public enum UNIFORM_TYPE{UNDEFINED, INT,FLOAT,VEC2,VEC3,VEC4,MAT4,TEXTURE,MULTISAMPLED_TEXTURE}

	/*
	This class load a shader (vertex and fragment) and manage allow to simply load the uniform variables

	How to use?
	In the constructor super( , ) and getAllUniformLocations() should be called ;

	To make a shader a class extending ShaderProgram should be made.
	For each uniform variable in the shader of name "NAME" a ShaderAttrib  new ShaderAttrib("NAME", this); should be added to the class.
	This object will allow to change the value of uniform variable

	For each uniform array in the shader of name "NAME" of size K a ShaderAttribArray  new ShaderAttribArray("NAME",K, this); should be added to the class.
	This object will allow to change the value of the element uniform array

	The function bind attribute must be called with all the name of the input variable of the vertex shader.
	To each one an integer must be associated. This integer will be the id of the VBO which will be streamed to this input variable.

	 */
	protected List<ShaderAttrib>attribs=new ArrayList<>();//uniform variables objects
	protected List<ShaderAttribArray>attribArrays=new ArrayList<>();//array of uniform variables objects
	 	private int programID;
	    private int vertexShaderID;
	    private int fragmentShaderID;

		//buffer used to load matrix
	    private static FloatBuffer matrixBuffer=BufferUtils.createFloatBuffer(16);

		private boolean isUsed=false;

	    public ShaderProgram(String vertexFile,String fragmentFile){

	        vertexShaderID = loadShader(vertexFile,GL20.GL_VERTEX_SHADER);

	        fragmentShaderID = loadShader(fragmentFile,GL20.GL_FRAGMENT_SHADER);
	        programID = GL20.glCreateProgram();
	        GL20.glAttachShader(programID, vertexShaderID);
	        GL20.glAttachShader(programID, fragmentShaderID);
	        bindAttributes();
	        GL20.glLinkProgram(programID);
	        GL20.glValidateProgram(programID);
	   
	    }
	    
	    public void addShaderAttrib(ShaderAttrib attrib) {
	    	attribs.add(attrib);
	    }
	    public void addShaderAttribArray(ShaderAttribArray attrib) {
	    	attribArrays.add(attrib);
	    }
	    protected void getAllUniformLocations() {
	    	for (ShaderAttrib shaderAttrib : attribs) {
	    		int id=getUniformlocation(shaderAttrib.getPath());
				shaderAttrib.setLocation(id);
			}
	    	for (ShaderAttribArray shaderAttrib : attribArrays) {
				for (int i = 0; i < shaderAttrib.getNumberAttrib(); i++) {
					
					int id=getUniformlocation(shaderAttrib.getPath(i));
					shaderAttrib.setLocation( id,i);
				}
			}
	    }
	    	
		//return the id of a uniform variable
	    protected int getUniformlocation(String unifromName) {
	    	return GL20.glGetUniformLocation(programID, unifromName);
	    }
	    
	    protected abstract void bindAttributes();
	    
	    protected void bindAttribute(int attribute, String variableName){
	        GL20.glBindAttribLocation(programID, attribute, variableName);
	    }
	    //load the shader form the files
	    private static int loadShader(String file, int type){
	        StringBuilder shaderSource = new StringBuilder();
	        
	        try{
	        	InputStream inputStream= MainLoop.class.getResourceAsStream(file);
	        	
	            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	            String line;
	            while((line = reader.readLine())!=null){
	            	
	                shaderSource.append(line).append("\n");
	            }
	            reader.close();
	        }catch(IOException e){
	            e.printStackTrace();
	            System.exit(-1);
	        }

	        int shaderID = GL20.glCreateShader(type);
	        GL20.glShaderSource(shaderID, shaderSource);
	        GL20.glCompileShader(shaderID);
	        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS )== GL11.GL_FALSE){
				System.err.println("Shader: "+file);
	            System.err.println(GL20.glGetShaderInfoLog(shaderID, 500));
	            System.err.println("Could not compile shader!");
	            System.exit(-1);
	        }
	        return shaderID;
	    }
		//load the different type of variables
	    protected void loadFloat(int location,float value) {
	    	GL20.glUniform1f(location,value);
	    }
	    
	    protected void loadInt(int location,int value) {
	    	GL20.glUniform1i(location,value);
	    }
	    
	    protected void loadVector(Integer location,Vector3 vector3) {
			GL20.glUniform3f(location, vector3.x, vector3.y, vector3.z);
		}
	    protected void loadVector(Integer location,Vector4f vector4f) {
			GL20.glUniform4f(location, vector4f.x, vector4f.y, vector4f.z,vector4f.w);
		}
	    protected void load2DVector(Integer location,Vector2f vector2f) {
			GL20.glUniform2f(location, vector2f.x, vector2f.y);
		}
	    protected void loadBoolean(int location ,boolean bool) {
			float value=0;
			if (bool) {
				value=1;
			}
			GL20.glUniform1f(location,value);
		}

	    protected void loadMatrix(int location,Matrix4f matrix4f) {
			matrix4f.get(matrixBuffer);

			GL20.glUniformMatrix4fv(location, false, matrixBuffer);
		}
	    //start the shader -> usefull to change uniform or use the shader
	    public void start(){
			isUsed=true;
	        GL20.glUseProgram(programID);
	    }
		//stop the shader mandatory to use another shader
	    public void stop(){
			isUsed=false;
	        GL20.glUseProgram(0);
	    }
	     //clean up the sahder when the window is closed
	    public void cleanUp(){
	        stop();
	        GL20.glDetachShader(programID, vertexShaderID);
	        GL20.glDetachShader(programID, fragmentShaderID);
	        GL20.glDeleteShader(vertexShaderID);
	        GL20.glDeleteShader(fragmentShaderID);
	        GL20.glDeleteProgram(programID);
	    }

	public boolean isUsed() {
		return isUsed;
	}
}
