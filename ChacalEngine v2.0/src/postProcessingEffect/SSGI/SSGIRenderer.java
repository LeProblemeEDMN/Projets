package postProcessingEffect.SSGI;

import entity.Light.PointLight;
import entity.Light.SpotLight;
import main.Constantes;
import main.DisplayManager;
import main.MainLoop;
import main.Pipeline;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL32;

import rendering.capture_object.Fbo;
import rendering.capture_object.MultisampleFBO;
import rendering.deffered_rendering.Deffered_workflow;
import rendering.postprocessing.PostProcessing;
import rendering.postprocessing.PostProcessingEffect;

import java.util.List;

public class SSGIRenderer implements PostProcessingEffect {

    //instance
    public static SSGIRenderer instance;
    public static String NAME="SSGI";
    /*render the effect with the input texture id_text. if start=true bind and unbind the posporcessing mesh*/
    public static void render(int id_text,boolean start){
        PostProcessing.renderEffect(NAME,id_text,start);
    }

    private Fbo fbo;

    private SSGIShader shader;
    private int cubeMapTexture;
    public static String[] TEXTURE_FILES= {"skybox/right","skybox/left","skybox/top","skybox/bottom","skybox/back","skybox/front"};

    public SSGIRenderer() {
        this.fbo = new Fbo(DisplayManager.getWidth(),DisplayManager.getHeight(),Fbo.RGBA,Fbo.NONE);
        shader=new SSGIShader();
        shader.init();

        cubeMapTexture= MainLoop.LOADER.loadCubeMap(TEXTURE_FILES);

        PostProcessing.registerEffect(this);
    }
    @Override
    public void render(int lightTexture){
        MultisampleFBO multi_fbo=((Deffered_workflow)Pipeline.getRenderingWorkflow(Deffered_workflow.NAME)).getMultisample_fbo();
        shader.start();
        loadViewMatrix();
        fbo.bindFrameBuffer();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL32.GL_TEXTURE_2D, lightTexture);

        for (int i = 1; i < 3; i++) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);
            GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, multi_fbo.getTextureId(i));
        }

        // Texture profondeur multisample (exemple à l’unité 3)
        GL13.glActiveTexture(GL13.GL_TEXTURE0 + 3);
        GL11.glBindTexture(GL32.GL_TEXTURE_2D_MULTISAMPLE, multi_fbo.getTextureId(3));

        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, cubeMapTexture);


        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
        fbo.unbindFrameBuffer();
        shader.stop();
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
        fbo.cleanUp();
        this.fbo = new Fbo(w,h,Fbo.RGBA,Fbo.NONE);

        shader.resize();

    }

    public void loadViewMatrix() {
        Matrix4f invViewMat = new Matrix4f();
        Pipeline.getViewMat().invert(invViewMat);

        shader.invViewMatrix.loadMatrix4f(invViewMat);
        shader.cameraPosition.loadVector3(Pipeline.camera.getPosition());
        Pipeline.getProjMat().mul(Pipeline.getViewMat(),invViewMat);
        shader.projectionViewMatrix.loadMatrix4f(invViewMat);
    }
}
