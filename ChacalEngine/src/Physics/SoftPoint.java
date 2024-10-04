package Physics;



import Entity.EntityWall;
import Main.MainGame;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

import java.util.ArrayList;
import java.util.List;

public class SoftPoint {
    private final static float kd=5f,ks=70, ceil = 1f; //4 ks=18 6 ks=30 8 ks=40 10 ks =54
    private final static float g = 9.81f;

    private Vector3 position;
    private Vector3 speed = new Vector3();
    private float mass = 1;
    private Vector3 acc = new Vector3();
    private List<SoftPoint>neighbours = new ArrayList<>();
    private List<Float> distances = new ArrayList<>();

    private EntityWall e;

    public SoftPoint(Vector3 position, EntityWall e) {
        this.position = position;
        this.e = e;
    }

    public void addNeighbour(SoftPoint p){
        float d = p.getPosition().getSub(getPosition()).length();
        distances.add(d);
        neighbours.add(p);

        p.getNeighbours().add(this);
        p.getDistances().add(d);
    }

    public void calculForce(){
        //calcul force
        Vector3 forces = new Vector3();
        forces.add(0,-mass*g,0);

        for (int i = 0; i < neighbours.size(); i++) {
            SoftPoint p = neighbours.get(i);
            Vector3 AB = p.getPosition().getSub(getPosition());
            float currentDist = AB.length();
            float f1 = ks * (currentDist - distances.get(i));
            float f2 = kd * AB.getMul(1/currentDist).dotProduct(p.getSpeed().getSub(getSpeed()));

            forces.add(AB.getMul(1/currentDist).getMul(f1 + f2));

        }
        acc = forces.getMul(1/mass);
    }

    public void update(float dt){
        speed.add(acc.getMul(dt));
        position.add(speed.getMul(dt));
        // System.out.println(this+" "+position);
        //3d
        e.setPosition(getPosition());
        e.setTransformationMatrixWall();

        //collisions
       /* if(position.y<0){
            position.y=0.01f;
            setSpeed(Maths.reflect(getSpeed(), new Vector3(0,1,0), 0.5f));
        }*/
        if(position.y<100-position.getX() && position.getX()<100){
            position.y=0.01f + 100-position.getX();
            setSpeed(Maths.reflect(getSpeed(), new Vector3(1,1,0).normalize(), 0.5f));
        }
        if(position.y<position.getX()-100 && position.getX()>100){
            position.y=0.01f + position.getX()-100;
            setSpeed(Maths.reflect(getSpeed(), new Vector3(-1,1,0).normalize(), 0.5f));
        }
        for (int i = 0; i < SoftBody.points.size(); i++) {
            SoftPoint p = SoftBody.points.get(i);
            if(p!=this) {
                Vector3 AB = p.getPosition().getSub(getPosition());
                float currentDist = AB.length();
                AB.normalize();
                if (currentDist < ceil) {
                    float dDist = ceil - currentDist;
                    p.getPosition().add(AB.getMul(dDist * 0.55f));
                    getPosition().add(AB.getMul(dDist * -0.55f));
                    p.setSpeed(Maths.reflect(p.getSpeed(), AB, 1));
                    setSpeed(Maths.reflect(getSpeed(), AB.getMul(-1), 1));
                }
            }
        }
    }

    public Vector3 getPosition() {
        return position;
    }

    public List<Float> getDistances() {
        return distances;
    }

    public List<SoftPoint> getNeighbours() {
        return neighbours;
    }

    public Vector3 getSpeed() {
        return speed;
    }

    public void setSpeed(Vector3 speed) {
        this.speed = speed;
    }
}
