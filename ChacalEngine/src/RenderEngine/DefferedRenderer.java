package RenderEngine;

import Entity.Entity;
import Loader.RawModel;
import Loader.TexturedModel;
import Main.MainRender;
import PostProcessing.PostProcessing;
import PostProcessing.def.RendererDeffered;
import ShaderEngine.entityDeffered.EntityDefferedShader;
import ShaderEngine.entityNormalDeffered.EntityNormalDefferedShader;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;

import java.util.List;
import java.util.Map;

public class DefferedRenderer {
    private EntityDefferedShader shader=new EntityDefferedShader();
    private EntityNormalDefferedShader normalShader=new EntityNormalDefferedShader();
    private RendererDeffered deffered;
    private Fbo fbo=new Fbo(Display.getWidth(),Display.getHeight());
    private Fbo couleur=new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
    private Fbo pos=new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
    private Fbo normal=new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
    private Fbo autre=new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
    private Fbo finalFbo=new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);

    public DefferedRenderer(RendererDeffered deffered) {
        this.deffered = deffered;
    }

    public void initRender() {
        shader.start();
        shader.loadViewMatrix(MainRender.CAMERA.getViewMatrix());
    }
    public void initRenderNormal() {
        normalShader.start();
        normalShader.loadViewMatrix(MainRender.CAMERA.getViewMatrix());
    }


    public void render(Map<TexturedModel, List<Entity>> entities,Map<TexturedModel, List<Entity>> entitiesNormal) {

        long tot=0;
        fbo.bindFrameBuffer();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0, 0, 	0);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0,0, 0, 	0);
        initRender();
        for (TexturedModel model : entities.keySet()) {
            RawModel rawModel = model.getRawModel();
            bindModel(rawModel);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureId());
            shader.reflectivity.loadFloat(model.getTexture().getReflectivity());
            shader.shineDamper.loadFloat(model.getTexture().getShineDamper());
            shader.useFakeLightning.loadBoolean(model.getTexture().isUseFakeLightning());

            if(model.getTexture().isTransparance()) {
                MasterRenderer.disableCulling();
            }

            for (Entity entity : entities.get(model)) {
                prepareInstance(entity);

                if(entity.isHaveCubeMap()) {

                    shader.materialValue.loadVector2D(new Vector2f(model.getTexture().getReflectionMaterial(), model.getTexture().getRefractionMaterial()));
                    GL13.glActiveTexture(GL13.GL_TEXTURE1);
                    GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP,entity.getMap().getTexture());
                }else {
                    shader.materialValue.loadVector2D(new Vector2f(0, 0));
                }
                //long t=System.currentTimeMillis();
                GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(),
                        GL11.GL_UNSIGNED_INT, 0);
                //tot+=System.currentTimeMillis()-t;
            }
            if(model.getTexture().isTransparance()) {
                MasterRenderer.enableCulling();
            }
        }
        shader.stop();

        initRenderNormal();
        for (TexturedModel model : entitiesNormal.keySet()) {
            RawModel rawModel = model.getRawModel();
            bindModelNormal(rawModel);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureId());
            GL13.glActiveTexture(GL13.GL_TEXTURE2);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getNormalMap());
            normalShader.reflectivity.loadFloat(model.getTexture().getReflectivity());
            normalShader.shineDamper.loadFloat(model.getTexture().getShineDamper());
            normalShader.useFakeLightning.loadBoolean(model.getTexture().isUseFakeLightning());

            if(model.getTexture().isTransparance()) {
                MasterRenderer.disableCulling();
            }

            for (Entity entity : entitiesNormal.get(model)) {
                prepareInstanceNormal(entity);

                if(entity.isHaveCubeMap()) {

                    normalShader.materialValue.loadVector2D(new Vector2f(model.getTexture().getReflectionMaterial(), model.getTexture().getRefractionMaterial()));
                    GL13.glActiveTexture(GL13.GL_TEXTURE1);
                    GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP,entity.getMap().getTexture());
                }else {
                    normalShader.materialValue.loadVector2D(new Vector2f(0, 0));
                }
                //long t=System.currentTimeMillis();
                GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(),
                        GL11.GL_UNSIGNED_INT, 0);
                //tot+=System.currentTimeMillis()-t;
            }
            if(model.getTexture().isTransparance()) {
                MasterRenderer.enableCulling();
            }
        }
        normalShader.stop();

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL20.glDisableVertexAttribArray(3);
        GL30.glBindVertexArray(0);

        fbo.unbindFrameBuffer();
        fbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT0,couleur);
        fbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT1,normal);
        fbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT2,pos);
        fbo.resolveToFbo(GL30.GL_COLOR_ATTACHMENT3,autre);
        PostProcessing.start();
        finalFbo.bindFrameBuffer();
        deffered.render(couleur.getColourTexture(),normal.getColourTexture(),pos.getColourTexture(),autre.getColourTexture());
        finalFbo.unbindFrameBuffer();
        PostProcessing.end();
        //finalFbo.resolveToScreen();
    }

    private void prepareInstance(Entity entity) {
        ///Matrix4f modelMatrix = Maths.createTransfromationMatrix(entity.getPosition().getOglVec(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        shader.transformationMatrix.loadMatrix4f(entity.getTransformationMatrix());
    }
    private void prepareInstanceNormal(Entity entity) {
        ///Matrix4f modelMatrix = Maths.createTransfromationMatrix(entity.getPosition().getOglVec(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        normalShader.transformationMatrix.loadMatrix4f(entity.getTransformationMatrix());
    }

    private void bindModel(RawModel rawModel) {
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
    }
    private void bindModelNormal(RawModel rawModel) {
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
    }
    public void cleanUp() {
        normalShader.cleanUp();
        shader.cleanUp();
    }
    public EntityDefferedShader getShader() {
        return shader;
    }

    public Fbo getFbo() {
        return fbo;
    }


    public void resize() {
        fbo.cleanUp();
        fbo=new Fbo(Display.getWidth(),Display.getHeight());
        couleur.cleanUp();
        couleur=new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
        normal.cleanUp();
        normal=new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
        pos.cleanUp();
        pos=new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
        autre.cleanUp();
        autre=new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
        finalFbo.cleanUp();
        finalFbo=new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);

        shader.start();
        shader.projectionMatrix.loadMatrix4f(MainRender.CAMERA.getProjectionMatrix());
        shader.stop();
    }

    public void setShader(EntityDefferedShader shader) {
        this.shader = shader;
    }

    public RendererDeffered getDeffered() {
        return deffered;
    }

    public void setDeffered(RendererDeffered deffered) {
        this.deffered = deffered;
    }

    public void setFbo(Fbo fbo) {
        this.fbo = fbo;
    }

    public Fbo getCouleur() {
        return couleur;
    }

    public void setCouleur(Fbo couleur) {
        this.couleur = couleur;
    }

    public Fbo getPos() {
        return pos;
    }

    public void setPos(Fbo pos) {
        this.pos = pos;
    }

    public Fbo getFinalFbo() {
        return finalFbo;
    }

    public Fbo getNormal() {
        return normal;
    }

    public void setNormal(Fbo normal) {
        this.normal = normal;
    }

    public Fbo getAutre() {
        return autre;
    }

    public void setAutre(Fbo autre) {
        this.autre = autre;
    }
}
