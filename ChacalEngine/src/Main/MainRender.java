package Main;

import Animation.renderer.AnimatedModelRenderer;
import Entity.Camera;
import Entity.Entity;
import Gui.FondRendu.ImageShader;
import Gui.GuiInGame;
import Init.EnviroMapRenderer;
import Init.LightInit.PointLightInit;
import Init.LightInit.SpotLightInit;
import Init.Renderer.EntityCubeMapRender;
import Loader.ConfigLoader;
import Loader.CubeMap.CubeMap;
import PostProcessing.PostProcessing;
import PostProcessing.SSAO.SSAORenderer;
import PostProcessing.def.RendererDeffered;
import RenderEngine.DefferedRenderer;
import RenderEngine.Fbo;
import RenderEngine.MasterRenderer;
import RenderEngine.gui.GuiRenderer;
import RenderEngine.gui.GuiTexture;
import RenderEngine.gui.font.FontType;
import RenderEngine.gui.font.GUIText;
import RenderEngine.gui.font.TextMaster;
import RenderEngine.shadow.ShadowMapMasterRenderer;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import toolbox.maths.ProgramStats;
import toolbox.maths.Vector3;
import tortue.Tortue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainRender {
	public static boolean DLSS_ON=true;
	public static Vector3 SKY_COLOR;
	public static float GRADIENT,DENSITY;
	
	public static List<GuiTexture>guis=new ArrayList<>();
	
	public static MasterRenderer masterRenderer;
	public static GuiRenderer guiRenderer;
	
	public static Camera CAMERA;
	
	private static ImageShader imageRenderer;

	public static Fbo fbo;

	//public static AnimatedModelRenderer animatedModelRenderer=new AnimatedModelRenderer();
	public static ShadowMapMasterRenderer shadowMapMasterRenderer;
	private static SSAORenderer ssaoRenderer;
	private static DefferedRenderer defferedRenderer;
	private static RendererDeffered deffered;
	public static int s0;
	public static void Init(ConfigLoader configLoader) {
		CAMERA=new Camera(configLoader);
		CAMERA.setPosition(new Vector3(250,25,300));
		//CAMERA.setPosition(Tortue.base.getMul(1));
		CAMERA.setPitch(-90);
	//	CAMERA.setPitch(MainGame.spots.get(0).getRotation().x);
		
		SKY_COLOR=configLoader.getVector3Parameter("skyColor");
		GRADIENT=configLoader.getFloatParameter("gradient");
		DENSITY=configLoader.getFloatParameter("density");
		
		masterRenderer=new MasterRenderer();
		shadowMapMasterRenderer=new ShadowMapMasterRenderer(CAMERA);
		
		TextMaster.init(MainLoop.LOADER);
		guiRenderer=new GuiRenderer(MainLoop.LOADER);
		masterRenderer.entities.clear();
		//ssaoRenderer = new SSAORenderer();
		for (Entity e : MainGame.entities) {
			masterRenderer.processEntity(e);
		}
		imageRenderer=new ImageShader();
		PostProcessing.init(MainLoop.LOADER, configLoader);
		if(DLSS_ON)
			fbo=new Fbo(Display.getWidth()/2,Display.getHeight()/2,Fbo.DEPTH_RENDER_BUFFER);
		else
			fbo=new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_RENDER_BUFFER);
		fbo=new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
        // fbo=new ShadowFrameBuffer(Display.getWidth(),Display.getHeight());
		File cali= new File("res/other/calibri.fnt");
		int nb=MainLoop.LOADER.loadTexture("other/calibri");
		Register.type=new FontType(nb, cali);
		Register.text=new GUIText("fps=0", 1, Register.type, new Vector2f(10, 10), 1f, false,true);
		Register.text.setColour(0, 1,0);

		deffered=new RendererDeffered();
		defferedRenderer=new DefferedRenderer(deffered);

	//	s0= MainLoop.LOADER.loadTexture("s0");
	//	guis.add(new GuiTexture(PostProcessing.dlssScattering.getOutputTexture(), new Vector2f(0.75f, 0.0f),  new Vector2f(0.25f, 0.25f)));
	//	guis.add(new GuiTexture(shadowMapMasterRenderer.getShadowMap(), new Vector2f(0.75f, -0.75f),  new Vector2f(0.25f, 0.25f)));

	}
	
	public static void PostInit() {
		long t=System.currentTimeMillis();
		SpotLightInit.initSpot(MainGame.spots, masterRenderer.entities, MainLoop.CONFIG.getIntParameter("shadowResolution"), MainLoop.CONFIG.getIntParameter("shadowResolution"));
		masterRenderer.getEntityRenderer().getShader().loadSpot(MainGame.spots);
		deffered.getShader().loadSpot(MainGame.spots);
		masterRenderer.getEntityRendererNormalMap().getShader().loadSpot(MainGame.spots);
		long d1=System.currentTimeMillis()-t;
		t=System.currentTimeMillis();
		
		PointLightInit.initLight(MainGame.pointLigths, masterRenderer.entities, MainLoop.CONFIG.getIntParameter("shadowResolution"));
		masterRenderer.getEntityRenderer().getShader().loadPoints(MainGame.pointLigths);
		deffered.getShader().loadPoints(MainGame.pointLigths);
		masterRenderer.getEntityRendererNormalMap().getShader().loadPoints(MainGame.pointLigths);
		long d2=System.currentTimeMillis()-t;
		t=System.currentTimeMillis();
		
		EntityCubeMapRender.initCube(MainGame.spots, MainGame.pointLigths);
		EnviroMapRenderer.init(MainLoop.CONFIG.getIntParameter("materialResolution"));
		for (Entity e : MainGame.entities) {
			if(e.isHaveCubeMap()) {
			
			CubeMap map=new CubeMap(CubeMap.newEmptyCubeMap(MainLoop.CONFIG.getIntParameter("materialResolution")),MainLoop.LOADER,MainLoop.CONFIG.getIntParameter("materialResolution"));
			
			EnviroMapRenderer.renderEnvironmentMap(masterRenderer.entities,masterRenderer.entitiesNormalMap,map, e.getPosition().getOglVec(),e,MainLoop.CONFIG.getIntParameter("materialResolution"));
			e.setMap(map);
		
			}
		}
		//PointLightInit2.initLight(new Vector3(-50f, 0f,-5f),masterRenderer.entities,masterRenderer.entitiesNormalMap, MainGame.entities.get(0), MainLoop.CONFIG.getIntParameter("materialResolution"));
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		
		long d3=System.currentTimeMillis()-t;
		System.out.println("Intitialisation des spots en "+d1+",des points en "+d2+",creation des cubeMaps en "+d3);
		
		EnviroMapRenderer.cleanUp();
		SpotLightInit.cleanUp();
		PointLightInit.cleanUp();
		System.out.println(CAMERA.getPosition());
		MainGame.body.initrender();
	}
	public static long lastPointTotal=0;

	public static int FPS_TIME=(int)(1000.0/40);
	public static boolean smoothfps=true;
	public static long lastFrame=0;
	public static void Update() {
		ProgramStats.startStat(ProgramStats.mainPreparation);
		resize();
        CAMERA.update();
		if(System.currentTimeMillis()-lastFrame>=FPS_TIME || !smoothfps) {
		    lastFrame=System.currentTimeMillis();


            ProgramStats.startAndRemoveStat(ProgramStats.shadow);
            shadowMapMasterRenderer.render(MainGame.pointLigths.get(0), MainGame.entities);

           // long t=System.currentTimeMillis();
			SpotLightInit.initSpot(MainGame.spots, masterRenderer.entities, MainLoop.CONFIG.getIntParameter("shadowResolution"), MainLoop.CONFIG.getIntParameter("shadowResolution"));
			//System.out.println(System.currentTimeMillis()-t);

			ProgramStats.startAndRemoveStat(ProgramStats.normalRender);
            ProgramStats.totalPoint = lastPointTotal;
            fbo.bindFrameBuffer();
            masterRenderer.render(MainGame.entities);
			MainGame.body.render();
            //animatedModelRenderer.render(Register.animatedModel, CAMERA, new Vector3(0, -1, 0));
            lastPointTotal = ProgramStats.totalPoint;
			//PostProcessing.flareManager.render(MainRender.CAMERA, MainGame.pointLigths.get(0).getPosition().getOglVec());
            fbo.unbindFrameBuffer();


            PostProcessing.doPostProcessing(fbo.getDepthTexture(), fbo.getColourTexture());

            if (GuiInGame.LAGOMETER_ACTIV) GuiInGame.renderBuffered(imageRenderer);
        }
		ProgramStats.startAndRemoveStat(ProgramStats.postProcessing);
        PostProcessing.render();
        ProgramStats.startAndRemoveStat(ProgramStats.gui);

        TextMaster.render();
        guiRenderer.Render(guis);

	//	defferedRenderer.render(masterRenderer.entities,masterRenderer.entitiesNormalMap);

		ProgramStats.removeStat();
	}
	public static void CleanUp() {
		masterRenderer.cleanUp();
		TextMaster.CleanUp();
		guiRenderer.cleanUp();
		imageRenderer.cleanUp();
		guis.clear();
		PostProcessing.cleanUp();
		shadowMapMasterRenderer.cleanUp();
	}
	public static void resize() {
		if(Display.wasResized()) {
			System.out.print(Display.getWidth()+" "+Display.getHeight());
			GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
			CAMERA.createProjectionMatrix();
			masterRenderer.resize();
			PostProcessing.Resized();
			
		}
	}
	
}
