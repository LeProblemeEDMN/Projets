package PostProcessing.godRays;

import Entity.Light.Light;
import Main.MainGame;
import Main.MainRender;
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderProgram;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.*;
import toolbox.maths.Vector3;

import java.util.List;

public class LightShader extends ShaderProgram{
	public static final int MAX_LIGHTS=256;
	
	private static final String VERTEX_FILE = "/PostProcessing/godRays/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "/PostProcessing/godRays/lightFragment.txt";

	public ShaderAttrib sunPos=new ShaderAttrib("sunPos", this);
	public ShaderAttrib rayon=new ShaderAttrib("rayon", this);
	public ShaderAttrib color=new ShaderAttrib("color", this);

	private int location_depthTexture;
	private int location_cloudTexture;
	private int location_lightPosition[];
    private int location_lightColor[];
    private int location_lightRadius[];
    private int location_nbL;
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
		location_cloudTexture = super.getUniformlocation("cloudTexture");
		location_nbL=super.getUniformlocation("nbL");
		location_viewportSize=super.getUniformlocation("viewportSize");
		
		location_lightColor=new int[MAX_LIGHTS];
		location_lightPosition=new int[MAX_LIGHTS];
		location_lightRadius=new int[MAX_LIGHTS];
		for (int i=0;i<MAX_LIGHTS;i++) {
			location_lightColor[i]=super.getUniformlocation("lightColour["+i+"]");
			location_lightPosition[i]=super.getUniformlocation("lightPosition["+i+"]");
			location_lightRadius[i]=super.getUniformlocation("lightRadius["+i+"]");
			
			super.loadFloat(location_lightRadius[i], 0.08f);
			super.loadVector(location_lightPosition[i], new Vector3(0, 0,0));
			super.loadVector(location_lightColor[i],  new Vector3(0, 0,0));
			
		}
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
		super.loadInt(location_cloudTexture, 1);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	public void loadLight(List<Light> light,Matrix4f projViewMatrix) {
		float nb=light.size();
		if(nb>MAX_LIGHTS) nb=MAX_LIGHTS;
			
			float finalnb=nb;
			
			Vector3f regard=new Vector3f(0, 0,-1);
			Matrix3f matrix3f=new Matrix3f();
			double angleY=Math.toRadians(MainRender.CAMERA.getYaw()-90);
			matrix3f.m00=(float)(Math.cos(angleY));
			matrix3f.m22=(float)(Math.cos(angleY));
			matrix3f.m11=1;
			matrix3f.m02=(float)(Math.sin(angleY));
			matrix3f.m20=(float)(-Math.sin(angleY));
			
			Matrix3f matrix3f2=new Matrix3f();
			double angleX=Math.toRadians( -MainRender.CAMERA.getPitch());
			matrix3f2.m11=(float)(Math.cos(angleX));
			matrix3f2.m22=(float)(Math.cos(angleX));
			matrix3f2.m00=1;
			matrix3f2.m12=(float)(Math.sin(angleX));
			matrix3f2.m21=(float)(-Math.sin(angleX));
			
			Matrix3f.transform(matrix3f, new Vector3f(1, 0, 0), regard);
			Matrix3f.transform(matrix3f2, regard, regard);
			regard.normalise();
		
			for (int i=0;i<nb;i++) {
			
				Vector3 camLightVec=new Vector3(light.get(i).getPosition());
				camLightVec.sub(MainRender.CAMERA.getPosition());
				camLightVec.normalize();
				float dot=Vector3f.angle(regard, camLightVec.getOglVec());
				dot=(float)Math.toDegrees(dot);
				
				if(dot<70.0&&dot>-70.0) {
					
				Vector3 sunpos=new Vector3(light.get(i).getPosition());
				
				Vector4f vector3f=new Vector4f(sunpos.x, sunpos.y, sunpos.z,1.0f);
				vector3f=Matrix4f.transform(projViewMatrix, vector3f, null);
				Vector3f vector2f=new Vector3f(0.5f+(0.5f*vector3f.x/vector3f.w), 0.5f+(0.5f*vector3f.y/vector3f.w),0.5f+(0.5f*vector3f.z/vector3f.w));
				
				
			
				Vector2f dist=new Vector2f(vector2f.x-0.5f, vector2f.y-0.5f);
				float length=(float)Math.sqrt(dist.x*dist.x+dist.y*dist.y);
				
				
				Vector3 color=new Vector3(light.get(i).getColor());
				color.mul(new Vector3(1-length, 1-length, 1-length));
				
				
				if(vector2f.x>=0&&vector2f.x<=1&&vector2f.y>=0&&vector2f.y<=1 ) {
					//System.out.println(light.get(i).getName()+" "+vector2f);
					super.loadFloat(location_lightRadius[i], 0.4f);//radius

					
					super.loadVector(location_lightPosition[i], new Vector3(vector2f));
					super.loadVector(location_lightColor[i], color);
				}else {
					super.loadFloat(location_lightRadius[i], 0f);
					super.loadVector(location_lightPosition[i], new Vector3(-1,-1,100000));
					super.loadVector(location_lightColor[i],  new Vector3(0,0,0));
					finalnb--;
				}
				//fin du dot product
			}else {
				super.loadFloat(location_lightRadius[i], 0f);
				super.loadVector(location_lightPosition[i], new Vector3(-1,-1,100000));
				super.loadVector(location_lightColor[i],  new Vector3(0,0,0));
				finalnb--;
				
			}
			
		}

		super.load2DVector(location_viewportSize, new Vector2f(Display.getWidth(), Display.getHeight()));
		super.loadFloat(location_nbL, finalnb);

	}

	public void loadSunPos(){
		Vector3 sunpos= MainGame.pointLigths.get(0).getPosition();
		Vector4f vector3f=new Vector4f(sunpos.x, sunpos.y, sunpos.z,1.0f);
		vector3f=Matrix4f.transform(MainRender.CAMERA.getProjectionViewMatrix(), vector3f, null);
		Vector2f vector2f=new Vector2f(0.5f+(0.5f*vector3f.x/vector3f.w), 0.5f+(0.5f*vector3f.y/vector3f.w));
		if(vector3f.z>=0)this.sunPos.loadVector2D(vector2f);//if sun is in front of the player
		else this.sunPos.loadVector2D(new Vector2f(300,300));
		this.color.loadVector3(MainGame.pointLigths.get(0).getColor());
	}
}
