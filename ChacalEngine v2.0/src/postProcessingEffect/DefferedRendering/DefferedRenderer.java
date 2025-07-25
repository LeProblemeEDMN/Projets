package postProcessingEffect.DefferedRendering;

import entity.Light.PointLight;
import entity.Light.SpotLight;
import main.*;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL32;
import postProcessingEffect.SSGI.SSGIRenderer;
import rendering.capture_object.Fbo;
import rendering.capture_object.MultisampleFBO;
import rendering.deffered_rendering.Deffered_workflow;
import rendering.postprocessing.PostProcessing;
import rendering.postprocessing.PostProcessingEffect;
import rendering.shadow.CascadedShadowMap;

import java.util.List;

public class DefferedRenderer implements PostProcessingEffect {
    private Fbo fbo;

    //instance
    public static DefferedRenderer instance;
    public static String NAME="DefferedRendering";
    /*render the effect with the input texture id_text. if start=true bind and unbind the posporcessing mesh*/
    public static void render(boolean start){
        PostProcessing.renderEffect(NAME,-1,start);
    }

    private DefferedRenderingShader shader;
    private int cubeMapTexture;

    private int maximum_number_of_light;
    public static String[] TEXTURE_FILES= {"skybox/right","skybox/left","skybox/top","skybox/bottom","skybox/back","skybox/front"};

    public DefferedRenderer() {
        this.fbo = new Fbo(DisplayManager.getWidth(),DisplayManager.getHeight(),Fbo.RGBA,Fbo.NONE);
        shader=new DefferedRenderingShader();
        shader.init(0.0000025f,1f);

        cubeMapTexture= MainLoop.LOADER.loadCubeMap(TEXTURE_FILES);

        PostProcessing.registerEffect(this);
        maximum_number_of_light=shader.points_attenuation.getNumberAttrib();
    }

    public void render(int useless_texture){
        MultisampleFBO multi_fbo=((Deffered_workflow)Pipeline.getRenderingWorkflow(Deffered_workflow.NAME)).getMultisample_fbo();

        shader.start();
        loadViewMatrix();
        shader.r_shadow.loadFloat(CascadedShadowMap.instance.r);
        shader.d0_shadow.loadFloat(CascadedShadowMap.instance.d_0);
        fbo.bindFrameBuffer();
        for (int i = 0; i < 3; i++) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
            GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, multi_fbo.getTextureId(i));
        }

        // Texture profondeur multisample (exemple à l’unité 3)
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + 3);
        GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, multi_fbo.getTextureId(3));

        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cubeMapTexture);


        //bind les textures et matrices du cascaded shadow map
        for (int i = 0; i < Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt(); i++) {
            GL13.glActiveTexture(GL13.GL_TEXTURE5+i);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, CascadedShadowMap.instance.getFBOTexture(i));
            shader.toShadowMapSpace.loadMatrix4f(CascadedShadowMap.instance.getToShadowMapSpaceMatrix(i),i);
        }

        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
        fbo.unbindFrameBuffer();
        shader.stop();
    }

    public void init(List<SpotLight> spots,List<PointLight>points){
        loadPoints(points);
        loadSpot(spots);
    }

    public int getOutputTexture(){
        return fbo.getColourTexture();
    }


    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Fbo getOutFbo() {
        return fbo;
    }

    public void cleanUp(){
        fbo.cleanUp();
        shader.cleanUp();
    }
    public void resize(int w,int h){
        shader.resize();
        fbo.cleanUp();
        this.fbo = new Fbo(w,h,Fbo.RGBA,Fbo.NONE);
    }

    public void loadViewMatrix() {
        Matrix4f invViewMat = new Matrix4f();
        Pipeline.getViewMat().invert(invViewMat);
        shader.invViewMatrix.loadMatrix4f(invViewMat);
        shader.cameraPosition.loadVector3(Pipeline.camera.getPosition());
        Pipeline.getProjMat().mul(Pipeline.getViewMat(),invViewMat);
        shader.projectionViewMatrix.loadMatrix4f(invViewMat);
    }

    public void loadSpot(List<SpotLight> spots) {
        shader.start();
        int s=spots.size();
        if (s>maximum_number_of_light)s=maximum_number_of_light;
        for (int i = 0; i < s; i++) {
            SpotLight light=spots.get(i);
            shader.positionSpot.loadVector3(light.getPosition(), i);
            shader.spots_color.loadVector3(light.getColor(), i);
            shader.spots_attenuation.loadVector3(light.getAttenuation(), i);
            shader.spots_lookVec.loadVector3(light.getDirectionVector().getNormalize(), i);
            shader.spots_angle.loadFloat((float) Math.toRadians(light.getAngle()), i);
            //add 0.05 to make a smooth transition
            shader.spots_cos_angle.loadFloat((float)Math.cos(Math.toRadians(light.getAngle())+0.05),i);
            shader.spots_far.loadFloat(light.getMaxRange(),i);
        }
        shader.numberSpots.loadInt(spots.size());
        shader.stop();
    }

    public void loadPoints(List<PointLight>points) {
        shader.start();
        int s=points.size();
        if (s>maximum_number_of_light)s=maximum_number_of_light;
        for (int i = 0; i < s; i++) {
            PointLight light=points.get(i);
            shader.positionPoint.loadVector3(light.getPosition(), i);
            shader.points_color.loadVector3(light.getColor(), i);
            shader.points_attenuation.loadVector3(light.getAttenuation(), i);
            shader.points_far.loadFloat(light.getMaxRange(),i);
        }
        shader.numberPoints.loadInt(points.size());
        shader.stop();
    }

}
