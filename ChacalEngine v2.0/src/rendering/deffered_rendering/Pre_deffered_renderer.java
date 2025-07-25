package rendering.deffered_rendering;

import ShaderEngine.entityDefferedPre.EntityDefferedPreShader;
import ShaderEngine.entityNormalMap.EntityNormalMapShader;
import entity.Entity;
import loading.RawModel;
import loading.TexturedModel;
import main.Constantes;
import main.MainLoop;
import main.Pipeline;
import main.World;
import org.joml.Vector2f;
import org.lwjgl.opengl.*;
import rendering.render_objects.Renderer;
import rendering.shadow.CascadedShadowMap;
import toolbox.OpenGlUtils;

import java.util.List;

public class Pre_deffered_renderer extends Renderer {
    private EntityDefferedPreShader shader;
    private int normal_map_texture;
    public Pre_deffered_renderer() {
        this.shader = new EntityDefferedPreShader();
        shader.init();
        normal_map_texture=MainLoop.LOADER.loadTexture("normalBase.png");
    }

    public void initRender() {
        shader.start();
        shader.viewMatrix.loadMatrix4f(Pipeline.getViewMat());

        for (int i = 0; i < Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt(); i++) {
            GL13.glActiveTexture(GL13.GL_TEXTURE5+i);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, CascadedShadowMap.instance.getFBOTexture(i));
        }


    }

    public void prepareTexture(TexturedModel model){
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureId());// model.getTexture().getTextureId());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getARMMap());
        if(model.getTexture().hasNormalMap()){
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getNormalMap());
        }else{
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, normal_map_texture);
        }
        if(model.getTexture().hasDisplacementMap()){
            GL13.glActiveTexture(GL13.GL_TEXTURE3);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getDisplacementMap());
        }else{
            GL13.glActiveTexture(GL13.GL_TEXTURE3);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, normal_map_texture);
        }
        shader.useFakeLightning.loadBoolean(model.getTexture().isUseFakeLightning());

        if(model.getTexture().isTransparance())  OpenGlUtils.cullBackFaces(false);
        else{
            OpenGlUtils.cullBackFaces(true);
        }

    }

    public void prepareModel(RawModel rawModel){
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
    }

    @Override
    public void render(List<Entity> entities,int vertexCount) {

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
        int error = GL11.glGetError();
        if (error != 0) {
            System.out.println("OpenGL Error before rendering: " + error);
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(transformationVBO);
    }

    public void stop(){
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
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
        shader.stop();
    }

    public EntityDefferedPreShader getShader() {
        return shader;
    }

    @Override
    public void cleanUp() {
        shader.cleanUp();
    }
}
