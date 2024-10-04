package optimisation;

import java.util.ArrayList;
import java.util.List;

public class Polynomial {
    public static double EPS=0.000001;
    public static List<Double> find_root(double[] P){
        if(P.length<=1){
            return new ArrayList<>();
        }
        List<Double> deriv_roots=find_root(derivate(P));
        //System.out.println(deriv_roots);
        double[] intervals=new double[deriv_roots.size()+2];

        intervals[0]=-99999;
        for (int i = 0; i < deriv_roots.size(); i++)  intervals[1+i] = deriv_roots.get(i);
        intervals[intervals.length-1]=99999;

        List<Double> roots=new ArrayList<>();
        double minvalue =evaluate(P,intervals[0]);

        for (int i = 1; i < intervals.length; i++) {

            double v_max=evaluate(P,intervals[i]);

            if(Math.abs(v_max)<EPS){
                roots.add(intervals[i]);
                continue;
            }

            if((v_max>0 && minvalue>0) || (v_max<0 && minvalue<0)){
                minvalue=v_max;
                continue;
            }

            boolean increase=minvalue<0;

            //dichotomie
            double min=intervals[i-1];
            double max= intervals[i];
            while (max-min > EPS){
                double center = (min + max) / 2;
                double eval=evaluate(P,center);

                if(increase){
                    if(eval<0)min=center;
                    else max=center;
                }else{
                    if(eval<0)max=center;
                    else min=center;
                }
            }
            minvalue=v_max;
            roots.add((min+max)/2);
        }

        return roots;
    }

    public static double evaluate(double[] P,double x){
        double v=0;
        double pow=1;
        for (int i = 0; i < P.length; i++) {
            v+=pow*P[i];
            pow*=x;
        }
        return v;
    }
    public static double[] derivate(double[] P){
        double[] dP=new double[P.length-1];
        for (int i = 0; i < dP.length; i++) {
            dP[i]=P[i+1]*(i+1);
        }
        return dP;
    }
}
