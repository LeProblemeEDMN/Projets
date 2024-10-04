package Loader.LOD;

import java.util.List;

public class Polygon {
    private List<Triangle>trianglesList;
    private Point center;

    public Polygon( Point center) {
        this.trianglesList = center.triangles;
        this.center = center;
    }

    public void simplifyPolygon(List<Triangle>triangles){
        //REMOVE CENTER ET TRIANGLE;
        System.out.println(center.neighbours);
        this.center.reorderTriangle();
        System.out.println(center.pos);
        System.out.println(center.neighbours);
        for (int i = 0; i < center.neighbours.size()-1; i++) {
            //Triangle t=new Triangle(center.neighbours.get(i),center.neighbours.get(i+1),center.neighbours.get(i+2));
            Triangle t=new Triangle(center,center.neighbours.get(i),center.neighbours.get(i+1));
            triangles.add(t);
        }

    }

    public static Polygon createPolygon(Point center, List<Triangle>triangles){
        if(!(triangles.containsAll(center.triangles) && center.triangles.size()>=3))return null;

        Polygon p=new Polygon(center);
        triangles.removeAll(center.triangles);
        return p;
    }
}
