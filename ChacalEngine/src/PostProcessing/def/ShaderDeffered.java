package PostProcessing.def;

import Entity.Light.Light;
import Entity.Light.PointLight;
import Entity.Light.SpotLight;
import Main.MainRender;
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderAttribArray;
import ShaderEngine.ShaderProgram;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.*;
import toolbox.maths.Vector3;

import java.util.List;

public class ShaderDeffered extends ShaderProgram{
	public static int NUMBER_LIGHT=10;
	public static int numberSpot=10;
	private static final String VERTEX_FILE = "/PostProcessing/def/simpleVertex.txt";
	private static final String FRAGMENT_FILE = "/PostProcessing/def/combineFragment.txt";

	public ShaderAttrib couleur=new ShaderAttrib("colourTexture", this);
	public ShaderAttrib normal=new ShaderAttrib("normalTexture", this);
	public ShaderAttrib position=new ShaderAttrib("posTexture", this);
	public ShaderAttrib autre=new ShaderAttrib("autre", this);
	
	public ShaderAttrib camera=new ShaderAttrib("camera", this);
	public ShaderAttrib skyColor=new ShaderAttrib("skyColor", this);
	public ShaderAttrib viewMatrix=new ShaderAttrib("viewMatrix", this);
	public ShaderAttrib projMatrix=new ShaderAttrib("projMatrix", this);
	public ShaderAttrib screenSize=new ShaderAttrib("screenSize", this);
	
	public ShaderAttrib useLightScattering=new ShaderAttrib("useLightScattering", this);
	public ShaderAttrib sunPos=new ShaderAttrib("sunPos", this);
	public ShaderAttrib exposure=new ShaderAttrib("exposure", this);
	public ShaderAttrib decay=new ShaderAttrib("decay", this);
	public ShaderAttrib density=new ShaderAttrib("density", this);
	public ShaderAttrib weight=new ShaderAttrib("weight", this);
	public ShaderAttrib NUM_SAMPLES=new ShaderAttrib("NUM_SAMPLES", this);
	
	public ShaderAttrib densityFog=new ShaderAttrib("densityFog", this);
	public ShaderAttrib gradient=new ShaderAttrib("gradient", this);
	public ShaderAttrib worldtime=new ShaderAttrib("worldTime", this);
	public ShaderAttrib moveFactor=new ShaderAttrib("moveFactor", this);


	public ShaderAttribArray spotPosition=new ShaderAttribArray("positionSpot[", "]", numberSpot, this);
	public ShaderAttribArray pointPosition=new ShaderAttribArray("positionPoint[", "]", numberSpot, this);
	public ShaderAttribArray spotMatrix=new ShaderAttribArray("spotMatrix[", "]", numberSpot, this);
	public ShaderAttribArray spotInUse=new ShaderAttribArray("spotInUse[", "]", 5, this);
	public ShaderAttribArray pointInUse=new ShaderAttribArray("pointInUse[", "]", 5, this);
	public ShaderAttribArray pointInUseVertex=new ShaderAttribArray("pointInUseVertex[", "]", 5, this);

	public ShaderAttribArray textureSpot=new ShaderAttribArray("spots[", "].textSpot", numberSpot, this);
	public ShaderAttribArray color=new ShaderAttribArray("spots[", "].color", numberSpot, this);
	public ShaderAttribArray angle=new ShaderAttribArray("spots[", "].angle", numberSpot, this);
	public ShaderAttribArray look=new ShaderAttribArray("spots[", "].lookVec", numberSpot, this);
	public ShaderAttribArray attenuation=new ShaderAttribArray("spots[", "].attenuation", numberSpot, this);
	public ShaderAttribArray farSpot=new ShaderAttribArray("spots[", "].far", numberSpot, this);

	public ShaderAttribArray attenuationPoint=new ShaderAttribArray("points[", "].attenuation", numberSpot, this);
	public ShaderAttribArray colorPoint=new ShaderAttribArray("points[", "].color", numberSpot, this);
	public ShaderAttribArray texturePoint=new ShaderAttribArray("textCubes[", "]", numberSpot, this);
	public ShaderAttribArray farPoint=new ShaderAttribArray("points[", "].far", numberSpot, this);


	public ShaderAttrib shadowDistance=new ShaderAttrib("shadowDistance", this);
	public ShaderAttrib shadowSize=new ShaderAttrib("mapSize", this);
	public ShaderDeffered() {
	super(VERTEX_FILE, FRAGMENT_FILE);
	getAllUniformLocations();
	}
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		
	}

	public void init(List<SpotLight>spots) {
		start();
		for (int i = 0; i < numberSpot; i++) {
			textureSpot.loadInt(i+10, i);
			texturePoint.loadInt(i+10+numberSpot,i);
		}
		skyColor.loadVector3(MainRender.SKY_COLOR);
		projMatrix.loadMatrix4f(MainRender.CAMERA.getProjectionMatrix());
		gradient.loadFloat(MainRender.GRADIENT);
		density.loadFloat(MainRender.DENSITY);
		shadowDistance.loadFloat(40);

		stop();
	}

	public void loadSpotAttenuation(List<SpotLight>spots) {
		int s=spots.size();
		if (s>numberSpot)s=numberSpot;
		for (int i = 0; i < s; i++) {
			attenuation.loadVector3(spots.get(i).getAttenuationCurrent(),i);
		}
	}
	public void loadSpot(List<SpotLight>spots) {
		start();
		int s=spots.size();
		if (s>numberSpot)s=numberSpot;
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

		stop();
	}
	public void loadPoints(List<PointLight>points) {
		start();
		int s=points.size();
		if (s>numberSpot)s=numberSpot;
		for (int i = 0; i < s; i++) {
			PointLight light=points.get(i);
			pointPosition.loadVector3(light.getPosition(), i);
			//	System.out.println(arg0);
			colorPoint.loadVector3(light.getColor(), i);
			attenuationPoint.loadVector3(light.getAttenuation(), i);
			farPoint.loadFloat(light.getMaxRange(),i);
			GL13.glActiveTexture(GL13.GL_TEXTURE0 +numberSpot+10+i);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP,light.getShadowMap().getTexture());
		}
		stop();
	}
	public void loadLightLS(List<Light> light, Matrix4f projViewMatrix) {
			
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
			regard.y=-regard.y;

				Vector3 camLightVec=new Vector3(light.get(0).getPosition());
				camLightVec.sub(MainRender.CAMERA.getPosition());
				camLightVec.normalize();
				float dot=Vector3f.angle(regard, camLightVec.getOglVec());
				dot=(float)Math.toDegrees(dot);
			//	System.out.println(dot+" "+light.get(0).getPosition()+" "+regard);
				if(dot<70.0&&dot>-70.0) {
					
				Vector3 sunpos=new Vector3(light.get(0).getPosition());
				
				Vector4f vector3f=new Vector4f(sunpos.x, sunpos.y, sunpos.z,1.0f);
				vector3f=Matrix4f.transform(projViewMatrix, vector3f, null);
				Vector3f vector2f=new Vector3f(0.5f+(0.5f*vector3f.x/vector3f.w), 0.5f+(0.5f*vector3f.y/vector3f.w),0.5f+(0.5f*vector3f.z/vector3f.w));
				
				
			
				Vector2f dist=new Vector2f(vector2f.x-0.5f, vector2f.y-0.5f);
				float length=(float)Math.sqrt(dist.x*dist.x+dist.y*dist.y);
				
				
				Vector3 color=new Vector3(light.get(0).getColor());
				color.mul(new Vector3(1-length, 1-length, 1-length));
				
				if(vector2f.x>=0&&vector2f.x<=1&&vector2f.y>=0&&vector2f.y<=1 ) {
					//System.out.println(light.get(i).getName()+" "+vector2f);
				//	super.loadFloat(location_lightRadius[i], light.get(i).getRadiusRays());
				//	System.out.println(new Vector2f(vector2f.x, vector2f.y));
					sunPos.loadVector2D(new Vector2f(vector2f.x, vector2f.y));
					useLightScattering.loadBoolean(true);
				}else {
					useLightScattering.loadBoolean(false);
				
				}
				//fin du dot product
			}else {
				useLightScattering.loadBoolean(false);
				
			}
		
		
	}
	public void loadSpotInUse(int... ids) {
		int l=ids.length;
		if(l>5)l=5;
		for (int i = 0; i < l; i++) {
			spotInUse.loadFloat(ids[i], i);
		}
		if(l<5) {
			for (int i = l; i < 5; i++) {
				spotInUse.loadFloat(-1, i);
			}
		}
	}
	public void loadPointInUse(int... ids) {
		int l=ids.length;
		if(l>5)l=5;
		for (int i = 0; i < l; i++) {
			pointInUse.loadFloat(ids[i], i);
			pointInUseVertex.loadFloat(ids[i], i);
		}
		if(l<5) {
			for (int i = l; i < 5; i++) {
				pointInUse.loadFloat(-1, i);
				pointInUseVertex.loadFloat(-1, i);
			}
		}
	}
}
