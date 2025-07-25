package simulation.unit;

import toolbox.maths.Vector3;

public class Model {
    private Vector3 position;
    private float speed;
    private float rotation;
    private float health;
    private boolean alive;

    private float maxAngularSpeed,maxLinearSpeed;
    private float positionTresh=0.1f;//if the square of the model distance to the target is smaller than positionTresh then we are arrived.

    public void update(float dt,Vector3 target,float targetRotation){

        if(position.squareDistanceTo(target)<positionTresh)speed=0;
        else{
            //update speed max or less if close to the end
        }
        //same for rotation

        //check for collisions on the path

        position.add(new Vector3(Math.cos(rotation)*speed,0,Math.sin(rotation)*speed));
    }

    public void getHeight(){
        position.y=0;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }


}
