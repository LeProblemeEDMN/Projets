package Entity;

import Loader.RawModel;

import toolbox.maths.Vector3;

public class Plan {
    private Vector3 position=new Vector3();
    private Vector3 taille=new Vector3();
    private Vector3 color=new Vector3();
    public static RawModel MODEL;
    public Plan(Vector3 position, Vector3 color,Vector3 size) {
        this.position = position;
        this.color = color;
        this.taille=size;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getColor() {
        return color;
    }

    public Vector3 getTaille() {
        return taille;
    }
}
