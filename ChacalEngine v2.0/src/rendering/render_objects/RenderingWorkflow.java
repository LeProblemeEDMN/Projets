package rendering.render_objects;

import entity.Entity;
import loading.TexturedModel;
import main.DisplayManager;
import main.Pipeline;
import rendering.capture_object.Fbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RenderingWorkflow {
    /*
    The workflow draw something to a FBO.
    It is the brick of the rendering. Each workflow will have a precise role (shadow or post processing or rendering objects...)
     */
    public Fbo fbo;//where the iamge is drawn
    protected List<Renderer> renderers=new ArrayList<Renderer>();//different rendered called
    protected int idWorkflow;
    protected String name;//unique name used to find the workflow
    protected boolean useFBO=true;

    public RenderingWorkflow(String name,boolean useFBO) {
        this.name = name;
        this.useFBO=useFBO;
        if(useFBO)
            this.fbo = new Fbo(DisplayManager.getWidth(), DisplayManager.getHeight(),Fbo.RGBA,Fbo.DEPTH_TEXTURE);
        idWorkflow = Pipeline.renderingWorkflows.size();

    }
    //function who render to the FBO Called by pipeline
    public abstract void process(Map<TexturedModel, List<Entity>> entitesMap);

    //fucniton called when the window is resized
    public void resize(int w,int h){
        for (Renderer renderer: renderers){
            renderer.resize(w,h);
        }

        if(useFBO) {
            fbo.cleanUp();
            fbo = new Fbo(w, h,Fbo.RGBA,Fbo.DEPTH_TEXTURE);
        }
    }
    //function called when the window is closed
    public void cleanUp(){
        if(useFBO)fbo.cleanUp();
    }


    public Fbo getFbo() {
        return fbo;
    }

    public int getOutTexture(){
        return fbo.getColourTexture();
    }

    public int getIdWorkflow() {
        return idWorkflow;
    }

    public String getName() {
        return name;
    }
}
