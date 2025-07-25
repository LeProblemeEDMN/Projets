package rendering.depthWorkflow;

import entity.Entity;
import loading.TexturedModel;
import main.DisplayManager;
import main.MainLoop;
import main.Pipeline;
import main.World;
import rendering.render_objects.RenderingWorkflow;
import rendering.shadow.ShadowFrameBuffer;
import rendering.sky.SkyboxRenderer;
import toolbox.maths.Frustum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Depth_workflow extends RenderingWorkflow {
    /*
    display simple objects with shadow
     */
    private Depth_renderer renderer;
    public ShadowFrameBuffer fbo_depth;

    public static Depth_workflow instance;
    public static String NAME="DepthWorkflow";
    public Depth_workflow(){
        super(NAME,false);
        renderer = new Depth_renderer();
        renderers.add(renderer);
        fbo_depth=new ShadowFrameBuffer(DisplayManager.getWidth(),DisplayManager.getHeight());
    }

    @Override
    public void process(Map<TexturedModel, List<Entity>> entitesMap) {
        fbo_depth.bindFrameBuffer();
        Pipeline.prepare();

        renderer.initRender();
        for(TexturedModel model : World.lodSortedEntities.keySet()){
            List<Entity>[] entities_LOD=World.lodSortedEntities.get(model);
            renderer.prepareTexture(model);
            for(int i=0;i<entities_LOD.length;i++) {
                if (entities_LOD[i].isEmpty()) continue;
                renderer.prepareModel(model.getRawModel(i));
                renderer.render(entities_LOD[i],model.getRawModel(i).getVertexCount());
            }
        }
        /*Frustum frustum = Frustum.getFrustum(Pipeline.getViewMat(), Pipeline.getProjMat());
        for(TexturedModel model : entitesMap.keySet()){
            renderer.prepareTexture(model);
            List<Entity>[] entities_LOD=new List[model.getNumberLOD()];
            for(int i=0;i<entities_LOD.length;i++)
                entities_LOD[i]=new ArrayList<>();

            for (Entity e: entitesMap.get(model)) {
                if(Pipeline.camera.can_be_seen(e)){//Pipeline.camera.getPosition().squareDistanceTo(e.getPosition())<e.renderingDistance*e.renderingDistance) {
                    if (e.inFrustum(frustum)) {
                        int l_id=model.getLODId(e.screen_percent(Pipeline.camera.getPosition()));
                        entities_LOD[l_id].add(e);
                    }
                }
            }
            for(int i=0;i<entities_LOD.length;i++) {
                if (entities_LOD[i].isEmpty()) continue;
                renderer.prepareModel(model.getRawModel(i));
                renderer.render(entities_LOD[i],model.getRawModel(i).getVertexCount());
            }
        }*/

        renderer.stop();
        fbo_depth.unbindFrameBuffer();
    }

    @Override
    public int getOutTexture() {


        return fbo_depth.getShadowMap();
    }

    @Override
    public void cleanUp() {
        super.cleanUp();
        renderer.cleanUp();
        fbo_depth.cleanUp();
    }

    @Override
    public void resize(int w, int h) {
        super.resize(w, h);
        fbo_depth.cleanUp();
        fbo_depth=new ShadowFrameBuffer(w,h);
    }
}
