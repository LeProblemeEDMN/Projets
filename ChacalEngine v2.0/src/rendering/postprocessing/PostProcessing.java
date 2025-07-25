package rendering.postprocessing;

import ShaderEngine.depth.DepthShader;
import loading.Loader;
import loading.RawModel;
import main.*;
import org.lwjgl.BufferUtils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import postProcessingEffect.DefferedRendering.DefferedRenderer;
import postProcessingEffect.FXAA.FXAA;
import postProcessingEffect.FXAA.FXAAShader;
import postProcessingEffect.SSAO.SSAO;
import postProcessingEffect.SSGI.SSGIRenderer;
import postProcessingEffect.ToneMapping.ToneMapping;
import postProcessingEffect.ToneMapping.ToneMappingShader;
import postProcessingEffect.gaussianBlur.HorizontalBlur;
import postProcessingEffect.gaussianBlur.VerticalBlur;
import rendering.capture_object.Fbo;
import rendering.deffered_rendering.Deffered_workflow;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;


public class PostProcessing {
	public static HashMap<String,PostProcessingEffect> EFFECTS = new HashMap<String,PostProcessingEffect>();

	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	public static RawModel quad;
	public static DepthShader depth_shader;
	private static Fbo out_fbo;

	public static void init(Loader loader){
		quad = loader.loadToVAO(POSITIONS, 2);
		//
		depth_shader=new DepthShader();
		depth_shader.init();
		Initialisation.init_post_processing();
		/*HorizontalBlur.instance=new HorizontalBlur();
		VerticalBlur.instance=new VerticalBlur();
		ToneMapping.instance=new ToneMapping();
		FXAA.instance=new FXAA();
		SSAO.instance=new SSAO();
		DefferedRenderer.instance=new DefferedRenderer();
		SSGIRenderer.instance=new SSGIRenderer();*/

	}

	public static void registerEffect(PostProcessingEffect effect){
		EFFECTS.put(effect.getName(),effect);
	}

	public static void renderEffect(String name,int id_text,boolean start){
		if(!EFFECTS.containsKey(name)){
			System.err.println("Cannot find and effect with the name"+name);
		}else{
			if(start)start();
			PostProcessingEffect effect = EFFECTS.get(name);
			effect.render(id_text);
			setOut_fbo(effect.getOutFbo());
			if(start)end();
		}
	}

	public static void renderEffect(String name,boolean start){
		if(!EFFECTS.containsKey(name)){
			System.err.println("Cannot find and effect with the name"+name);
		}else{
			if(start)start();
			PostProcessingEffect effect = EFFECTS.get(name);
			effect.render(out_fbo.getColourTexture());
			setOut_fbo(effect.getOutFbo());
			if(start)end();
		}
	}



	public static void blur(int id_text,boolean start){
		if(start)start();
		HorizontalBlur.render(id_text,false);
		VerticalBlur.render(HorizontalBlur.instance.getOutputTexture(), false);
		setOut_fbo(VerticalBlur.instance.getOutFbo());
		if(start)end();
	}



	public static void rendererDepth(int depthTexture){
		start();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);  // Permet d'écrire dans le z-buffer
		GL11.glColorMask(false,false,false,false);
		depth_shader.start();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);// model.getTexture().getTextureId());
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		depth_shader.stop();
		GL11.glColorMask(true,true,true,true);
		end();
		
	}

	public static Fbo getOut_fbo() {
		return out_fbo;
	}

	public static void setOut_fbo(Fbo out_fbo) {
		PostProcessing.out_fbo = out_fbo;
	}
	
	public static void start(){
		GL11.glDepthMask(false);
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	public static void end(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL11.glDepthMask(true);
	}

	public static void cleanUp(){
		for(PostProcessingEffect effect : EFFECTS.values()){
			effect.cleanUp();
		}
	}

	public static void resize(int width, int height) {
		for(PostProcessingEffect effect : EFFECTS.values()){
			effect.resize(width, height);
		}
	}
}
