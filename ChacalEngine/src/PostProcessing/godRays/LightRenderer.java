package PostProcessing.godRays;

import Entity.Light.Light;
import Loader.ConfigLoader;
import PostProcessing.ImageRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;

import java.util.List;

public class LightRenderer {
	private ImageRenderer renderer;
	private LightShader shader;
	
	public LightRenderer(int targetFboWidth, int targetFboHeight,ConfigLoader configLoader){
		shader = new LightShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadExposures(configLoader.getFloatParameter("godRayExposures"));
		shader.loadNumSamples(configLoader.getIntParameter("godRayNumberSamples"));
		shader.loadDensity(configLoader.getFloatParameter("godRayDensity"));
		shader.loadDecay(configLoader.getFloatParameter("godRayDecay"));
		shader.loadWeight(configLoader.getFloatParameter("godRayWeight"));
		shader.rayon.loadFloat(0.0001f);
		shader.stop();
		renderer = new ImageRenderer(targetFboWidth, targetFboHeight);
	//	MainRender.guis.add(new GuiTexture(renderer.getOutputTexture(), new Vector2f(-0.6f, -0.6f), new Vector2f(0.15f, 0.15f)));
		
	}
	
	public void render(int depthTexture,int cloudTexture){
		shader.start();
		shader.loadSunPos();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, cloudTexture);
		renderer.renderQuad();

		shader.stop();
		
	}
	
	public void cleanUp(){
		renderer.cleanUp();
		shader.cleanUp();
	}
	public int getOutputTexture(){
		return renderer.getOutputTexture();
	}
}
