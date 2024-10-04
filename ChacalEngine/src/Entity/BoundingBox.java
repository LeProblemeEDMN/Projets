package Entity;

import toolbox.maths.Vector3;

public class BoundingBox {
    public float minX,maxX,minY,maxY,minZ,maxZ;

    public boolean intersect(Vector3 ori,Vector3 dir){
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
}
