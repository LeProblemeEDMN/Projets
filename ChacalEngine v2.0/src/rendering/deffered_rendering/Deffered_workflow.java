package rendering.deffered_rendering;

import entity.Entity;
import entity.Light.PointLight;
import entity.Light.SpotLight;
import loading.TexturedModel;
import main.*;
import rendering.capture_object.Fbo;
import rendering.capture_object.MultisampleFBO;
import rendering.depthWorkflow.Depth_workflow;
import rendering.render_objects.RenderingWorkflow;
import rendering.sky.SkyboxRenderer;

import java.util.List;
import java.util.Map;

public class Deffered_workflow extends RenderingWorkflow {
    /*
    display simple objects with shadow
     */
    //private Simple_renderer renderer;
    private Pre_deffered_renderer pre_pass;
    MultisampleFBO multisample_fbo;
    //private SkyboxRenderer renderer_skybox;
    public static Deffered_workflow instance;
    public static String NAME="DefferedRendering";
    public Deffered_workflow(){
        super(NAME,true);

        pre_pass =new Pre_deffered_renderer();
        renderers.add(pre_pass);
        multisample_fbo=new MultisampleFBO(DisplayManager.getWidth(), DisplayManager.getHeight());
    }

    @Override
    public void process(Map<TexturedModel, List<Entity>> entitesMap) {
        //fbo.bindFrameBuffer();
        multisample_fbo.bind();
        Pipeline.prepare();
        //set  the depth buffer to the value of the depth map allow to render less triangles
        //if(Constantes.Z_PRE_PASS.getValue_string().equals("true")) Pipeline.postProcessing.rendererDepth(Pipeline.depth_workflow.getOutTexture());

       // renderer_skybox.render(Pipeline.getViewMat());

        pre_pass.initRender();
        //use the world LOD sorted entities because we render from the camera viewpoint
        for(TexturedModel model : World.lodSortedEntities.keySet()){
            List<Entity>[] entities_LOD=World.lodSortedEntities.get(model);
            pre_pass.prepareTexture(model);
            for(int i=0;i<entities_LOD.length;i++) {
                if (entities_LOD[i].isEmpty()) continue;
                pre_pass.prepareModel(model.getRawModel(i));
                pre_pass.render(entities_LOD[i],model.getRawModel(i).getVertexCount());
            }
        }

        pre_pass.stop();
        multisample_fbo.unbind();
        //multisample_fbo.resolveDepthToFBO(fbo);
       // multisample_fbo.resolveToFBO(3,fbo);
       // fbo.unbindFrameBuffer();
    }

    @Override
    public int getOutTexture() {
        return multisample_fbo.getTextureId(0);//super.getOutTexture();
    }

    @Override
    public void cleanUp() {
        //renderer.cleanUp();
        pre_pass.cleanUp();
    }

    @Override
    public void resize(int w, int h) {
        super.resize(w, h);
        multisample_fbo.cleanup();
        multisample_fbo = new MultisampleFBO(w, h);
    }

    public MultisampleFBO getMultisample_fbo() {
        return multisample_fbo;
    }
}
