package Animation.loaders;

import Animation.ColladaParser.colladaLoader.ColladaLoader;
import Animation.ColladaParser.dataStructures.AnimatedModelData;
import Animation.ColladaParser.dataStructures.JointData;
import Animation.ColladaParser.dataStructures.MeshData;
import Animation.ColladaParser.dataStructures.SkeletonData;
import Animation.animatedModel.AnimatedModel;
import Animation.animatedModel.Joint;
import Loader.RawModel;
import Loader.Texture.ModelTexture;
import Loader.Texture.MyFile;
import Main.MainLoop;


public class AnimatedModelLoader {

	/**
	 * Creates an AnimatedEntity from the data in an entity file. It loads up
	 * the collada model data, stores the extracted data in a VAO, sets up the
	 * joint heirarchy, and loads up the entity's texture.
	 * 
	 * @param entityFile
	 *            - the file containing the data for the entity.
	 * @return The animated entity (no animation applied though)
	 */
	public static AnimatedModel loadEntity(String modelFile, String textureFile) {
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile,3);//3=Max_WEIGHTS
		RawModel model = createVao(entityData.getMeshData());
		ModelTexture texture = loadTexture(textureFile);
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = createJoints(skeletonData.headJoint);
		return new AnimatedModel(model, texture, headJoint, skeletonData.jointCount);
	}

	/**
	 * Loads up the diffuse texture for the model.
	 * 
	 * @param textureFile
	 *            - the texture file.
	 * @return The diffuse texture.
	 */
	private static ModelTexture loadTexture(String textureFile) {
		ModelTexture diffuseTexture = new ModelTexture(MainLoop.LOADER.loadTexture(textureFile), textureFile);
		
		return diffuseTexture;
	}

	/**
	 * Constructs the joint-hierarchy skeleton from the data extracted from the
	 * collada file.
	 * 
	 * @param data
	 *            - the joints data from the collada file for the head joint.
	 * @return The created joint, with all its descendants added.
	 */
	private static Joint createJoints(JointData data) {
		Joint joint = new Joint(data.index, data.nameId, data.bindLocalTransform);
		for (JointData child : data.children) {
			joint.addChild(createJoints(child));
		}
		return joint;
	}

	/**
	 * Stores the mesh data in a VAO.
	 * 
	 * @param data
	 *            - all the data about the mesh that needs to be stored in the
	 *            VAO.
	 * @return The VAO containing all the mesh data for the model.
	 */
	private static RawModel createVao(MeshData data) {
		RawModel vao = MainLoop.LOADER.loadToVOA(data);
		
		return vao;
	}

}
