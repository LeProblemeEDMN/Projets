package postProcessingEffect.ToneMapping;

//This class have been automatiquely generated from the vertex shader: vertexTM.txt and fragment shader: fragmentTM.txt
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderAttribArray;
import ShaderEngine.ShaderProgram;import main.Pipeline;
import main.Constantes;
import main.DisplayManager;
import org.joml.*;
import toolbox.maths.Vector3;
import java.lang.Math;
public class ToneMappingShader extends ShaderProgram{
    //uniform variable of type sampler2D of the fragment shader
    public ShaderAttrib colourMap=new ShaderAttrib("colourMap", this,UNIFORM_TYPE.TEXTURE);
    //uniform variable of type float of the fragment shader
    public ShaderAttrib exposure=new ShaderAttrib("exposure", this,UNIFORM_TYPE.FLOAT);
    //uniform variable of type float of the fragment shader
    public ShaderAttrib gamma=new ShaderAttrib("gamma", this,UNIFORM_TYPE.FLOAT);
    public static String VERTEX_PATH = "/postProcessingEffect/ToneMapping/vertexTM.txt";
    public static String FRAGMENT_PATH = "/postProcessingEffect/ToneMapping/fragmentTM.txt";

    public ToneMappingShader() {
        super(VERTEX_PATH, FRAGMENT_PATH);
	    getAllUniformLocations();
    }
    public void init() {
        start();
        colourMap.loadInt(0);
        exposure.loadFloat(1);
        gamma.loadFloat(2.2f);
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