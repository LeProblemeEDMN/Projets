package Loader;

import Loader.Texture.ModelTexture;

public class TexturedModel {
	private RawModel rawModel;
	private ModelTexture texture;
	private boolean grass=false;
	
	
	public TexturedModel(RawModel model,ModelTexture modelTexture) {
		this.rawModel=model;
		this.texture=modelTexture;
		
	}
	
	
	public void setTexture(ModelTexture texture) {
		this.texture = texture;
	}
	
	public RawModel getRawModel() {
		return rawModel;
	}
	public ModelTexture getTexture() {
		return texture;
	}

	public boolean isGrass() {
		return grass;
	}

	public void setGrass(boolean grass) {
		this.grass = grass;
	}
}
