package ShaderEngine.entity;

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
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import rendering.shadow.Shadow;

import java.util.List;

public class EntityShader extends ShaderProgram{
	public static int numberSpot=10;
	public static String VERTEX= "/ShaderEngine/entity/vertexShader.txt";
	public static String FRAGMENT= "/ShaderEngine/entity/fragmentShader.txt";

	//public ShaderAttrib transformationMatrix=new ShaderAttrib("transformationMatrix", this);
	public ShaderAttrib projectionMatrix=new ShaderAttrib("projectionMatrix", this);
	public ShaderAttrib viewMatrix=new ShaderAttrib("viewMatrix", this);
	public ShaderAttrib invViewMatrix=new ShaderAttrib("invViewMatrix", this);

	public ShaderAttrib density=new ShaderAttrib("density", this);
	public ShaderAttrib gradient=new ShaderAttrib("gradient", this);
	public ShaderAttribArray spotPosition=new ShaderAttribArray("positionSpot[", "]", numberSpot, this);
	public ShaderAttribArray pointPosition=new ShaderAttribArray("positionPoint[", "]", numberSpot, this);
	public ShaderAttribArray spotMatrix=new ShaderAttribArray("spotMatrix[", "]", numberSpot, this);
	//5 spots + 1 pour dire cb sont utilisé
	public ShaderAttribArray spotInUse=new ShaderAttribArray("spotInUse[", "]", 5+1, this);
	public ShaderAttribArray pointInUse=new ShaderAttribArray("pointInUse[", "]", 5+1, this);
	public ShaderAttribArray pointInUseVertex=new ShaderAttribArray("pointInUseVertex[", "]", 5+1, this);
	public ShaderAttrib useFakeLightning=new ShaderAttrib("useFakeLightning", this);
	public ShaderAttrib r_shadow=new ShaderAttrib("r_shadow", this);
	public ShaderAttrib d0_shadow=new ShaderAttrib("d0_shadow", this);
	public ShaderAttribArray sunMap=new ShaderAttribArray("sunTexture[","]",Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt(), this);
	public ShaderAttrib mapSize=new ShaderAttrib("mapSize", this);

	public ShaderAttribArray toShadowMapSpace=new ShaderAttribArray("toShadowMapSpace[","]",Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt(), this);
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


	public ShaderAttrib ssaoTexture=new ShaderAttrib("ssaoTexture", this);
	public ShaderAttrib ARMtexture=new ShaderAttrib("ARMtexture", this);
	public ShaderAttrib skyColor=new ShaderAttrib("skyColor", this);
	public ShaderAttrib reflectivity=new ShaderAttrib("reflectivity_max", this);
	public ShaderAttrib shineDamper=new ShaderAttrib("shineDamper_max", this);
	public ShaderAttrib modelTexture=new ShaderAttrib("modelTexture", this);
	public ShaderAttrib materialValue=new ShaderAttrib("materialValue", this);
	public ShaderAttrib screenSize=new ShaderAttrib("screenSize", this);

	public EntityShader() {
		super(VERTEX, FRAGMENT);
		getAllUniformLocations();
	}

	public void init() {
		start();
		for (int i = 0; i < numberSpot; i++) {
			textureSpot.loadInt(i+10, i);
			texturePoint.loadInt(i+10+numberSpot,i);
		}
		skyColor.loadVector3(Constantes.SKY_COLOR.getVector3());
		projectionMatrix.loadMatrix4f(Pipeline.getProjMat());
		gradient.loadFloat(Constantes.GRADIENT.getFloat());
		density.loadFloat(Constantes.DENSITY.getFloat());
		
		modelTexture.loadInt(0);
		ARMtexture.loadInt(1);
		for (int i = 0; i < Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt(); i++) {
			sunMap.loadInt(3+i,i);
		}
		ssaoTexture.loadInt(Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt()+3);

		mapSize.loadVector2D(new Vector2f(Constantes.SHADOW_MAP_SIZE.getInt(), Constantes.SHADOW_MAP_SIZE.getInt()));
		screenSize.loadVector2D(new Vector2f(DisplayManager.getWidth(),DisplayManager.getHeight()));
		stop();
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		super.bindAttribute(5, "transformationMatrix");
	}
	public void loadViewMatrix(Matrix4f v) {
		viewMatrix.loadMatrix4f(v);
		invViewMatrix.loadMatrix4f(v.invert(v));
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
			spotMatrix.loadMatrix4f(light.getToShadowMapSpace(), i);
			farSpot.loadFloat(light.getMaxRange(),i);
			/*GL13.glActiveTexture(GL13.GL_TEXTURE0+ 10 +i);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, light.getShadowMap());*/
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

			colorPoint.loadVector3(light.getColor(), i);
			attenuationPoint.loadVector3(light.getAttenuation(), i);
			farPoint.loadFloat(light.getMaxRange(),i);

		}
		stop();
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
		spotInUse.loadFloat(l,5);
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
		pointInUse.loadFloat(l,5);
		pointInUseVertex.loadFloat(l,5);
	}
}
