package ShaderEngine.entityI;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;

import Entity.Light.PointLight;
import Entity.Light.SpotLight;
import Main.MainGame;
import Main.MainRender;
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderAttribArray;
import ShaderEngine.ShaderProgram;

public class EntityIShader extends ShaderProgram{
	public int numberSpot=10;
	public static String VERTEX="/ShaderEngine/entityI/vertexShader.txt";
	public static String FRAGMENT="/ShaderEngine/entityI/fragmentShader.txt";
	
	public ShaderAttrib transformationMatrix=new ShaderAttrib("transformationMatrix", this);
	public ShaderAttrib projectionMatrix=new ShaderAttrib("projectionMatrix", this);
	public ShaderAttrib viewMatrix=new ShaderAttrib("viewMatrix", this);
	public ShaderAttrib invViewMatrix=new ShaderAttrib("invViewMatrix", this);
	
	public ShaderAttrib density=new ShaderAttrib("density", this);
	public ShaderAttrib gradient=new ShaderAttrib("gradient", this);
	public ShaderAttrib shadowDistance=new ShaderAttrib("shadowDistance", this);
	
	public ShaderAttribArray spotPosition=new ShaderAttribArray("positionSpot[", "]", numberSpot, this);
	public ShaderAttribArray pointPosition=new ShaderAttribArray("positionPoint[", "]", numberSpot, this);
	public ShaderAttribArray spotMatrix=new ShaderAttribArray("spotMatrix[", "]", numberSpot, this);
	public ShaderAttribArray spotInUse=new ShaderAttribArray("spotInUse[", "]", 5, this);
	public ShaderAttribArray pointInUse=new ShaderAttribArray("pointInUse[", "]", 5, this);
	public ShaderAttribArray pointInUseVertex=new ShaderAttribArray("pointInUseVertex[", "]", 5, this);
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

	
	public ShaderAttrib skyColor=new ShaderAttrib("skyColor", this);
	public ShaderAttrib reflectivity=new ShaderAttrib("reflectivity", this);
	public ShaderAttrib shineDamper=new ShaderAttrib("shineDamper", this);
	public ShaderAttrib modelTexture=new ShaderAttrib("modelTexture", this);
	
	public EntityIShader() {
		super(VERTEX, FRAGMENT);
		getAllUniformLocations();
	}
	
	public void init() {
		start();
		for (int i = 0; i < numberSpot; i++) {
			textureSpot.loadInt(i+10, i);
			texturePoint.loadInt(i+10+numberSpot,i);
		}
		skyColor.loadVector3(MainRender.SKY_COLOR);
		gradient.loadFloat(MainRender.GRADIENT);
		density.loadFloat(MainRender.DENSITY);
		shadowDistance.loadFloat(40);
		
		modelTexture.loadInt(0);
		
		stop();
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		
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
