package hyperVector;

import java.util.Arrays;

public class HyperVecHelper {

    public static HyperVector mul(HyperVector h,float a,HyperVector out){
        float[] nv=new float[h.sizevalue[h.rg()-1]];
        for (int i = 0; i < h.sizevalue[h.rg()-1]; i++) {
            nv[i]=h.values[i]*a;
        }
        if (out!=null){
            out.setValues(nv);
        }
        HyperVector vector=new HyperVector(h.getDimension());
        vector.setValues(nv);
        return vector;
    }
    public static HyperVector add(HyperVector h,HyperVector v,HyperVector out){
        if (!Arrays.equals(h.dimension,v.dimension))return null;

        float[] nv=new float[h.sizevalue[h.rg()-1]];
        for (int i = 0; i < h.sizevalue[h.rg()-1]; i++) {
            nv[i]=h.values[i]+v.values[i];
        }
        if (out!=null){
            out.setValues(nv);
        }
        HyperVector vector=new HyperVector(h.getDimension());
        vector.setValues(nv);
        return vector;
    }

    public static HyperVector mul(HyperVector h,HyperVector v){
        int[]dimension=new int[h.rg()+v.rg()-2];
        for (int i = 0; i < h.rg()-1; i++) dimension[i]=h.dimension[i];
        for (int i = 1; i < v.rg(); i++) dimension[i+h.rg()-2]=v.dimension[i];

        HyperVector c=new HyperVector(dimension);
        float[] cv=new float[c.getSizevalue()];

        int[]dh=new int[h.rg()-1];
        dh[0]=-1;
        for (int i = 0; i < h.sizevalue[h.rg()-2]; i++) {
            int[]dv=new int[v.rg()-1];
            dv[0]=-1;
            dh[0]++;
            for (int k = 0; k < h.rg()-1; k++) {
                if(dh[k]>=h.dimension[k]){
                    dh[k]-=h.dimension[k];
                    if(k+1<h.rg()-1)dh[k+1]++;
                }
            }

            for (int j = 0; j < v.sizevalue[v.rg()-1]/v.sizevalue[0]; j++) {
                dv[0]++;
                for (int k = 0; k < v.rg()-1; k++) {
                    if(dv[k]>=v.dimension[k+1]){
                        dv[k]-=v.dimension[k+1];
                        if(k+1<v.rg()-1)dv[k+1]++;
                    }
                }
                int idT=dh[0];
                int idH=dh[0];
                int idV=0;
                for (int l = 0; l < h.rg()-2; l++){
                    idT+=c.sizevalue[l]*dh[l+1];
                    idH+=h.sizevalue[l]*dh[l+1];
                }
                for (int l = 0; l < v.rg()-1; l++){
                    idT+=c.sizevalue[l+h.rg()-2]*dv[l];
                    idV+=v.sizevalue[l]*dv[l];
                }

                float value=0;
                for (int k = 0; k < v.dimension[0]; k++) {
                   // System.out.println(idV+" "+k);
                    //System.out.println(idH+"     "+h.sizevalue[h.rg()-2]+" "+k+" ");
                    value+=h.getValues()[idH+h.sizevalue[h.rg()-2]*k]*v.getValues()[idV+k];
                }
                cv[idT]=value;
            }
        }
        c.setValues(cv);
        return c;
    }

    public static HyperVector transform(HyperVector h,HyperVector v){
        int[]dimension=new int[h.rg()-1];
        for (int i = 0; i < h.rg()-1; i++) dimension[i]=h.dimension[i];

        HyperVector c=new HyperVector(dimension);
        float[] cv=new float[c.getSizevalue()];

        int[]dh=new int[h.rg()-1];
        for (int i = 0; i < h.sizevalue[h.rg()-2]; i++) {
            dh[0]++;
            for (int k = 0; k < h.rg()-1; k++) {
                if(dh[k]>=h.dimension[k+1]){
                    dh[k]-=h.dimension[k+1];
                    if(k+1<h.rg()-1)dh[k+1]++;
                }
            }

                int idT=dh[0];
                int idH=dh[0];

                for (int l = 0; l < h.rg()-2; l++){
                    idT+=c.sizevalue[l]*dh[l+1];
                    idH+=h.sizevalue[l]*dh[l+1];
                }

                float value=0;
                for (int k = 0; k < v.dimension[0]; k++) {
                    value+=h.getValues()[idH+h.sizevalue[h.rg()-2]*k]*v.getValues()[k];
                }
                cv[idT]=value;
            }

        c.setValues(cv);
        return c;
    }
    public static HyperVector generateI(int dim,int size){
        int[] sizeDim=new int[dim];
        Arrays.fill(sizeDim,size);
        HyperVector I=new HyperVector(sizeDim);
        float[] values=new float[I.sizevalue[dim-1]];
        for (int i = 0; i < size; i++) {
            int id=i;
            for (int j = 1; j < dim; j++) {
                id+=I.sizevalue[j-1]*i;
            }
            values[id]=1;
        }
        I.setValues(values);
        System.out.println(Arrays.toString(values));
        return I;
    }
}
