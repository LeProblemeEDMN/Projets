package postProcessingEffect.gaussianBlur;

import main.DisplayManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import postProcessingEffect.SSGI.SSGIRenderer;
import rendering.capture_object.Fbo;
import rendering.capture_object.ImageRenderer;
import rendering.postprocessing.PostProcessing;
import rendering.postprocessing.PostProcessingEffect;

public class HorizontalBlur implements PostProcessingEffect {



	public static HorizontalBlur instance;
	public static String NAME="HorizontalBlur";
	/*render the effect with the input texture id_text. if start=true bind and unbind the posporcessing mesh*/
	public static void render(int id_text,boolean start){
		PostProcessing.renderEffect(NAME,id_text,start);
	}

	private ImageRenderer renderer;
	private HorizontalBlurShader shader;
	
	public HorizontalBlur(){
		shader = new HorizontalBlurShader();
		shader.start();
		shader.targetWidth.loadFloat(DisplayManager.getWidth());
		shader.stop();
		renderer = new ImageRenderer(DisplayManager.getWidth(),DisplayManager.getHeight());
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

	}

	public ImageRenderer getRenderer() {
	return renderer;
}

	public HorizontalBlurShader getShader() {
		return shader;
	}
}
