package rendering.shadow;

import entity.Camera;
import entity.Entity;
import loading.RawModel;
import loading.TexturedModel;
import main.Constantes;
import main.Pipeline;
import main.World;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;
import postProcessingEffect.gaussianBlur.HorizontalBlur;
import postProcessingEffect.gaussianBlur.VerticalBlur;
import rendering.capture_object.Fbo;
import rendering.postprocessing.PostProcessing;
import rendering.postprocessing.SSAOWorkflow;
import rendering.render_objects.RenderingWorkflow;
import rendering.shadow.EntityShadow.EntityShadowShader;
import toolbox.OpenGlUtils;
import toolbox.maths.Frustum;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

import java.nio.FloatBuffer;
import java.util.*;

public class CascadedShadowMap extends RenderingWorkflow {
    /*
    //OUTDATED
    On souhaite couvrir le champ de vue (paramètre FOV et longueur L) avec N shadow map.

    La rotation rx et ry peut être choisie librement pour modéliser l'orientation du soleil mais comme la camera tourne,
    il faut que les shadow map soient tourné avec rz=r_y de la camera du joueur. Si ce n'est pas fait, les shadow map ne*
    couvriront pas tout le champ de vue du joueur.

    Chaque shadow map (i0,...,i=N-1) est de taille (2sin(FOV/2)d_i,d_i,P) et aura une texture de taille (2sin(FOV/2)S_y,S_y).
    On suppose que d_i=d_0r^i. On sait alors que L=d_0*(1-r^N)/(1-r).

    La résolution en pixel de l'ecran des ombres est approximé par la formule R(v)=W/FOV*atan(d_i/(v*S_y)) avec v la distance
     entre la camera et le point observe si R(v) est trop grand les omrbes vont paraitres pixélisée.

     Il faut donc déterminet d_0,r,S_y. Pour cela on dispose de deux equations L=d_0*(1-r^N)/(1-r) et R(v)=W/FOV*atan(d_i/(v*S_y)).
     On doit donc fixer l'un des trois paramètres et choisir les autres. Ou calculer R(v) a deux endroits.

     */
    /*

     */
    public static String NAME="CascadeShadowMap";
    public static CascadedShadowMap instance;

    private Vector3 lightDir;
    public static float FAR_SKY=1000f;
    public Fbo[] fbos;
    public float d_0,r;
    public int S_y;
    Matrix4f offsetMatrix=createOffset();
    Matrix4f[] projectionMatrices;
    Matrix4f[] viewMatrices;
    Matrix4f[] VPMatrices;
    Matrix4f[] shadowMapSpaceMatrices;
    float[] levels={1,0.2f,0.07f,0.01f};
    private EntityShadowShader shader;

    //variance shadow map
    private Fbo captureFbo,hblur_fbo;

    public CascadedShadowMap() {
        super(NAME,false);
        float r=Constantes.CASCADED_SHADOW_R.getFloat();
        S_y=Constantes.SHADOW_MAP_SIZE.getInt();
        this.r=r;
        this.d_0=FAR_SKY*(1-r)/(1-(float)Math.pow(r, Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt()));
        fbos=new Fbo[Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt()];
        projectionMatrices=new Matrix4f[Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt()];
        viewMatrices=new Matrix4f[Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt()];
        VPMatrices=new Matrix4f[Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt()];
        shadowMapSpaceMatrices=new Matrix4f[Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt()];
        for(int i=0;i<Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt();i++){
            fbos[i]=new Fbo(S_y,S_y,Fbo.RG_32,Fbo.DEPTH_TEXTURE);
        }
        captureFbo=new Fbo(S_y,S_y,Fbo.RG_32,Fbo.DEPTH_TEXTURE);
        hblur_fbo=new Fbo(S_y,S_y,Fbo.RG_32,Fbo.NONE);
        shader=new EntityShadowShader();

    }

    public void process(Map<TexturedModel, List<Entity>> entitesMap){
        lightDir= World.pointLigths.get(0).getPosition().getMul(1).normalize();//new Vector3(0.7f,0.7f,-0.3f).normalize();

        Frustum[] frustums=new Frustum[Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt()];
        Vector3[] centers=new Vector3[Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt()];
        float[] quick_tests=new float[Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt()];
        float near=0;
        Vector3 light_dir_world_pos=new Vector3(Maths.rotateVector(lightDir.x,lightDir.y,new Vector3f(0,0,1)));
        Matrix4f invVP=new Matrix4f(Pipeline.getProjMat()).mul(Pipeline.getViewMat()).invert();
        //creation des view et projection matrix des différentes shadow map.
        // pour cela on divise le frustum en NUMBER_CASCADE_SHADOW_MAP.getInt() parties et on fait une shadow map
        // englobant chacune de ses parties
        for(int i=0;i<Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt() ;i++) {
            //plan eloigne du frustum
            float far=this.d_0*(1-(float)Math.pow(r, i+1))/(1-r);
            //on calcule la distance du plan éloigné dans le screen space (ndc)
            far=(-Pipeline.getProjMat().m22()*far+Pipeline.getProjMat().m32())/far;

            Vector3 mean=new Vector3();
            Vector3[] corners=new Vector3[8];
            //calcule les 8 coins du frustum dans les coordonées du monde
            int id=0;
            for(int j=-1;j<=1;j+=2) {
                for(int k=-1;k<=1;k+=2) {
                    //reconvertit les coins a l'aide de l'inverse d ela matrice de viewProj.
                    Vector4f v=invVP.transform(new Vector4f(j,k,near,1));
                    corners[id]=new Vector3(v.x/v.w, v.y/v.w,v.z/v.w);
                    mean.add(corners[id]);
                    id++;

                    v=invVP.transform(new Vector4f(j,k,far,1));
                    corners[id]=new Vector3(v.x/v.w, v.y/v.w,v.z/v.w);
                    mean.add(corners[id]);
                    id++;
                }
            }

            //calcule la view matrix à partir du milieux des points et de al direction
            mean.mul(1.0f/8);
            centers[i]=mean;

            float length_frustum=300;//hauteur du frustum. (les objets plus loin ne feront pas d'ombre
            //génére la view matrix.
            viewMatrices[i]=new Matrix4f().lookAt(centers[i].getOglVec().sub(lightDir.getOglVec().mul(-length_frustum)),centers[i].getOglVec(),new Vector3f(0.0f, 1.0f, 0.0f));

            //fait la boite englobante dans l'espace de view
            Vector3 max=new Vector3(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY);
            Vector3 min=new Vector3(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY);

            //transforme les coins du frustum dans la vue des ombres
            for(int j=0;j<8;j++) {
                //utilise transformPosition?
                Vector4f v=viewMatrices[i].transform(new Vector4f(corners[j].x,corners[j].y,corners[j].z,1));
                Entity.getMaxAndMin(min,max,new Vector3(v.x,v.y,v.z));
            }
            //calcule la proj matrix poru quelle englobe tout
             projectionMatrices[i] = new Matrix4f().ortho
                    (min.x, max.x, min.y, max.y, 0, max.z-min.z+length_frustum , true);


            //calcule quick_tests à partir des coins de la boite englobante
            quick_tests[i]=max.x*max.x+max.y*max.y+0*(max.z*max.z);

            VPMatrices[i]=new Matrix4f(projectionMatrices[i]);
            VPMatrices[i].mul(viewMatrices[i]);
            shadowMapSpaceMatrices[i]=createOffset().mul(VPMatrices[i]);
            frustums[i]=Frustum.getFrustum(viewMatrices[i],projectionMatrices[i]);

            near=far;
        }

        //chaque hash map contient tt els entite trié par modèle qui doivent être rendu.
        Map<TexturedModel, List<Entity>>[]maps=new Map[Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt()];
        for(int i=0;i<Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt();i++){
            maps[i]= new HashMap<>();
        }

        int[] level=new int[Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt()];
        //pour chaque modèle le trie en fonction de
        for(TexturedModel model : entitesMap.keySet()){
            List<Entity>[] entities=new List[Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt()];
            for(int i=0;i<Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt();i++) {entities[i]=new ArrayList<>();}
            for (Entity e: entitesMap.get(model)) {
                for (int i = 0; i <Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt(); i++) {
                    if(distance_proj(e.getPosition(),centers[i],light_dir_world_pos)<quick_tests[i] || e.isLarge()){//centers[i].squareDistanceTo(e.getPosition())<quick_tests[i]){

                        if (e.inFrustum(frustums[i]) || e.isLarge()) {
                            entities[i].add(e);
                            e.temp=i;
                            level[i]++;
                        }
                    }
                }
            }

            for(int i=0;i<Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt();i++) {
                if(entities[i].size() == 0)continue;
                maps[i].put(model,entities[i]);
            }
        }


        //fait le rendu
        for(int i=0;i<Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt();i++) {
        //int i=1;
            if (i==0) fbos[i].bindFrameBuffer();
            else fbos[i].bindFrameBuffer();

            shader.start();
            //System.out.println("clear");
            GL11.glClearColor(1,1, 0, 	1);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);
            //
            shader.VPMatrix.loadMatrix4f(VPMatrices[i]);
            for(TexturedModel model : maps[i].keySet()) {
                render_instance(maps[i].get(model), model,0.2f);
            }
            shader.stop();
            if (i==0) fbos[i].unbindFrameBuffer();
            else fbos[i].unbindFrameBuffer();

            //variance shadow map

            if(i==0) {
                PostProcessing.start();
                hblur_fbo.bindFrameBuffer();
                GL11.glClearColor(1, 1, 0, 1);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                HorizontalBlur.instance.getShader().start();
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos[i].getColourTexture());
                GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
                HorizontalBlur.instance.getShader().stop();
                hblur_fbo.unbindFrameBuffer();

                fbos[i].bindFrameBuffer();
                GL11.glClearColor(1, 1, 0, 1);
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                VerticalBlur.instance.getShader().start();
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, hblur_fbo.getColourTexture());
                GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
                VerticalBlur.instance.getShader().stop();
                fbos[i].unbindFrameBuffer();
                PostProcessing.end();
            }
        }

        //desactive le modele charge
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);



    }
    public float distance_proj(Vector3 pos,Vector3 center,Vector3 ld){
        float dpx=pos.x-center.x;
        float dpy=pos.y-center.y;
        float dpz=pos.z-center.z;
        float dl=ld.x*dpx+ld.y*dpy+ld.z*dpz;
        dpx-=ld.x*dl;
        dpy-=ld.y*dl;
        dpz-=ld.z*dl;
        return dpx*dpx+dpy*dpy+dpz*dpz;
    }
    public void render_instance(List<Entity> entities,TexturedModel model,float LOD_level){
        RawModel rawModel=model.getRawModel(model.getLODId(LOD_level));

        //laod the model
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        if(model.getTexture().isTransparance()){
            OpenGlUtils.cullBackFaces(false);
        }
        else{
            OpenGlUtils.cullBackFaces(true);
        }

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
        for (int i = 0; i < Constantes.NUMBER_CASCADE_SHADOW_MAP.getInt(); i++) {
            fbos[i].cleanUp();
        }
        shader.cleanUp();
    }

    public int getFBOTexture(int id){
        return fbos[id].getColourTexture();
    }

    public Matrix4f getToShadowMapSpaceMatrix(int id){
        return shadowMapSpaceMatrices[id];
    }

    private Matrix4f createOrthoProjectionMatrix(float width, float height, float length) {
        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.identity();
        projectionMatrix.m00(2f / width);
        projectionMatrix.m11(2f / height);
        projectionMatrix.m22(-1f / length);
        projectionMatrix.m32(-200/length);
        projectionMatrix.m33(1);
        return projectionMatrix;
    }

    private static Matrix4f createOffset() {
        Matrix4f offset = new Matrix4f();
        offset.translate(new Vector3f(0.5f, 0.5f, 0.5f));
        offset.scale(new Vector3f(0.5f, 0.5f, 0.5f));
        return offset;
    }
}
