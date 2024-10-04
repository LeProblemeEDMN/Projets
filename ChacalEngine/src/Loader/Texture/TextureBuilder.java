package Loader.Texture;

import org.newdawn.slick.opengl.Texture;


public class TextureBuilder {
	  private boolean clampEdges = false;
	    private boolean mipmap = false;
	    private boolean anisotropic = true;
	    private boolean nearest = false;
	     
	    private MyFile file;
	     
	    protected TextureBuilder(MyFile textureFile){
	        this.file = textureFile;
	    }
	     
	    public ModelTexture create(){
	        TextureData textureData = TextureUtils.decodeTextureFile(file);
	        int textureId = TextureUtils.loadTextureToOpenGL(textureData, this);
	        return new ModelTexture(textureId,file.getPath());
	    }
	     
	    public TextureBuilder clampEdges(){
	        this.clampEdges = true;
	        return this;
	    }
	     
	    public TextureBuilder normalMipMap(){
	        this.mipmap = true;
	        this.anisotropic = false;
	        return this;
	    }
	     
	    public TextureBuilder nearestFiltering(){
	        this.mipmap = false;
	        this.anisotropic = false;
	        this.nearest = true;
	        return this;
	    }
	     
	    public TextureBuilder anisotropic(){
	        this.mipmap = true;
	        this.anisotropic = true;
	        return this;
	    }
	     
	    protected boolean isClampEdges() {
	        return clampEdges;
	    }
	 
	    protected boolean isMipmap() {
	        return mipmap;
	    }
	 
	    protected boolean isAnisotropic() {
	        return anisotropic;
	    }
	 
	    protected boolean isNearest() {
	        return nearest;
	    }
}
