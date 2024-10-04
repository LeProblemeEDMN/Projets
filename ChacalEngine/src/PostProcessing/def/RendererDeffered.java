package PostProcessing.def;

import Main.MainGame;
import Main.MainLoop;
import Main.MainRender;
import PostProcessing.ImageRenderer;
import RenderEngine.shadow.ShadowFrameBuffer;
import RenderEngine.shadow.ShadowMapMasterRenderer;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector2f;

public class RendererDeffered {
	
	public static float WAVE_SPEED=0.02f;
	
	public ShaderDeffered shader ;
	private ImageRenderer renderer;
	private int idNormWater,idDudv;
	
	private float moveFactor;
	
	public RendererDeffered() {
		shader=new ShaderDeffered();
		renderer=new ImageRenderer();
		
		shader.start();
		shader.couleur.loadInt(0);
		shader.normal.loadInt(1);
		shader.position.loadInt(2);
		shader.autre.loadInt(3);
		shader.init(MainGame.spots);


		shader.projMatrix.loadMatrix4f(MainRender.CAMERA.getProjectionMatrix());
		shader.screenSize.loadVector2D(new Vector2f(Display.getWidth(),Display.getHeight()));
		shader.skyColor.loadVector3(MainRender.SKY_COLOR);
		shader.exposure.loadFloat(MainLoop.CONFIG.getFloatParameter("godRayExposures"));
		shader.NUM_SAMPLES.loadFloat(MainLoop.CONFIG.getIntParameter("godRayNumberSamples"));
		shader.density.loadFloat(MainLoop.CONFIG.getFloatParameter("godRayDensity"));
		shader.decay.loadFloat(MainLoop.CONFIG.getFloatParameter("godRayDecay"));
		shader.weight.loadFloat(MainLoop.CONFIG.getFloatParameter("godRayWeight"));
		shader.densityFog.loadFloat(MainRender.DENSITY);
		shader.gradient.loadFloat(MainRender.GRADIENT);
		shader.shadowDistance.loadFloat(16);
		if(ShadowFrameBuffer.needToViewPort)shader.shadowSize.loadVector2D(new Vector2f(ShadowMapMasterRenderer.SHADOW_MAP_SIZE, ShadowMapMasterRenderer.SHADOW_MAP_SIZE));
		else shader.shadowSize.loadVector2D(new Vector2f(Display.getWidth(),Display.getHeight()));
		
		shader.stop();
	}
	long debut=System.currentTimeMillis();
	public void render(int colourTexture, int normalTexture,int pos,int autreText){
		

		shader.start();

		shader.moveFactor.loadFloat(moveFactor);
		shader.loadSpotAttenuation(MainGame.spots);
		shader.loadLightLS(MainGame.lights,MainRender.CAMERA.getProjectionViewMatrix());
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pos);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, autreText);

		shader.loadSpotInUse(0);
		shader.loadPointInUse(0);
		shader.worldtime.loadFloat(System.currentTimeMillis()-debut);
		shader.camera.loadVector3(MainRender.CAMERA.getPosition());
		shader.viewMatrix.loadMatrix4f(MainRender.CAMERA.getViewMatrix());
		renderer.renderQuad();
		shader.stop();
	}
	
	public void resize() {
		shader.start();
		shader.projMatrix.loadMatrix4f(MainRender.CAMERA.getProjectionMatrix());
		shader.screenSize.loadVector2D(new Vector2f(Display.getWidth(),Display.getHeight()));

		shader.stop();
	}
	
	public void cleanUp() {
		shader.cleanUp();
		renderer.cleanUp();
	}

	public  ShaderDeffered getShader() {
		return shader;
	}
}
