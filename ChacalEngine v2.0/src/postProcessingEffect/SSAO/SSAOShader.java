package postProcessingEffect.SSAO;

//This class have been automatiquely generated from the vertex shader: vertexSSAO.txt and fragment shader: fragmentSSAO.txt
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderAttribArray;
import ShaderEngine.ShaderProgram;import main.Pipeline;
import main.Constantes;
import main.DisplayManager;
import org.joml.*;
import toolbox.maths.Vector3;
import java.lang.Math;
public class SSAOShader extends ShaderProgram{
    //uniform variable of type sampler2D of the fragment shader
    public ShaderAttrib depthMap=new ShaderAttrib("depthMap", this,UNIFORM_TYPE.TEXTURE);
    //Array of uniform variable of type vec3 of the fragment shader
    public ShaderAttribArray samples=new ShaderAttribArray("samples[", "]", 64, this, UNIFORM_TYPE.VEC3);
    //uniform variable of type mat4 of the fragment shader
    public ShaderAttrib projection=new ShaderAttrib("projection", this,UNIFORM_TYPE.MAT4);
    //uniform variable of type mat4 of the fragment shader
    public ShaderAttrib projectionInverse=new ShaderAttrib("projectionInverse", this,UNIFORM_TYPE.MAT4);
    //uniform variable of type float of the fragment shader
    public ShaderAttrib radius=new ShaderAttrib("radius", this,UNIFORM_TYPE.FLOAT);
    //uniform variable of type float of the fragment shader
    public ShaderAttrib bias=new ShaderAttrib("bias", this,UNIFORM_TYPE.FLOAT);
    public static String VERTEX_PATH = "/postProcessingEffect/SSAO/vertexSSAO.txt";
    public static String FRAGMENT_PATH = "/postProcessingEffect/SSAO/fragmentSSAO.txt";

    public SSAOShader() {
        super(VERTEX_PATH, FRAGMENT_PATH);
	    getAllUniformLocations();
    }
    public void init(float radius_value,float bias_value) {
        start();
        Matrix4f invProjMat = new Matrix4f();
        Pipeline.getProjMat().invert(invProjMat);
        for (int i = 0; i < Constantes.SSAO_NUMBER_SAMPLES.getInt(); i++) {
          Vector3 sample=new Vector3(Math.random()*2-1,Math.random()*2-1,Math.random()*2-1).normalize();
          float length=(float) (Math.random()*Math.random())*radius_value;
          sample.mul(length);
          samples.loadVector3(sample,i);
        }
        
        depthMap.loadInt(0);
        projection.loadMatrix4f(Pipeline.getProjMat());
        projectionInverse.loadMatrix4f(invProjMat);
        radius.loadFloat(radius_value);
        bias.loadFloat(bias_value);
       stop();
    }
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
    public void resize(){
        start();
        Matrix4f invProjMat = new Matrix4f();
        Pipeline.getProjMat().invert(invProjMat);
        
        projection.loadMatrix4f(Pipeline.getProjMat());
        projectionInverse.loadMatrix4f(invProjMat);
        stop();
    }
}