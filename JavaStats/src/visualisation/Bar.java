package visualisation;

import java.awt.*;

public class Bar {

    public static void bar(float[] x, float[] y, Color color, String legend){
        bar(x, y, -1, color, legend);
    }

    public static void bar(float[] x, int[] y, float width,Color color, String legend){
        float[] yfloat = new float[y.length];
        for (int i = 0; i < y.length; i++) {
            yfloat[i] = y[i];
        }
        bar(x, yfloat, width, color, legend);
    }

    public static void bar(float[] x, int[] y, Color color, String legend){
        bar(x, y, -1, color, legend);
    }

    public static void bar(float[] x, float[] y, float width, Color color, String legend){
        for (int i = 0; i < y.length; i++) {
            if(width>0) {
                GraphicShape rect = new GraphicShape(x[i], 0, x[i] + width, y[i], true, color, GraphicShape.RECTANGLE);
                Graph.figure.getShapes().add(rect);
            }else {
                GraphicShape rect = new GraphicShape(x[i], 0, x[i + 1], y[i], true, color, GraphicShape.RECTANGLE);
                Graph.figure.getShapes().add(rect);
            }
        }

        if(legend!= null && legend.length()>0){
            Graph.figure.addLegend(legend, color);
        }

    }
}
