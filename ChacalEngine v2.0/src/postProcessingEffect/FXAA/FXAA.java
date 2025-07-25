package postProcessingEffect.FXAA;

import main.DisplayManager;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import postProcessingEffect.DefferedRendering.DefferedRenderer;
import postProcessingEffect.SSAO.SSAOShader;
import rendering.capture_object.Fbo;
import rendering.postprocessing.PostProcessing;
import rendering.postprocessing.PostProcessingEffect;

public class FXAA implements PostProcessingEffect {

    public static FXAA instance;
    public static String NAME="FXAA";
    /*render the effect with the input texture id_text. if start=true bind and unbind the postporcessing mesh*/
    public static void render(int id_text,boolean start){
        PostProcessing.renderEffect(NAME,id_text,start);
    }

    private Fbo fbo;

    private FXAAShader shader;

    public FXAA() {
        this.fbo = new Fbo(DisplayManager.getWidth(),DisplayManager.getHeight(),Fbo.RGBA,Fbo.NONE);
        shader=new FXAAShader();
        shader.init();
        PostProcessing.registerEffect(this);
    }
    @Override
    public void render(int colortexture){
        shader.start();
        fbo.bindFrameBuffer();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colortexture);
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

    public void cleanUp(){
        fbo.cleanUp();
        shader.cleanUp();
    }
    public void resize(int w,int h){

        fbo.cleanUp();
        this.fbo = new Fbo(w,h,Fbo.RGBA,Fbo.NONE);
        shader.resize();
    }
    @Override
    public Fbo getOutFbo() {
        return fbo;
    }
}
