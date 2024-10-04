package entity;

import org.lwjgl.util.vector.Vector2f;

public class Triangle {
    public Vector3 normale,A,B,C,NB,NC;
    public Vector2f AT,BT,CT;
    public Vector3 u,v,N;
    public float beta,gamma,alpha;
    public float m11,m12,m22,detm;

    public Triangle(Vector3 a,Vector3 b,Vector3 c){
        A = a;
        B = b;
        C = c;
        process();
        N.normalize();
    }
    public void process() {
        u=B.getSub(A);
        v=C.getSub(A);
        m11=u.squarelength();
        m12=u.dotProduct(v);
        m22=v.squarelength();
        detm=m11*m22-m12*m12;
        N=B.getSub(A).crossProduct(C.getSub(A));
    }
    public Triangle(Vector3 a,Vector3 b,Vector3 c,Vector3 N){
        A = a;
        B = b;
        C = c;
        process();
        N=normale;
        N.normalize();
    }

  /*  private boolean isInPlan(Vector3 p){
        return p.dotProduct(N)+d==0;
    }*/

  /*  public float intersect(Droite d){
        float div = d.D.dotProduct(N);
        if(div==0) return -99999;
        return -(this.d + d.O.dotProduct(N))/div;
    }*/

   /* public float isInTriangle(Droite d,float mint){
        float t = intersect(d);

        if(t>=mint || t<0)return -99999;

        Vector3 intersectPoint = new Vector3(d.O.x+d.D.x*t-origin.x,d.O.y+d.D.y*t-origin.y,d.O.z+d.D.z*t-origin.z);
        float u = intersectPoint.dotProduct(U)/ul2;
        if(u<0)return -99999;
        float v = intersectPoint.dotProduct(V)/vl2;

        if(v>0 && (v+u)<1)return t;
        return -99999;
    }*/

    public Vector3 intersect(Vector3 origin,Vector3 direction,float t) {
        Vector3 P=origin.getAdd(direction.getMul(t));
        Vector3 w=P.getSub(A);

        float b11=w.dotProduct(u);
        float b21=w.dotProduct(v);
        float detb=b11*m22-b21*m12;

        if(detb<=0 || detb>=detm)return null;

        //float g12=b11;
        //float g22=b21;
        //float detg=m11*g22-m12*g12;
        beta=detb/detm;
        gamma=(m11*b21-m12*b11)/detm;
        if(gamma<=0 || beta+gamma>=1)return null;

        alpha=1-beta-gamma;
        //if(alpha<=0 )return null;//|| alpha>=1
        return P;
    }

    public TriangleIntersection intersectTriangle(Droite d,float mint){
        /*float t = intersect(d);
        if(t>=mint || t<0)return null;

        Vector3 intersectPoint = new Vector3(d.O.x+d.D.x*t-origin.x,d.O.y+d.D.y*t-origin.y,d.O.z+d.D.z*t-origin.z);

        float u = intersectPoint.dotProduct(U)/ul2;
        if(u<max)return null;
        float v = intersectPoint.dotProduct(V)/vl2;

        if(v>max && (v+u)<1) {

            float cc=v;
            float bc=u;
            float ac=1-u-v;
            Vector2f textCoord = new Vector2f((textA.x*ac+textB.x*bc+textC.x*cc),(textA.y*ac+textB.y*bc+textC.y*cc));
            return new TriangleIntersection(t,textCoord,this,intersectPoint.getAdd(origin));
        }*/
        float t=C.getSub(d.O).dotProduct(N)/d.D.dotProduct(N);

        if(t>=mint || t<0)return null;

        Vector3 P=intersect(d.O,d.D,t);
        if(P!=null){
            Vector2f textureCoord=new Vector2f(BT.x*beta+CT.x*gamma+AT.x*alpha,BT.y*beta+CT.y*gamma+AT.y*alpha);
            return new TriangleIntersection(t,textureCoord,this,P);
        }

        return null;
    }

    public Vector3 getN() {
        return N;
    }

    public void setN(Vector3 n) {
        N = n;
    }

    public void setTextA(Vector2f AT) {
        this.AT = AT;
    }

    public void setTextB(Vector2f BT) {
        this.BT = BT;
    }

    public void setTextC(Vector2f CT) {
        this.CT = CT;
    }
}
