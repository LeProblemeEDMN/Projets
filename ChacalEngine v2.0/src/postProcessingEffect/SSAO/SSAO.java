package postProcessingEffect.SSAO;

import main.DisplayManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import postProcessingEffect.SSGI.SSGIRenderer;
import rendering.capture_object.Fbo;
import rendering.capture_object.ImageRenderer;
import rendering.postprocessing.PostProcessing;
import rendering.postprocessing.PostProcessingEffect;

public class SSAO implements PostProcessingEffect {

    public static SSAO instance;
    public static String NAME="SSAO";
    /*render the effect with the input texture id_text. if start=true bind and unbind the posporcessing mesh*/
    public static void render(int id_text,boolean start){
        PostProcessing.renderEffect(NAME,id_text,start);
    }

    private Fbo fbo;

    private SSAOShader shader;

    public SSAO() {
        this.fbo = new Fbo(DisplayManager.getWidth(),DisplayManager.getHeight(),Fbo.RED,Fbo.NONE);
        shader=new SSAOShader();
        shader.init(1f,0.0000025f);
        PostProcessing.registerEffect(this);
    }

    public void render(int depthTexture){
        shader.start();
        fbo.bindFrameBuffer();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        //System.out.println(depthTexture);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);// model.getTexture().getTextureId());
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
        this.fbo = new Fbo(w,h,Fbo.RED,Fbo.NONE);
    }

}
