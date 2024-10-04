package Loader.LOD;

import toolbox.maths.Vector3;

import java.util.ArrayList;
import java.util.List;

public class Point {
    public Vector3 pos,norm=new Vector3();
    public int normWeight=0;
    public List<Point>neighbours=new ArrayList<>();
    public List<Triangle>triangles=new ArrayList<>();
    public float tx,ty;
    public int textureWeight=0;
    private boolean isLock=false;
    private int id;

    public Point(Vector3 pos,int id) {
        this.id=id;
        this.pos = pos;
    }

    public void addNeighbours(Point a,Point c){
        if(!neighbours.contains(a))neighbours.add(a);
        if(!neighbours.contains(c))neighbours.add(c);
    }

    public void updateNorm(Vector3 norm){
        if(!isLock()) {
            this.norm = norm.getAdd(this.norm.getMul(normWeight));
            this.norm.getMul(1 / (normWeight+1));
            normWeight++;
        }
    }
    public void updateText(float x,float y){
        if(!isLock()) {
            this.tx=(this.tx*textureWeight+x)/(textureWeight+1);
            this.ty=(this.ty*textureWeight+y)/(textureWeight+1);
            textureWeight++;
        }
    }

    public void reorderTriangle(){
        List<Triangle>ts=new ArrayList<>();
        List<Point>orderPoints=new ArrayList<>();
        ts.addAll(triangles);
        Triangle t=ts.get(0);
        Point p=t.getA();
        Point p3=t.getB();
        if(t.getA()==this){
            p=t.getB();
            p3=t.getC();
        }else if(t.getB()==this){
            p3=t.getC();
        }
       // orderPoints.add(p3);
        orderPoints.add(p);
        ts.remove(0);
        while (ts.size()>0){
            boolean find=false;
            for (int i = 0; i < ts.size(); i++) {
                Triangle t2=ts.get(i);
                if(t2.containPoint(p)){
                    find=true;
                    ts.remove(i);
                    Point p2=t.getA();
                    if(p2==this || p2==p){
                        p2=t.getB();
                        if(p2==this || p2==p){
                            p2=t.getC();
                        }
                    }
                    p=p2;
                    break;
                }
            }
            if(!find){
                Triangle t2=ts.get(0);
                p=t2.getA();
                if(t2.getA()==this){
                    p=t2.getB();
                }
            }
            ts.remove(0);
            orderPoints.add(p);
        }
        neighbours=orderPoints;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public Vector3 getPosition() {
        return pos;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Point{" +
                "pos=" + pos +
                '}';
    }
}
