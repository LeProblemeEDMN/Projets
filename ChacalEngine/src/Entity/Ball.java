package Entity;

import Loader.RawModel;
import Loader.TexturedModel;
import toolbox.maths.Vector3;


public class Ball {
    public static float RAYON=1;
    private Vector3 position=new Vector3();
    private Vector3 vitesse=new Vector3();
    private Vector3 acc=new Vector3();
    private Vector3 color=new Vector3();
    public static RawModel MODEL;
    public static TexturedModel[] TEXTURED;
    public float weight=1000f;

    public Entity entity;

    public Ball(Vector3 position, int color) {
        this.position = position;
       // this.color = color;
        entity=new Entity(TEXTURED[color],position,0,0,0,2*RAYON,"");
        entity.radiusSphere=2;
        entity.useSphereFrustum=true;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getColor() {
        return color;
    }

    public Vector3 getAcc() {
        return acc;
    }

    public static float getRAYON() {
        return RAYON;
    }

    public static void setRAYON(float RAYON) {
        Ball.RAYON = RAYON;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Vector3 getVitesse() {
        return vitesse;
    }

    public void setVitesse(Vector3 vitesse) {
        this.vitesse = vitesse;
    }

    public void setAcc(Vector3 acc) {
        this.acc = acc;
    }

    public void setColor(Vector3 color) {
        this.color = color;
    }

    public static RawModel getMODEL() {
        return MODEL;
    }

    public static void setMODEL(RawModel MODEL) {
        Ball.MODEL = MODEL;
    }

    public float getWeight() {
        return weight;
    }

    public Entity getEntity() {
        return entity;
    }
}
