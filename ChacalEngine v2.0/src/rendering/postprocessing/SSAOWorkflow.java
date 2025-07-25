package rendering.postprocessing;

import entity.Entity;
import loading.TexturedModel;
import main.Pipeline;
import postProcessingEffect.SSAO.SSAO;
import rendering.depthWorkflow.Depth_workflow;
import rendering.render_objects.RenderingWorkflow;

import java.util.List;
import java.util.Map;

public class SSAOWorkflow extends RenderingWorkflow {
    int texture;
    public static SSAOWorkflow instance;
    public static String NAME="SSAOWorkflow";
    public SSAOWorkflow() {
        super(NAME, false);
    }

    @Override
    public void process(Map<TexturedModel, List<Entity>> entitesMap) {
        SSAO.render(Depth_workflow.instance.getOutTexture(),false);
        texture=PostProcessing.getOut_fbo().getColourTexture();
    }


    @Override
    public int getOutTexture() {
        return texture;
    }
}
