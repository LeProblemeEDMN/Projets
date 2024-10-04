package RenderEngine.PPRenderer;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;

import Entity.Entity;
import Loader.RawModel;
import Loader.TexturedModel;
import Main.MainRender;
import PostProcessing.Fbo;
import PostProcessing.SSAO.geometry.ShaderGeometry;
import RenderEngine.MasterRenderer;
import RenderEngine.gui.GuiTexture;

public class PPRenderer {
	private Fbo multisample,depth,bloom;
	private ShaderGeometry shaderGeometry=new ShaderGeometry();
	public PPRenderer() {
		//multisample=new Fbo(Display.getWidth(), Display.getWidth());
		depth=new Fbo(Display.getWidth(), Display.getWidth(),Fbo.DEPTH_TEXTURE);
		MainRender.guis.add(new GuiTexture(depth.getColourTexture(), new Vector2f(0f, -0.6f), new Vector2f(0.3f, 0.3f)));
		
	}
	
	public void render() {
		depth.bindFrameBuffer();
		shaderGeometry.start();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(1,1, 1, 	1);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);
		
		for (int i = 0; i < 2; i++) {
			Map<TexturedModel, List<Entity>> entities=MainRender.masterRenderer.getEntities();
			if(i>=1) entities=MainRender.masterRenderer.getEntitiesNormalMap();
		
		shaderGeometry.viewMatrix.loadMatrix4f(MainRender.CAMERA.getViewMatrix());
		shaderGeometry.projectionMatrix.loadMatrix4f(MainRender.CAMERA.getProjectionMatrix());
		MasterRenderer.disableCulling();
		for (TexturedModel tm : entities.keySet()) {
			RawModel model = tm.getRawModel();
			GL30.glBindVertexArray(model.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			for (Entity entity : entities.get(tm)) {
		shaderGeometry.transformationMatrix.loadMatrix4f(entity.getTransformationMatrix());
				
		GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(),
				GL11.GL_UNSIGNED_INT, 0);
			}
		}}
		shaderGeometry.stop();
		depth.unbindFrameBuffer();
	//	multisample.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0, depth);
	//	multisample.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1, bloom);
	}
	public int getOutTexture() {
		return depth.getColourTexture();
	}
}
