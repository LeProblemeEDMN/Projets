package RenderEngine.sky;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import Entity.Camera;
import Loader.Loader;
import Loader.RawModel;
import Main.MainGame;
import Main.MainRender;

public class SkyboxRenderer {
	public static float SIZE = 500f;
	private static final float[] VERTICES = {        
		    -SIZE,  SIZE, -SIZE,
		    -SIZE, -SIZE, -SIZE,
		    SIZE, -SIZE, -SIZE,
		     SIZE, -SIZE, -SIZE,
		     SIZE,  SIZE, -SIZE,
		    -SIZE,  SIZE, -SIZE,

		    -SIZE, -SIZE,  SIZE,
		    -SIZE, -SIZE, -SIZE,
		    -SIZE,  SIZE, -SIZE,
		    -SIZE,  SIZE, -SIZE,
		    -SIZE,  SIZE,  SIZE,
		    -SIZE, -SIZE,  SIZE,

		     SIZE, -SIZE, -SIZE,
		     SIZE, -SIZE,  SIZE,
		     SIZE,  SIZE,  SIZE,
		     SIZE,  SIZE,  SIZE,
		     SIZE,  SIZE, -SIZE,
		     SIZE, -SIZE, -SIZE,

		    -SIZE, -SIZE,  SIZE,
		    -SIZE,  SIZE,  SIZE,
		     SIZE,  SIZE,  SIZE,
		     SIZE,  SIZE,  SIZE,
		     SIZE, -SIZE,  SIZE,
		    -SIZE, -SIZE,  SIZE,

		    -SIZE,  SIZE, -SIZE,
		     SIZE,  SIZE, -SIZE,
		     SIZE,  SIZE,  SIZE,
		     SIZE,  SIZE,  SIZE,
		    -SIZE,  SIZE,  SIZE,
		    -SIZE,  SIZE, -SIZE,

		    -SIZE, -SIZE, -SIZE,
		    -SIZE, -SIZE,  SIZE,
		     SIZE, -SIZE, -SIZE,
		     SIZE, -SIZE, -SIZE,
		    -SIZE, -SIZE,  SIZE,
		     SIZE, -SIZE,  SIZE
		};
	public static String[] TEXTURE_FILES= {"skybox/right","skybox/left","skybox/top","skybox/bottom","skybox/back","skybox/front"};
	
	private RawModel cube;
	private int texture;
	private SkyboxShader shader;
	public SkyboxRenderer (Loader loader,Matrix4f projectionMatrix) {
		cube=loader.loadToVAO(VERTICES, 3,"null");
		texture=loader.loadCubeMap(TEXTURE_FILES);
		shader=new SkyboxShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	public void render(Matrix4f vMatrix4f) {
		shader.start();
		GL11.glDepthMask(false);
		shader.loadViewMatrix(vMatrix4f);
		GL30.glBindVertexArray(cube.getVaoID());
		shader.loadLight(MainGame.pointLigths.get(0));
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL11.glDepthMask(true);
		shader.stop();
	}
	public void cleanUp() {
		shader.cleanUp();
	}
public void updateProjectionMatrix() {
	shader.start();
	shader.loadProjectionMatrix(MainRender.CAMERA.getProjectionMatrix());
	shader.stop();
}








}
