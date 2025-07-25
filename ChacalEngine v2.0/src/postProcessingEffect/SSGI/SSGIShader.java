package postProcessingEffect.SSGI;

//This class have been automatiquely generated from the vertex shader: vertexSSGI.txt and fragment shader: fragmentSSGI.glsl
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderAttribArray;
import ShaderEngine.ShaderProgram;import main.Pipeline;
import main.Constantes;
import main.DisplayManager;
import org.joml.*;
import toolbox.maths.Vector3;
import java.lang.Math;
public class SSGIShader extends ShaderProgram{
    //uniform variable of type sampler2D of the fragment shader
    public ShaderAttrib lightTexture=new ShaderAttrib("lightTexture", this,UNIFORM_TYPE.TEXTURE);
    //uniform variable of type sampler2DMS of the fragment shader
    public ShaderAttrib normalTexture=new ShaderAttrib("normalTexture", this,UNIFORM_TYPE.MULTISAMPLED_TEXTURE);
    //uniform variable of type sampler2DMS of the fragment shader
    public ShaderAttrib AMRText=new ShaderAttrib("AMRText", this,UNIFORM_TYPE.MULTISAMPLED_TEXTURE);
    //uniform variable of type sampler2DMS of the fragment shader
    public ShaderAttrib depthTex=new ShaderAttrib("depthTex", this,UNIFORM_TYPE.MULTISAMPLED_TEXTURE);
    //uniform variable of type mat4 of the fragment shader
    public ShaderAttrib invProjMatrix=new ShaderAttrib("invProjMatrix", this,UNIFORM_TYPE.MAT4);
    //uniform variable of type mat4 of the fragment shader
    public ShaderAttrib invViewMatrix=new ShaderAttrib("invViewMatrix", this,UNIFORM_TYPE.MAT4);
    //uniform variable of type vec2 of the fragment shader
    public ShaderAttrib screenSize=new ShaderAttrib("screenSize", this,UNIFORM_TYPE.VEC2);
    //uniform variable of type vec2 of the fragment shader
    public ShaderAttrib linearize_param=new ShaderAttrib("linearize_param", this,UNIFORM_TYPE.VEC2);
    //uniform variable of type vec3 of the fragment shader
    public ShaderAttrib cameraPosition=new ShaderAttrib("cameraPosition", this,UNIFORM_TYPE.VEC3);
    //uniform variable of type samplerCube of the fragment shader
    public ShaderAttrib skyMap=new ShaderAttrib("skyMap", this,UNIFORM_TYPE.TEXTURE);
    //Array of uniform variable of type vec3 of the fragment shader
    public ShaderAttribArray samples=new ShaderAttribArray("samples[", "]", 64, this, UNIFORM_TYPE.VEC3);
    //uniform variable of type mat4 of the fragment shader
    public ShaderAttrib projectionMatrix=new ShaderAttrib("projectionMatrix", this,UNIFORM_TYPE.MAT4);
    //uniform variable of type mat4 of the fragment shader
    public ShaderAttrib projectionViewMatrix=new ShaderAttrib("projectionViewMatrix", this,UNIFORM_TYPE.MAT4);
    public static String VERTEX_PATH = "/postProcessingEffect/SSGI/vertexSSGI.txt";
    public static String FRAGMENT_PATH = "/postProcessingEffect/SSGI/fragmentSSGI.glsl";

    public SSGIShader() {
        super(VERTEX_PATH, FRAGMENT_PATH);
	    getAllUniformLocations();
    }
    public void init() {
        start();
        Matrix4f invProjMat = new Matrix4f();
        Pipeline.getProjMat().invert(invProjMat);
        Random rdm=new Random(0);
        for (int i = 0; i < Constantes.SSGI_NUMBER_SAMPLES.getInt(); i++) {
            Vector3 sample=new Vector3(rdm.nextFloat()*2-1,rdm.nextFloat()*2-1,rdm.nextFloat()*2-1).normalize();
            samples.loadVector3(sample,i);
              System.out.println(sample);
        }
        
        lightTexture.loadInt(0);
        normalTexture.loadInt(1);
        AMRText.loadInt(2);
        depthTex.loadInt(3);
        invProjMatrix.loadMatrix4f(invProjMat);
        screenSize.loadVector2D(new Vector2f(DisplayManager.getWidth(),DisplayManager.getHeight()));
        linearize_param.loadVector2D(new Vector2f(Pipeline.getProjMat().m22(),Pipeline.getProjMat().m32()));
        skyMap.loadInt(4);
        projectionMatrix.loadMatrix4f(Pipeline.getProjMat());
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
        
        invProjMatrix.loadMatrix4f(invProjMat);
        screenSize.loadVector2D(new Vector2f(DisplayManager.getWidth(),DisplayManager.getHeight()));
        linearize_param.loadVector2D(new Vector2f(Pipeline.getProjMat().m22(),Pipeline.getProjMat().m32()));
        projectionMatrix.loadMatrix4f(Pipeline.getProjMat());
        stop();
    }
}