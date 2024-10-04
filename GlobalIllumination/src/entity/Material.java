package entity;

import java.awt.image.BufferedImage;

public class Material {
    public float r,t;
    public float newRayCoefficient;
    public BufferedImage texture;
    public float phi_max;

    public Material(float r, float newRayCoefficient,float phi, BufferedImage texture) {
        this.r = r;
        this.t = 1-r;
        this.newRayCoefficient = newRayCoefficient;
        this.texture = texture;
        this.phi_max = phi;
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }

    public float getT() {
        return t;
    }

    public void setT(float t) {
        this.t = t;
    }

    public float getNewRayCoefficient() {
        return newRayCoefficient;
    }

    public void setNewRayCoefficient(float newRayCoefficient) {
        this.newRayCoefficient = newRayCoefficient;
    }

    public BufferedImage getTexture() {
        return texture;
    }

    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }

    public float getPhi_max() {
        return phi_max;
    }

    public void setPhi_max(float phi_max) {
        this.phi_max = phi_max;
    }
}
