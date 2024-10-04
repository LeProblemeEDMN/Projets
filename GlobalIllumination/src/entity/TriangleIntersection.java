package entity;

import org.lwjgl.util.vector.Vector2f;

public class TriangleIntersection {
    public float t;
    public Vector2f textureCoord;
    public Triangle triangle;
    public Vector3 point;
    public Entity e;

    public TriangleIntersection(float t, Vector2f textureCoord, Triangle triangle) {
        this.t = t;
        this.textureCoord = textureCoord;
        this.triangle = triangle;
    }

    public TriangleIntersection(float t, Vector2f textureCoord, Triangle triangle, Vector3 point) {
        this.t = t;

        this.textureCoord = textureCoord;
        this.triangle = triangle;
        this.point = point;
    }

    public float getT() {
        return t;
    }

    public void setT(float t) {
        this.t = t;
    }

    public Vector2f getTextureCoord() {
        return textureCoord;
    }

    public void setTextureCoord(Vector2f textureCoord) {
        this.textureCoord = textureCoord;
    }

    public Triangle getTriangle() {
        return triangle;
    }

    public void setTriangle(Triangle triangle) {
        this.triangle = triangle;
    }

    public Vector3 getPoint() {
        return point;
    }

    public void setPoint(Vector3 point) {
        this.point = point;
    }
}
