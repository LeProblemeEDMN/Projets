package visualisation;

import java.awt.*;

public class GraphicShape {
    public static final int RECTANGLE=1,CIRCLE=2,LINE=3;
    public float dx,dy,ex,ey;
    public boolean fill;
    public Color color;
    public int type;

    public GraphicShape() {
    }

    public GraphicShape(float dx, float dy, float ex, float ey, boolean fill, Color color, int type) {
        this.dx = dx;
        this.dy = dy;
        this.ex = ex;
        this.ey = ey;
        this.fill = fill;
        this.color = color;
        this.type = type;
    }

    @Override
    public String toString() {
        return "GraphicShape{" +
                "dx=" + dx +
                ", dy=" + dy +
                ", ex=" + ex +
                ", ey=" + ey +
                ", fill=" + fill +
                ", color=" + color +
                ", type=" + type +
                '}';
    }
}
