package entity;

import utils.MathsUtils;

public class Light {
    public Vector3 position;
    public Vector3 color;
    public Vector3 attenuation;
    public float maxRadiusSquared;
    public float lightRadius=0;//softShadows

    public Light(Vector3 position, Vector3 color, Vector3 attenuation, float maxRadiusSquared) {
        this.position = position;
        this.color = color;
        this.attenuation = attenuation;
        this.maxRadiusSquared = maxRadiusSquared;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getRandomPosition() {
        return position.getAdd(MathsUtils.randomVector(lightRadius));
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Vector3 getColor() {
        return color;
    }

    public void setColor(Vector3 color) {
        this.color = color;
    }

    public Vector3 getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Vector3 attenuation) {
        this.attenuation = attenuation;
    }

    public float getMaxRadiusSquared() {
        return maxRadiusSquared;
    }

    public void setMaxRadiusSquared(float maxRadius) {
        this.maxRadiusSquared = maxRadius;
    }

    public float getLightRadius() {
        return lightRadius;
    }

    public void setLightRadius(float lightRadius) {
        this.lightRadius = lightRadius;
    }
}
