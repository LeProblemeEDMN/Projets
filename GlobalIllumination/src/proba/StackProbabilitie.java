package proba;

import rendering_raytracing.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StackProbabilitie {
    public List<Float> values = new ArrayList<>();
    public float[][] law = new float[nbVal][2];
    public float[][] error = new float[nbVal][2];
    private static int nbVal=1000;
    private static float l=0.1f;
    public float min,max;
    float a=1f;
    public void addValue(float v){
        values.add(v);
        values = values.stream().sorted().collect(Collectors.toList());
        l=(values.get(values.size()-1)-values.get(0))/6/(float) Math.log(values.size());
        if(Double.isNaN(l))l=0.1f;

        min=values.get(0)-l/2;
        max=values.get(values.size()-1)+l/2;
        float step = (max-min)/nbVal;
        int beginStep=0;
        int endStep=0;
        for (int i = 0; i < nbVal; i++) {
            law[i][0]=min+i*step;
            error[i][0]=min+i*step;
            while (endStep<values.size() && values.get(endStep)<law[i][0]+l/2)endStep++;
            while (beginStep<values.size() && values.get(beginStep)<law[i][0]-l/2)beginStep++;
            law[i][1]=(endStep-beginStep)/(l*values.size());
            for (int j = beginStep; j < endStep; j++) {
                error[i][1]+=a*(error[i][0]-values.get(j))/l;
            }
            error[i][1]/=Math.sqrt(endStep-beginStep);
            if(endStep-beginStep<1)error[i][1]=a;
        }
    }

    public float getErrorToUniform(float a,float b){
        float h=1/(b-a);
        float err=0;

        float dx=(max-min)/nbVal;
        for (int i = 0; i < nbVal; i++) {
            if(law[i][0]<a)
                err+=Math.abs(law[i][1])*dx;
            else if(law[i][0]>b)
                err+=Math.abs(law[i][1])*dx;
            else
                err+=Math.abs(law[i][1]-h)*dx;
        }
        return err;
    }

    public void render(Renderer renderer,float a,float b){
        float m = 0;
        for (int i = 0; i < nbVal; i++) {
            m=Math.max(m, law[i][1]);
        }
        //m=1;
        System.out.println(m);
        m*=1.1f;

        BufferedImage image = new BufferedImage(720,480,BufferedImage.TYPE_INT_RGB);
        Graphics2D g=image.createGraphics();
        g.fill(new Rectangle(0,0,720,480));


        for (int i = 0; i < nbVal-1; i++) {
            int xa=(int)(719*(law[i][0]-a)/(b-a));
            int xb=(int)(719*(law[i+1][0]-a)/(b-a));
            int y=(int)(479*law[i][1]/m);
            int ye=(int)(479*(law[i][1]+error[i][1])/m);
            g.setColor(Color.BLACK);
            g.drawLine(xa,y,xb,y);
            g.drawLine(xb,y,xb,(int)(479*law[i+1][1]/m));
            g.setColor(Color.red);
            g.drawLine(xa,ye,xb,ye);
        }
        g.setColor(Color.RED);
        g.drawLine(0,(int)(479/m/(b-a)),719,(int)(479/m/(b-a)));
        ImageIcon icon = new ImageIcon(image);
        renderer.lbl.setIcon(icon);
    }

}
