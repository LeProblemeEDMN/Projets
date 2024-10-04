package entity;

import loading.Loader;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BoundingBox {
    public static int total_bb=0;
    private static final int MAX_TRIANGLE_PER_BOX=20;
    public float minX,maxX,minY,maxY,minZ,maxZ;

    public List<Triangle> triangles=new ArrayList<>();
    public List<BoundingBox> bbs=new ArrayList<>();
    public boolean isFinalBox=true;

    public BoundingBox(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
        total_bb++;
    }
    public BoundingBox(Vector3 min,Vector3 max) {
        this.minX = min.x;
        this.maxX = max.x;
        this.minY = min.y;
        this.maxY = max.y;
        this.minZ = min.z;
        this.maxZ = max.z;
        total_bb++;
    }
        //plus lent
    public TriangleIntersection intersectTriangleV2(Droite d, float tmin, boolean exitIfIntersect){
        List<BoundingBox> boxes=new ArrayList<>();
        boxes.add(this);
        TriangleIntersection best=null;
        while (!boxes.isEmpty()){
            BoundingBox bb=boxes.get(0);
            if(bb.isFinalBox){
                for (Triangle triangle : bb.triangles) {
                    TriangleIntersection i = triangle.intersectTriangle(d, tmin);

                    if (i != null && i.t < tmin) {
                        if (exitIfIntersect) return i;
                        tmin = i.t;

                        best = i;
                    }
                }

            }else{
                boxes.addAll(bb.bbs.stream().filter(aabb->aabb.intersect(d.O,d.D)).collect(Collectors.toList()));
            }
            boxes.remove(0);
        }
        return best;
    }

    public TriangleIntersection intersectTriangle(Droite d, float tmin, boolean exitIfIntersect){
        if(isFinalBox){
            TriangleIntersection bestIntersection = null;
            for (Triangle triangle : triangles) {
                TriangleIntersection i = triangle.intersectTriangle(d, tmin);

                if (i != null && i.t < tmin) {

                    if (exitIfIntersect) return i;
                    tmin = i.t;

                    bestIntersection = i;
                }
            }
            return bestIntersection;
        }else{
            List<BoundingBox>hitbbs=bbs.stream().filter(bb->bb.intersect(d.O,d.D)).collect(Collectors.toList());
            TriangleIntersection bestIntersection=null;
            for(BoundingBox bb:hitbbs){
                TriangleIntersection i=bb.intersectTriangle(d,tmin,exitIfIntersect);
                if (i != null && i.t < tmin) {
                    if (exitIfIntersect) return i;
                    tmin = i.t;

                    bestIntersection = i;
                }
            }
            return bestIntersection;
        }
    }

    public void calculateBB(){
        if(triangles.size()<MAX_TRIANGLE_PER_BOX)return;
        isFinalBox=false;
        Vector3 middle =new Vector3(minX+maxX,minY+maxY,minZ+maxZ).mul(0.5f);
        bbs.add(new BoundingBox(new Vector3(minX,minY,minZ),middle));
        bbs.add(new BoundingBox(middle,new Vector3(maxX,maxY,maxZ)));
        bbs.add(new BoundingBox(new Vector3(middle.x,minY,minZ),new Vector3(maxX, middle.y, middle.z)));
        bbs.add(new BoundingBox(new Vector3(minX,middle.y,minZ),new Vector3(middle.x, maxY, middle.z)));
        bbs.add(new BoundingBox(new Vector3(middle.x,middle.y,minZ),new Vector3(maxX, maxY, middle.z)));
        bbs.add(new BoundingBox(new Vector3(middle.x,minY,middle.z),new Vector3(maxX, middle.y, maxZ)));
        bbs.add(new BoundingBox(new Vector3(minX,middle.y,middle.z),new Vector3(middle.x, maxY, maxZ)));
        bbs.add(new BoundingBox(new Vector3(minX,minY,middle.z),new Vector3(middle.x, middle.y, maxZ)));
        for (Triangle t:triangles) {
            for (int i = 0; i < bbs.size(); i++) {
                if(bbs.get(i).isInside(t.A)){
                    bbs.get(i).triangles.add(t);
                    break;
                }
            }
        }

        bbs.removeIf(t->t.triangles.size()==0);
        for(BoundingBox bb:bbs)bb.calculateBB();
        bbs.forEach(BoundingBox::updateMaxMinWithTriangles);
    }

    public boolean isInside(Vector3 v){
        return (v.x>=minX && v.x<=maxX)&&(v.y>=minY && v.y<=maxY) && (v.z>=minZ && v.z<=maxZ);
    }

    public void updateMaxMinWithTriangles(){
        Vector3 min=triangles.get(0).A.getMul(1);
        Vector3 max=triangles.get(0).A.getMul(1);
        triangles.stream().forEach(t-> Loader.getMaxAndMinTriangle(min,max,t.A,t.B,t.C));
        minX=min.x;
        maxX=max.x;
        minY=min.y;
        maxY=max.y;
        minZ=min.z;
        maxZ=max.z;
    }

    public boolean intersect(Vector3 ori, Vector3 dir){
        float txmin=-99999;
        float txmax=99999;
        if(dir.x!=0) {
            float t1 = (maxX - ori.x) / dir.x;
            float t2 = (minX - ori.x) / dir.x;
            txmin=Math.min(t1,t2);
            txmax=Math.max(t1,t2);
            if(txmax<0)return false;
        }else if(ori.x>maxX && ori.x<minX){
            return false;
        }

        float tymin=-99999;
        float tymax=99999;
        if(dir.y!=0) {
            float t1 = (maxY - ori.y) / dir.y;
            float t2 = (minY - ori.y) / dir.y;
            tymin=Math.min(t1,t2);
            tymax=Math.max(t1,t2);
            if(tymax<0)return false;
        }else if(ori.y>maxY && ori.y<minY){
            return false;
        }

        float tzmin=-99999;
        float tzmax=99999;
        if(dir.z!=0) {
            float t1 = (maxZ - ori.z) / dir.z;
            float t2 = (minZ - ori.z) / dir.z;
            tzmin=Math.min(t1,t2);
            tzmax=Math.max(t1,t2);
            if(tzmax<0)return false;
        }else if(ori.z>maxZ && ori.z<minZ){
            return false;
        }

        boolean xyInter=(txmin>tymin && txmin<tymax) || (tymin>txmin && tymin<txmax);
        boolean xzInter=(txmin>tzmin && txmin<tzmax) || (tzmin>txmin && tzmin<txmax);
        boolean zyInter=(tzmin>tymin && tzmin<tymax) || (tymin>tzmin && tymin<tzmax);
        return xyInter&&xzInter&&zyInter;
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "minX=" + minX +
                ", maxX=" + maxX +
                ", minY=" + minY +
                ", maxY=" + maxY +
                ", minZ=" + minZ +
                ", maxZ=" + maxZ +
                '}';
    }
}
