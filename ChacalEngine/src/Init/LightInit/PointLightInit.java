package Init.LightInit;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import Entity.Entity;
import Entity.Light.PointLight;
import Entity.Light.SpotLight;
import Init.Renderer.EntityShadowRenderer;
import Loader.TexturedModel;
import Loader.CubeMap.CubeMap;
import Loader.CubeMap.CubeMapCamera;
import Main.MainGame;
import Main.MainLoop;
import Main.MainRender;
import RenderEngine.Fbo;
import toolbox.maths.Frustum;
import toolbox.maths.Vector3;

public class PointLightInit {
	private static Fbo frameBuffer;
	private static EntityShadowRenderer shadowRenderer=new EntityShadowRenderer();
	
	public static void initLight(List<PointLight>lights,Map<TexturedModel, List<Entity>> entities,int cubeMapSize) {
		for (int j=0;j<lights.size();j++) {
			PointLight spotLight=lights.get(j);	
			//frameBuffer=new Fbo(cubeMapSize, cubeMapSize,Fbo.DEPTH_TEXTURE);
			CubeMap cubeMap=new CubeMap(CubeMap.newEmptyCubeMap(cubeMapSize),MainLoop.LOADER,cubeMapSize);
			CubeMapCamera.FAR=spotLight.getMaxRange();
			
			CubeMapCamera camera = new CubeMapCamera(spotLight.getPosition().getOglVec());
			
			//create fbo
			int fbo = GL30.glGenFramebuffers();
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
			GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
			
			//attach depth buffer
			int depthBuffer = GL30.glGenRenderbuffers();
			GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
			GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, cubeMap.getSize(), cubeMap.getSize());
			GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER,
					depthBuffer);
			//indicate that we want to render to the entire face
			GL11.glViewport(0, 0, cubeMap.getSize(), cubeMap.getSize());
			
			shadowRenderer.getShaderPL().start();
			shadowRenderer.getShaderPL().far.loadFloat(spotLight.getMaxRange());
			shadowRenderer.getShaderPL().stop();
			//loop faces
			for (int i = 0; i < 6; i++) {

				//attach face to fbo as color attachment 0
				GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
						GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, cubeMap.getTexture(), 0);
				
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glClearColor(1,1,1, 1);
				GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);
				
				//point camera in the right direction
				camera.switchToFace(i);
				//render scene to fbo, and therefore to the current face of the cubemap
				Frustum frustum=Frustum.getFrustum(camera.getvMatrix(), camera.getPnMatrix());
				shadowRenderer.renderPL(entities, camera.getProjectionViewMatrix(),spotLight.getMaxRange(),spotLight.getPosition(),frustum,j);
				shadowRenderer.renderPL(MainRender.masterRenderer.getEntitiesNormalMap(), camera.getProjectionViewMatrix(),spotLight.getMaxRange(),spotLight.getPosition(),frustum,j);
			}
			
			//stop rendering to fbo
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
			
			
			//delete fbo
			GL30.glDeleteRenderbuffers(depthBuffer);
			GL30.glDeleteFramebuffers(fbo);
			
			GL13.glActiveTexture(GL13.GL_TEXTURE1 );
	        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cubeMap.getTexture());
			GL30.glGenerateMipmap(GL13.GL_TEXTURE_CUBE_MAP);
			spotLight.setShadowMap(cubeMap);
		}
		//GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		//PointLightInit2.initLight(lights, entities, cubeMapSize);
		}
	
	public static void cleanUp() {
		shadowRenderer.cleanUp();
	}
}
