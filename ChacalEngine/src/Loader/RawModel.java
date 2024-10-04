package Loader;

import toolbox.maths.AxisAlignedBB;
import toolbox.maths.ProgramStats;

public class RawModel {
	private int vaoID;
	private int vertexCount;
	private String modelpath;
	private AxisAlignedBB aabb;
	public int getVaoID() {
		return vaoID;
	}
	public RawModel(int vaoID, int vertexCount,String modelPath) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.modelpath=modelPath;
}
	/**
	 * @return The number of vertices in the model.
	 */
	public int getVertexCount() {
		ProgramStats.totalPoint+=vertexCount;
		return vertexCount;
}
	public String getModelpath() {
		return modelpath;
	}
	public void setAabb(AxisAlignedBB aabb) {
		this.aabb = aabb;
	}
	public AxisAlignedBB getAabb() {
		return aabb;
	}
}
