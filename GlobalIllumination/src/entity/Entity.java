package entity;


import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Entity {
    public List<Triangle>triangleList;
    public BoundingBox boundingBox;
    public Material material;

    public Entity(List<Triangle> triangleList, Material material) {
        this.triangleList = triangleList;
        this.material = material;
    }

    public Entity(BoundingBox boundingBox, Material material) {
        this.boundingBox = boundingBox;
        this.material = material;
        triangleList= boundingBox.triangles;
    }

    public TriangleIntersection intersect(Droite d, float tmin, boolean exitIfIntersect){
        if(boundingBox!=null){
           if(!boundingBox.intersect(d.O,d.D))return null;
           return boundingBox.intersectTriangle(d,tmin,exitIfIntersect);

        }else {
            TriangleIntersection bestIntersection = null;
            for (Triangle triangle : triangleList) {
                TriangleIntersection i = triangle.intersectTriangle(d, tmin);
                if (i != null && i.t < tmin) {
                    if (exitIfIntersect) return i;
                    tmin = i.t;

                    bestIntersection = i;
                }
            }
            return bestIntersection;
        }

    }

    public Color getRGB(Vector2f t){

        return new Color(material.texture.getRGB((int)(material.texture.getWidth()*t.x*0.99999),(int)(material.texture.getHeight()*t.y*0.99999)));
    }
}
