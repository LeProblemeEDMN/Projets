package loading.Texture;

public class ModelTexture {
	//colour of the texture
	private int textureId;
	//normal map
	private int normalMap=-1;
	//displacement map for parallax mapping
	private int displacementMap=-1;
	/*represent 3 things:
		-red channel:ambient occlusion of the texture
		-green channel: roughness (equivalent to shine damper)
		-blue channel: metaliness (equivalent to reflectivity).
	 */

	private int ARMtexture;
	//relfection and refraction of the material
	private float refractionMaterial=0.0f;
	private float reflectionMaterial=0.0f;
	//Blinn-phong parameters
	private float shineDamper=20;
	private float reflectivity=1;
	//is this object transparent
	private boolean transparance=false;
	//does the object need fake lighting (grass for example).
	private boolean useFakeLightning=false;

	
	private int numberOfRows=1;


	public float getRefractionMaterial() {
		return refractionMaterial;
	}
	public void setRefractionMaterial(float refractionMaterial) {
		this.refractionMaterial = refractionMaterial;
	}
	public float getReflectionMaterial() {
		return reflectionMaterial;
	}
	public void setReflectionMaterial(float reflectionMaterial) {
		this.reflectionMaterial = reflectionMaterial;
	}

	public ModelTexture(int id,int ARM_id) {
		this.textureId=id;
		this.ARMtexture=ARM_id;
	}
	public ModelTexture(String name) {
		this.textureId=TextureUtils.getAlbedo(name);
		this.ARMtexture=TextureUtils.getARM(name);
		this.normalMap=TextureUtils.getNormal(name);
		this.displacementMap=TextureUtils.getDisplacement(name);
	}
	public ModelTexture() {
		// TODO Auto-generated constructor stub
	}
	public void setARMMap(int specularMap) {
		this.ARMtexture = specularMap;

	}
	public int getARMMap() {
		return ARMtexture;
	}
	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}
	public boolean hasNormalMap(){return this.normalMap>=0;}
	public int getNormalMap() {
		return normalMap;
	}
	public boolean isUseFakeLightning() {
		return useFakeLightning;
	}
	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}
	public int getNumberOfRows() {
	return numberOfRows;
			}
	public void setNumberOfRows(int numberOfRows) {
	this.numberOfRows = numberOfRows;
			}
	public void setUseFakeLightning(boolean useFakeLightning) {
		this.useFakeLightning = useFakeLightning;
	}

	public boolean isTransparance() {
		return transparance;
	}

	public void setTransparance(boolean transparance) {
		this.transparance = transparance;
	}
	
	public int getTextureId() {
		return this.textureId;
	}

	public float getShineDamper() {
		return this.shineDamper;
	}
	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}
	public float getReflectivity() {
		return this.reflectivity;
	}
	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public boolean hasDisplacementMap(){
		return this.displacementMap>=0;
	}

	public int getDisplacementMap() {
		return displacementMap;
	}

	public void setDisplacementMap(int displacementMap) {
		this.displacementMap = displacementMap;
	}
}
