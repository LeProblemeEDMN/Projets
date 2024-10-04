package Main;

import Entity.Entity;
import Entity.Light.Light;
import Entity.Light.PointLight;
import Entity.Light.SpotLight;
import Entity.*;
import Gui.GuiInGame;
import Loader.TexturedModel;
import Physics.Animator;
import Physics.SoftBody;
import PostProcessing.PostProcessing;
import RenderEngine.DisplayManager;
import Scene.Balls.BallsArrete;
import Scene.Balls.Ballsbullet;
import Scene.Scene;
import toolbox.InputManager;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;
import tortue.Tortue;

import java.util.*;


public class MainGame {
	public static List<Entity>entities=new ArrayList<>();
	public static Map<TexturedModel, List<Entity>> entitesMap=new HashMap<TexturedModel, List<Entity>>();
	public static List<Ball>ballPhysic=new ArrayList<>();
	public static List<Plan>PLANS=new ArrayList<>();

	public static List<Light>lights=new ArrayList<>();
	public static List<SpotLight>spots=new ArrayList<>();
	public static List<PointLight>pointLigths=new ArrayList<>();


	public static double rho=Math.toRadians(30);
	public static double phi= Math.toRadians(80);
	public static double timeDay=60;
	public static double distance=1000000;

	public static Vector3 sunset=new Vector3(1,0.57f,0.245f);
	public static Vector3 midDay= new Vector3(1f,1f,0.8f);
	public static Vector3 sunrise=new Vector3(1,0.57f,0.245f);

	public static Scene scene;

	public static SoftBody body;

	public static void Init() {
		InputManager.init();

		//scene=new Ballsbullet(500,100000,new Vector3(250,50,250),new Vector3(0,0,0),new Vector3(5,100,0),1,0,0);
		//scene=new BallsArrete(0.1f,new Vector3(0,2,0),new Vector3(250,0,250));
		body=new SoftBody();
	}


	public static float t=0;
	private static float lastExposureUpdate=0;
	public static void Update() {

		/*Iterator<Float> iterator =scene.getMapBall().keySet().iterator();
		while (iterator.hasNext()){
			if(iterator.hasNext()) {
				float t = iterator.next();
				if (DisplayManager.totalsec > t) {
					List<Ball> balls = scene.getMapBall().get(t);
					ballPhysic.addAll(balls);
					//System.out.println(entitesMap);
					for (int i = 0; i < balls.size(); i++) {
						processEntity(balls.get(i).entity);
						entities.add(balls.get(i).entity);
					}
					iterator.remove();
				}
			}
		}*/

		//if(DisplayManager.totalsec-15>0) {
			Animator.update(DisplayManager.getFrameTimeSecond());
			body.update(0.01f);
			/*for (int i = 0; i < ballPhysic.size(); i++) {

				ballPhysic.get(i).entity.setPosition(ballPhysic.get(i).getPosition());
				//System.out.println(ballPhysic.get(i).entity.getPosition());
				ballPhysic.get(i).entity.setTransformationMatrix();
			}*/
		//}
		//Tortue.update(DisplayManager.getFrameTimeSecond());
		t+= DisplayManager.getFrameTimeSecond();
		if(t>lastExposureUpdate+0.1f) {
			float dt=t-lastExposureUpdate;
			lastExposureUpdate=t;
			double value = PostProcessing.autoexposure();
			float target = 110;
			if (value < target) {
				float de = target - (float) value;
				PostProcessing.combineFilter.exposure += de * dt * 0.01;
				PostProcessing.combineFilter2.exposure += de * dt * 0.01;
				PostProcessing.combineFilter.loadExposure();
				PostProcessing.combineFilter2.loadExposure();
			} else if(value>target+10){
				float de = target+10 - (float) value;
				//System.out.println(PostProcessing.combineFilter.exposure+" "+target);
				PostProcessing.combineFilter.exposure += de * dt * 0.01;
				PostProcessing.combineFilter2.exposure += de * dt * 0.01;
				PostProcessing.combineFilter.loadExposure();
				PostProcessing.combineFilter2.loadExposure();
			}
		}


		InputManager.update();
		if(InputManager.information.isClicked()) {
			GuiInGame.LAGOMETER_ACTIV=!GuiInGame.LAGOMETER_ACTIV;
		}
		Register.dragon.setRotY(Register.dragon.getRotY()+90*DisplayManager.getFrameTimeSecond());
		Register.dragon.setTransformationMatrix();

		Register.animatedModel.update();

		phi+= Math.PI*DisplayManager.getFrameTimeSecond()/timeDay;
		if (phi>Math.PI)phi-= Math.PI;
		pointLigths.get(0).setPosition(new Vector3(Math.cos(phi)*Math.cos(rho)*distance, Math.sin(phi)*Math.cos(rho)*distance,Math.sin(rho)*distance));

		double avancement= Math.abs(phi- Math.PI/2)/(Math.PI/2);
		pointLigths.get(0).setColor(Maths.lerp(sunset,midDay,(float) Math.pow(avancement,3f)).mul(0.4f+0.6f*(float)Math.sqrt(1-avancement)));
	}
	public static void CleanUp() {
		
	}
	public static void processEntity(Entity entity) {
		for (int i = 0; i < entity.getListTexturedModel().size(); i++) {
			TexturedModel model=entity.getTexturedModel(i);
				List<Entity>batch=entitesMap.get(model);
				if(batch!=null) {
					batch.add(entity);
				}else {
					List<Entity>newBatch=new ArrayList<Entity>();
					newBatch.add(entity);
					entitesMap.put(model, newBatch);
				}
		}
	}
}
