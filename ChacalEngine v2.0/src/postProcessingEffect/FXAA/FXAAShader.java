package postProcessingEffect.FXAA;

//This class have been automatiquely generated from the vertex shader: vertexFXAA.txt and fragment shader: fragmentFXAA.txt
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderAttribArray;
import ShaderEngine.ShaderProgram;import main.Pipeline;
import main.Constantes;
import main.DisplayManager;
import org.joml.*;
import toolbox.maths.Vector3;
import java.lang.Math;
public class FXAAShader extends ShaderProgram{
    //uniform variable of type sampler2D of the fragment shader
    public ShaderAttrib screenTexture=new ShaderAttrib("screenTexture", this,UNIFORM_TYPE.TEXTURE);
    //uniform variable of type vec2 of the fragment shader
    public ShaderAttrib inverseScreenSize=new ShaderAttrib("inverseScreenSize", this,UNIFORM_TYPE.VEC2);
    //Array of uniform variable of type int of the fragment shader
    public ShaderAttribArray strides=new ShaderAttribArray("strides[", "]", 15, this, UNIFORM_TYPE.INT);
    public static String VERTEX_PATH = "/postProcessingEffect/FXAA/vertexFXAA.txt";
    public static String FRAGMENT_PATH = "/postProcessingEffect/FXAA/fragmentFXAA.txt";

    public FXAAShader() {
        super(VERTEX_PATH, FRAGMENT_PATH);
	    getAllUniformLocations();
    }
    public void init() {
        start();
        screenTexture.loadInt(0);
        inverseScreenSize.loadVector2D(new Vector2f(1.0f/ DisplayManager.getWidth(),1.0f/DisplayManager.getHeight()));
       stop();
    }
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
    public void resize(){
        start();
        inverseScreenSize.loadVector2D(new Vector2f(1.0f/ DisplayManager.getWidth(),1.0f/DisplayManager.getHeight()));
        stop();
    }
}