package visualisation;

import java.awt.*;
import java.util.Arrays;

public class Scatter {
    public static void scatter(float[] x, float[] y, float size, Color color){
        Color[] colors = new Color[x.length];
        Arrays.fill(colors, color);
        scatter(x, y, size, colors);
    }

    public static void scatter(float[] x, float[] y, float size, Color[] colors){
        if(x.length!=y.length){
            System.err.println("X and y have not the same length");
        }

        float maxX = -999;
        float maxY = -999;
        float minX = 999;
        float minY = 999;
        for (int i = 0; i < x.length; i++) {
            maxX = Math.max(maxX, x[i]);
            minX = Math.min(minX, x[i]);
            maxY = Math.max(maxY, y[i]);
            minY = Math.min(minY, y[i]);
        }

        float rx = (maxX - minX) / 200 * size;//moitié du rayon
        float ry = (maxY - minY) / 200 * size;

        for (int i = 0; i < x.length; i++) {
            GraphicShape circle = new GraphicShape(x[i] - rx,y[i] - ry, x[i] + rx,y[i] + ry,true,colors[i],GraphicShape.CIRCLE);
            Graph.figure.getShapes().add(circle);
        }
    }
}
