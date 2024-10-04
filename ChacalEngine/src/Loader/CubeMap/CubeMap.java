package Loader.CubeMap;

import Loader.Loader;
import Loader.RawModel;
import Loader.Texture.MyFile;
import Loader.Texture.TextureUtils;

public class CubeMap {
	private static final float SIZE = 100f;
    
    private static final float[] VERTICES = {        
        -SIZE,  SIZE, -SIZE,
        -SIZE, -SIZE, -SIZE,
        SIZE, -SIZE, -SIZE,
         SIZE, -SIZE, -SIZE,
         SIZE,  SIZE, -SIZE,
        -SIZE,  SIZE, -SIZE,
 
        -SIZE, -SIZE,  SIZE,
        -SIZE, -SIZE, -SIZE,
        -SIZE,  SIZE, -SIZE,
        -SIZE,  SIZE, -SIZE,
        -SIZE,  SIZE,  SIZE,
        -SIZE, -SIZE,  SIZE,
 
         SIZE, -SIZE, -SIZE,
         SIZE, -SIZE,  SIZE,
         SIZE,  SIZE,  SIZE,
         SIZE,  SIZE,  SIZE,
         SIZE,  SIZE, -SIZE,
         SIZE, -SIZE, -SIZE,
 
        -SIZE, -SIZE,  SIZE,
        -SIZE,  SIZE,  SIZE,
         SIZE,  SIZE,  SIZE,
         SIZE,  SIZE,  SIZE,
         SIZE, -SIZE,  SIZE,
        -SIZE, -SIZE,  SIZE,
 
        -SIZE,  SIZE, -SIZE,
         SIZE,  SIZE, -SIZE,
         SIZE,  SIZE,  SIZE,
         SIZE,  SIZE,  SIZE,
        -SIZE,  SIZE,  SIZE,
        -SIZE,  SIZE, -SIZE,
 
        -SIZE, -SIZE, -SIZE,
        -SIZE, -SIZE,  SIZE,
         SIZE, -SIZE, -SIZE,
         SIZE, -SIZE, -SIZE,
        -SIZE, -SIZE,  SIZE,
         SIZE, -SIZE,  SIZE
    };
     
    private RawModel cube;
    private int texture;
    private int size;
    
    public CubeMap(String[] textureFiles, Loader loader){
        cube = loader.loadToVAO(VERTICES,3,"null");
        texture = loader.loadCubeMap(textureFiles);
    }
    int sizeMap=0;
    public CubeMap(int text, Loader loader,int sizeM){
        cube = loader.loadToVAO(VERTICES,3,"null");
        texture = text;
        sizeMap=sizeM;
    }
    
     public int getSize() {
		return sizeMap;
	}
     public void setSize(int size) {
		this.sizeMap = size;
	}
    public RawModel getCube(){
        return cube;
    }
     
    public int getTexture(){
        return texture;
    }
    public static int newCubeMap(MyFile[] textureFiles) {
        int cubeMapId = TextureUtils.loadCubeMap(textureFiles);
        //TODO needs to know size!
        return cubeMapId;
    }
     
    public static int newEmptyCubeMap(int size) {
        int cubeMapId = TextureUtils.createEmptyCubeMap(size);
        return cubeMapId;
    }

}
