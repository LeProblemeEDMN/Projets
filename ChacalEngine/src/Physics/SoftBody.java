package Physics;

import Entity.Entity;
import Entity.EntityWall;
import Loader.NormalObjLoader.NormalMappedObjLoader;
import Loader.RawModel;
import Loader.Texture.ModelTexture;
import Loader.TexturedModel;
import Main.MainGame;
import Main.MainLoop;
import Main.MainRender;
import RenderEngine.DisplayManager;
import RenderEngine.MasterRenderer;
import ShaderEngine.softEntity.SoftShader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import toolbox.maths.AxisAlignedBB;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SoftBody {

    private TexturedModel model;
    public static List<SoftPoint> points=new ArrayList<>();

    public int x=10,y=10,z=1;
    public SoftPoint[][] tablP = new SoftPoint[x][y];

    public SoftShader shader;

    public SoftBody(){
        //model=new TexturedModel(NormalMappedObjLoader.loadOBJ("res/cube.txt", MainLoop.LOADER),new ModelTexture(MainLoop.LOADER.loadTexture("Bricks3"),""));
        model=new TexturedModel(createModel(x,y,z+1),new ModelTexture(MainLoop.LOADER.loadTexture("Bricks3"),""));
        model.getTexture().setTransparance(true);
        model.getTexture().setReflectivity(0.8f);
        model.getTexture().setShineDamper(16f);
        //model.getTexture().setNormalMap(MainLoop.LOADER.loadTexture("Bricks3normale"));

        for (int i = x-1; i >=0 ; i--) {
            for (int j = y-1; j >=0 ; j--) {
                tablP[i][j] = addPoint(new Vector3(100+i*5,j*5+60,180));
                points.add(tablP[i][j]);
                if(i+1<x){
                    tablP[i][j].addNeighbour(tablP[i+1][j]);
                    if(j-1>=0){
                        tablP[i][j].addNeighbour(tablP[i+1][j-1]);
                    }
                }
                if(j+1<y){
                    tablP[i][j].addNeighbour(tablP[i][j+1]);
                    if(i+1<x){
                        tablP[i][j].addNeighbour(tablP[i+1][j+1]);
                    }

                }
            }
        }

    }

    public void initrender(){
        shader = new SoftShader();
        shader.init(MainGame.spots);
        shader.start();
        shader.reflectivity.loadFloat(1);
        shader.shineDamper.loadFloat(10);
        shader.stop();
    }

    public void render(){
        shader.start();

        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, MainRender.shadowMapMasterRenderer.getShadowMap());
        shader.toShadowMapSpace.loadMatrix4f(MainRender.shadowMapMasterRenderer.getToShadowMapSpaceMatrix());
        shader.loadViewMatrix(MainRender.CAMERA.getViewMatrix());
        shader.loadSpotAttenuation(MainGame.spots);
        MasterRenderer.disableCulling();
        shader.transformationMatrix.loadMatrix4f(Maths.createTransfromationMatrix(new Vector3f(100,60,180),0,0,0,1));
        //shader.loadSpotInUse(entity.spotIds);
        //shader.loadPointInUse(entity.pointIds);
        shader.sizeBody.loadInt(x,0);
        shader.sizeBody.loadInt(y,1);
        shader.sizeBody.loadInt(z+1,2);
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                for (int k = 0; k < z; k++) {
                    shader.posSoftPoint.loadVector3(tablP[i][j].getPosition(),i*y+j);
                }
            }
        }

        GL30.glBindVertexArray(model.getRawModel().getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(),
                GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    public RawModel createModel(int x,int y,int z){
        float[] points=new float[(x) * (y) * (z) * 3];
        float[] texturesArray=new float[(x) * (y) * (z) * 2];
        float[] normals=new float[(x) * (y) * (z) * 3];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                for (int k = 0; k < z; k++) {
                    int mul = (i * (y * z) + j * z + k) * 3;
                    points[mul]= i;
                    points[mul + 1]= j;
                    points[mul + 2]= k;
                    normals[mul + 1]= 1;
                }
            }
        }

        List<Integer>indices = new ArrayList<>();
        //crée les triangles
        for (int i = 0; i < 6; i++) {
            int mulX = i<2 ? 1:0;
            int mulY = i>=2 && i<4 ? 1:0;
            int mulZ = i>=4 ? 1:0;
            int pX = i%2==1 ? mulX*(x-1) : 0;
            int pY = i%2==1 ? mulY*(y-1) : 0;
            int pZ = i%2==1 ? mulZ*(z-1) : 0;

            //sur quel axe se déplacer
            int[] moveA = {mulZ == 1 ? 1 : 0, mulX == 1 ? 1 : 0, mulY == 1 ? 1 : 0};
            int[] moveB = {mulY == 1 ? 1 : 0, mulZ == 1 ? 1 : 0, mulX == 1 ? 1 : 0};
            //limite de péplacement
            int moveALim = mulX == 1 ? y : mulY == 1 ? z : x;
            int moveBLim = mulX == 1 ? z : mulY == 1 ? x : y;
            for (int j = 0; j < moveALim - 1; j++) {
                for (int k = 0; k < moveBLim - 1; k++) {
                    //4 points du polygone
                    int id1 = ((pX + j*moveA[0] + k*moveB[0]) * (y*z) + z * (pY + j*moveA[1] + k*moveB[1]) + (pZ + j*moveA[2] + k*moveB[2]));
                    int id2 = ((pX + (j+1)*moveA[0] + k*moveB[0]) * (y*z) + z * (pY + (j+1)*moveA[1] + k*moveB[1]) + (pZ + (j+1)*moveA[2] + k*moveB[2]));
                    int id3 = ((pX + (j+1)*moveA[0] + (k+1)*moveB[0]) * (y*z) + z * (pY + (j+1)*moveA[1] + (k+1)*moveB[1]) + (pZ + (j+1)*moveA[2] + (k+1)*moveB[2]));
                    int id4 = ((pX + j*moveA[0] + (k+1)*moveB[0]) * (y*z) + z * (pY + j*moveA[1] + (k+1)*moveB[1]) + (pZ + j*moveA[2] + (k+1)*moveB[2]));
                    //System.out.println(i +" // "+j+" "+ Arrays.toString(moveA)+"    "+k+" "+Arrays.toString(moveB));

                    System.out.println(i +" // "+(pX + j*moveA[0] + k*moveB[0])+" "+(pY + j*moveA[1] + k*moveB[1])+" "+(pZ + j*moveA[2] + k*moveB[2]));
                    System.out.println(i +" // "+vec(id1,points) + "    " + vec(id2,points)+ "    "+ vec(id3,points) + "    "+ vec(id4,points));
                    indices.add(id4);
                    indices.add(id3);
                    indices.add(id1);

                    indices.add(id2);
                    indices.add(id1);
                    indices.add(id3);

                }
            }
        }
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i);
        }
        RawModel model=  MainLoop.LOADER.loadToVOA(points, texturesArray, normals, indicesArray,"softBody");
        model.setAabb(new AxisAlignedBB(new Vector3(-9999,-9999,-9999), new Vector3(9999,9999,9999)));
        return model;
    }
    private String vec(int id, float[] pos){
        return pos[id]+" "+pos[id+1] + " "+pos[id+2];
    }
    public SoftPoint addPoint(Vector3 position){
        EntityWall e = new EntityWall(model, position, 0,0, 0, new Vector3(5,5,5), "soft");
        SoftPoint p = new SoftPoint(position, e);
        //MainGame.entities.add(e);
        return p;
    }

    public void update(float dt){

        for (SoftPoint p: points){
            p.calculForce();
        }
        for (SoftPoint p: points){
            p.update(dt);
        }
       // System.out.println( Math.abs( points.get(0).getPosition().x-points.get(2).getPosition().x)-Math.abs( points.get(1).getPosition().x-points.get(2).getPosition().x));
    }

}
