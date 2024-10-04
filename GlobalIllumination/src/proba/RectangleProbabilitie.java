package proba;

import rendering_raytracing.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RectangleProbabilitie {
    public List<Float> values = new ArrayList<>();
    public List<Float> height = new ArrayList<>();
    public void addValue(float v){
        values.add(v);
        values = values.stream().sorted().collect(Collectors.toList());
        height.clear();
        float tot=0;
        float t2=0;
        for (int i = 0; i < values.size()-1; i++) {
            float dx=values.get(i+1)-values.get(i);
            height.add(1/dx/(values.size()-1));
            tot+=height.get(i);
            //System.out.print(" "+height.get(i));
            t2+=dx*height.get(i);
        }
        //System.out.println(" Moy:"+tot/(values.size()-1)+" area:"+t2);
    }

    public float getErrorToUniform(float a,float b){
        float h=1/(b-a);
        float err=0;
        err+=h*(values.get(0)-a);
        for (int i = 0; i < values.size()-1; i++) {
            float dx=values.get(i+1)-values.get(i);
            err+=Math.abs(height.get(i)-1)*dx;
        }
        if(values.get(values.size()-1)<b)err+=h*(b-values.get(values.size()-1));
        return err;
    }

    public void render(Renderer renderer,float a,float b){
        float max = 0;
        max=height.stream().max(Float::compare).get();

        BufferedImage image = new BufferedImage(720,480,BufferedImage.TYPE_INT_RGB);
        Graphics2D g=image.createGraphics();
        g.fill(new Rectangle(0,0,720,480));

        g.setColor(Color.BLACK);
        for (int i = 0; i < values.size()-1; i++) {
            int xa=(int)(719*(values.get(i)-a)/(b-a));
            int xb=(int)(719*(values.get(i+1)-a)/(b-a));
            int y=(int)(479*height.get(i)/max);
            g.drawLine(xa,y,xb,y);
        }
        ImageIcon icon = new ImageIcon(image);
        renderer.lbl.setIcon(icon);
    }

}
