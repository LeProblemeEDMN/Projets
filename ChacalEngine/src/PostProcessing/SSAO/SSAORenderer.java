package PostProcessing.SSAO;



import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;

import Entity.Entity;
import Loader.RawModel;
import Loader.TexturedModel;
import Main.MainRender;
import Main.Register;
import PostProcessing.Fbo;
import PostProcessing.ImageRenderer;
import PostProcessing.PostProcessing;
import PostProcessing.SSAO.geometry.ShaderGeometry;
import PostProcessing.SSAO.normal.ShaderNormal;
import PostProcessing.SSAO.ssao.SSAOShader;
import PostProcessing.gaussianBlur.HorizontalBlur;
import PostProcessing.gaussianBlur.HorizontalBlurShader;
import PostProcessing.gaussianBlur.VerticalBlur;
import RenderEngine.MasterRenderer;
import RenderEngine.gui.GuiTexture;

public class SSAORenderer {
	private ShaderGeometry shaderGeometry=new ShaderGeometry();
	private SSAOShader ssaoShader=new SSAOShader();
	private ImageRenderer renderer;
	private ShaderNormal shaderNormal=new ShaderNormal();
	public Fbo geometryFbo,ssaoFbo,normalFbo;
	public HorizontalBlur horizontalBlur;
	private VerticalBlur verticalBlur;
		private int noise;
	
			public SSAORenderer() {
				ssaoFbo=new Fbo(Display.getWidth(), Display.getHeight(),Fbo.DEPTH_TEXTURE);
				geometryFbo=new Fbo(Display.getWidth(), Display.getHeight(),Fbo.POS_BUFFER);
				normalFbo=new Fbo(Display.getWidth(), Display.getHeight(),Fbo.DEPTH_TEXTURE);
				
				renderer = new ImageRenderer();
				horizontalBlur=new HorizontalBlur(Display.getWidth(), Display.getHeight());
				verticalBlur=new VerticalBlur(Display.getWidth(), Display.getHeight());
				
				MainRender.guis.add(new GuiTexture(geometryFbo.getColourTexture(), new Vector2f(-0.6f, -0.6f), new Vector2f(0.15f, 0.15f)));
				MainRender.guis.add(new GuiTexture(horizontalBlur.getOutputTexture(), new Vector2f(0f, -0.6f), new Vector2f(0.3f, 0.3f)));
				MainRender.guis.add(new GuiTexture(normalFbo.getColourTexture(), new Vector2f(0.6f, -0.6f), new Vector2f(0.15f, 0.15f)));
				
				noise=SSAO.createSSAONoiseTexture();
				//MainRender.guis.add(new GuiTexture(noise, new Vector2f(0.6f, -0f), new Vector2f(0.15f, 0.15f)));
				
			}
			
			public void render(Map<TexturedModel, List<Entity>> entities) {
			
				geometryFbo.bindFrameBuffer();
				shaderGeometry.start();
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);
				GL11.glClearColor(0,0, 0, 	2);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);
				
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
					
				shaderGeometry.stop();
				geometryFbo.unbindFrameBuffer();
				normal(entities);
				
				MasterRenderer.enableCulling();
				ssaoFbo.bindFrameBuffer();
				ssaoShader.start();
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);
				//GL11.glClearColor(Main.MainRender.SKY_COLOR.x,Main.MainRender.SKY_COLOR.y, Main.MainRender.SKY_COLOR.z, 	2);
				
				ssaoShader.projectionMatrix.loadMatrix4f(MainRender.CAMERA.getProjectionMatrix());
				ssaoShader.viewMatrix.loadMatrix4f(MainRender.CAMERA.getViewMatrix());
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D,geometryFbo.getColourTexture());
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D,normalFbo.getColourTexture());
				GL13.glActiveTexture(GL13.GL_TEXTURE2);
				GL11.glBindTexture(GL11.GL_TEXTURE_2D,noise);
				
				PostProcessing.start();
				
				renderer.renderQuad();
				
				ssaoShader.stop();
				ssaoFbo.unbindFrameBuffer();
			//	ssaoFbo.resolveToScreen();
				verticalBlur.render(ssaoFbo.getColourTexture());
				horizontalBlur.render(verticalBlur.getOutputTexture());
				PostProcessing.end();
				//horizontalBlur.getRenderer().getFbo().resolveToScreen();
			}
			}
			public void normal(Map<TexturedModel, List<Entity>> entities) {
				normalFbo.bindFrameBuffer();
				shaderNormal.start();
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);
				GL11.glClearColor(0,0, 0, 	2);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);
				
				shaderNormal.viewMatrix.loadMatrix4f(MainRender.CAMERA.getViewMatrix());
				shaderNormal.projectionMatrix.loadMatrix4f(MainRender.CAMERA.getProjectionMatrix());
				MasterRenderer.disableCulling();
				for (TexturedModel tm : entities.keySet()) {
					RawModel model = tm.getRawModel();
					GL30.glBindVertexArray(model.getVaoID());
					GL20.glEnableVertexAttribArray(0);
					GL20.glEnableVertexAttribArray(2);
					for (Entity entity : entities.get(tm)) {
						shaderNormal.transformationMatrix.loadMatrix4f(entity.getTransformationMatrix());
						
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
					}
					
				shaderNormal.stop();
				normalFbo.unbindFrameBuffer();
			}}
}
