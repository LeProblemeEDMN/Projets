package Loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import Animation.ColladaParser.dataStructures.MeshData;
import Loader.Texture.TextureData;
import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;


public class Loader {
	
	private List<Integer>vaos=new  ArrayList<Integer>();
	private List<Integer>vbos=new  ArrayList<Integer>();
	private List<Integer>textures=new  ArrayList<Integer>();
	
	
	public RawModel loadToVOA(float[] positions,float[]textureCoord,float[]normals, int []indices,String path) {
		int vaoID = createVAO();
		bindIndiceBuffer(indices);
		storeDataInAttributeList(0,3, positions);
		storeDataInAttributeList(1,2, textureCoord);
		storeDataInAttributeList(2, 3, normals);
		unbindVAO();
	
		return new RawModel(vaoID, indices.length,path);
		
	}
	public RawModel loadToVOA(float[] positions,float[]textureCoord,float[]normals,float []tangents, int []indices,String path) {
		int vaoID = createVAO();
		bindIndiceBuffer(indices);
		storeDataInAttributeList(0,3, positions);
		storeDataInAttributeList(1,2, textureCoord);
		storeDataInAttributeList(2, 3, normals);
		storeDataInAttributeList(3, 3, tangents);
		unbindVAO();
	
		return new RawModel(vaoID, indices.length,path);
		
	}
	public RawModel loadToVOA(MeshData data) {
		int vaoID = createVAO();
		bindIndiceBuffer(data.getIndices());
		storeDataInAttributeList(0,3, data.getVertices());
		storeDataInAttributeList(1,2, data.getTextureCoords());
		storeDataInAttributeList(2,3, data.getNormals());
		storeDataInAttributeListInt(3,3, data.getJointIds());
		/*for(int i=0;i<data.getJointIds().length;i++) {
			System.out.println(data.getJointIds()[i]);
		}*/
		storeDataInAttributeList(4,3, data.getVertexWeights());
	
		unbindVAO();
	
		return new RawModel(vaoID,  data.getVertices().length,"meshdata");
		
	}
	
	public RawModel loadToVOA(float[] positions,float[]textureCoord, int []indices,String path) {
		int vaoID = createVAO();
		bindIndiceBuffer(indices);
		storeDataInAttributeList(0,3, positions);
		storeDataInAttributeList(1,2, textureCoord);
		
		unbindVAO();
	
		return new RawModel(vaoID, indices.length,path);
		
	}
	public int loadToVOA(float[] positions,float[]textureCoord) {
		int vaoID = createVAO();
		
		storeDataInAttributeList(0,2, positions);
		storeDataInAttributeList(1,2, textureCoord);
		
		unbindVAO();
	
		return vaoID;
		
	}
	 public RawModel loadToVAO(float[] positions, int dimensions,String path) {
	        int vaoID = createVAO();
	        this.storeDataInAttributeList(0, dimensions, positions);
	        unbindVAO();
	        return new RawModel(vaoID, positions.length / dimensions,path);
	    }
	public int loadTexture(String fileName) {
		Texture texture=null;
		try {
			
			texture=TextureLoader.getTexture("PNG", new FileInputStream("res/"+fileName+".png"));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS,0f);
			if(GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				float amount=Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			}else {
				System.out.println("filter anisropic marche po");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int textureId=texture.getTextureID();
		textures.add(textureId);
		return textureId;
	}
	public int loadCubeMap(String[] textureFiles) {
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
 
        for (int i = 0; i < textureFiles.length; i++) {
            TextureData data = decodeTextureFile("res/" + textureFiles[i] + ".png");
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0,
                    GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
        }
         
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        textures.add(texID);
        return texID;
    }
	
	private TextureData decodeTextureFile(String fileName) {
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try {
			FileInputStream in = new FileInputStream(fileName);
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tried to load texture " + fileName + ", didn't work");
			System.exit(-1);
		}
		return new TextureData(buffer, width, height);
	}
	private void unbindVAO() {
		
		GL30.glBindVertexArray(0);
	}
	public void cleanUp() {
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for (int texture : textures) {
			GL15.glDeleteBuffers(texture);
		}
}
	private void storeDataInAttributeList(int attriburenumber,int coordinateSize, float[] positions) {
		int vboId=GL15.glGenBuffers();
		vbos.add(vboId);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		
		FloatBuffer buffer=storeDataInFloatBuffer(positions);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer,GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attriburenumber, coordinateSize, GL11.GL_FLOAT,false, 4*coordinateSize,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
	}
	private void storeDataInAttributeListInt(int attriburenumber,int coordinateSize, int[] positions) {
		int vboId=GL15.glGenBuffers();
		vbos.add(vboId);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		
		IntBuffer buffer=storeDataInIntBuffer(positions);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer,GL15.GL_STATIC_DRAW);
		GL30.glVertexAttribIPointer(attriburenumber, coordinateSize, GL11.GL_INT, 4*coordinateSize,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
	}
	private int createVAO() {
		int VAOid=GL30.glGenVertexArrays();
		vaos.add(VAOid);
		GL30.glBindVertexArray(VAOid);
		return VAOid;
	}
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
}
	
	private void bindIndiceBuffer(int[]indices) {
		int vboID=GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
}
}
