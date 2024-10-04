package Scene.Balls;

import Entity.Ball;

import Entity.Plan;
import Main.MainGame;
import Main.MainRender;
import Scene.Scene;

import toolbox.maths.Vector3;

import java.util.ArrayList;
import java.util.List;

public class Ballsbullet extends Scene {
    public Ballsbullet(float weightStatic,float weightBullet,Vector3 pos, Vector3 momentumStatic,Vector3 momentumBullet, int nbLayer, int size,int nbBullet) {
//        MainRender.CAMERA.setPosition(new Vector3(0,7,30));



        MainGame.PLANS.add(new Plan(new Vector3(-500,-10,-500),new Vector3(-500,1,-500),new Vector3(1000,11,1000)));


        for (int i = -size; i <= size; i+=1) {
            for (int j = -size; j <= size; j+=1) {
                for (int k = 0; k < nbLayer; k++) {
                    Ball balle1=new Ball(new Vector3(i*2* Ball.RAYON,1+k*2* Ball.RAYON,j*2* Ball.RAYON).add(pos),0);
                    balle1.weight=weightStatic;
                    balle1.setVitesse(momentumStatic.getMul(1));
                    MainGame.ballPhysic.add(balle1);
                    MainGame.entities.add(balle1.entity);
                    MainGame.processEntity(balle1.entity);
                }

            }
        }
        for (int i = 0; i < nbBullet; i++) {
            Ball balle=new Ball(new Vector3((Math.random()*4*size-2*size)* Ball.getRAYON(),1,(Math.random()*4*size-2*size)* Ball.getRAYON()).add(pos),0);
            balle.weight=weightBullet;
            balle.setVitesse(momentumBullet.getMul(1));
            List<Ball> balls=new ArrayList<>();
            balls.add(balle);
            this.getMapBall().put(new Float(2+(float)Math.random()*3),balls);
        }
        System.out.println(this.getMapBall());
    }
}
