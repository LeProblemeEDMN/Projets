package Physics;

import Entity.Ball;
import Main.MainGame;
import toolbox.maths.Maths;
import toolbox.maths.Vector3;

import java.util.List;

public class Animator {

    public static Vector3 g=new Vector3(0,-9.81,0);
  //  public static Vector3 g=new Vector3(-9.81*Math.sin(Math.PI/6),-9.81*Math.cos(Math.PI/6),0);
    public static float C=0.5f;
    public static float Cx=0.4f;
    public static float densite=1.225f;
    public static float COEFF_FRICTION=0.5f*Cx*densite* (float)Math.PI* Ball.RAYON* Ball.RAYON;//0.5*Cx*densite volumique air * surface
    public static void update(float dt){
        for (Ball ball: MainGame.ballPhysic) {
            List<CollisionInfo>infos=CollisionDetector.detect(ball);
            if(infos.size()>0){
                for (CollisionInfo info: infos) {
                    if(info.type== CollisionInfo.TypeIntersction.PLAN){
                        Vector3 v0v=ball.getVitesse().getMul(1);
                     //   System.out.println(ball.getPosition());
                        ball.getPosition().add(info.deltaposition.getAdd(info.normale.getMul(0.001f)));
                      //  System.out.println(ball.getPosition()+" "+info.deltaposition+" "+info.normale);
                        Vector3 I = ball.getVitesse();
                        Vector3 R = Maths.reflect(I, info.normale.normalize(),C);


                        Vector3 H = Maths.reflect(I, info.normale,0).normalize();
                     //   System.out.println("R+"+R);
                     //   System.out.println(ball.getVitesse());
                        ball.setVitesse(R);
                     //   System.out.println(ball.getVitesse());
                     //   System.out.println();
                        if(Math.abs(ball.getVitesse().y)<0.1f){
                            ball.getVitesse().y=0;
                        }

                       // ball.getVitesse().add(H.getMul(c*ball.weight*dt));
                       // System.out.println(ball.getVitesse().x);
                    /*    System.out.println("MUR:    "+v0v+" "+ball.getVitesse());
                        System.out.println(ball.getVitesse().squarelength()*ball.weight+"   "+(v0v.squarelength()*ball.weight));
                        System.out.println();*/

                    }else{
                       // System.out.println(ball.getPosition()+" "+info.ball.getPosition());
                        ball.getPosition().add(info.deltaposition.getAdd(info.normale.getMul(0.0001f)));
                      //  System.out.println(ball.getPosition()+" "+info.ball.getPosition());
                        Vector3 v0v=ball.getVitesse().getMul(1);
                        Vector3 v1v=info.ball.getVitesse().getMul(1);
                        Vector3 dv=v0v.getAdd(v1v);

                        Vector3 I = ball.getVitesse().getNormalize();
                        Vector3 R = Maths.reflect(I, info.normale,1);

                        float e0=ball.getVitesse().length()*ball.weight;
                        float e1=info.ball.getVitesse().length()*info.ball.weight;
                        float v0=ball.getVitesse().length();
                        float v1=info.ball.getVitesse().length();

                      /*  System.out.println(dv+"   "+info.normale);
                        ball.setVitesse(Maths.reflect(v0v,info.normale,0));
                        System.out.println(ball.getVitesse()+" "+Maths.reflect(dv.getMul(-1),info.normale,1f)+" "+(1+1)*info.normale.scalarProduct(dv));
                        ball.getVitesse().add(Maths.reflect(dv.getMul(-1),info.normale,1f));

                        info.ball.setVitesse(Maths.reflect(v1v,info.normale.getMul(-1),0));
                        System.out.println(info.ball.getVitesse()+" "+Maths.reflect(dv.getMul(-1),info.normale.getMul(-1),1f));
                        info.ball.getVitesse().add(Maths.reflect(dv.getMul(-1),info.normale.getMul(-1),1f));
*/
                        float ua=info.normale.dotProduct(v0v);
                        float ub=info.normale.dotProduct(v1v);
                   //     System.out.println(ua+" "+ub+" "+info.normale+"    "+ DisplayManager.totalSec);
                        float va=(C*info.ball.weight*(ub-ua)+info.ball.weight*ub+ball.weight*ua)/(info.ball.weight+ball.weight);
                        float vb=(C*ball.weight*(ua-ub)+info.ball.weight*ub+ball.weight*ua)/(info.ball.weight+ball.weight);

                        ball.getVitesse().sub(info.normale.getMul(ua));
                        info.ball.getVitesse().sub(info.normale.getMul(ub));

                        ball.getVitesse().add(info.normale.getMul(va));
                        info.ball.getVitesse().add(info.normale.getMul(vb));

                      /*  System.out.println(v0v+" "+ball.getVitesse()+"    "+v1v+" "+info.ball.getVitesse());
                        System.out.println((ball.getVitesse().squarelength()*ball.weight+info.ball.getVitesse().squarelength()*info.ball.weight)+"   "+(v0*v0*ball.weight+v1*v1*info.ball.weight));
                        System.out.println();*/
                    }

                }
            }
            float normeAir=COEFF_FRICTION*ball.getVitesse().squarelength()/ball.getWeight();
            ball.getVitesse().mul(1-normeAir*dt);

            ball.getVitesse().add(g.getMul(dt));
        }

        for (Ball ball: MainGame.ballPhysic) {
            ball.getPosition().add(ball.getVitesse().getMul(dt));
        }
    }

    private static float secondDegre(float a,float b,float c){
        float delta=b*b-4*a*c;
        System.out.println(a+" "+b+" "+c);
        if(delta<0){
            System.out.println("DELTA INF 0");
            return 0;
        }
        float x1=(-b-(float)Math.sqrt(delta))/(2*a);
        float x2=(-b+(float)Math.sqrt(delta))/(2*a);
        System.out.println(x1+" x "+x2);
        if(x1>x2 && x1>=0) return x1;
        return x2;
    }
}
