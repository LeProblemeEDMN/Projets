package old;

import entity.Entity;
import loading.CubeMap.CubeMap;
import loading.CubeMap.CubeMapCamera;
import loading.TexturedModel;
import main.Constantes;
import main.MainLoop;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.joml.*;
import rendering.sky.SkyboxRenderer;
import toolbox.maths.Frustum;

import java.lang.Math;
import java.util.List;
import java.util.Map;



public class EnviroMapRenderer {
	//static EntityInitRender renderer=new EntityInitRender();
	static EntityCubeMapRender renderer=new EntityCubeMapRender();
	static SkyboxRenderer skyboxRenderer;
	static int fbo,depthBuffer;
	public static void init(int size) {


		CubeMapCamera.FAR=1000;
		skyboxRenderer=new SkyboxRenderer(MainLoop.LOADER, createProjectionMatrixCubeMapSkyBox(1000));
		
		//create fbo
		fbo = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);

		//attach depth buffer
		depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, size, size);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER,
				depthBuffer);

		//indicate that we want to render to the entire face
		GL11.glViewport(0, 0, size,size);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
	public static void renderEnvironmentMap(Map<TexturedModel, List<Entity>> entities, CubeMap cubeMap, Vector3f center, Entity entity, int size) {

		CubeMapCamera camera = new CubeMapCamera(center);
		renderer.initProj(camera.getPnMatrix());


		//loop faces
		for (int i = 0; i < 6; i++) {

			//attach face to fbo as color attachment 0
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
					GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, cubeMap.getTexture(), 0);
			
			GL11.glClearColor(Constantes.SKY_COLOR.getVector3().x,Constantes.SKY_COLOR.getVector3().y, Constantes.SKY_COLOR.getVector3().z, 	2);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);
			
			//point camera in the right direction
			camera.switchToFace(i);

			//render scene to fbo, and therefore to the current face of the cubemap
			skyboxRenderer.render(camera.getvMatrix());
			renderer.initRender(camera.getvMatrix());
			Frustum frustum=Frustum.getFrustum(camera.getvMatrix(), camera.getPnMatrix());
			renderer.render(entities,entity,frustum);

		
		}
		GL13.glActiveTexture(GL13.GL_TEXTURE0 );
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cubeMap.getTexture());
		GL30.glGenerateMipmap(GL13.GL_TEXTURE_CUBE_MAP);

		
	}
	public static void cleanUp() {		//stop rendering to fbo
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		
		//delete fbo
		GL30.glDeleteRenderbuffers(depthBuffer);
		GL30.glDeleteFramebuffers(fbo);



		skyboxRenderer.cleanUp();

	}
	public static Matrix4f createProjectionMatrixCubeMapSkyBox(float FAR) {
		Matrix4f pnMatrix=new Matrix4f();

		float y_scale = (float) ((1f / Math.tan(Math.toRadians(CubeMapCamera.FOV_CUBEMAP / 2f))));
		float x_scale = y_scale / CubeMapCamera.ASPECT_RATIO;
		float frustum_length = FAR - CubeMapCamera.NEAR;

		pnMatrix.m00( x_scale);
		pnMatrix.m11(  y_scale);
		pnMatrix.m22(  -((FAR + CubeMapCamera.NEAR) / frustum_length));
		pnMatrix.m23(  -1);
		pnMatrix.m32(  -((2 * CubeMapCamera.NEAR * FAR) / frustum_length));
		pnMatrix.m33(  0);
		return pnMatrix;
	}
}
