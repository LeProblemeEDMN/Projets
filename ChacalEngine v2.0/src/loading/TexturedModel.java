package loading;

import loading.LOD.LODManager;
import loading.Texture.ModelTexture;

import java.util.ArrayList;
import java.util.List;

public class TexturedModel {
	//LOD models
	private RawModel[] rawModel;
	//pourcents associe au LOD model (par ordre croissant)
	private float[] percents;
	//texture of the model
	private ModelTexture texture;

	public TexturedModel(ModelTexture modelTexture,String LOD_name) {
		this.texture=modelTexture;
		this.percents= LODManager.percentsLOD.get(LOD_name);
		this.rawModel= LODManager.modelsLOD.get(LOD_name);
		if(this.rawModel==null){
			System.err.println("The model "+LOD_name+" could not be loaded");
		}
	}
	public TexturedModel(ModelTexture modelTexture,RawModel model) {
		this.texture=modelTexture;
		this.percents= new float[]{0};
		this.rawModel= new RawModel[]{model};
	}
	/*
	return the id of the LOD model depending of the screenview.
	 */
	public int getLODId(float screenView){
		for(int i=0;i<rawModel.length-1;i++){
			if(percents[i]>screenView) return i;
		}
		return getNumberLOD()-1;
	}

	public void setTexture(ModelTexture texture) {
		this.texture = texture;
	}
	

	public ModelTexture getTexture() {
		return texture;
	}

	public int getNumberLOD(){return rawModel.length;}
	public RawModel getRawModel(int lodId) {return rawModel[lodId];}
}
