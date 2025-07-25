package ShaderEngine.entityNormalMap;

import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderAttribArray;
import ShaderEngine.ShaderProgram;
import entity.Light.PointLight;
import entity.Light.SpotLight;
import main.Constantes;
import main.DisplayManager;
import main.Pipeline;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import rendering.shadow.Shadow;
import toolbox.maths.Vector3;

import java.util.List;

public class EntityNormalMapShader extends ShaderProgram{
	public static int numberSpot=50;
	public static int numberShadow=10;
	public static String VERTEX= "/ShaderEngine/entityNormalMap/vertexShader.txt";
	public static String FRAGMENT= "/ShaderEngine/entityNormalMap/fragmentShader.txt";

	//public ShaderAttrib transformationMatrix=new ShaderAttrib("transformationMatrix", this);
	public ShaderAttrib projectionMatrix=new ShaderAttrib("projectionMatrix", this);
	public ShaderAttrib viewMatrix=new ShaderAttrib("viewMatrix", this);
	public ShaderAttrib invViewMatrix=new ShaderAttrib("invViewMatrix", this);

	public ShaderAttrib density=new ShaderAttrib("density", this);
	public ShaderAttrib gradient=new ShaderAttrib("gradient", this);
	public ShaderAttribArray spotPosition=new ShaderAttribArray("positionSpot[", "]", numberSpot, this);
	public ShaderAttribArray positionSpotWorld=new ShaderAttribArray("positionSpotWorld[", "]", numberSpot, this);
	public ShaderAttribArray pointPosition=new ShaderAttribArray("positionPoint[", "]", numberSpot, this);
	public ShaderAttribArray spotMatrix=new ShaderAttribArray("spotMatrix[", "]", numberShadow, this);

	public ShaderAttribArray spotInUse=new ShaderAttribArray("spotInUse[", "]", numberSpot+1, this);
	public ShaderAttribArray pointInUse=new ShaderAttribArray("pointInUse[", "]", numberSpot+1, this);
	public ShaderAttribArray pointInUseVertex=new ShaderAttribArray("pointInUseVertex[", "]", numberSpot+1, this);
	public ShaderAttrib useFakeLightning=new ShaderAttrib("useFakeLightning", this);
	public ShaderAttrib r_shadow=new ShaderAttrib("r_shadow", this);
	public ShaderAttrib d0_shadow=new ShaderAttrib("d0_shadow", this);
	public ShaderAttribArray sunMap=new ShaderAttribArray("sunTexture[","]",Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt(), this);
	public ShaderAttrib mapSize=new ShaderAttrib("mapSize", this);

	public ShaderAttribArray toShadowMapSpace=new ShaderAttribArray("toShadowMapSpace[","]",Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt(), this);
	public ShaderAttribArray textureSpot=new ShaderAttribArray("spotTexture[","]", numberShadow, this);

	public ShaderAttribArray color=new ShaderAttribArray("spots[", "].color", numberSpot, this);
	public ShaderAttribArray angle=new ShaderAttribArray("spots[", "].angle", numberSpot, this);
	public ShaderAttribArray cos_angle=new ShaderAttribArray("spots[", "].cos_angle", numberSpot, this);
	public ShaderAttribArray look=new ShaderAttribArray("spots[", "].lookVec", numberSpot, this);
	public ShaderAttribArray attenuation=new ShaderAttribArray("spots[", "].attenuation", numberSpot, this);
	public ShaderAttribArray farSpot=new ShaderAttribArray("spots[", "].far", numberSpot, this);

	public ShaderAttribArray attenuationPoint=new ShaderAttribArray("points[", "].attenuation", numberSpot, this);
	public ShaderAttribArray colorPoint=new ShaderAttribArray("points[", "].color", numberSpot, this);
	public ShaderAttribArray pointShadows=new ShaderAttribArray("pointShadows[", "]", numberShadow, this);
	public ShaderAttribArray farPoint=new ShaderAttribArray("points[", "].far", numberSpot, this);

	public ShaderAttrib displacementTexture=new ShaderAttrib("displacementTexture", this);
	public ShaderAttrib normalTexture=new ShaderAttrib("normalTexture", this);
	public ShaderAttrib ssaoTexture=new ShaderAttrib("ssaoTexture", this);
	public ShaderAttrib ARMtexture=new ShaderAttrib("ARMtexture", this);
	public ShaderAttrib skyColor=new ShaderAttrib("skyColor", this);
	public ShaderAttrib reflectivity=new ShaderAttrib("reflectivity_max", this);
	public ShaderAttrib shineDamper=new ShaderAttrib("shineDamper_max", this);
	public ShaderAttrib modelTexture=new ShaderAttrib("modelTexture", this);
	public ShaderAttrib materialValue=new ShaderAttrib("materialValue", this);
	public ShaderAttrib screenSize=new ShaderAttrib("screenSize", this);

	public EntityNormalMapShader() {
		super(VERTEX, FRAGMENT);
		getAllUniformLocations();
	}

	public void init() {
		start();
		/*for (int i = 0; i < numberSpot; i++) {
			textureSpot.loadInt(i+10, i);
			texturePoint.loadInt(i+10+numberSpot,i);
		}*/
		System.err.println("TODO: LOAD SHADOW SPOTS ET POINTS");

		skyColor.loadVector3(Constantes.SKY_COLOR.getVector3());
		projectionMatrix.loadMatrix4f(Pipeline.getProjMat());
		gradient.loadFloat(Constantes.GRADIENT.getFloat());
		density.loadFloat(Constantes.DENSITY.getFloat());
		
		modelTexture.loadInt(0);
		ARMtexture.loadInt(1);
		normalTexture.loadInt(2);
		displacementTexture.loadInt(3);
		ssaoTexture.loadInt(4);
		for (int i = 0; i < Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt(); i++) {
			sunMap.loadInt(5+i,i);
		}


		mapSize.loadVector2D(new Vector2f(Constantes.SHADOW_MAP_SIZE.getInt(),Constantes.SHADOW_MAP_SIZE.getInt()));
		screenSize.loadVector2D(new Vector2f(DisplayManager.getWidth(),DisplayManager.getHeight()));
		stop();
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "tangent");
		super.bindAttribute(5, "transformationMatrix");
	}
	public void loadViewMatrix(Matrix4f v) {
		viewMatrix.loadMatrix4f(v);
		Matrix4f invViewMat = new Matrix4f();
		v.invert(invViewMat);
		invViewMatrix.loadMatrix4f(invViewMat);
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
			angle.loadFloat((float) Math.toRadians(light.getAngle()), i);
			//add 0.05 to make a smooth transition
			cos_angle.loadFloat((float)Math.cos(Math.toRadians(light.getAngle())+0.05),i);
			farSpot.loadFloat(light.getMaxRange(),i);
			spotInUse.loadFloat(-1,1+i);
			//spotMatrix.loadMatrix4f(light.getToShadowMapSpace(), i);
			/*GL13.glActiveTexture(GL13.GL_TEXTURE0+ 10 +i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, light.getShadowMap());*/
		}
	
		stop();
	}

	private Vector3 getEyeSpacePosition(Vector3 light, Matrix4f viewMatrix){
		Vector3f position = light.getOglVec();
		Vector4f eyeSpacePos = new Vector4f(position.x,position.y, position.z, 1f);
		eyeSpacePos=viewMatrix.transform(eyeSpacePos);
		return new Vector3(eyeSpacePos.x,eyeSpacePos.y,eyeSpacePos.z);
	}

	public void loadPosition(List<SpotLight>spots,List<PointLight>points,Matrix4f vm) {
		int s=spots.size();
		if (s>numberSpot)s=numberSpot;
		for (int i = 0; i < s; i++) {
			positionSpotWorld.loadVector3(spots.get(i).getPosition(), i);
			spotPosition.loadVector3(getEyeSpacePosition(spots.get(i).getPosition(),vm), i);
		}
		spotInUse.loadFloat(s,0);//load the number of used spots
		s=points.size();
		if (s>numberSpot)s=numberSpot;
		for (int i = 0; i < s; i++) {
			pointPosition.loadVector3(getEyeSpacePosition(points.get(i).getPosition(),vm), i);
		}

		pointInUse.loadFloat(s,0);//load the number of used spots
	}

	public void loadPoints(List<PointLight>points) {
		start();
		int s=points.size();
		if (s>numberSpot)s=numberSpot;
		for (int i = 0; i < s; i++) {
			PointLight light=points.get(i);
			pointPosition.loadVector3(light.getPosition(), i);
			colorPoint.loadVector3(light.getColor(), i);
			attenuationPoint.loadVector3(light.getAttenuation(), i);
			farPoint.loadFloat(light.getMaxRange(),i);
			pointInUse.loadFloat(-1,1+i);
		}
		stop();
	}

}
