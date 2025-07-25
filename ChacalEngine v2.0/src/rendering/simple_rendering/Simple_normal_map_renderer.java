package rendering.simple_rendering;

import ShaderEngine.entity.EntityShader;
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

public class Simple_normal_map_renderer extends Renderer {
    private EntityNormalMapShader shader;
    private int normal_map_texture;
    public Simple_normal_map_renderer() {
        this.shader = new EntityNormalMapShader();
        shader.init();
        shader.loadSpot(World.spots);
        shader.loadPoints(World.pointLigths);
        normal_map_texture=MainLoop.LOADER.loadTexture("normalBase.png");
    }

    public void initRender() {
        shader.start();
        //doit faire cela
        shader.loadPosition(World.spots,World.pointLigths,Pipeline.getViewMat());
        shader.r_shadow.loadFloat(CascadedShadowMap.instance.r);
        shader.d0_shadow.loadFloat(CascadedShadowMap.instance.d_0);
        shader.loadViewMatrix(Pipeline.getViewMat());

        for (int i = 0; i < Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt(); i++) {
            GL13.glActiveTexture(GL13.GL_TEXTURE5+i);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, CascadedShadowMap.instance.getFBOTexture(i));
        }
        //System.out.println();
        //enable les array de al matrice de transformation de l'objet
        for (int i = 0; i < Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt(); i++){
            shader.toShadowMapSpace.loadMatrix4f(CascadedShadowMap.instance.getToShadowMapSpaceMatrix(i),i);
        }

        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Pipeline.getRenderingWorkflow("SSAO").getOutTexture());
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
        System.out.println(w+" "+h);
        shader.screenSize.loadVector2D(new Vector2f(w,h));
        shader.stop();
    }

    public EntityNormalMapShader getShader() {
        return shader;
    }

    @Override
    public void cleanUp() {
        shader.cleanUp();
    }
}
