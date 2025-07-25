package postProcessingEffect.gaussianBlur;

//This class have been automatiquely generated from the vertex shader: horizontalBlurVertex.txt and fragment shader: blurFragment.txt
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderAttribArray;
import ShaderEngine.ShaderProgram;import main.Pipeline;
import main.Constantes;
import main.DisplayManager;
import org.joml.*;
import toolbox.maths.Vector3;
import java.lang.Math;
public class HorizontalBlurShader extends ShaderProgram{
    //uniform variable of type float of the vertex shader
    public ShaderAttrib targetWidth=new ShaderAttrib("targetWidth", this,UNIFORM_TYPE.FLOAT);
    //uniform variable of type sampler2D of the fragment shader
    public ShaderAttrib originalTexture=new ShaderAttrib("originalTexture", this,UNIFORM_TYPE.TEXTURE);
    public static String VERTEX_PATH = "/postProcessingEffect/gaussianBlur/horizontalBlurVertex.txt";
    public static String FRAGMENT_PATH = "/postProcessingEffect/gaussianBlur/blurFragment.txt";

    public HorizontalBlurShader() {
        super(VERTEX_PATH, FRAGMENT_PATH);
	    getAllUniformLocations();
    }
    public void init() {
        start();
        originalTexture.loadInt(0);
       stop();
    }
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
    public void resize(){
        start();
        stop();
    }
}