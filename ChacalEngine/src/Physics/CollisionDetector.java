package Physics;

import Entity.Ball;
import Entity.Plan;
import Main.MainGame;
import Main.MainRender;

import toolbox.maths.Vector3;

import java.util.ArrayList;
import java.util.List;

public class CollisionDetector {
    public static List<CollisionInfo>detect(Ball ball){
        Vector3 p=ball.getPosition();

        List<CollisionInfo> list=new ArrayList<>();
        for (Plan plan: MainGame.PLANS) {
            Vector3 pp=plan.getPosition();
            Vector3 sp=plan.getTaille();
            int n=list.size();
            if(p.x>pp.x- Ball.RAYON && p.x<pp.x+sp.x+ Ball.RAYON &&p.z>pp.z- Ball.RAYON && p.z<pp.z+sp.z+ Ball.RAYON){
                if(p.y- Ball.RAYON<pp.y+sp.y && p.y- Ball.RAYON>pp.y){
                   // System.out.println("test"+(pp.y+sp.y-(p.y-Ball.RAYON)));
                    CollisionInfo info=new CollisionInfo(new Vector3(0,1,0),new Vector3(0,pp.y+sp.y-(p.y- Ball.RAYON),0),plan);
                    list.add(info);
                }else if(p.y+ Ball.RAYON<pp.y+sp.y && p.y+ Ball.RAYON>pp.y){
                  //  System.out.println("test2"+(pp.y-Ball.RAYON -(p.y)));
                    CollisionInfo info=new CollisionInfo(new Vector3(0,-1,0),new Vector3(0,pp.y- Ball.RAYON -(p.y),0),plan);
                    list.add(info);

                }
            }
            else if(p.x>pp.x- Ball.RAYON && p.x<pp.x+sp.x+ Ball.RAYON &&p.y>pp.y- Ball.RAYON && p.y<pp.y+sp.y+ Ball.RAYON){
                if(p.z- Ball.RAYON<pp.z+sp.z && p.z- Ball.RAYON>pp.z){
                    CollisionInfo info=new CollisionInfo(new Vector3(0,0,1),new Vector3(0,pp.z+sp.z-(p.z- Ball.RAYON),0),plan);
                    list.add(info);
                }else if(p.z+ Ball.RAYON<pp.z+sp.z && p.z+ Ball.RAYON>pp.z){
                    CollisionInfo info=new CollisionInfo(new Vector3(0,0,-1),new Vector3(0,pp.z- Ball.RAYON -p.z,0),plan);
                    list.add(info);
                }
            }
            else if(p.y>pp.y- Ball.RAYON && p.y<pp.y+sp.y+ Ball.RAYON &&p.z>pp.z- Ball.RAYON && p.z<pp.z+sp.z+ Ball.RAYON){
                if(p.x- Ball.RAYON<pp.x+sp.x && p.x- Ball.RAYON>pp.x){
                    CollisionInfo info=new CollisionInfo(new Vector3(1,0,0),new Vector3(0,pp.x+sp.x-(p.x- Ball.RAYON),0),plan);
                    list.add(info);
                }else if(p.x+ Ball.RAYON<pp.x+sp.x && p.x+ Ball.RAYON>pp.x){
                    CollisionInfo info=new CollisionInfo(new Vector3(-1,0,0),new Vector3(0,pp.x- Ball.RAYON -(p.x),0),plan);
                    list.add(info);
                }
            }
            /*if(list.size()-n>1){
                Vector3 position=new Vector3();
                Vector3 normal=new Vector3();
                for (int i = 0; i < list.size()-n; i++) {
                    normal.add(list.get(list.size()-1).normale);
                    position.add(list.get(list.size()-1).deltaposition);
                    System.out.println(list.get(list.size()-1).deltaposition);
                    System.out.println(list.get(list.size()-1).normale);
                    list.remove(list.size()-1);
                }
                normal.normalize();
                CollisionInfo info=new CollisionInfo(normal,position,plan);
                System.out.println(position);
                System.out.println();
                list.add(info);
            }*/
        }
        for (Ball b: MainGame.ballPhysic) {
            float dist=b.getPosition().squareDistanceTo(p);
            if(dist<4* Ball.RAYON* Ball.RAYON && b.getVitesse().squarelength()<ball.getVitesse().squarelength()){
                Vector3 normal=p.getSub(b.getPosition()).normalize();

                CollisionInfo info=new CollisionInfo(normal,normal.getMul(2* Ball.RAYON- (float)Math.sqrt(dist)),b);
                list.add(info);
            }
        }

        return list;
    }
}
