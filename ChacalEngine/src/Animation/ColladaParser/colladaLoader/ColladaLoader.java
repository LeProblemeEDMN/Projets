package Animation.ColladaParser.colladaLoader;

import Animation.ColladaParser.dataStructures.AnimatedModelData;
import Animation.ColladaParser.dataStructures.AnimationData;
import Animation.ColladaParser.dataStructures.MeshData;
import Animation.ColladaParser.dataStructures.SkeletonData;
import Animation.ColladaParser.dataStructures.SkinningData;
import Animation.ColladaParser.xmlParser.XmlNode;
import Animation.ColladaParser.xmlParser.XmlParser;
import Loader.Texture.MyFile;

public class ColladaLoader {

	public static AnimatedModelData loadColladaModel(String colladaFile, int maxWeights) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);

		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
		SkinningData skinningData = skinLoader.extractSkinData();

		SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder);
		SkeletonData jointsData = jointsLoader.extractBoneData();

		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
		MeshData meshData = g.extractModelData();

		return new AnimatedModelData(meshData, jointsData);
	}

	public static AnimationData loadColladaAnimation(String colladaFile) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);
		XmlNode animNode = node.getChild("library_animations");
		XmlNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
		AnimationData animData = loader.extractAnimation();
		return animData;
	}

}
