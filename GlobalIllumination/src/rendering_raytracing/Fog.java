package rendering_raytracing;

import entity.Vector3;

import java.util.Random;

public class Fog {
    public final static double R = 0.001;
    public final static double rho = 40000;
    public final static double nw = 1.333;

    public static final Random RANDOM_FOG = new Random();

    public static double simulate_y() {
        double r = RANDOM_FOG.nextDouble() * R * R;
        double y = Math.sqrt(r) * (RANDOM_FOG.nextInt(2)*2-1);
        return y;
    }

    public static double getParametersIncidence(){
        double y=simulate_y();
        double arccos=Math.acos(y/R);
        double A0=Math.PI-arccos;
        double thetam1=A0-Math.PI/2;
        double theta0=Math.asin(Math.sin(thetam1)/nw);
        double div=Math.sin(Math.abs(thetam1)+ Math.abs(theta0))+Math.pow(10,-30);
        double ry=Math.pow(Math.sin(Math.abs(thetam1)-Math.abs(theta0))/div,2);

        int N=computeN(ry);
        double DA = Math.PI * N - 2 * N * theta0 - thetam1;
        DA=DA-Math.floor(DA/(2*Math.PI));
        if(DA<0)DA+=2*Math.PI;
        return DA;
    }

    public static Vector3 direction_fog(Vector3 dir){
        double DA=getParametersIncidence();
        Vector3 newV=new Vector3(RANDOM_FOG.nextFloat(),RANDOM_FOG.nextFloat(),RANDOM_FOG.nextFloat());
        newV=newV.sub(dir.getMul(dir.dotProduct(newV)));
        newV.normalize();
        return newV.mul((float) Math.sin(DA)).add(dir.getMul((float) Math.cos(DA)));
    }

    public static int computeN(double ry){
        double prob=RANDOM_FOG.nextDouble();
        int N=(int)(Math.max(0,-Math.log(prob/ry)/Math.log(1-ry)));
        return N;
    }

    public static double computeDistances(){
        double lamb = Math.PI * R * R * rho;
        return -Math.log(1-RANDOM_FOG.nextDouble())/lamb;
    }

}
