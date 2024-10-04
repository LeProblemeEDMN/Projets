package RenderEngine.shadow;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import Animation.animatedModel.Joint;
import Entity.Entity;

import Loader.RawModel;
import Loader.TexturedModel;
import Main.MainGame;
import RenderEngine.MasterRenderer;
import RenderEngine.shadow.EntityShadow.EntityShadowShader;


public class EntityShadowRender {
	private EntityShadowShader shader;
	public EntityShadowRender() {
		shader=new EntityShadowShader();
	}
	
	public void render(Map<TexturedModel, List<Entity>>models,Matrix4f mat) {
		shader.start();
		shader.VPMatrix.loadMatrix4f(mat);
		for (TexturedModel model : models.keySet()) {
			RawModel rawModel = model.getRawModel();
			begin(rawModel);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureId());
			if(model.getTexture().isTransparance()) {
				MasterRenderer.disableCulling();
			}
			
			for (Entity entity : models.get(model)) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
			}
			if(model.getTexture().isTransparance()) {
				MasterRenderer.enableCulling();
			
			}
		}
		end();
		shader.stop();
	}
		private void prepareInstance(Entity entity) {	
			shader.modelMatrix.loadMatrix4f(entity.getTransformationMatrix());
		}
	
	public void begin(RawModel model) {
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		
	}
	public void end() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(10);
		GL30.glBindVertexArray(0);
	}
	public void cleanUp() {
		shader.cleanUp();
	}
}
