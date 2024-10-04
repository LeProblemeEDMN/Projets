package visualisation;

import java.awt.*;


public class Plot {
    public static void plot(float[]x, float[]y,Color color){
        plot(x,y,color,null);
    }
    public static void plot(float[]x, float[]y,Color color,String legend){
        if(x.length!=y.length){
            System.err.println("X and y have not the same length");
        }

        for (int i = 0; i < x.length-1; i++) {
            GraphicShape line=new GraphicShape(x[i],y[i],x[i+1],y[i+1],true,color,GraphicShape.LINE);
            Graph.figure.getShapes().add(line);
        }

        if(legend!= null && legend.length()>0){
            Graph.figure.addLegend(legend, color);
        }
    }
    public static void plot(float[][]x, float[][]y,Color[] color){
        if(x.length!=y.length){
            System.err.println("X and y have not the same length");
        }

        for (int i = 0; i < x.length-1; i++) {
            plot(x[i],y[i],color[i]);
        }
    }

    public static void plot(float[][]x, float[][]y,Color[] color,String[] legends){
        if(x.length!=y.length){
            System.err.println("X and y have not the same length");
        }

        for (int i = 0; i < x.length-1; i++) {
            plot(x[i],y[i],color[i],legends[i]);
        }
    }
}
