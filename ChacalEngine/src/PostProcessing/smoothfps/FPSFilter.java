package PostProcessing.smoothfps;

import Main.MainRender;
import PostProcessing.ImageRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class FPSFilter {
	
	private ImageRenderer renderer;
	private FPSShader shader;
	
	public FPSFilter(){
		shader = new FPSShader();
		shader.start();
		shader.connectTextureUnits();
		shader.stop();
		renderer = new ImageRenderer();
		//renderer = new ImageRenderer();
	}

	long t=0;
	double x=0;
	double y=0;
	double vx=0;
	double vy=0;
	public void render(int colourTexture,int colourTexture2){
		shader.start();

		float delta=1-(float)(System.currentTimeMillis()- MainRender.lastFrame)/MainRender.FPS_TIME;
		delta=1;
		shader.loadDelta(delta);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture2);

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
	public void resize() {
		renderer.cleanUp();
		renderer = new ImageRenderer(Display.getWidth(),Display.getHeight());
	}
}
