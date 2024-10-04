package PostProcessing;

import Loader.ConfigLoader;
import Loader.Loader;
import Loader.RawModel;
import Loader.Texture.ModelTexture;
import Main.MainGame;
import Main.MainLoop;
import Main.MainRender;
import Main.Register;
import PostProcessing.DLSS.DLSS;
import PostProcessing.LightScaterring.godRays.LightScatteringRenderer;
import PostProcessing.bloom.CombineFilter;
import PostProcessing.godRays.LightRenderer;
import PostProcessing.lensFlare.FlareManager;
import PostProcessing.lensFlare.FlareTexture;
import PostProcessing.smoothfps.FPSFilter;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import toolbox.InputManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;


public class PostProcessing {
	
	private static final float[] POSITIONS = { -1, 1, -1, -1, 1, 1, 1, -1 };	
	public static RawModel quad;

	public static CombineFilter combineFilter;
	public static CombineFilter combineFilter2;

	private static ConfigLoader config;
	public static LightRenderer lightRenderer;
	public static DLSS dlssScattering;
	public static DLSS dlssGodRays;

	public static LightScatteringRenderer scatRenderer;

	private static FPSFilter fps;

	public static FlareManager flareManager;

	private static int idCombine=0;
	public static void init(Loader loader,ConfigLoader configLoader){
		quad = loader.loadToVAO(POSITIONS, 2,"null");
		config=configLoader;
		combineFilter=new CombineFilter(configLoader.getFloatParameter("contrast"),configLoader.getFloatParameter("exposure"));
		combineFilter2=new CombineFilter(configLoader.getFloatParameter("contrast"),configLoader.getFloatParameter("exposure"));
		lightRenderer=new LightRenderer(Display.getWidth()/2, Display.getWidth()/2, configLoader);
		scatRenderer=new LightScatteringRenderer(Display.getWidth()/2, Display.getWidth()/2, configLoader);
		dlssScattering =new DLSS();
		dlssGodRays =new DLSS();

		FlareTexture[]flares=new FlareTexture[9];
		for (int i = 1; i <=flares.length ; i++) {
			flares[i-1]=new FlareTexture(new ModelTexture(loader.loadTexture("lensFlare/tex"+i),"lensFlare/tex"+i),0.5f/i);
		}

		flareManager=new FlareManager(configLoader.getFloatParameter("flareSpacing"),configLoader.getFloatParameter("flareBrightness"),loader,MainRender.CAMERA.getProjectionMatrix(),flares);

		fps=new FPSFilter();
	}
	public static void Resized() {
		combineFilter.resize();
		lightRenderer.cleanUp();
		lightRenderer=new LightRenderer(Display.getWidth(), Display.getWidth(), config);
	}
	public static void doPostProcessing(int depthTexture,int colourTexture){
		start();
		if(MainRender.DLSS_ON) {
			dlssScattering.render(colourTexture);
			//colourTexture = dlss.getOutputTexture();
		}

		scatRenderer.render(depthTexture);
		//scatRenderer.renderer.fbo.bindFrameBuffer();
		/*if(true /*InputManager.inventory.isPressed()) {
			System.out.println("clicked");
			//GL11.glReadBuffer(GL11.GL_FRONT);

			int width = MainLoop.CONFIG.getIntParameter("width");//Display.getDisplayMode().getWidth();
			int height = MainLoop.CONFIG.getIntParameter("height");//Display.getDisplayMode().getHeight();
			int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
			ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
			//GL11.glReadBuffer(GL11.GL_FRONT);
			GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
          //  GL11.glDrawPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			File file = new File("res/screenshot.png");
			System.out.println("boucle");
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int i = (x + (width * y)) * bpp;
					int r = buffer.get(i) & 0xFF;
					int g = buffer.get(i + 1) & 0xFF;
					int b = buffer.get(i + 2) & 0xFF;
					image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
				}
			}
			System.out.println("capturé");
			try {
				ImageIO.write(image, "png", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		//scatRenderer.renderer.fbo.unbindFrameBuffer();
			lightRenderer.render(depthTexture,scatRenderer.getOutputTexture());
			dlssScattering.render(scatRenderer.getOutputTexture());
			dlssGodRays.render(lightRenderer.getOutputTexture());
			if(idCombine==1){
			combineFilter.render(colourTexture, dlssScattering.getOutputTexture(),dlssGodRays.getOutputTexture());
			//combineFilter.render(scatRenderer.getOutputTexture(),depthTexture);
			idCombine=0;
		}else{
			combineFilter2.render(colourTexture, dlssScattering.getOutputTexture(),dlssGodRays.getOutputTexture());
			//combineFilter2.render(scatRenderer.getOutputTexture(),depthTexture);
			idCombine=1;
		}

		end();
		
	}

	public static void render(){
		start();
		if(idCombine==1){
			fps.render(combineFilter.getOutputTexture(),combineFilter2.getOutputTexture());
		}else{
			fps.render(combineFilter2.getOutputTexture(),combineFilter.getOutputTexture());
		}
       // fps.render(dlss.getOutputTexture(),combineFilter2.getOutputTexture());
		end();

	}

	public static void cleanUp(){
		combineFilter.cleanUp();
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

	public static double autoexposure(){
		GL11.glReadBuffer(GL11.GL_FRONT);

		int width = Display.getDisplayMode().getWidth();
		int height = Display.getDisplayMode().getHeight();
		int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		int size=100;
		float nb=size*size*4*3;
		double tot=0;
		for (int x = width/2-2*size; x < width/2+2*size; x+=2) {
			for (int y = height/2-2*size; y < height/2+2*size; y+=2) {
				int i = (x + (width * y)) * bpp;
				float r = buffer.get(i)& 0xFF;
				float g = buffer.get(i + 1) & 0xFF;
				float b = buffer.get(i + 2) & 0xFF;
				tot+=(r+g+b)/nb;
			}
		}
		return tot;
	}

}
