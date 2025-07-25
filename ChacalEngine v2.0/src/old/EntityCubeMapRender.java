package old;

import ShaderEngine.entityOld.EntityShaderOld;
import entity.Entity;
import entity.Light.PointLight;
import entity.Light.SpotLight;
import loading.RawModel;
import loading.TexturedModel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.joml.*;
import toolbox.OpenGlUtils;
import toolbox.maths.AxisAlignedBB;
import toolbox.maths.Frustum;

import java.util.List;
import java.util.Map;

public class EntityCubeMapRender {
	static EntityShaderOld shader;
	//static EntityNMInitShader shaderNM;
	
	public static void initCube(List<SpotLight>spots,List<PointLight>points) {
		shader=new EntityShaderOld();
		shader.init(spots);
		shader.loadSpot(spots);
		shader.loadPoints(points);
		
		/*shaderNM=new EntityNMInitShader();
		shaderNM.init();
		shaderNM.loadSpot(spots);
		shaderNM.loadPoints(points);*/
	}
	public void initRender(Matrix4f viewMatrix) {
		shader.start();
		shader.loadViewMatrix(viewMatrix);
	}
	public void initProj(Matrix4f proj) {
		shader.start();
		shader.projectionMatrix.loadMatrix4f(proj);
		shader.stop();
	}
	
	public void render(Map<TexturedModel, List<Entity>> entities, Entity e, Frustum frustum) {
		for (TexturedModel model : entities.keySet()) {
			RawModel rawModel = null;// model.getRawModel();
			bindModel(rawModel);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureId());
			shader.reflectivity.loadFloat(model.getTexture().getReflectivity());
			shader.shineDamper.loadFloat(model.getTexture().getShineDamper());
			shader.useFakeLightning.loadBoolean(model.getTexture().isUseFakeLightning());

			if(model.getTexture().isTransparance()) {
				OpenGlUtils.cullBackFaces(false);
			}
			
			for (Entity entity : entities.get(model)) {
				if(e!=entity) {
				AxisAlignedBB aabb=entity.getAxisAlignedBB();
				if(frustum.cubeInFrustum((float)aabb.minX, (float)aabb.minY, (float)aabb.minZ, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ)) {
					prepareInstance(entity);
					
				GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
				}}
			}
			if(model.getTexture().isTransparance()) {
				OpenGlUtils.cullBackFaces(true);
			}
		}
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	shader.stop();
	}
	
	private void prepareInstance(Entity entity) {	
	//	Matrix4f modelMatrix = Maths.createTransfromationMatrix(entity.getPosition().getOglVec(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		System.out.println("BESOIN D'IMPLEM L4INSTANCE RENDERING PR LES CUBE RENDER MAP! ");
		//shader.transformationMatrix.loadMatrix4f(entity.getTransformationMatrix());
		shader.loadSpotInUse(entity.spotIds);
		shader.loadPointInUse(entity.pointIds);
		
	}
	private void bindModel(RawModel rawModel) {
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
	}


}
