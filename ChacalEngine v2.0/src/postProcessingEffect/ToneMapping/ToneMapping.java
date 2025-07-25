package postProcessingEffect.ToneMapping;

import main.Constantes;
import main.DisplayManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import postProcessingEffect.SSGI.SSGIRenderer;
import rendering.capture_object.Fbo;
import rendering.postprocessing.PostProcessing;
import rendering.postprocessing.PostProcessingEffect;

public class ToneMapping implements PostProcessingEffect {

    public static ToneMapping instance;
    public static String NAME="ToneMapping";
    /*render the effect with the input texture id_text. if start=true bind and unbind the posporcessing mesh*/
    public static void render(int id_text,float exposure,boolean start){
        instance.exposure=exposure;
        PostProcessing.renderEffect(NAME,id_text,start);
    }

    private Fbo fbo;
    private ToneMappingShader shader;
    public float exposure=1;
    public ToneMapping() {
        this.shader = new ToneMappingShader();
        shader.init();
        this.fbo=new Fbo(DisplayManager.getWidth(),DisplayManager.getHeight(),Fbo.RGBA,Fbo.NONE);
        PostProcessing.registerEffect(this);
    }

   /* public void render(int colorTexture,float exposure){

    }*/
    @Override
    public void render(int texture_input) {
        exposure=Constantes.EXPOSURE.getFloat();
        shader.start();
        fbo.bindFrameBuffer();
        shader.exposure.loadFloat(exposure);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture_input);
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

    public void resize(int width, int height) {
        fbo.cleanUp();
        this.fbo=new Fbo(width,height,Fbo.RGBA,Fbo.NONE);
    }
    public void cleanUp(){
        fbo.cleanUp();
        shader.cleanUp();
    }
}
