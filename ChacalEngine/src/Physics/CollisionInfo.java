package Physics;

import Entity.Ball;
import Entity.Plan;

import toolbox.maths.Vector3;

public class CollisionInfo {
    enum
    TypeIntersction{
        BALL,PLAN};


    public Vector3 normale;
    public Vector3 deltaposition;
    public TypeIntersction type;
    public Ball ball;
    public Plan plan;

    public CollisionInfo(Vector3 normale, Vector3 deltaposition, Ball ball) {
        this.normale = normale;
        this.deltaposition = deltaposition;
        this.ball = ball;
        type= TypeIntersction.BALL;
    }

    public CollisionInfo(Vector3 normale, Vector3 deltaposition, Plan plan) {
        this.normale = normale;
        this.deltaposition = deltaposition;
        this.plan = plan;
        type= TypeIntersction.PLAN;
    }

    @Override
    public String toString() {
        String line="Collision "+type+" pos:"+deltaposition+" norm:"+normale;
        return line;
    }
}
