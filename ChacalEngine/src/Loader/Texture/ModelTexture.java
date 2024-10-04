package Loader.Texture;

public class ModelTexture {
	private int textureId;
	 private int normalMap;
	private int specularMap;
	private float refractionMaterial=0.0f;
	private float reflectionMaterial=0.0f;
	
	private boolean transparance=false;
	private boolean useFakeLightning=false;
	private boolean hasSpecularMap=false;
	
	private int numberOfRows=1;
	String name="null";
	String path="null";
	String spePath="null";
	
	public String getPath() {
		return path;
	}
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
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ModelTexture(int id,String path) {
		this.textureId=id;
		this.path=path;
		
	}
	public ModelTexture() {
		// TODO Auto-generated constructor stub
	}
	public void setSpecularMap(int specularMap,String path) {
		this.specularMap = specularMap;
		this.hasSpecularMap=true;
		spePath=path;
	}
	public boolean isHasSpecularMap() {
		return hasSpecularMap;
	}
	public int getSpecularMap() {
		return specularMap;
	}
	public void setNormalMap(int normalMap) {
		this.normalMap = normalMap;
	}
	public int getNormalMap() {
		return normalMap;
	}
	public boolean isUseFakeLightning() {
		return useFakeLightning;
	}
	public void setTextureId(int textureId,String path) {
		this.textureId = textureId;
		this.path=path;
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

	public String getSpecularPath() {
		return spePath;
	}
	
	public int getTextureId() {
		return this.textureId;
	}
	private float shineDamper=10;
	private float reflectivity=1;
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
}
