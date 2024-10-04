package Init.LightInit;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import Entity.Entity;
import Init.EnviroMapRenderer;
import Init.Renderer.EntityCubeMapRender;
import Loader.TexturedModel;
import Loader.CubeMap.CubeMapCamera;
import Main.MainLoop;
import Main.MainRender;
import RenderEngine.Fbo;
import RenderEngine.gui.GuiTexture;
import RenderEngine.sky.SkyboxRenderer;
import toolbox.maths.Frustum;
import toolbox.maths.Vector3;

public class PointLightInit2 {
	private static Fbo frameBuffer;
	private static EntityCubeMapRender renderer=new EntityCubeMapRender();
	
	public static void initLight(Vector3 pos,Map<TexturedModel, List<Entity>> entities,Map<TexturedModel, List<Entity>> entitiesNM,Entity entity,int cubeMapSize) {

			
			CubeMapCamera.FAR=1000;
			CubeMapCamera camera = new CubeMapCamera(pos.getOglVec());
			SkyboxRenderer skyboxRenderer=new SkyboxRenderer(MainLoop.LOADER, EnviroMapRenderer.createProjectionMatrixCubeMapSkyBox(1000));
			renderer.initProj(camera.getPnMatrix());
			//loop faces
			for (int i = 0; i < 6; i++) {
				frameBuffer=new Fbo(cubeMapSize, cubeMapSize,Fbo.DEPTH_TEXTURE);

frameBuffer.bindFrameBuffer();

GL11.glEnable(GL11.GL_DEPTH_TEST);
GL11.glClearColor(Main.MainRender.SKY_COLOR.x,Main.MainRender.SKY_COLOR.y, Main.MainRender.SKY_COLOR.z, 	2);
GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);
				camera.switchToFace(i);
				//render scene to fbo, and therefore to the current face of the cubemap
				skyboxRenderer.render(camera.getvMatrix());
				renderer.initRender(camera.getvMatrix());
				Frustum frustum=Frustum.getFrustum(camera.getvMatrix(), camera.getPnMatrix());
				renderer.render(entities,entity,frustum);
				renderer.getShader().start();
				renderer.render(entitiesNM,entity,frustum);
				frameBuffer.unbindFrameBuffer();
				GuiTexture texture=null;
			
			switch (i) {
			case 0:
				texture=new GuiTexture(frameBuffer.getColourTexture(), new Vector2f(-0.9f, -0.6f), new Vector2f(0.15f, 0.15f));
				break;
			case 1:
				texture=new GuiTexture(frameBuffer.getColourTexture(), new Vector2f(-0.3f, -0.6f), new Vector2f(0.15f, 0.15f));
				break;
			case 2:
				texture=new GuiTexture(frameBuffer.getColourTexture(), new Vector2f(-0.6f, -0.3f), new Vector2f(0.15f, 0.15f));
				break;
			case 3:
				texture=new GuiTexture(frameBuffer.getColourTexture(), new Vector2f(-0.6f, -0.9f), new Vector2f(0.15f, 0.15f));
				break;
			case 4:
				texture=new GuiTexture(frameBuffer.getColourTexture(), new Vector2f(0f, -0.6f), new Vector2f(0.15f, 0.15f));
				break;
			case 5:
				texture=new GuiTexture(frameBuffer.getColourTexture(), new Vector2f(-0.6f, -0.6f), new Vector2f(0.15f, 0.15f));
				break;
			}
				MainRender.guis.add(texture);
			}
		
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		}
	public void cleanUp() {
	}
}
