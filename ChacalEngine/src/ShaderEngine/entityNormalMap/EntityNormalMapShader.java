package ShaderEngine.entityNormalMap;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import Entity.Light.PointLight;
import Entity.Light.SpotLight;
import Loader.CubeMap.CubeMapCamera;
import Main.MainGame;
import Main.MainRender;
import RenderEngine.shadow.ShadowMapMasterRenderer;
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderAttribArray;
import ShaderEngine.ShaderProgram;
import toolbox.maths.Vector3;

public class EntityNormalMapShader extends ShaderProgram{
	public int numberSpot=10;
	public static String VERTEX="/ShaderEngine/entityNormalMap/vertexShader.txt";
	public static String FRAGMENT="/ShaderEngine/entityNormalMap/fragmentShader.txt";
	
	public ShaderAttrib transformationMatrix=new ShaderAttrib("transformationMatrix", this);
	public ShaderAttrib projectionMatrix=new ShaderAttrib("projectionMatrix", this);
	public ShaderAttrib viewMatrix=new ShaderAttrib("viewMatrix", this);
	public ShaderAttrib invViewMatrix=new ShaderAttrib("invViewMatrix", this);
	
	public ShaderAttrib density=new ShaderAttrib("density", this);
	public ShaderAttrib gradient=new ShaderAttrib("gradient", this);
	
	public ShaderAttribArray spotPosition=new ShaderAttribArray("positionSpot[", "]", numberSpot, this);
	public ShaderAttribArray pointPosition=new ShaderAttribArray("positionPoint[", "]", numberSpot, this);
	public ShaderAttribArray spotMatrix=new ShaderAttribArray("spotMatrix[", "]", numberSpot, this);
	public ShaderAttribArray spotInUse=new ShaderAttribArray("spotInUse[", "]", 5, this);
	public ShaderAttribArray pointInUse=new ShaderAttribArray("pointInUse[", "]", 5, this);
	public ShaderAttrib useFakeLightning=new ShaderAttrib("useFakeLightning", this);
//	public ShaderAttrib cube=new ShaderAttrib("cube", this);
	
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
	public ShaderAttribArray posSpotWorld=new ShaderAttribArray("positionSpotWorld[","]", numberSpot, this);
	public ShaderAttribArray posPointWorld=new ShaderAttribArray("positionPointWorld[","]", numberSpot, this);
	
	
	public ShaderAttrib skyColor=new ShaderAttrib("skyColor", this);
	public ShaderAttrib reflectivity=new ShaderAttrib("reflectivity", this);
	public ShaderAttrib shineDamper=new ShaderAttrib("shineDamper", this);
	public ShaderAttrib modelTexture=new ShaderAttrib("modelTexture", this);
	public ShaderAttrib normalTexture=new ShaderAttrib("normalTexture", this);
	public ShaderAttrib entityCube=new ShaderAttrib("environmentMap", this);
	public ShaderAttrib materialValue=new ShaderAttrib("materialValue", this);
	
	public ShaderAttrib shadowDistance=new ShaderAttrib("shadowDistance", this);
	public ShaderAttrib toShadowMapSpace=new ShaderAttrib("toShadowMapSpace", this);
	public ShaderAttrib mapSize=new ShaderAttrib("mapSize", this);
	public ShaderAttrib sunTexture=new ShaderAttrib("sunTexture", this);
	
	public EntityNormalMapShader() {
		super(VERTEX, FRAGMENT);
		getAllUniformLocations();
	}
	
	public void init(List<SpotLight>spots) {
		start();
		for (int i = 0; i < numberSpot; i++) {
			textureSpot.loadInt(i+10, i);
			texturePoint.loadInt(i+10+numberSpot,i);
		}
		skyColor.loadVector3(MainRender.SKY_COLOR);
		projectionMatrix.loadMatrix4f(MainRender.CAMERA.getProjectionMatrix());
		gradient.loadFloat(MainRender.GRADIENT);
		density.loadFloat(MainRender.DENSITY);
		
		shadowDistance.loadFloat(ShadowMapMasterRenderer.SIZE);
		mapSize.loadVector2D(new Vector2f(ShadowMapMasterRenderer.SHADOW_MAP_SIZE, ShadowMapMasterRenderer.SHADOW_MAP_SIZE));
		
		modelTexture.loadInt(0);
		entityCube.loadInt(1);
		normalTexture.loadInt(2);
		sunTexture.loadInt(3);
		stop();
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "tangent");
	}
	public void loadViewMatrix(Matrix4f v) {
		viewMatrix.loadMatrix4f(v);
		invViewMatrix.loadMatrix4f(Matrix4f.invert(v, null));
		
	}
	
	public void loadSpot(List<SpotLight>spots) {
		start();
		int s=spots.size();
		if (s>numberSpot)s=numberSpot;
		for (int i = 0; i < s; i++) {
			SpotLight light=spots.get(i);
			posSpotWorld.loadVector3(light.getPosition(), i);
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
	public void loadPosition(List<SpotLight>spots,List<PointLight>points,Matrix4f vm) {
		int s=spots.size();
		if (s>numberSpot)s=numberSpot;
		for (int i = 0; i < s; i++) {
			attenuation.loadVector3(spots.get(i).getAttenuationCurrent(),i);
			spotPosition.loadVector3(getEyeSpacePosition(spots.get(i).getPosition(),vm), i);
		}
		s=points.size();
		if (s>numberSpot)s=numberSpot;
		for (int i = 0; i < s; i++) {
			pointPosition.loadVector3(getEyeSpacePosition(points.get(i).getPosition(),vm), i);
			//System.out.println(getEyeSpacePosition(points.get(i).getPosition(),vm));
		}
		colorPoint.loadVector3(MainGame.pointLigths.get(0).getColor(),0);
	}
	
	public void loadPoints(List<PointLight>points) {
		start();
		int s=points.size();
		if (s>numberSpot)s=numberSpot;
		for (int i = 0; i < s; i++) {
			PointLight light=points.get(i);
			posPointWorld.loadVector3(light.getPosition(), i);
		//	System.out.println(arg0);
			colorPoint.loadVector3(light.getColor(), i);
			attenuationPoint.loadVector3(light.getAttenuation(), i);
			farPoint.loadFloat(light.getMaxRange(),i);
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0 +numberSpot+10+i);
	        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP,light.getShadowMap().getTexture());
		}
		for (int i = s; i < numberSpot; i++) {
			
		}
		stop();
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
		}
		if(l<5) {
			for (int i = l; i < 5; i++) {
				pointInUse.loadFloat(-1, i);
			}
		}
	}
	private Vector3 getEyeSpacePosition(Vector3 light, Matrix4f viewMatrix){
		Vector3f position = light.getOglVec();
		Vector4f eyeSpacePos = new Vector4f(position.x,position.y, position.z, 1f);
		Matrix4f.transform(viewMatrix, eyeSpacePos, eyeSpacePos);
		return new Vector3(eyeSpacePos.x,eyeSpacePos.y,eyeSpacePos.z);
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
		for (int i = 0; i < 5; i++) {
			pointInUse.loadFloat(-1, i);
		}
		for (int i = 0; i < l; i++) {
			pointInUse.loadFloat(ids.get(i).intValue(), i);
		}
		//pointInUse.loadFloat(ids.size(), 0);
	}
}

