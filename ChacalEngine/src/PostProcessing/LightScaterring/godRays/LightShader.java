package PostProcessing.LightScaterring.godRays;

import Entity.Entity;
import Entity.Light.Light;
import Entity.Light.PointLight;
import Entity.Light.SpotLight;
import Main.MainGame;
import Main.MainRender;
import RenderEngine.shadow.ShadowMapMasterRenderer;
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderAttribArray;
import ShaderEngine.ShaderProgram;
import ShaderEngine.entity.EntityShader;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.*;
import toolbox.maths.Vector3;

import java.util.List;

public class LightShader extends ShaderProgram{
	public static final int MAX_LIGHTS=256;
	
	private static final String VERTEX_FILE = "/PostProcessing/LightScaterring/godRays/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "/PostProcessing/LightScaterring/godRays/lightFragment.txt";


	public ShaderAttrib cloudTexture=new ShaderAttrib("cloudTexture", this);
	public ShaderAttrib detailTexture=new ShaderAttrib("detailTexture", this);
	public ShaderAttribArray spotPosition=new ShaderAttribArray("positionSpot[", "]", EntityShader.numberSpot, this);
	public ShaderAttribArray pointPosition=new ShaderAttribArray("positionPoint[", "]", EntityShader.numberSpot, this);
	public ShaderAttribArray spotMatrix=new ShaderAttribArray("spotMatrix[", "]", EntityShader.numberSpot, this);
	public ShaderAttribArray spotInUse=new ShaderAttribArray("spotInUse[", "]", 5, this);
	public ShaderAttribArray pointInUse=new ShaderAttribArray("pointInUse[", "]", 5, this);
	public ShaderAttribArray pointInUseVertex=new ShaderAttribArray("pointInUseVertex[", "]", 5, this);
	public ShaderAttrib useFakeLightning=new ShaderAttrib("useFakeLightning", this);

	public ShaderAttrib sunMap=new ShaderAttrib("sunTexture", this);
	public ShaderAttrib mapSize=new ShaderAttrib("mapSize", this);
	public ShaderAttrib nbL=new ShaderAttrib("nbL", this);
	public ShaderAttrib move=new ShaderAttrib("move", this);
	public ShaderAttrib moveDetail=new ShaderAttrib("moveDetail", this);

	public ShaderAttrib camera=new ShaderAttrib("camera", this);
	public ShaderAttrib invViewMatrix=new ShaderAttrib("invViewMatrix", this);
	public ShaderAttrib invProjectionMatrix=new ShaderAttrib("invProjectionMatrix", this);
	public ShaderAttrib toShadowMapSpace=new ShaderAttrib("toShadowMapSpace", this);

	public ShaderAttribArray textureSpot=new ShaderAttribArray("spots[", "].textSpot", EntityShader.numberSpot, this);
	public ShaderAttribArray color=new ShaderAttribArray("spots[", "].color", EntityShader.numberSpot, this);
	public ShaderAttribArray angle=new ShaderAttribArray("spots[", "].angle", EntityShader.numberSpot, this);
	public ShaderAttribArray look=new ShaderAttribArray("spots[", "].lookVec", EntityShader.numberSpot, this);
	public ShaderAttribArray attenuation=new ShaderAttribArray("spots[", "].attenuation", EntityShader.numberSpot, this);
	public ShaderAttribArray farSpot=new ShaderAttribArray("spots[", "].far", EntityShader.numberSpot, this);

	public ShaderAttribArray attenuationPoint=new ShaderAttribArray("points[", "].attenuation", EntityShader.numberSpot, this);
	public ShaderAttribArray colorPoint=new ShaderAttribArray("points[", "].color", EntityShader.numberSpot, this);
	public ShaderAttribArray texturePoint=new ShaderAttribArray("textCubes[", "]", EntityShader.numberSpot, this);
	public ShaderAttribArray farPoint=new ShaderAttribArray("points[", "].far", EntityShader.numberSpot, this);

	public ShaderAttrib gradient=new ShaderAttrib("gradient", this);
	public ShaderAttrib sunPos=new ShaderAttrib("sunPos", this);
	public ShaderAttrib fogdensity=new ShaderAttrib("fogdensity", this);
	public ShaderAttrib rayon=new ShaderAttrib("rayon", this);

	private int location_depthTexture;

    private int location_viewportSize;
    private int location_exposures;
    private int location_NUM_SAMPLES;
    private int location_density;
    private int location_weight;
    private int location_decay;
	protected LightShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
	}
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		location_depthTexture = super.getUniformlocation("depthTexture");
		location_viewportSize=super.getUniformlocation("viewportSize");

		location_exposures=super.getUniformlocation("exposures");
		location_weight=super.getUniformlocation("weight");
		location_decay=super.getUniformlocation("decay");
		location_density=super.getUniformlocation("density");
		location_NUM_SAMPLES=super.getUniformlocation("NUM_SAMPLES");
	}
	public void loadExposures(float exp) {
		super.loadFloat(location_exposures, exp);
	}
	public void loadDensity(float exp) {
		super.loadFloat(location_density, exp);
	}
	public void loadWeight(float exp) {
		super.loadFloat(location_weight, exp);
	}
	public void loadDecay(float exp) {
		super.loadFloat(location_decay, exp);
	}
	public void loadNumSamples(int num) {
		super.loadInt(location_NUM_SAMPLES, num);
	}

	protected void connectTextureUnits(){
		super.loadInt(location_depthTexture, 0);
		cloudTexture.loadInt(1);
		detailTexture.loadInt(2);
	}

	public void init() {
		start();
		for (int i = 0; i < EntityShader.numberSpot; i++) {
			textureSpot.loadInt(i+10, i);
			texturePoint.loadInt(i+10+EntityShader.numberSpot,i);
		}
		invProjectionMatrix.loadMatrix4f(Matrix4f.invert(MainRender.CAMERA.getProjectionMatrix(),null));
		mapSize.loadVector2D(new Vector2f(ShadowMapMasterRenderer.SHADOW_MAP_SIZE, ShadowMapMasterRenderer.SHADOW_MAP_SIZE));
		sunMap.loadInt(3);

		stop();
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	public void loadSpot(List<SpotLight>spots) {

		int s=spots.size();
		if (s>EntityShader.numberSpot)s=EntityShader.numberSpot;
		for (int i = 0; i < s; i++) {
			SpotLight light=spots.get(i);
			spotPosition.loadVector3(light.getPosition(), i);
			color.loadVector3(light.getColor(), i);
			attenuation.loadVector3(light.getAttenuation(), i);
			look.loadVector3(light.getDirectionVector().getNormalize(), i);
			angle.loadFloat((float)Math.toRadians(light.getAngle()), i);
			spotMatrix.loadMatrix4f(light.getToShadowMapSpace(), i);
			farSpot.loadFloat(light.getMaxRange(),i);
			GL13.glActiveTexture(GL13.GL_TEXTURE0+ 10 +i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, light.getShadowMap());
		}


	}

	public void loadSunPos(){
		Vector3 sunpos= MainGame.pointLigths.get(0).getPosition();
		Vector4f vector3f=new Vector4f(sunpos.x, sunpos.y, sunpos.z,1.0f);
		vector3f=Matrix4f.transform(MainRender.CAMERA.getProjectionViewMatrix(), vector3f, null);
		Vector2f vector2f=new Vector2f(0.5f+(0.5f*vector3f.x/vector3f.w), 0.5f+(0.5f*vector3f.y/vector3f.w));
		this.sunPos.loadVector2D(vector2f);
		//this.pointPosition.loadVector3(sunpos,0);
	}

	public void loadPoints(List<PointLight>points) {

		int s=points.size();
		if (s>EntityShader.numberSpot)s=EntityShader.numberSpot;
		for (int i = 0; i < s; i++) {
			PointLight light=points.get(i);
			pointPosition.loadVector3(light.getPosition(), i);
			//	System.out.println(arg0);
			colorPoint.loadVector3(light.getColor(), i);
			attenuationPoint.loadVector3(light.getAttenuation(), i);
			farPoint.loadFloat(light.getMaxRange(),i);
			GL13.glActiveTexture(GL13.GL_TEXTURE0 +EntityShader.numberSpot+10+i);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP,light.getShadowMap().getTexture());
		}

	}
	public void loadSpotInUse(List<Integer> ids) {
		int l=ids.size();
		if(l>5)l=5;
		for (int i = 0; i < l; i++) {
			spotInUse.loadFloat(ids.get(i).intValue(), i);
		}
		if(l<5) {
			for (int i = l; i < 5; i++) {
				spotInUse.loadFloat(-1, i);
			}
		}
	}
	public void loadPointInUse(List<Integer> ids) {
		int l=ids.size();
		if(l>5)l=5;
		for (int i = 0; i < l; i++) {
			pointInUse.loadFloat(ids.get(i).intValue(), i);
			pointInUseVertex.loadFloat(ids.get(i).intValue(), i);
		}
		if(l<5) {
			for (int i = l; i < 5; i++) {
				pointInUse.loadFloat(-1, i);
				pointInUseVertex.loadFloat(-1, i);
			}
		}
	}
}
