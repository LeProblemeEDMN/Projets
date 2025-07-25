package rendering.shadow;

import entity.Entity;
import loading.RawModel;
import loading.TexturedModel;
import main.Constantes;
import main.Pipeline;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;
import rendering.render_objects.RenderingWorkflow;
import rendering.shadow.EntityShadow.EntityShadowShader;
import toolbox.OpenGlUtils;
import toolbox.maths.Frustum;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Shadow extends RenderingWorkflow {
    public Vector3 rotations;//orientation of the shadow
    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f viewMatrix = new Matrix4f();
    private EntityShadowShader shader=new EntityShadowShader();
    public ShadowFrameBuffer fbo_shadow;
    public Matrix4f VPMatrix;//Proj*View matrices used to faster the vertex shader
    public Matrix4f shadowMapSpaceMatrix;//matrix used by the other shader to determine the texture coordinate of a point.

    public float LOD_level=0;//cette varaible est utilisé pour déterminer quel nivau de LOD est utilisé pour génére les ombres.
    //si LOD_level=0 utilise le pire modèle si LOD_level=1 le meilleur.
    final float SHADOW_QUICK_TEST;

    public static String NAME="ShadowWorkflow";
    public static Shadow instance;

    public Shadow() {
        super(NAME,true);
        updateOrthoProjectionMatrix(Constantes.SHADOW_DISTANCE.getFloat(),Constantes.SHADOW_DISTANCE.getFloat(),Constantes.SHADOW_DISTANCE.getFloat()*2);
        fbo_shadow=new ShadowFrameBuffer(Constantes.SHADOW_MAP_SIZE.getInt(), Constantes.SHADOW_MAP_SIZE.getInt());
        shader=new EntityShadowShader();
        rotations=new Vector3(-70,66,0);
        SHADOW_QUICK_TEST=3*Constantes.SHADOW_DISTANCE.getFloat()*Constantes.SHADOW_DISTANCE.getFloat();
    }

    public void process(Map<TexturedModel, List<Entity>> entitesMap){

        Vector3 center=Pipeline.camera.getPosition().getAdd(Pipeline.camera.direction_view.getMul(Constantes.SHADOW_DISTANCE.getFloat()/2));
        prepare(center.getOglVec());
        Frustum frustum = Frustum.getFrustum(viewMatrix,projectionMatrix);
        for(TexturedModel model : entitesMap.keySet()){
            List<Entity>entities=new ArrayList<>();
            for (Entity e: entitesMap.get(model)) {
                if(center.squareDistanceTo(e.getPosition())<SHADOW_QUICK_TEST){
                    if (e.inFrustum(frustum)) {
                        entities.add(e);
                    }
                }
            }
            if(entities.size() == 0)continue;
            render_instance(entities,model);
        }

        //desactive le modele charge
        GL20.glDisableVertexAttribArray(0);
        //GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
        shader.stop();
        fbo_shadow.unbindFrameBuffer();

    }
    public void prepare(Vector3f center){
        //create view matrix

        viewMatrix= Maths.createTransfromationMatrix(center,rotations.x,rotations.y,0,new Vector3(1.0f,1.0f,1.0f)).invert();
        //start the FBO
        fbo_shadow.bindFrameBuffer();
        //clear the FBO
        Pipeline.prepare();

        VPMatrix=new Matrix4f(projectionMatrix);
        VPMatrix.mul(viewMatrix);

        shadowMapSpaceMatrix=createOffset().mul(VPMatrix);

        shader.start();
        shader.VPMatrix.loadMatrix4f(VPMatrix);
    }
    public void render_instance(List<Entity> entities,TexturedModel model){
        RawModel rawModel=model.getRawModel(model.getLODId(LOD_level));

        //laod the model
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        if(model.getTexture().isTransparance()) OpenGlUtils.cullBackFaces(false);
        else OpenGlUtils.cullBackFaces(true);

        int instanceCount=entities.size();
        //store the transformation matrices in a buffer
        FloatBuffer transformationMatrices =  MemoryUtil.memAllocFloat(instanceCount * 16); // 16 floats par matrice
        int j =0;

        for (Entity entity : entities) {
            entity.getTransformationMatrix().get(j*16, transformationMatrices);
            j++;
        }
        //ssendf the buffer to the GPU
        int transformationVBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, transformationVBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, transformationMatrices, GL15.GL_DYNAMIC_DRAW);

        //vbind the buffer so the gpu knows it is the trnasformation matrices
        int location=5;
        for (int i = 0; i < 4; i++) {
            GL20.glEnableVertexAttribArray(location + i);
            GL20.glVertexAttribPointer(location + i, 4, GL11.GL_FLOAT, false, 64, i * 16);
            GL33.glVertexAttribDivisor(location + i, 1); // Change par instance
        }
        //draw the objects
        GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0, instanceCount);

        //destroy the buffer and free memory.
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(transformationVBO);
        MemoryUtil.memFree(transformationMatrices);
    }

    @Override
    public void cleanUp() {
        fbo.cleanUp();
        shader.cleanUp();
    }
    public int getFBOTexture(int id){
        return fbo_shadow.getShadowMap();
    }
    public Matrix4f getToShadowMapSpaceMatrix(int id) {
        return shadowMapSpaceMatrix;
    }
    private void updateOrthoProjectionMatrix(float width, float height, float length) {
        projectionMatrix.identity();
        projectionMatrix.m00(2f / width);
        projectionMatrix.m11(2f / height);
        projectionMatrix.m22(-2f / length);

        projectionMatrix.m33(1);
    }

    private static Matrix4f createOffset() {
        Matrix4f offset = new Matrix4f();
        offset.translate(new Vector3f(0.5f, 0.5f, 0.5f));
        offset.scale(new Vector3f(0.5f, 0.5f, 0.5f));
        return offset;
    }
}
