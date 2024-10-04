package Scene.Balls;

import Entity.Ball;

import Entity.Plan;
import Main.MainGame;
import Main.MainRender;
import Scene.Scene;

import toolbox.maths.Vector3;

public class BallsArrete extends Scene {
    public BallsArrete(float weightStatic, Vector3 momentum,Vector3 bias) {
        MainRender.CAMERA.setPosition(new Vector3(0,7,30));



        MainGame.PLANS.add(new Plan(new Vector3(-500,-1,-500),new Vector3(-500,1,-500),new Vector3(1000,1,1000)));
        //MainGame.PLANS.add(new Plan(new Vector3(0,0,0),new Vector3(1,1,1),new Vector3(10,3,10)));




                    Ball balle1=new Ball(bias.getAdd(0,10,0),0);
                    balle1.weight=weightStatic;
                    balle1.setVitesse(momentum.getMul(1));
                    MainGame.ballPhysic.add(balle1);
                    MainGame.processEntity(balle1.entity);
    }
}
