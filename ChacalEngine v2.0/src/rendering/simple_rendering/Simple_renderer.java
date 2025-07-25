package rendering.simple_rendering;

import ShaderEngine.entity.EntityShader;
import ShaderEngine.entityOld.EntityShaderOld;
import entity.Entity;
import loading.RawModel;
import loading.TexturedModel;
import main.Constantes;
import main.Pipeline;
import main.World;
import org.joml.Vector2f;
import org.lwjgl.opengl.*;

import rendering.render_objects.Renderer;
import rendering.shadow.CascadedShadowMap;
import toolbox.OpenGlUtils;

import java.util.List;

public class Simple_renderer extends Renderer {
    private EntityShader shader;
    public Simple_renderer() {
        this.shader = new EntityShader();
        shader.init();
        shader.loadSpot(World.spots);
        shader.loadPoints(World.pointLigths);

        //System.out.println(Pipeline.shadowWorkflow.r+" "+Pipeline.shadowWorkflow.d_0);
    }

    public void initRender() {
        shader.start();
        shader.r_shadow.loadFloat(CascadedShadowMap.instance.r);
        shader.d0_shadow.loadFloat(CascadedShadowMap.instance.d_0);
        shader.loadViewMatrix(Pipeline.getViewMat());

        for (int i = 0; i < Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt(); i++) {
            GL13.glActiveTexture(GL13.GL_TEXTURE3+i);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, CascadedShadowMap.instance.getFBOTexture(i));
        }
        //enable les array de al matrice de transformation de l'objet
        for (int i = 0; i < Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt(); i++) shader.toShadowMapSpace.loadMatrix4f(CascadedShadowMap.instance.getToShadowMapSpaceMatrix(i),i);

        GL13.glActiveTexture(GL13.GL_TEXTURE3+Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt());
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Pipeline.getRenderingWorkflow("SSAO").getOutTexture());
    }

    public void prepareTexture(TexturedModel model){
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureId());// model.getTexture().getTextureId());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getARMMap());

        shader.reflectivity.loadFloat(model.getTexture().getReflectivity());
        shader.shineDamper.loadFloat(model.getTexture().getShineDamper());
        shader.useFakeLightning.loadBoolean(model.getTexture().isUseFakeLightning());

        if(model.getTexture().isTransparance())  OpenGlUtils.cullBackFaces(false);
        else{
            OpenGlUtils.cullBackFaces(true);
        }
        shader.materialValue.loadVector2D(new Vector2f(0, 0));
    }

    public void prepareModel(RawModel rawModel){
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
    }

    @Override
    public void render(List<Entity> entities,int vertexCount) {
        shader.loadSpotInUse(entities.get(0).spotIds);
        shader.loadPointInUse(entities.get(0).pointIds);

        int instanceCount=entities.size();

        int transformationVBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, transformationVBO);
        //use the flaotbuffer already containing the floattbuffer for each entities list
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
        //MemoryUtil.memFree(transformationMatrices);
    }

    public void stop(){
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
        //GL15.glDeleteBuffers(transformationVBO);
        //disable les colonnes de l'instanciation.
        int transformationVBO=5;
        for (int i = 0; i < 4; i++) {
            GL20.glDisableVertexAttribArray(transformationVBO + i);
        }
        shader.stop();
    }

    @Override
    public void resize(int w, int h) {
        shader.start();
        shader.projectionMatrix.loadMatrix4f(Pipeline.getProjMat());
        shader.screenSize.loadVector2D(new Vector2f(w,h));
        shader.stop();
    }

    @Override
    public void cleanUp() {
        shader.cleanUp();
    }
}
