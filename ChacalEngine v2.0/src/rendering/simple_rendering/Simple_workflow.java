package rendering.simple_rendering;

import entity.Entity;
import entity.Light.Light;
import entity.Light.PointLight;
import entity.Light.SpotLight;
import loading.TexturedModel;
import main.Constantes;
import main.MainLoop;
import main.Pipeline;
import main.World;
import rendering.depthWorkflow.Depth_workflow;
import rendering.render_objects.Renderer;
import rendering.render_objects.RenderingWorkflow;

import rendering.shadow.CascadedShadowMap;
import rendering.sky.SkyboxRenderer;
import toolbox.maths.AxisAlignedBB;
import toolbox.maths.Frustum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Simple_workflow extends RenderingWorkflow {
    /*
    display simple objects with shadow
     */
    //private Simple_renderer renderer;
    private Simple_normal_map_renderer renderer_normal_map;
    private SkyboxRenderer renderer_skybox;

    public static String NAME="SimpleWorkflow";
    public static Simple_workflow instance;
    public Simple_workflow(){
        super(NAME,true);
        //renderer = new Simple_renderer();
        renderer_normal_map=new Simple_normal_map_renderer();
        renderer_skybox=new SkyboxRenderer(MainLoop.LOADER,Pipeline.getProjMat());
        //renderers.add(renderer);
        renderers.add(renderer_skybox);
        renderers.add(renderer_normal_map);
    }

    @Override
    public void process(Map<TexturedModel, List<Entity>> entitesMap) {
        fbo.bindFrameBuffer();
        Pipeline.prepare();
        //set  the depth buffer to the value of the depth map allow to render less triangles
        if(Constantes.Z_PRE_PASS.getValue_string().equals("true")) Pipeline.postProcessing.rendererDepth(Depth_workflow.instance.getOutTexture());

        renderer_skybox.render(Pipeline.getViewMat());

        renderer_normal_map.initRender();
        //use the world LOD sorted entities because we render from the camera viewpoint
        for(TexturedModel model : World.lodSortedEntities.keySet()){
            List<Entity>[] entities_LOD=World.lodSortedEntities.get(model);
            renderer_normal_map.prepareTexture(model);
            for(int i=0;i<entities_LOD.length;i++) {
                if (entities_LOD[i].isEmpty()) continue;
                renderer_normal_map.prepareModel(model.getRawModel(i));
                renderer_normal_map.render(entities_LOD[i],model.getRawModel(i).getVertexCount());
            }
        }

        renderer_normal_map.stop();
        fbo.unbindFrameBuffer();
    }

    public void loadLights(List<PointLight> pointLights, List<SpotLight> spotLights){
        renderer_normal_map.getShader().start();
        renderer_normal_map.getShader().loadPoints(pointLights);
        renderer_normal_map.getShader().loadSpot(spotLights);
        renderer_normal_map.getShader().stop();
    }

    @Override
    public void cleanUp() {
        //renderer.cleanUp();
        renderer_normal_map.cleanUp();
    }

    @Override
    public void resize(int w, int h) {
        super.resize(w, h);
    }
}
