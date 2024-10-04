package PostProcessing.LightScaterring.godRays;

import Entity.Light.Light;
import Init.CloudGenerator;
import Loader.ConfigLoader;
import Main.MainGame;
import Main.MainRender;
import Main.Register;
import PostProcessing.ImageRenderer;
import RenderEngine.DisplayManager;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;
import toolbox.maths.Vector3;

import java.util.ArrayList;
import java.util.List;

public class LightScatteringRenderer {
	public ImageRenderer renderer;
	private LightShader shader;
	private int cloudTexture,cloudDetails;
	private Vector4f speedCloud=new Vector4f(1000,2500,3000,-200);
	private Vector4f speedCloudDetail=new Vector4f(100f,-800f,777f,2);
	private Vector4f posCloud=new Vector4f(0,0,0,0);
	private Vector4f posCloudDetail=new Vector4f(0.1f,0,0.08f,1f);
	public LightScatteringRenderer(int targetFboWidth, int targetFboHeight, ConfigLoader configLoader){
		shader = new LightShader();
		shader.start();
		shader.connectTextureUnits();
		shader.loadExposures(configLoader.getFloatParameter("godRayExposures"));
		shader.loadNumSamples(configLoader.getIntParameter("godRayNumberSamples"));
		shader.loadDensity(configLoader.getFloatParameter("godRayDensity"));
		shader.loadDecay(configLoader.getFloatParameter("godRayDecay"));
		shader.loadWeight(configLoader.getFloatParameter("godRayWeight"));
		shader.gradient.loadFloat(configLoader.getFloatParameter("gradient"));
		shader.fogdensity.loadFloat(configLoader.getFloatParameter("density")*2);
		shader.rayon.loadFloat(0.005f);
		shader.stop();
		shader.init();
		renderer = new ImageRenderer(targetFboWidth, targetFboHeight);
		long ms= System.currentTimeMillis();
		cloudTexture= CloudGenerator.cloudTexture();
		/*CloudGenerator.WIDTH_CLOUD=250;
		CloudGenerator.DEPTH_CLOUD=250;
		CloudGenerator.HEIGHT_CLOUD=250;*/
		cloudDetails= CloudGenerator.cloudTexture();
		System.out.println("Cloud:"+(System.currentTimeMillis()-ms));
	//	MainRender.guis.add(new GuiTexture(renderer.getOutputTexture(), new Vector2f(-0.6f, -0.6f), new Vector2f(0.15f, 0.15f)));
		
	}
	private float totalTime=1;
	private float goal=0.5f;
	public void render(int depthTexture){

		posCloud.x+= DisplayManager.getFrameTimeSecond()*speedCloud.x;
		posCloud.y+= DisplayManager.getFrameTimeSecond()*speedCloud.y;
		posCloud.z+= DisplayManager.getFrameTimeSecond()*speedCloud.z;

		posCloud.w+= DisplayManager.getFrameTimeSecond()/totalTime*(posCloud.w-goal);
		totalTime-=DisplayManager.getFrameTimeSecond();
		if(totalTime<0){
			posCloud.w=goal;
			totalTime=(float) Math.random()*5+5;
			goal=0.5f+ (float) Math.random();
		}


		posCloudDetail.x+= DisplayManager.getFrameTimeSecond()*speedCloudDetail.x;
		posCloudDetail.y+= DisplayManager.getFrameTimeSecond()*speedCloudDetail.y;
		posCloudDetail.z+= DisplayManager.getFrameTimeSecond()*speedCloudDetail.z;
		posCloudDetail.w= DisplayManager.getFrameTimeSecond()*speedCloudDetail.w;

		shader.start();
		shader.loadSpot(MainGame.spots);
		shader.loadPoints(MainGame.pointLigths);

		shader.toShadowMapSpace.loadMatrix4f(MainRender.shadowMapMasterRenderer.getToShadowMapSpaceMatrix());
		shader.loadSunPos();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL12.GL_TEXTURE_3D, cloudTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL12.GL_TEXTURE_3D, cloudDetails);

		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, MainRender.shadowMapMasterRenderer.getShadowMap());

		shader.move.loadVector4f(posCloud);
		shader.moveDetail.loadVector4f(posCloudDetail);
		shader.camera.loadVector3(MainRender.CAMERA.getPosition());
		shader.invViewMatrix.loadMatrix4f(Matrix4f.invert(MainRender.CAMERA.getViewMatrix(),null));

		List<Integer>spots=new ArrayList<>();
		for (int i = 0; i < MainGame.spots.size(); i++) {
			if(MainGame.spots.get(i).getPosition().squareDistanceTo(MainRender.CAMERA.getPosition())<MainGame.spots.get(i).getMaxRange()*MainGame.spots.get(i).getMaxRange()){
				spots.add(i);
			}
		}
		List<Integer>points=new ArrayList<>();
		for (int i = 0; i < MainGame.pointLigths.size(); i++) {
			//System.out.println(i+" "+MainGame.pointLigths.get(i).getPosition().squareDistanceTo(MainRender.CAMERA.getPosition())+" "+MainGame.pointLigths.get(i).getMaxRange()*MainGame.pointLigths.get(i).getMaxRange());
			if(MainGame.pointLigths.get(i).getPosition().squareDistanceTo(MainRender.CAMERA.getPosition())<MainGame.pointLigths.get(i).getMaxRange()*MainGame.pointLigths.get(i).getMaxRange()){
				points.add(i);
				//System.out.println(i);
			}
		}
		if(!points.contains(0)){
			points.add(0);
		}
		//System.out.println(points);
		shader.loadPointInUse(points);
		shader.loadSpotInUse(spots);

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
