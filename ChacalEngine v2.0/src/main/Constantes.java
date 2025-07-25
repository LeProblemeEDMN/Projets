package main;

import toolbox.maths.Vector3;

public class Constantes {
    public static Constantes SKY_COLOR = new Constantes("SKY_COLOR", new Vector3(0.6f,0.6f,0.8f));
    public static Constantes GRADIENT = new Constantes("GRADIENT", 1.5f);
    public static Constantes DENSITY = new Constantes("DENSITY", 0.0004f);
    public static Constantes NUMBER_CASCADE_SHADOW_MAP = new Constantes("NUMBER_CASCADE_SHADOW_MAP", 4);
    public static Constantes SSAO_NUMBER_SAMPLES = new Constantes("SSAO_NUMBER_SAMPLES", 64);
    public static Constantes SSGI_NUMBER_SAMPLES = new Constantes("SSGI_NUMBER_SAMPLES", 64);

    public static Constantes EXPOSURE=new Constantes("EXPOSURE", 2f);
    public static Constantes Z_PRE_PASS=new Constantes("Z_PRE_PASS", "true");
    public static Constantes ANTIALIASING=new Constantes("ANTIALIASING", "FXAA");

    public static Constantes SHADOW_MAP_SIZE=new Constantes("SHADOW_MAP_SIZE", 4096);
    public static Constantes SHADOW_DISTANCE=new Constantes("SHADOW_DISTANCE", 400.0f);

    public static Constantes CASCADED_SHADOW_R=new Constantes("CASCADED_SHADOW_R", 1.5f);


    public String name;
    public float value;
    public String value_string;
    public Vector3 vector3;


    public Constantes(String name, float value) {
        this.name = name;
        this.value = value;
    }

    public Constantes(String name, String value_string) {
        this.name = name;
        this.value_string = value_string;
    }

    public Constantes(String name, Vector3 vector3) {
        this.name = name;
        this.vector3 = vector3;
    }

    public String getName() {
        return name;
    }

    public float getFloat() {
        return value;
    }

    public int getInt(){
        return (int) value;
    }

    public Vector3 getVector3() {
        return vector3;
    }

    public String getValue_string() {
        return value_string;
    }
}
