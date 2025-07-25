package ShaderEngine.entityDefferedPre;

//This class have been automatiquely generated from the vertex shader: vertexShader.txt and fragment shader: fragmentShader.txt
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderAttribArray;
import ShaderEngine.ShaderProgram;import main.Pipeline;
import main.Constantes;
import main.DisplayManager;
import org.joml.*;
import toolbox.maths.Vector3;
import java.lang.Math;
public class EntityDefferedPreShader extends ShaderProgram{
    //uniform variable of type mat4 of the vertex shader
    public ShaderAttrib projectionMatrix=new ShaderAttrib("projectionMatrix", this,UNIFORM_TYPE.MAT4);
    //uniform variable of type mat4 of the vertex shader
    public ShaderAttrib viewMatrix=new ShaderAttrib("viewMatrix", this,UNIFORM_TYPE.MAT4);
    //uniform variable of type float of the vertex shader
    public ShaderAttrib useFakeLightning=new ShaderAttrib("useFakeLightning", this,UNIFORM_TYPE.FLOAT);
    //uniform variable of type sampler2D of the fragment shader
    public ShaderAttrib modelTexture=new ShaderAttrib("modelTexture", this,UNIFORM_TYPE.TEXTURE);
    //uniform variable of type sampler2D of the fragment shader
    public ShaderAttrib ARMtexture=new ShaderAttrib("ARMtexture", this,UNIFORM_TYPE.TEXTURE);
    //uniform variable of type sampler2D of the fragment shader
    public ShaderAttrib normalTexture=new ShaderAttrib("normalTexture", this,UNIFORM_TYPE.TEXTURE);
    //uniform variable of type sampler2D of the fragment shader
    public ShaderAttrib displacementTexture=new ShaderAttrib("displacementTexture", this,UNIFORM_TYPE.TEXTURE);
    public static String VERTEX_PATH = "/ShaderEngine/entityDefferedPre/vertexShader.txt";
    public static String FRAGMENT_PATH = "/ShaderEngine/entityDefferedPre/fragmentShader.txt";

    public EntityDefferedPreShader() {
        super(VERTEX_PATH, FRAGMENT_PATH);
	    getAllUniformLocations();
    }
    public void init() {
        start();
        projectionMatrix.loadMatrix4f(Pipeline.getProjMat());
        modelTexture.loadInt(0);
        ARMtexture.loadInt(1);
        normalTexture.loadInt(2);
        displacementTexture.loadInt(3);
       stop();
    }
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
        super.bindAttribute(3, "tangent");
        super.bindAttribute(5, "transformationMatrix");
    }
    public void resize(){
        start();
        stop();
    }
}