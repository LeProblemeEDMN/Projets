package Entity;

import org.lwjgl.util.vector.Vector3f;

import Loader.RawModel;
import Loader.TexturedModel;
import Loader.NormalObjLoader.NormalMappedObjLoader;
import Loader.ObjLoader.objFileLoader;
import Main.MainLoop;
import toolbox.maths.AxisAlignedBB;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

public class EntityWall extends Entity {
	private Vector3 scaleVector=new Vector3();
	public static RawModel baseModel=NormalMappedObjLoader.loadOBJ("res/cube.txt", MainLoop.LOADER);
	public EntityWall(TexturedModel texturedModel, Vector3 position, float rotX, float rotY, float rotZ, Vector3 scale,
			int textureindex, String name) {
		super(texturedModel, position, rotX, rotY, rotZ, 1, textureindex, name);
		scaleVector=scale;
		updateAABBWall();
		setTransformationMatrixWall();
	}
	public EntityWall(TexturedModel texturedModel, Vector3 position, float rotX, float rotY, float rotZ, Vector3 scale,String name) {
		super(texturedModel, position, rotX, rotY, rotZ, 1, name);
		scaleVector=scale;
		updateAABBWall();
		setTransformationMatrixWall();
	}

	public void setTransformationMatrixWall() {

		this.transformationMatrix = Maths.createTransfromationMatrix(getPosition().getOglVec(), getRotX(), getRotY(), getRotZ(), scaleVector.x,scaleVector.y,scaleVector.z);
	}

	public void updateAABBWall() {
		AxisAlignedBB aabb=new AxisAlignedBB(new Vector3(), scaleVector);
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

	public Vector3 getScaleVector() {
		return scaleVector;
	}

	public void setScaleVector(Vector3 scaleVector) {
		this.scaleVector = scaleVector;
	}
}
