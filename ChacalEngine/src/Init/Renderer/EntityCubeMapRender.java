package Init.Renderer;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import Entity.Entity;
import Entity.Light.PointLight;
import Entity.Light.SpotLight;
import Loader.RawModel;
import Loader.TexturedModel;
import Main.MainGame;
import Main.MainRender;
import RenderEngine.MasterRenderer;
import ShaderEngine.entityI.EntityIShader;
import ShaderEngine.entityNMInit.EntityNMInitShader;
import toolbox.maths.AxisAlignedBB;
import toolbox.maths.Frustum;
import toolbox.maths.Maths;

public class EntityCubeMapRender {
	static EntityIShader shader;
	static EntityNMInitShader shaderNM;
	
	public static void initCube(List<SpotLight>spots,List<PointLight>points) {
		shader=new EntityIShader();
		shader.init();
		shader.loadSpot(spots);
		shader.loadPoints(points);
		
		shaderNM=new EntityNMInitShader();
		shaderNM.init();
		shaderNM.loadSpot(spots);
		shaderNM.loadPoints(points);
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
	
	public void render(Map<TexturedModel, List<Entity>> entities,Entity e,Frustum frustum) {
		for (TexturedModel model : entities.keySet()) {
			RawModel rawModel = model.getRawModel();
			bindModel(rawModel);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureId());
			shader.reflectivity.loadFloat(model.getTexture().getReflectivity());
			shader.shineDamper.loadFloat(model.getTexture().getShineDamper());
			shader.useFakeLightning.loadBoolean(model.getTexture().isUseFakeLightning());
			
			if(model.getTexture().isTransparance()) {
				MasterRenderer.disableCulling();
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
				MasterRenderer.enableCulling();
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
		shader.transformationMatrix.loadMatrix4f(entity.getTransformationMatrix());
		shader.loadSpotInUse(entity.spotIds);
		shader.loadPointInUse(entity.pointIds);
		
	}
	// normal Map ----------------------------------------------------------
	public void initRenderNM(Matrix4f viewMatrix) {
		shaderNM.start();
		shaderNM.loadViewMatrix(viewMatrix);
		shaderNM.loadPosition(MainGame.spots, MainGame.pointLigths, MainRender.CAMERA.getViewMatrix());
	}
	public void initProjNM(Matrix4f proj) {
		shaderNM.start();
		shaderNM.projectionMatrix.loadMatrix4f(proj);
		shaderNM.stop();
	}
	
	public void renderNM(Map<TexturedModel, List<Entity>> entities,Entity e,Frustum frustum) {
		for (TexturedModel model : entities.keySet()) {
			RawModel rawModel = model.getRawModel();
			bindModelNM(rawModel);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureId());
			GL13.glActiveTexture(GL13.GL_TEXTURE2);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getNormalMap());
			shaderNM.reflectivity.loadFloat(model.getTexture().getReflectivity());
			shaderNM.shineDamper.loadFloat(model.getTexture().getShineDamper());
			shaderNM.useFakeLightning.loadBoolean(model.getTexture().isUseFakeLightning());
			
			if(model.getTexture().isTransparance()) {
				MasterRenderer.disableCulling();
			}
			
			for (Entity entity : entities.get(model)) {
				if(e!=entity) {
				AxisAlignedBB aabb=entity.getAxisAlignedBB();
				if(frustum.cubeInFrustum((float)aabb.minX, (float)aabb.minY, (float)aabb.minZ, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ)) {
					prepareInstanceNM(entity);
					
				GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
				}}
			}
			if(model.getTexture().isTransparance()) {
				MasterRenderer.enableCulling();
			}
		}
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL30.glBindVertexArray(0);
		shaderNM.stop();
	}
	private void prepareInstanceNM(Entity entity) {	
		//Matrix4f modelMatrix = Maths.createTransfromationMatrix(entity.getPosition().getOglVec(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shaderNM.transformationMatrix.loadMatrix4f(entity.getTransformationMatrix());
		shaderNM.loadSpotInUse(entity.spotIds);
		shaderNM.loadPointInUse(entity.pointIds);
		
	}
	
	private void bindModel(RawModel rawModel) {
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
	}
	private void bindModelNM(RawModel rawModel) {
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
	}
	public static EntityIShader getShader() {
		return shader;
	}
	public static EntityNMInitShader getShaderNM() {
		return shaderNM;
	}
}
