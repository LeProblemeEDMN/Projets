package Init.Renderer;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import Entity.Entity;
import Loader.RawModel;
import Loader.TexturedModel;
import Main.MainGame;
import Main.MainRender;
import RenderEngine.MasterRenderer;
import ShaderEngine.entity.EntityShader;
import ShaderEngine.shadow.ShadowShader;
import ShaderEngine.shadowPointLight.ShadowPointLightShader;
import toolbox.maths.AxisAlignedBB;
import toolbox.maths.Frustum;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

public class EntityShadowRenderer {
	private ShadowShader shader=new ShadowShader();
	private ShadowPointLightShader shaderPL=new ShadowPointLightShader();
	//private EntityShader shader=new EntityShader();
	
	
	public void render(Map<TexturedModel, List<Entity>> entities,Matrix4f viewProjMatrix,Frustum frustum,int lightId) {
		shader.start();

		for (TexturedModel model : entities.keySet()) {
			RawModel rawModel = model.getRawModel();
			bindModel(rawModel);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureId());
			if(model.getTexture().isTransparance()) {
				MasterRenderer.disableCulling();
			}
			for (Entity entity : entities.get(model)) {
				AxisAlignedBB aabb=entity.getAxisAlignedBB();
				if(frustum.cubeInFrustum((float)aabb.minX, (float)aabb.minY, (float)aabb.minZ, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ)) {
				if(!entity.spotIds.contains(lightId))entity.spotIds.add(lightId);
				prepareInstance(entity,viewProjMatrix);
				GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
				}
			}
			if(model.getTexture().isTransparance()) {
				MasterRenderer.enableCulling();
			}
		}
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
	shader.stop();
	}
	
	private void prepareInstance(Entity entity,Matrix4f projectionViewMatrix) {
		
	//	Matrix4f modelMatrix = Maths.createTransfromationMatrix(entity.getPosition().getOglVec(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		
		Matrix4f mvpMatrix = Matrix4f.mul(projectionViewMatrix, entity.getTransformationMatrix(), null);
		shader.mvpMatrix.loadMatrix4f(mvpMatrix);
	}
	
	private void bindModel(RawModel rawModel) {
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
	}
	public void renderPL(Map<TexturedModel, List<Entity>> entities,Matrix4f viewProjMatrix,float far,Vector3 spotPos,Frustum frustum,int lightId) {
		shaderPL.start();
		shaderPL.vpMatrix.loadMatrix4f(viewProjMatrix);
		shaderPL.far.loadFloat(far);
		shaderPL.lightPos.loadVector3(spotPos);
		for (TexturedModel model : entities.keySet()) {
			RawModel rawModel = model.getRawModel();
			bindModel(rawModel);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureId());
			if(model.getTexture().isTransparance()) {
				MasterRenderer.disableCulling();
			}
			for (Entity entity : entities.get(model)) {
				AxisAlignedBB aabb=entity.getAxisAlignedBB();
				if(frustum.cubeInFrustum((float)aabb.minX, (float)aabb.minY, (float)aabb.minZ, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ)) {
				if(!entity.pointIds.contains(lightId)) {
					entity.pointIds.add(lightId);
				}
				prepareInstancePL(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
				}
				
			}
			if(model.getTexture().isTransparance()) {
				MasterRenderer.enableCulling();
			}
		}
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		shaderPL.stop();
	}
private void prepareInstancePL(Entity entity) {
		
	//	Matrix4f modelMatrix = Maths.createTransfromationMatrix(entity.getPosition().getOglVec(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		
		//Matrix4f mvpMatrix = Matrix4f.mul(projectionViewMatrix, modelMatrix, null);
		shaderPL.modelMatrix.loadMatrix4f(entity.getTransformationMatrix());
	}
public void cleanUp() {
	shader.cleanUp();
	shaderPL.cleanUp();
}
public ShadowPointLightShader getShaderPL() {
	return shaderPL;
}
public ShadowShader getShader() {
	return shader;
}
}
