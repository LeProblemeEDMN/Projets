package Loader.ObjLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import Loader.Loader;
import Loader.RawModel;
import Loader.TexturedModel;
import Loader.Texture.ModelTexture;
import toolbox.maths.AxisAlignedBB;
import toolbox.maths.Vector3;

public class objFileLoaderWithMtl {
	 
	    private static final String RES_LOC = "res/";
	 
	    public static List<TexturedModel> loadOBJ(String objFileName,String mtlFileName, Loader loader) {
	    	List<ModelTexture> modelTextures=Mtlloader.loadMTL(mtlFileName, loader);
	    	List<TexturedModel> models=new ArrayList<>();
	    	FileReader isr = null;
	        File objFile = new File( objFileName );
	        try {
	            isr = new FileReader(objFile);
	        } catch (FileNotFoundException e) {
	            System.err.println("File not found in res folder!");
	            System.exit(-1);
	        }
	        BufferedReader reader = new BufferedReader(isr);
	        String line;
	        List<Vertex> vertices = new ArrayList<Vertex>();
	        List<Integer> materialId = new ArrayList<Integer>();
	        List<Vector2f> textures = new ArrayList<Vector2f>();
	        List<Vector3f> normals = new ArrayList<Vector3f>();
	        List<Integer> indices = new ArrayList<Integer>();
	        
	        Vector3 minVec=new Vector3();
			Vector3 maxVec=new Vector3();
	        
	        try {
	            while (true) {
	                line = reader.readLine();
	               
	                if (line.startsWith("v ")) {
	                    String[] currentLine = line.split(" ");
	                    Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]),
	                            (float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
	                    Vertex newVertex = new Vertex(vertices.size(), vertex);
	                    vertices.add(newVertex);
	                    getMaxAndMin(minVec, maxVec, vertex);
	                } else if (line.startsWith("vt ")) {
	                    String[] currentLine = line.split(" ");
	                    Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
	                            (float) Float.valueOf(currentLine[2]));
	                    textures.add(texture);
	                } else if (line.startsWith("vn ")) {
	                    String[] currentLine = line.split(" ");
	                    Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]),
	                            (float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
	                    normals.add(normal);
	                } else if (line.startsWith("f ")||line.startsWith("usemtl ")) {
	                	
	                    break;
	                }
	            }
	            int idMtl=0;
	            while (line != null && (line.startsWith("f ")||line.startsWith("usemtl "))) {
	            	if(line.startsWith("usemtl ")) {
	            		if(!indices.isEmpty()&&!vertices.isEmpty()) {
	            			
	            			 RawModel model=store(loader, vertices, textures, normals, indices, objFileName);
	            			 model.setAabb(new AxisAlignedBB(minVec, maxVec));
	            			 models.add(new TexturedModel(model, modelTextures.get(idMtl)));
	            			 
	            			indices.clear();
	            			 minVec=new Vector3();
	            			 maxVec=new Vector3();
	            		}
	            		String name=line.substring(7);
	            		
	            		for (int j = 0; j < modelTextures.size(); j++) {
						if(modelTextures.get(j).getName().equals(name)) {
							
							idMtl=j;
							}
	            		}
						
	            	}else {
	            		
	                String[] currentLine = line.split(" ");
	                String[] vertex1 = currentLine[1].split("/");
	                String[] vertex2 = currentLine[2].split("/");
	                String[] vertex3 = currentLine[3].split("/");
	               
	                processVertex(vertex1,idMtl,materialId, vertices, indices);
	                
	                processVertex(vertex2,idMtl,materialId, vertices, indices);
	               
	                processVertex(vertex3,idMtl,materialId, vertices, indices);
	                
	            	}
	                line = reader.readLine();
	                
	            }
	           
	            RawModel model=store(loader, vertices, textures, normals, indices, objFileName);
		        models.add(new TexturedModel(model, modelTextures.get(idMtl)));
		        
	            reader.close();
	        } catch (Exception e) {
	            System.err.println("Error reading the file after MTL");
	            System.exit(-1);
	        }
	        
	        return models;
	    }
	 
	    private static RawModel store(Loader loader,List<Vertex> vertices ,List<Vector2f>textures,List<Vector3f>normals, List<Integer> indices,String objFileName) {
	    	float[] verticesArray = new float[vertices.size() * 3];
	        float[] texturesArray = new float[vertices.size() * 2];
	        float[] normalsArray = new float[vertices.size() * 3];
	        convertDataToArrays(vertices, textures, normals,verticesArray, texturesArray, normalsArray);
	        int[] indicesArray = convertIndicesListToArray(indices);
	        RawModel model= loader.loadToVOA(verticesArray, texturesArray, normalsArray, indicesArray,objFileName);
		       return model;
	        
	    }
	    
	    private static void processVertex(String[] vertex,int mat,List<Integer>material, List<Vertex> vertices, List<Integer> indices) {
	    	
	    	int index = Integer.parseInt(vertex[0]);
	    	index =Math.abs(index )-1;
	        Vertex currentVertex = vertices.get(index);
	       
	        int textureIndex = (int)Math.abs(Integer.parseInt(vertex[1])) - 1;
	        int normalIndex = (int)Math.abs(Integer.parseInt(vertex[2])) - 1;
	      
	        if (!currentVertex.isSet()) {
	        	
	            currentVertex.setTextureIndex(textureIndex);
	            currentVertex.setNormalIndex(normalIndex);
	            indices.add(index);
	            material.add(new Integer(mat));
	        } else {
	        	
	            dealWithAlreadyProcessedVertex(currentVertex,mat,material, textureIndex, normalIndex, indices, vertices);
	          
	        }
	    }
	 
	    private static int[] convertIndicesListToArray(List<Integer> indices) {
	        int[] indicesArray = new int[indices.size()];
	        for (int i = 0; i < indicesArray.length; i++) {
	            indicesArray[i] = indices.get(i);
	        }
	        return indicesArray;
	    }

	    
	    private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures, List<Vector3f> normals,
	    		float[] verticesArray, float[] texturesArray, float[] normalsArray) {
	        float furthestPoint = 0;
	        int nb=0;
	        for (int i = 0; i < vertices.size(); i++) {	
	            Vertex currentVertex = vertices.get(i);
	            if (currentVertex.getLength() > furthestPoint) {
	                furthestPoint = currentVertex.getLength();
	            }	        
	            Vector3f position = currentVertex.getPosition();            
	            if(currentVertex.getTextureIndex()<0) currentVertex.setTextureIndex(0);   
	            Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());	                               
	            if(currentVertex.getNormalIndex()<0) currentVertex.setNormalIndex(0);   
	            Vector3f normalVector = normals.get(currentVertex.getNormalIndex());           
	            verticesArray[i * 3] = position.x;
	            verticesArray[i * 3 + 1] = position.y;
	            verticesArray[i * 3 + 2] = position.z;
	            texturesArray[i * 2] = textureCoord.x;
	            texturesArray[i * 2 + 1] = 1 - textureCoord.y;
	            normalsArray[i * 3] = normalVector.x;
	            normalsArray[i * 3 + 1] = normalVector.y;
	            normalsArray[i * 3 + 2] = normalVector.z;
	           
	        }
	        return furthestPoint;
	    }
	 
	    private static void dealWithAlreadyProcessedVertex(Vertex previousVertex,int mat,List<Integer>material, int newTextureIndex, int newNormalIndex,
	            List<Integer> indices, List<Vertex> vertices) {
	        if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
	            indices.add(previousVertex.getIndex());
	            material.add(new Integer(mat));
	            
	        } else {
	            Vertex anotherVertex = previousVertex.getDuplicateVertex();
	            if (anotherVertex != null) {
	                dealWithAlreadyProcessedVertex(anotherVertex,mat,material, newTextureIndex, newNormalIndex, indices, vertices);
	            } else {
	                Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
	               
	                duplicateVertex.setTextureIndex(newTextureIndex);
	                duplicateVertex.setNormalIndex(newNormalIndex);
	                previousVertex.setDuplicateVertex(duplicateVertex);
	                vertices.add(duplicateVertex);
	                indices.add(duplicateVertex.getIndex());
	                material.add(new Integer(mat));
	            }
	 
	        }
	    }
	    public static void getMaxAndMin(Vector3 min,Vector3 max,Vector3f vec) {
			if(min.x>vec.x) {
				min.x=vec.x;
			}else if(max.x<vec.x){
				max.x=vec.x;
			}
			if(min.y>vec.y) {
				min.y=vec.y;
			}else if(max.y<vec.y){
				max.y=vec.y;
			}
			if(min.z>vec.z) {
				min.z=vec.z;
			}else if(max.z<vec.z){
				max.z=vec.z;
			}
		}
	
}
