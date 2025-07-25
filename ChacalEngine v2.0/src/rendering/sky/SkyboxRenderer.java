package rendering.sky;


import entity.Entity;
import loading.Loader;
import loading.RawModel;
import main.MainLoop;
import main.Pipeline;
import main.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.joml.*;
import rendering.render_objects.Renderer;
import toolbox.OpenGlUtils;

import java.util.List;

public class SkyboxRenderer extends Renderer {
	/*
	Renderer used to draw the sky. Display a skybox.

	 */
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

		cube=loader.loadToVAO(VERTICES, 3);
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
		shader.loadLight(World.pointLigths.get(0));
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cube.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL11.glDepthMask(true);
		shader.stop();
	}

	@Override
	public void render(List<Entity> entities,int vertexCount) {
		System.err.println("Essaie de rendre des entitées par le skybox renderer");
	}

	@Override
	public void resize(int w, int h) {
		updateProjectionMatrix();
	}

	public void cleanUp() {
		shader.cleanUp();
	}

	public void updateProjectionMatrix() {
		shader.start();
		shader.loadProjectionMatrix(Pipeline.getProjMat());
		shader.stop();
	}

}
