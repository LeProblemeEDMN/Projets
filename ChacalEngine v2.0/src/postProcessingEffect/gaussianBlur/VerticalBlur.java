package postProcessingEffect.gaussianBlur;

import main.DisplayManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import postProcessingEffect.SSGI.SSGIRenderer;
import rendering.capture_object.Fbo;
import rendering.capture_object.ImageRenderer;
import rendering.postprocessing.PostProcessing;
import rendering.postprocessing.PostProcessingEffect;

public class VerticalBlur implements PostProcessingEffect {

	public static VerticalBlur instance;
	public static String NAME="VerticalBlur";
	/*render the effect with the input texture id_text. if start=true bind and unbind the posporcessing mesh*/
	public static void render(int id_text,boolean start){
		PostProcessing.renderEffect(NAME,id_text,start);
	}

	private ImageRenderer renderer;
	private VerticalBlurShader shader;
	
	public VerticalBlur(){
		shader = new VerticalBlurShader();
		renderer = new ImageRenderer(DisplayManager.getWidth(),DisplayManager.getHeight());
		shader.start();
		shader.targetHeight.loadFloat(DisplayManager.getHeight());
		shader.stop();
		PostProcessing.registerEffect(this);
	}

	
	public void render(int texture){
		shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		renderer.renderQuad();
		shader.stop();
	}



	public int getOutputTexture(){
		return renderer.getOutputTexture();
	}


	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Fbo getOutFbo() {
		return renderer.getFbo();
	}

	public void cleanUp(){
		renderer.cleanUp();
		shader.cleanUp();
	}

	@Override
	public void resize(int width, int height) {
		renderer.cleanUp();
		renderer = new ImageRenderer(width, height);
	}

	public ImageRenderer getRenderer() {
		return renderer;
	}

	public VerticalBlurShader getShader() {
		return shader;
	}
}
