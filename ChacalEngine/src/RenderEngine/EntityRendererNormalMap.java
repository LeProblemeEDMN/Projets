package RenderEngine;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import Entity.Entity;
import Loader.ConfigLoader;
import Loader.RawModel;
import Loader.TexturedModel;
import Main.MainGame;
import Main.MainLoop;
import Main.MainRender;
import ShaderEngine.entity.EntityShader;
import ShaderEngine.entityNormalMap.EntityNormalMapShader;
import toolbox.maths.Maths;

public class EntityRendererNormalMap {
	EntityNormalMapShader shader;

	
	public EntityRendererNormalMap() {
		shader=new EntityNormalMapShader();
		shader.init(MainGame.spots);
		
	}
	public void initRender() {
		shader.start();
		shader.loadViewMatrix(MainRender.CAMERA.getViewMatrix());
		shader.loadPosition(MainGame.spots, MainGame.pointLigths, MainRender.CAMERA.getViewMatrix());
	}
	
	public void render(Map<TexturedModel, List<Entity>> entities) {
	//	shader.loadSpotInUse(0);
	//	shader.loadPointInUse(0,1,2);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, MainRender.shadowMapMasterRenderer.getShadowMap());
		shader.toShadowMapSpace.loadMatrix4f(MainRender.shadowMapMasterRenderer.getToShadowMapSpaceMatrix());
		for (TexturedModel model : entities.keySet()) {
			RawModel rawModel = model.getRawModel();
			bindModel(rawModel);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureId());
			GL13.glActiveTexture(GL13.GL_TEXTURE2);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getNormalMap());
			shader.reflectivity.loadFloat(model.getTexture().getReflectivity());
			shader.shineDamper.loadFloat(model.getTexture().getShineDamper());
			shader.useFakeLightning.loadBoolean(model.getTexture().isUseFakeLightning());
			
			if(model.getTexture().isTransparance()) {
				MasterRenderer.disableCulling();
			}
			
			for (Entity entity : entities.get(model)) {
				prepareInstance(entity);
	
				if(entity.isHaveCubeMap()) {
					shader.materialValue.loadVector2D(new Vector2f(model.getTexture().getReflectionMaterial(), model.getTexture().getRefractionMaterial()));
					
					GL13.glActiveTexture(GL13.GL_TEXTURE1);
					GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP,entity.getMap().getTexture());
					//shader.materialValue.loadVector2D(new Vector2f(0, 0));
				}else {
					shader.materialValue.loadVector2D(new Vector2f(0, 0));
				}
				
				GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
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
	shader.stop();
	}
	
	private void prepareInstance(Entity entity) {	
		//Matrix4f modelMatrix = Maths.createTransfromationMatrix(entity.getPosition().getOglVec(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		//Matrix4f modelMatrix = Maths.createTransfromationMatrix(entity.getPosition().getOglVec(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		
		shader.transformationMatrix.loadMatrix4f(entity.getTransformationMatrix());
		shader.loadSpotInUse(entity.spotIds);
		shader.loadPointInUse(entity.pointIds);
		//System.out.println(entity.spotIds);
	}
	
	
	private void bindModel(RawModel rawModel) {
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
	}
	
	public void cleanUp() {
		shader.cleanUp();

	}
	public EntityNormalMapShader getShader() {
		return shader;
	}
	public void resize() {
		shader.start();
		shader.projectionMatrix.loadMatrix4f(MainRender.CAMERA.getProjectionMatrix());
		shader.stop();
		
	}
}
