package loading;

import toolbox.maths.AxisAlignedBB;
import toolbox.maths.ProgramStats;

public class RawModel {
	//ID de la VAO containing the model data
	private int vaoID;
	//number of vertices of the model
	private int vertexCount;
	//Bounding box containing the model.
	private AxisAlignedBB aabb;
	public int getVaoID() {
		return vaoID;
	}
	public RawModel(int vaoID, int vertexCount) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
	}
	/**
	 * @return The number of vertices in the model.
	 */
	public int getVertexCount() {
		//count the total numebr of trangle rendered
		ProgramStats.totalPoint+=vertexCount;
		return vertexCount;
	}
	public void setAabb(AxisAlignedBB aabb) {
		this.aabb = aabb;
	}
	public AxisAlignedBB getAabb() {
		return aabb;
	}
}
