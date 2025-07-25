package postProcessingEffect.DefferedRendering;

//This class have been automatiquely generated from the vertex shader: vertexDR.txt and fragment shader: fragmentDR.glsl
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderAttribArray;
import ShaderEngine.ShaderProgram;import main.Pipeline;
import main.Constantes;
import main.DisplayManager;
import org.joml.*;
import toolbox.maths.Vector3;
import java.lang.Math;
public class DefferedRenderingShader extends ShaderProgram{
    //uniform variable of type sampler2DMS of the fragment shader
    public ShaderAttrib albedoTexture=new ShaderAttrib("albedoTexture", this,UNIFORM_TYPE.MULTISAMPLED_TEXTURE);
    //uniform variable of type sampler2DMS of the fragment shader
    public ShaderAttrib normalTexture=new ShaderAttrib("normalTexture", this,UNIFORM_TYPE.MULTISAMPLED_TEXTURE);
    //uniform variable of type sampler2DMS of the fragment shader
    public ShaderAttrib AMRText=new ShaderAttrib("AMRText", this,UNIFORM_TYPE.MULTISAMPLED_TEXTURE);
    //uniform variable of type sampler2DMS of the fragment shader
    public ShaderAttrib depthTex=new ShaderAttrib("depthTex", this,UNIFORM_TYPE.MULTISAMPLED_TEXTURE);
    //uniform variable of type mat4 of the fragment shader
    public ShaderAttrib projectionMatrix=new ShaderAttrib("projectionMatrix", this,UNIFORM_TYPE.MAT4);
    //uniform variable of type mat4 of the fragment shader
    public ShaderAttrib projectionViewMatrix=new ShaderAttrib("projectionViewMatrix", this,UNIFORM_TYPE.MAT4);
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
    //uniform variable of type vec3 of the fragment shader
    public ShaderAttrib fogColor=new ShaderAttrib("fogColor", this,UNIFORM_TYPE.VEC3);
    //uniform variable of type float of the fragment shader
    public ShaderAttrib fogDensity=new ShaderAttrib("fogDensity", this,UNIFORM_TYPE.FLOAT);
    //uniform variable of type float of the fragment shader
    public ShaderAttrib fogGradient=new ShaderAttrib("fogGradient", this,UNIFORM_TYPE.FLOAT);
    //Array of uniform variable of type vec3 of the fragment shader
    public ShaderAttribArray samples=new ShaderAttribArray("samples[", "]", 64, this, UNIFORM_TYPE.VEC3);
    //uniform variable of type float of the fragment shader
    public ShaderAttrib radius=new ShaderAttrib("radius", this,UNIFORM_TYPE.FLOAT);
    //uniform variable of type float of the fragment shader
    public ShaderAttrib bias=new ShaderAttrib("bias", this,UNIFORM_TYPE.FLOAT);
    //Array of uniform structure of type PointLight of the fragment shader
    public ShaderAttribArray points_attenuation=new ShaderAttribArray("points[", "].attenuation", 50, this, UNIFORM_TYPE.VEC3);
    public ShaderAttribArray points_color=new ShaderAttribArray("points[", "].color", 50, this, UNIFORM_TYPE.VEC3);
    public ShaderAttribArray points_far=new ShaderAttribArray("points[", "].far", 50, this, UNIFORM_TYPE.FLOAT);
    //Array of uniform structure of type Spot of the fragment shader
    public ShaderAttribArray spots_attenuation=new ShaderAttribArray("spots[", "].attenuation", 50, this, UNIFORM_TYPE.VEC3);
    public ShaderAttribArray spots_lookVec=new ShaderAttribArray("spots[", "].lookVec", 50, this, UNIFORM_TYPE.VEC3);
    public ShaderAttribArray spots_angle=new ShaderAttribArray("spots[", "].angle", 50, this, UNIFORM_TYPE.FLOAT);
    public ShaderAttribArray spots_cos_angle=new ShaderAttribArray("spots[", "].cos_angle", 50, this, UNIFORM_TYPE.FLOAT);
    public ShaderAttribArray spots_color=new ShaderAttribArray("spots[", "].color", 50, this, UNIFORM_TYPE.VEC3);
    public ShaderAttribArray spots_far=new ShaderAttribArray("spots[", "].far", 50, this, UNIFORM_TYPE.FLOAT);
    //Array of uniform variable of type vec3 of the fragment shader
    public ShaderAttribArray positionSpot=new ShaderAttribArray("positionSpot[", "]", 50, this, UNIFORM_TYPE.VEC3);
    //Array of uniform variable of type vec3 of the fragment shader
    public ShaderAttribArray positionPoint=new ShaderAttribArray("positionPoint[", "]", 50, this, UNIFORM_TYPE.VEC3);
    //uniform variable of type int of the fragment shader
    public ShaderAttrib numberSpots=new ShaderAttrib("numberSpots", this,UNIFORM_TYPE.INT);
    //uniform variable of type int of the fragment shader
    public ShaderAttrib numberPoints=new ShaderAttrib("numberPoints", this,UNIFORM_TYPE.INT);
    //Array of uniform variable of type sampler2D of the fragment shader
    public ShaderAttribArray sunTexture=new ShaderAttribArray("sunTexture[", "]", 4, this, UNIFORM_TYPE.TEXTURE);
    //uniform variable of type float of the fragment shader
    public ShaderAttrib d0_shadow=new ShaderAttrib("d0_shadow", this,UNIFORM_TYPE.FLOAT);
    //uniform variable of type float of the fragment shader
    public ShaderAttrib r_shadow=new ShaderAttrib("r_shadow", this,UNIFORM_TYPE.FLOAT);
    //uniform variable of type vec2 of the fragment shader
    public ShaderAttrib mapSize=new ShaderAttrib("mapSize", this,UNIFORM_TYPE.VEC2);
    //Array of uniform variable of type mat4 of the fragment shader
    public ShaderAttribArray toShadowMapSpace=new ShaderAttribArray("toShadowMapSpace[", "]", 4, this, UNIFORM_TYPE.MAT4);
    public static String VERTEX_PATH = "/postProcessingEffect/DefferedRendering/vertexDR.txt";
    public static String FRAGMENT_PATH = "/postProcessingEffect/DefferedRendering/fragmentDR.glsl";

    public DefferedRenderingShader() {
        super(VERTEX_PATH, FRAGMENT_PATH);
	    getAllUniformLocations();
    }
    public void init(float ssao_bias,float ssao_radius) {
        start();
        Matrix4f invProjMat = new Matrix4f();
        Pipeline.getProjMat().invert(invProjMat);
        Random rdm=new Random(0);
        for (int i = 0; i < Constantes.SSAO_NUMBER_SAMPLES.getInt(); i++) {
            Vector3 sample=new Vector3(rdm.nextFloat()*2-1,rdm.nextFloat()*2-1,rdm.nextFloat()*2-1).normalize();
        float length=(float) (rdm.nextFloat()*rdm.nextFloat())*ssao_radius;
        sample.mul(length);
        samples.loadVector3(sample,i);
        }
        
        albedoTexture.loadInt(0);
        normalTexture.loadInt(1);
        AMRText.loadInt(2);
        depthTex.loadInt(3);
        projectionMatrix.loadMatrix4f(Pipeline.getProjMat());
        invProjMatrix.loadMatrix4f(invProjMat);
        screenSize.loadVector2D(new Vector2f(DisplayManager.getWidth(),DisplayManager.getHeight()));
        linearize_param.loadVector2D(new Vector2f(Pipeline.getProjMat().m22(),Pipeline.getProjMat().m32()));
        skyMap.loadInt(4);
        fogColor.loadVector3(Constantes.SKY_COLOR.getVector3());
        fogDensity.loadFloat(Constantes.DENSITY.getFloat());
        fogGradient.loadFloat(Constantes.GRADIENT.getFloat());
        radius.loadFloat(ssao_radius);
        bias.loadFloat(ssao_bias);
        for (int i = 0; i < 4; i++){
           sunTexture.loadInt(i+5, i);
        }
        mapSize.loadVector2D(new Vector2f(Constantes.SHADOW_MAP_SIZE.getInt(), Constantes.SHADOW_MAP_SIZE.getInt()));
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
        
        projectionMatrix.loadMatrix4f(Pipeline.getProjMat());
        invProjMatrix.loadMatrix4f(invProjMat);
        screenSize.loadVector2D(new Vector2f(DisplayManager.getWidth(),DisplayManager.getHeight()));
        linearize_param.loadVector2D(new Vector2f(Pipeline.getProjMat().m22(),Pipeline.getProjMat().m32()));
        stop();
    }
}