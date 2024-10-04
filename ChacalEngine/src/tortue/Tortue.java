package tortue;


import Entity.EntityWall;
import Loader.Loader;
import Loader.NormalObjLoader.NormalMappedObjLoader;
import Loader.Texture.ModelTexture;
import Loader.TexturedModel;
import Main.MainGame;
import Main.Register;
import org.lwjgl.util.vector.Vector3f;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

import java.util.ArrayList;
import java.util.List;

public class Tortue {
    public static TexturedModel model;
    public static final float TIME_MUL=999999;
    public static Vector3 base=new Vector3(1000,30,0);
    public static List<event>events=new ArrayList<>();
    public static void init( Loader loader){
        model=new TexturedModel(NormalMappedObjLoader.loadOBJ("res/cube.txt", loader),new ModelTexture(loader.loadTexture("Bricks3"),""));
        model.getTexture().setReflectivity(1f);
        model.getTexture().setShineDamper(32f);
        model.getTexture().setTransparance(true);
        model.getTexture().setNormalMap(loader.loadTexture("Bricks3normale"));

       // addEvent(new Vector3(),0,0,20);
        addEvent(new Vector3(500,5,5),45,0,1);
        //carre(1200,new Vector3(-600,-600,-600),0,360/1200f);
        pyramide(20,new Vector3(0,0,0),0,0);
        MainGame.entities.add(events.get(0).entity);
        MainGame.processEntity(events.get(0).entity);

    }

    public static void carre(int size,Vector3 position,float rota,float dO){
       // Vector3 position=new Vector3(0,0,0);
        float t=0;
        float phy=0;
        for (int i = 0; i < 4; i++) {
            addEvent(position,0,90*i+rota,size);
            addEvent(position.getAdd(0,size-1,0),0,90*i+rota,size);
            addEvent(position.getAdd(new Vector3(Maths.rotateVector(0,i*90+rota,new Vector3f(0,0,1))).mul(new Vector3(-1,1,1))),-90,i*90+rota,size);
            position=position.getAdd(new Vector3(Maths.rotateVector(0,i*90+rota,new Vector3f(0,0,size))).mul(new Vector3(-1,1,1)));
            //System.out.println(Maths.rotateVector(0,i*90,new Vector3f(0,0,size)));
        }
        if(size>2){
            carre(size-2,new Vector3(Maths.rotateVector(0,rota+dO+135,new Vector3f((size-2)/(float)Math.sqrt(2),0,0))).mul(new Vector3(-1,1,1)).getAdd(new Vector3(0,1-size/2,0)),rota+dO,dO);
          /*  System.out.println(new Vector3(Maths.rotateVector(0,rota+dO+135,new Vector3f((size-2)/(float)Math.sqrt(2),0,0))).mul(new Vector3(-1,1,1)).getAdd(new Vector3(0,1-size/2,0)));
            System.out.println();*/
        }

    }

    public static void pyramide(int size,Vector3 position,float rota,float dO){
        float s3=(float)Math.sqrt(3);
        float l=(float)Math.tan(Math.toRadians(30))*size/2;
        float h= (float)Math.sin(Math.toRadians(54.5f))*size;
        addEvent(position.getAdd(-l,-l,-size/2),0,0,size);
        addEvent(position.getAdd(-l,-l,size/2),0,120,size);
        addEvent(position.getAdd(s3/2*size-l,-l,0),0,-120,size);

        addEvent(position.getAdd(-l,-l,-size/2),-54.5f,30,size);
        addEvent(position.getAdd(-l,-l,size/2),-54.5f,150,size);
        addEvent(position.getAdd(s3/2*size-l,-l,0),-54.5f,-90,size);

        if(size>2){
            float nl=(float)Math.tan(Math.toRadians(30))*size/4;
            float h2= (float)Math.sin(Math.toRadians(54.5f))*size/2;
            pyramide(size/2,position.getAdd(new Vector3(0,h-l-h2+nl,0)),rota+dO,dO);
            pyramide(size/2,position.getAdd(new Vector3(-l+nl,-l+nl,-size/2+size/4)),rota+dO,dO);
            pyramide(size/2,position.getAdd(new Vector3(-l+nl,-l+nl,size/2-size/4)),rota+dO,dO);
            pyramide(size/2,position.getAdd(new Vector3(s3/2*size-l-nl,-l+nl,0)),rota+dO,dO);
          //  System.out.println(h);
        }
    }



    public static void addEvent(Vector3 pos,float theta,float phy,float l){
        event e=new event();
        e.length=l;
        e.tottime=l;
        EntityWall wall=new EntityWall(Register.model,pos.getAdd(base),theta,phy,0,new Vector3(0.1,0.1,l),"barre");
        wall.pointIds.add(0);
        wall.pointIds.add(MainGame.pointLigths.size()-1);
        e.entity=wall;
        events.add(e);
    }

    public static void update(float dt){
        if(events.size()>0) {
            dt *= TIME_MUL;
            while (dt > 0) {
                event e = events.get(0);
                e.time+=dt;
                e.entity.getScaleVector().z=e.length* Math.min(e.time,e.tottime)/e.tottime;
                e.entity.setTransformationMatrixWall();
                if(e.time>e.tottime){
                    dt=e.time-e.tottime;
                    events.remove(0);
                    if(events.size()==0){
                        return;
                    }else{
                        e=events.get(0);
                        MainGame.entities.add(e.entity);
                        MainGame.processEntity(e.entity);
                    }
                }else dt=0;
            }
        }
    }
}
