package Init.LightInit;

import java.util.List;
import java.util.Map;

import Main.MainGame;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Entity.Entity;
import Entity.Light.SpotLight;
import Init.Renderer.EntityShadowRenderer;
import Loader.TexturedModel;
import Main.MainRender;
import RenderEngine.Fbo;
import RenderEngine.ShadowFrameBuffer;
import RenderEngine.gui.GuiTexture;
import ShaderEngine.entity.EntityShader;
import toolbox.maths.Frustum;

public class SpotLightInit {
	private static Fbo frameBuffer;
	private static EntityShadowRenderer shadowRenderer=new EntityShadowRenderer();
	//private static EntityShader shadowRenderer=new EntityShader();
	public static void initSpot(List<SpotLight>lights,Map<TexturedModel, List<Entity>> entities,int width,int height) {
		//frameBuffer=new Fbo(width, height,Fbo.DEPTH_TEXTURE);
		for (int i=0;i<lights.size();i++) {
			SpotLight spotLight=lights.get(i);
			if(spotLight.fbo==null){
				spotLight.fbo=new Fbo(width, height,Fbo.DEPTH_TEXTURE);
				Matrix4f vm=createViewMatrix(spotLight.getPosition().getOglVec(), spotLight.getRotation().x, spotLight.getRotation().y);
				Matrix4f pm=createProjectionMatrix(width, height, spotLight.getMaxRange(), 0.1f,spotLight.getAngle()*2f);
				Matrix4f ViewProjMatrix=Matrix4f.mul(pm,vm , null);
				spotLight.setToShadowMapSpace(ViewProjMatrix);
				spotLight.setViewMatrix(vm);
				spotLight.setProjectionMatrix(pm);
			}
			frameBuffer=spotLight.fbo;
			

			frameBuffer.bindFrameBuffer();
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glClearColor(1,1,1, 1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);

			shadowRenderer.getShader().start();
			shadowRenderer.getShader().far.loadFloat(spotLight.getMaxRange());
			shadowRenderer.getShader().stop();
			
			Frustum frustum=Frustum.getFrustum(spotLight.getViewMatrix(), spotLight.getProjectionMatrix());
			//shadowRenderer.render(entities, spotLight.getToShadowMapSpace(),frustum,i);
			shadowRenderer.render(MainGame.entitesMap, spotLight.getToShadowMapSpace(),frustum,i);
			//shadowRenderer.render(MainRender.masterRenderer.getEntitiesNormalMap(), spotLight.getToShadowMapSpace(),frustum,i);
			
			frameBuffer.unbindFrameBuffer();
			GL13.glActiveTexture(GL13.GL_TEXTURE0 );
	        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, frameBuffer.getColourTexture());
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			spotLight.setShadowMap(frameBuffer.getColourTexture());
			//GuiTexture texture=new GuiTexture(frameBuffer.getColourTexture(), new Vector2f(0.75f, 0.75f),  new Vector2f(0.25f, 0.25f));
			//MainRender.guis.add(texture);
		}
	}
	
	private static Matrix4f createViewMatrix(Vector3f cameraPos,float pitch,float yaw) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(180), new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0), viewMatrix,
                viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;
    }
	private static Matrix4f createProjectionMatrix(int w,int h,float far,float near,float FOV){
    	Matrix4f projectionMatrix = new Matrix4f();
		float aspectRatio = (float) w / (float)h;
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = far - near;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((far + near) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * near * far) / frustum_length);
		projectionMatrix.m33 = 0;
		return projectionMatrix;
    }

	public static void cleanUp() {
		//shadowRenderer.cleanUp();
		
	}
}
