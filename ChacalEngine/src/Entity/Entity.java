package Entity;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import Loader.TexturedModel;
import Loader.CubeMap.CubeMap;
import Loader.ObjLoader.objFileLoader;
import toolbox.maths.AxisAlignedBB;
import toolbox.maths.Frustum;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

public class Entity {
	protected String name;
	private List<TexturedModel> texturedModel=new ArrayList<>();
	protected Vector3 position;
	protected float rotX,rotY,rotZ;
	private float scale;
	private String mtlPass="";
	private boolean vegetation;
	private int textureIndex=0;
	protected CubeMap map;
	private boolean haveCubeMap=false;
	protected Matrix4f transformationMatrix=new Matrix4f();
	private AxisAlignedBB axisAlignedBB;
	
	public List<Integer>spotIds=new ArrayList<>();
	public List<Integer>pointIds=new ArrayList<>();

	public float renderingDistance=10000;

	public boolean useSphereFrustum=false;
	public float radiusSphere=1;

	public Entity(TexturedModel texturedModel, Vector3 position, float rotX, float rotY, float rotZ, float scale,String name) {
		this.name=name;
		addTexturedModel(texturedModel);
		this.position = new Vector3(position);
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.map=null;
		updateAABB();
		setTransformationMatrix();
		
	}
	public Entity(TexturedModel texturedModel, Vector3 position, float rotX, float rotY, float rotZ, float scale,int textureindex,String name) {
		this.name=name;
		addTexturedModel(texturedModel);
		this.position =  new Vector3(position);
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.textureIndex=textureindex;
		this.map=null;
		updateAABB();
		setTransformationMatrix();
	}
	
	public void updateAABB() {
		AxisAlignedBB aabb=getTexturedModel().getRawModel().getAabb().multiplySize(scale);
	//	System.out.println(aabb+" "+rotX+" "+rotY);
		Vector3 Xmax=new Vector3(Maths.rotateVectorAAbb(rotX, rotY, new Vector3f((float)aabb.maxX,0,0)));
		Vector3 Xmin=new Vector3(Maths.rotateVectorAAbb(rotX, rotY, new Vector3f((float)aabb.minX,0,0)));
		Vector3 Ymax=new Vector3(Maths.rotateVectorAAbb(rotX, rotY, new Vector3f(0,(float)aabb.maxY,0)));
		Vector3 Ymin=new Vector3(Maths.rotateVectorAAbb(rotX, rotY, new Vector3f(0,(float)aabb.minY,0)));
		Vector3 Zmax=new Vector3(Maths.rotateVectorAAbb(rotX, rotY, new Vector3f(0,0,(float)aabb.maxZ)));
		Vector3 Zmin=new Vector3(Maths.rotateVectorAAbb(rotX, rotY, new Vector3f(0,0,(float)aabb.minZ)));
		
		Vector3 minVec=new Vector3();
		Vector3 maxVec=new Vector3();
		getMaxAndMin(minVec, maxVec, Xmax.getAdd(Ymax).getAdd(Zmax));
		getMaxAndMin(minVec, maxVec, Xmax.getAdd(Ymax).getAdd(Zmin));
		getMaxAndMin(minVec, maxVec, Xmax.getAdd(Ymin).getAdd(Zmax));
		getMaxAndMin(minVec, maxVec, Xmax.getAdd(Ymin).getAdd(Zmin));
		getMaxAndMin(minVec, maxVec, Xmin.getAdd(Ymax).getAdd(Zmax));
		getMaxAndMin(minVec, maxVec, Xmin.getAdd(Ymax).getAdd(Zmin));
		getMaxAndMin(minVec, maxVec, Xmin.getAdd(Ymin).getAdd(Zmax));
		getMaxAndMin(minVec, maxVec, Xmin.getAdd(Ymin).getAdd(Zmin));
		aabb=new AxisAlignedBB(minVec, maxVec);
	//	System.out.println(aabb);
		setAxisAlignedBB(aabb.offset(getPosition()));
	}
	
	
	public CubeMap getMap() {
		return map;
	}
	public void setMap(CubeMap map) {
		this.map = map;
	}
	public float getTextureXoffset() {
		int column=textureIndex%this.texturedModel.get(0).getTexture().getNumberOfRows();
		return (float)column/(float)this.texturedModel.get(0).getTexture().getNumberOfRows();
	}
	public float getTextureYoffset() {
		int column=textureIndex/this.texturedModel.get(0).getTexture().getNumberOfRows();
		return (float)column/(float)this.texturedModel.get(0).getTexture().getNumberOfRows();
	}
	public float getTextureXoffset(int i) {
		int column=textureIndex%this.texturedModel.get(i).getTexture().getNumberOfRows();
		return (float)column/(float)this.texturedModel.get(i).getTexture().getNumberOfRows();
	}
	public float getTextureYoffset(int i) {
		int column=textureIndex/this.texturedModel.get(i).getTexture().getNumberOfRows();
		return (float)column/(float)this.texturedModel.get(i).getTexture().getNumberOfRows();
	}
	public void increaseValue(float dx,float dy,float dz) {
		this.position.x+=dx;
		this.position.y+=dy;
		this.position.z+=dz;
		setTransformationMatrix();
	}
	public void increaseRotation(float dx,float dy,float dz) {
		this.rotX+=dx;
		this.rotY+=dy;
		this.rotZ+=dz;
		setTransformationMatrix();
	}

	public TexturedModel getTexturedModel() {
		return this.texturedModel.get(0);
	}
	public List<TexturedModel> getListTexturedModel() {
		return this.texturedModel;
	}
	public TexturedModel getTexturedModel(int i) {
		return this.texturedModel.get(i);
	}
	public String getMtlPass() {
		return mtlPass;
	}
	public void setMtlPass(String mtlPass) {
		this.mtlPass = mtlPass;
	}
	public void setTexturedModel(List<TexturedModel> texturedModel,String mtlPath) {
		this.mtlPass=mtlPath;
		this.texturedModel = texturedModel;
	}
	public void addTexturedModel(TexturedModel texturedModel) {
		this.texturedModel.add( texturedModel);
		if(texturedModel.getTexture().getRefractionMaterial()>0 ||texturedModel.getTexture().getReflectionMaterial()>0)this.haveCubeMap=true;
	}
	public Vector3 getPosition() {
		return position;
	}
	public void setPosition(Vector3 position) {
		this.position = position;
		setTransformationMatrix();
	}

	public float getRotX() {
		return this.rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
		setTransformationMatrix();
	}

	public float getRotY() {
		return this.rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
		setTransformationMatrix();
	}

	public float getRotZ() {
		return this.rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
		setTransformationMatrix();
	}

	public float getScale() {
		return this.scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
		setTransformationMatrix();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setVegetation(boolean vegetation) {
		this.vegetation = vegetation;
	}
	public boolean isVegetation() {
		return vegetation;
	}
	public boolean isHaveCubeMap() {
		return haveCubeMap;
	}
	public AxisAlignedBB getAxisAlignedBB() {
		return axisAlignedBB;
	}
	public void setAxisAlignedBB(AxisAlignedBB axisAlignedBB) {
		this.axisAlignedBB = axisAlignedBB;
	}
    public static void getMaxAndMin(Vector3 min,Vector3 max,Vector3 vec) {
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
    public Matrix4f getTransformationMatrix() {
		return transformationMatrix;
	}
    public void setTransformationMatrix() {
		this.transformationMatrix = Maths.createTransfromationMatrix(getPosition().getOglVec(), getRotX(), getRotY(), getRotZ(), getScale());
	}

	public void setRenderingDistance(float renderingDistance) {
		this.renderingDistance = renderingDistance;
	}

	public float getRenderingDistance() {
		return renderingDistance;
	}

	public boolean inFrustum(Frustum frustum){
		if (this.useSphereFrustum){
			return frustum.sphereInFrustum(position.x,position.y,position.z,radiusSphere);
		}else{
			return frustum.cubeInFrustum((float) getAxisAlignedBB().minX, (float) getAxisAlignedBB().minY, (float) getAxisAlignedBB().minZ, (float) getAxisAlignedBB().maxX, (float) getAxisAlignedBB().maxY, (float) getAxisAlignedBB().maxZ);
		}
	}

}
