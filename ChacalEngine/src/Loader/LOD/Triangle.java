package Loader.LOD;

import toolbox.maths.Vector3;

public class Triangle {
    private Point a,b,c;
    private float ax,bx,cx,ay,by,cy;
    private Vector3 an,bn,cn;

    public Triangle(Point a, Point b, Point c) {
        this.a = a;
        this.b = b;
        this.c = c;
        a.addNeighbours(b,c);
        b.addNeighbours(a,c);
        c.addNeighbours(a,b);
        a.triangles.add(this);
        b.triangles.add(this);
        c.triangles.add(this);
    }

    public void setAn(Vector3 an) {
        this.an = an;
        a.updateNorm(an);
    }

    public void setBn(Vector3 bn) {
        this.bn = bn;
        b.updateNorm(bn);
    }

    public void setCn(Vector3 cn) {
        this.cn = cn;
        c.updateNorm(cn);
    }

    public void setATexture(float x,float y){
        ax=x;
        ay=y;
        a.updateText(x,y);
    }

    public void setBTexture(float x,float y){
        bx=x;
        by=y;
        b.updateText(x,y);
    }

    public void setCTexture(float x,float y){
        cx=x;
        cy=y;
        c.updateText(x,y);
    }

    public Point getA() {
        return a;
    }

    public Point getB() {
        return b;
    }

    public Point getC() {
        return c;
    }
    public boolean containPoint(Point p){
        return p==a || p==b || p==c;
    }
}
