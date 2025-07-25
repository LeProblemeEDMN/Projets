package rendering.depthWorkflow;

import entity.Entity;
import loading.RawModel;
import loading.TexturedModel;
import main.Pipeline;
import main.World;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;
import rendering.render_objects.Renderer;
import rendering.shadow.EntityShadow.EntityShadowShader;
import toolbox.OpenGlUtils;

import java.util.List;

public class Depth_renderer extends Renderer {
    private EntityShadowShader shader;

    public Depth_renderer() {
        this.shader = new EntityShadowShader();

    }

    public void initRender() {
        shader.start();
        Matrix4f VP=new Matrix4f(Pipeline.getProjMat()).mul(Pipeline.getViewMat());
        shader.VPMatrix.loadMatrix4f(VP);
    }

    public void prepareTexture(TexturedModel model){
        if(model.getTexture().isTransparance())  OpenGlUtils.cullBackFaces(false);
        else{
            OpenGlUtils.cullBackFaces(true);
        }
    }

    public void prepareModel(RawModel rawModel){
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
    }

    @Override
    public void render(List<Entity> entities,int vertexCount) {
        int instanceCount=entities.size();

        int transformationVBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, transformationVBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, World.bufferSortedEntities.get(entities), GL15.GL_DYNAMIC_DRAW);

        // Appel de rendu
        int location=5;
        for (int i = 0; i < 4; i++) {
            GL20.glEnableVertexAttribArray(location + i);
            GL20.glVertexAttribPointer(location + i, 4, GL11.GL_FLOAT, false, 64, i * 16);
            GL33.glVertexAttribDivisor(location + i, 1); // Change par instance
        }
        GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0, instanceCount);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(transformationVBO);
    }

    public void stop(){
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        int transformationVBO=5;
        for (int i = 0; i < 4; i++) {
            GL20.glDisableVertexAttribArray(transformationVBO + i);
        }
        shader.stop();
    }

    @Override
    public void resize(int w, int h) {

    }

    @Override
    public void cleanUp() {
        shader.cleanUp();
    }
}
