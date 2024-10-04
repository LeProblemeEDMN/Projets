package PostProcessing.bloom;

import PostProcessing.ImageRenderer;
import RenderEngine.DisplayManager;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector2f;

public class CombineFilter {
	
	private ImageRenderer renderer;
	private CombineShader shader;
	public float exposure=1;
	
	public CombineFilter(float contrast,float exposure){
		shader = new CombineShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadContrast(contrast);
		shader.loadExposure(exposure);
		this.exposure=exposure;
		shader.stop();
		renderer = new ImageRenderer(Display.getWidth(),Display.getHeight());
		//renderer = new ImageRenderer();
	}

	long t=0;
	double x=0;
	double y=0;
	double vx=0;
	double vy=0;
	public void render(int colourTexture,int scatteringTexture,int godTexture){
		shader.start();
		if(System.currentTimeMillis()>t){
			t=(long)(Math.random()*1000)+System.currentTimeMillis();
			vx=Math.random()*0.04-0.02;
			vy=Math.random()*0.04-0.02;
		}
		double dt=(double)Math.max(t-System.currentTimeMillis(),1)/1000d;
		double dx=vx-x;
		x+=dx/dt* DisplayManager.getFrameTimeSecond();
		double dy=vy-y;
		y+=dy/dt* DisplayManager.getFrameTimeSecond();
		shader.loadMove(new Vector2f((float)(x+0.02),(float)(y+0.02)));


		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, godTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, scatteringTexture);

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
	public void loadExposure(){
		shader.start();
		shader.loadExposure(this.exposure);
		shader.stop();
	}
}
