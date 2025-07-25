package main;

import GUI.font.FontManager;
import GUI.GuiManager;
import entity.Camera;
import org.lwjgl.opengl.GL11;
import org.joml.*;
import postProcessingEffect.DefferedRendering.DefferedRenderer;
import postProcessingEffect.FXAA.FXAA;
import postProcessingEffect.SSGI.SSGIRenderer;
import postProcessingEffect.ToneMapping.ToneMapping;
import rendering.capture_object.Fbo;
import rendering.deffered_rendering.Deffered_workflow;
import rendering.depthWorkflow.Depth_workflow;
import rendering.postprocessing.PostProcessing;
import rendering.postprocessing.SSAOWorkflow;
import rendering.render_objects.RenderingWorkflow;
import rendering.shadow.CascadedShadowMap;
import rendering.shadow.ShadowFrameBuffer;
import rendering.simple_rendering.Simple_workflow;
import toolbox.MousePicker;
import toolbox.maths.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.lwjgl.opengl.GL11C.glBlendFunc;

public class Pipeline {
    /*
    The rendering is done using multipel workflows. Each workflow draw something in a frame buffer.
    The shown image is the image stored in the  framebuffer of the last workflow rendered.
    An example of a pipeline is
    -a first workflow to make a shadow map
    -a second workflow for rendering objects with their shadow (use the shadow map drawn before)
    -and finally a post processing to modify the gamma.

     */

    /*
    Pipeline:
    Cascaded shadow map
    Depth texture (for SSAO and optimizing forward rendering)
    SSAO +blur
    Forward rendering-> fill depthBuffer with depthTexture
                     -> skybox
                     -> simple rendering.
     PostProcessing Pipeline
     Screen
     */
    public static Camera camera;

    public static List<RenderingWorkflow> renderingWorkflows = new ArrayList<>();
    public static HashMap<String, RenderingWorkflow> renderingWorkflowMap = new HashMap<>();

    public static RenderingWorkflow getRenderingWorkflow(String name){return renderingWorkflowMap.get(name);}
    public static boolean haveRenderingWorkflow(String name){return renderingWorkflowMap.containsKey(name);}

    public static MousePicker mousePicker;//reconstruct ray leaving form teh mouse cursor

    public static Matrix4f getProjMat(){ return camera.getProjectionMatrix();}
    public static Matrix4f getViewMat(){ return camera.getViewMatrix();}
    public static PostProcessing postProcessing;

    /*public static CascadedShadowMap shadowWorkflow;
    public static Depth_workflow depth_workflow;
    public static Deffered_workflow deffered_workflow;
    public static SSAOWorkflow ssao;*/

    public static List<String> postprocessingEffects=new ArrayList<>();//list containing all of the postprocessing effects in the order they will eb executed
    //public static Shadow shadowWorkflow;
    public static void init(ConfigLoader configLoader){
        camera = new Camera(configLoader);
        mousePicker = new MousePicker(camera,camera.getProjectionMatrix());


        Initialisation.init_rendering_workflows();

        postProcessing=new PostProcessing();
        postProcessing.init(MainLoop.LOADER);
    }

    public static void render(){
        mousePicker.update();//update the ray
        for (RenderingWorkflow renderingWorkflow:renderingWorkflows) {
            renderingWorkflow.process(World.entitesMap);
        }
        //do the post processing
        postProcessing(renderingWorkflows.get(renderingWorkflows.size()-1).getFbo());

        //deffered_workflow.getMultisample_fbo().resolveToScreen(1);
        PostProcessing.getOut_fbo().bindFrameBuffer();
        PostProcessing.getOut_fbo().resolveToScreen();
        //shadowWorkflow.fbos[0].resolveToScreen();

        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable( GL11.GL_BLEND );
        GuiManager.render();

        GL11.glDisable( GL11.GL_BLEND );
        FontManager.renderMessages();
    }

    /*
    Do the post processing
     */
    public static void postProcessing(Fbo fbo){
        PostProcessing.setOut_fbo(fbo);
        PostProcessing.start();
        for (String effectName:postprocessingEffects){

            PostProcessing.renderEffect(effectName,PostProcessing.getOut_fbo().getColourTexture(),false);
        }

       /* DefferedRenderer.render(false);
        //SSGIRenderer.render(PostProcessing.getOut_fbo().getColourTexture(),false);

        if(Constantes.ANTIALIASING.value_string.equals("FXAA")){
            FXAA.render(PostProcessing.getOut_fbo().getColourTexture(), false);
        }
        ToneMapping.render(PostProcessing.getOut_fbo().getColourTexture(), Constantes.EXPOSURE.getFloat(), false);
        */PostProcessing.end();
    }

    public static void replaceWorkflow(RenderingWorkflow wr){
        RenderingWorkflow old=renderingWorkflowMap.get(wr.getName());
        renderingWorkflows.add(renderingWorkflows.indexOf(old),wr);
        renderingWorkflows.remove(old);
        renderingWorkflowMap.put(wr.getName(),wr);

    }


    public static void cleanup(){
        for (RenderingWorkflow r:renderingWorkflows)
            r.cleanUp();
        PostProcessing.cleanUp();
        GuiManager.cleanUp();
    }

    public static void addRW(RenderingWorkflow rw){
        renderingWorkflows.add(rw);
        renderingWorkflowMap.put(rw.getName(), rw);
    }

    public static void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClearColor(Constantes.SKY_COLOR.vector3.x,Constantes.SKY_COLOR.vector3.y, Constantes.SKY_COLOR.vector3.z, 	2);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);

    }

    public static void resize(int width, int height) {
        camera.createProjectionMatrix();
        for(RenderingWorkflow rw:renderingWorkflows){
            rw.resize(width, height);
        }
        PostProcessing.resize(width,height);
        GuiManager.resize(width,height);
        mousePicker.resize();
    }
}
