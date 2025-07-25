package entity;

import loading.CubeMap.CubeMap;
import loading.TexturedModel;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import org.joml.Vector4f;
import toolbox.maths.AxisAlignedBB;
import toolbox.maths.Frustum;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

import java.util.ArrayList;
import java.util.List;

public class Entity {
	//texture model of the object
	private TexturedModel texturedModel;
	//position and rotation of the object
	protected Vector3 position;
	protected float rotX,rotY,rotZ;
	//sclae of the object
	private Vector3 scale;
	//does the model have a cube map for reflection and refraction
	private boolean haveCubeMap=false;

	protected Matrix4f transformationMatrix=new Matrix4f();
	private AxisAlignedBB axisAlignedBB;
	//lights that light the object
	public List<Integer>spotIds=new ArrayList<>();
	public List<Integer>pointIds=new ArrayList<>();
	//when this object should not be rendered anymore
	public float renderingDistance=10000;
	//is the frustum test realized with a sphere or a cube
	public boolean useSphereFrustum=false;
	public float radiusSphere=1;

	public int temp;//for data

	public boolean large=false;

	public Entity(TexturedModel texturedModel, Vector3 position, float rotX, float rotY, float rotZ, float scale) {

		setTexturedModel(texturedModel);
		this.position = new Vector3(position);
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = new Vector3(scale,scale,scale);

		setAxisAlignedBB(new AxisAlignedBB(0,0,0,0,0,0));
		setTransformationMatrix();
		updateAABB();

		
	}
	public Entity(TexturedModel texturedModel, Vector3 position, float rotX, float rotY, float rotZ, Vector3 scale_construct) {

		setTexturedModel(texturedModel);
		this.position = new Vector3(position);
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale_construct;

		setAxisAlignedBB(new AxisAlignedBB(0,0,0,0,0,0));
		setTransformationMatrix();
		updateAABB();


	}

	//fonction a appeler pour update la position d'un objet
	public void update(){
		setTransformationMatrix();
		updateAABB();

	}

	//perceived size of the object on the screen.
	public float screen_percent(Vector3 position){
		double distance= axisAlignedBB.getCenter().squareDistanceTo(position);
		double maxSize= Math.max(axisAlignedBB.maxX-axisAlignedBB.minX,Math.max(axisAlignedBB.maxY-axisAlignedBB.minY,axisAlignedBB.maxZ-axisAlignedBB.minZ));
		return (float) Math.pow(distance*4/(maxSize*maxSize)+1,-0.5);
	}

	public void setLights(){
		pointIds.add(0);
	}

	/*update the AABB so it contains all the object in the world space*/
	public void updateAABB() {
		//pk mul par scale
		AxisAlignedBB aabb=getTexturedModel().getRawModel(0).getAabb().multiplySize(1.0f);
		/*aabb.minX*=scale.x;
		aabb.maxX*=scale.x;
		aabb.minY*=scale.y;
		aabb.maxY*=scale.y;
		aabb.minZ*=scale.z;
		aabb.maxZ*=scale.z;*/
		//fait une rotation de la base
		Vector4f e1=transformationMatrix.transform(new Vector4f(1,0,0,0));
		Vector4f e2=transformationMatrix.transform(new Vector4f(0,1,0,0));
		Vector4f e3=transformationMatrix.transform(new Vector4f(0,0,1,0));

		Vector3 Xmax=new Vector3(e1.x,e1.y,e1.z).mul((float)aabb.maxX);
		Vector3 Xmin=new Vector3(e1.x,e1.y,e1.z).mul((float)aabb.minX);
		Vector3 Ymax=new Vector3(e2.x,e2.y,e2.z).mul((float)aabb.maxY);
		Vector3 Ymin=new Vector3(e2.x,e2.y,e2.z).mul((float)aabb.minY);
		Vector3 Zmax=new Vector3(e3.x,e3.y,e3.z).mul((float)aabb.maxZ);
		Vector3 Zmin=new Vector3(e3.x,e3.y,e3.z).mul((float)aabb.minZ);
		
		Vector3 minVec=Xmax.getAdd(Ymax).getAdd(Zmax);
		Vector3 maxVec=minVec.getMul(1);

		getMaxAndMin(minVec, maxVec, Xmax.getAdd(Ymax).getAdd(Zmin));
		getMaxAndMin(minVec, maxVec, Xmax.getAdd(Ymin).getAdd(Zmax));
		getMaxAndMin(minVec, maxVec, Xmax.getAdd(Ymin).getAdd(Zmin));
		getMaxAndMin(minVec, maxVec, Xmin.getAdd(Ymax).getAdd(Zmax));
		getMaxAndMin(minVec, maxVec, Xmin.getAdd(Ymax).getAdd(Zmin));
		getMaxAndMin(minVec, maxVec, Xmin.getAdd(Ymin).getAdd(Zmax));
		getMaxAndMin(minVec, maxVec, Xmin.getAdd(Ymin).getAdd(Zmin));
		minVec.add(getPosition());
		maxVec.add(getPosition());

		axisAlignedBB.minX=minVec.x;
		axisAlignedBB.minY=minVec.y;
		axisAlignedBB.minZ=minVec.z;
		axisAlignedBB.maxX=maxVec.x;
		axisAlignedBB.maxY=maxVec.y;
		axisAlignedBB.maxZ=maxVec.z;
	}



	public TexturedModel getTexturedModel() {
		return this.texturedModel;
	}
	//change the textured model
	public void setTexturedModel(TexturedModel texturedModel) {
		this.texturedModel = texturedModel;
		if(texturedModel.getTexture().getRefractionMaterial()>0 ||texturedModel.getTexture().getReflectionMaterial()>0)this.haveCubeMap=true;
	}
	public Vector3 getPosition() {
		return position;
	}
	public void setPosition(Vector3 position) {
		this.position = position;
		//setTransformationMatrix();
	}

	public float getRotX() {
		return this.rotX;
	}

	public void setRotX(float rotX) {
		this.rotX = rotX;
		//setTransformationMatrix();
	}

	public float getRotY() {
		return this.rotY;
	}

	public void setRotY(float rotY) {
		this.rotY = rotY;
		//setTransformationMatrix();
	}

	public float getRotZ() {
		return this.rotZ;
	}

	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
		//setTransformationMatrix();
	}

	public Vector3 getScale() {
		return this.scale;
	}

	public void setScale(float scale) {
		this.scale = new Vector3(scale,scale,scale);
		//setTransformationMatrix();
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


	public boolean isLarge() {
		return large;
	}

	public void setLarge(boolean large) {
		this.large = large;
	}
}
