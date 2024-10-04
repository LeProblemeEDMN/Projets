package rendering_raytracing;

import entity.*;
import utils.Constants;
import utils.MathsUtils;


import java.awt.*;
import java.util.List;

public class Scene {
    public List<Light> lights;
    public List<Entity> entities;



    public Vector3 computeRay(Droite ray,int depth,boolean isInside){
        TriangleIntersection intersection=null;
        boolean insideFog = ray.O.y<50;

        while (intersection==null && insideFog) {
            double dist_fog=Fog.computeDistances();
            intersection = findNearestIntersection(ray,(float)dist_fog);

            //fog
            //check with box bb

            boolean move=intersection==null || intersection.t>dist_fog;
            if(move){
                //System.out.println(dist_fog);
                intersection=null;
                ray.O=ray.O.getAdd(ray.D.getMul((float) dist_fog));

                ray.D=Fog.direction_fog(ray.D);

            }
            insideFog = Math.abs(ray.O.y)<30 && ray.O.x>0;
        }


        if(intersection==null){
            Vector3 posToLightVec = lights.get(0).getRandomPosition().getSub(ray.O);
            posToLightVec.normalize();
            if(ray.D.dotProduct(posToLightVec)>Math.cos(Math.toRadians(5))){
                return new Vector3(lights.get(0).color).getMul(ray.D.dotProduct(posToLightVec));
            }
            //TODO soleil
            return new Vector3(Constants.SKY_COLOR);
        }

        Color c = intersection.e.getRGB(intersection.textureCoord);
        Vector3 color = new Vector3(c.getRed()/255f,c.getGreen()/255f,c.getBlue()/255f);
        Vector3 lightAccummulation = light(intersection);

        float coeff=intersection.e.material.newRayCoefficient;

        if(depth>0){
            if(Math.random()<intersection.e.material.t)
                ray = MathsUtils.newRay(intersection.getPoint(),materialReflection(ray.D,intersection.getTriangle().N,intersection.e.material));
            else {
                Vector3 dir = materialRefraction(ray.D, intersection.getTriangle().N, isInside ? 1.33f : 1, isInside ? 1 : 1.33f);
                if(dir!=null){
                    isInside=!isInside;
                    ray=MathsUtils.newRay(intersection.getPoint(), dir);
                }else{
                    ray = MathsUtils.newRay(intersection.getPoint(),materialReflection(ray.D,intersection.getTriangle().N,intersection.e.material));
                }
            }
            lightAccummulation.mul(1/(1+coeff));
            lightAccummulation.add(computeRay(ray,depth-1,isInside).getMul(coeff/(1+coeff)));
        }
        return color.mul(lightAccummulation);//.add(new Vector3(0.9f,0.9f,0.0f));
    }

    public Vector3 materialReflection(Vector3 I,Vector3 N,Material material){
        Vector3[] basis = MathsUtils.createOrthonormalBasis(MathsUtils.reflect(I,N),N);
        float phi = (float) (2*Math.random()-1)*material.getPhi_max();
        float theta = (float) (2*Math.PI*Math.random());
        Vector3 basisCoord= new Vector3(Math.cos(phi),Math.sin(phi)*Math.cos(theta),Math.sin(phi)*Math.sin(theta));
        return MathsUtils.changeBasis(basis,basisCoord);
    }

    public static Vector3 materialRefraction(Vector3 I,Vector3 N,float n1,float n2){
        float dot=I.dotProduct(N);
        if(dot>0){
            N=N.getMul(-1);
        }else{
            dot=-dot;
        }
        Vector3 ip=I.getSub(N.getMul(N.dotProduct(I)));
        double thetaBase = n1/n2*Math.sqrt(1-dot*dot);
        if(thetaBase>1 || thetaBase<-1)return null;
        double theta=Math.asin(thetaBase);
        Vector3 R = ip.getAdd(N.getMul((float) (Math.cos(theta)*ip.length()/thetaBase)));

        if(R.dotProduct(N)>0)R= MathsUtils.reflect(R,N);
        R.normalize();
        return R;
    }

    public Vector3 light(TriangleIntersection intersection){
        Vector3 lightAccummulation = new Vector3();
        for (Light l:lights){
            Vector3 posToLightVec = l.getRandomPosition().getSub(intersection.getPoint());
            float d=posToLightVec.length();
            posToLightVec.mul(1/d);//normalize
            if(d*d<l.maxRadiusSquared && findIfIntersection(MathsUtils.newRay(intersection.getPoint(),posToLightVec))==null){
                float attenuation = l.attenuation.x+l.attenuation.y*d+l.attenuation.z*d*d;
                float dot = Math.abs(intersection.getTriangle().N.dotProduct(posToLightVec));

                lightAccummulation.add(l.getColor().getMul(dot/attenuation));
            }
        }
        return lightAccummulation;
    }

    public TriangleIntersection findNearestIntersection(Droite d,float distMax){
        TriangleIntersection intersection=null;
        Entity entity=null;
        for (Entity e:entities) {
            TriangleIntersection i=e.intersect(d,intersection!=null?intersection.t:distMax,false);
            if(i!=null){
                intersection=i;
                entity=e;
            }
        }
        if(intersection==null) return null;
        intersection.e=entity;

        return intersection;
    }

    public TriangleIntersection findIfIntersection(Droite d){
        for (Entity e:entities) {
            TriangleIntersection i=e.intersect(d,999999,true);
            if(i!=null){
                return i;
            }
        }
        return null;
    }
}
