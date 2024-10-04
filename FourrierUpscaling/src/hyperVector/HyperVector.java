package hyperVector;

import java.util.Arrays;

public class HyperVector {
    public int[]dimension;
    public float[]values;
    public int[] sizevalue;

    public HyperVector(int[]d) {
        this.dimension=d;
        sizevalue=new int[d.length];
        int v=1;
        for (int i = 0; i < rg(); i++) {
            v*=this.dimension[i];
            this.sizevalue[i]=v;
        }
        this.values=new float[this.sizevalue[d.length-1]];
    }

    public boolean setValue(float v,int... dim){
        int idH=dim[0];
        for (int l = 0; l < rg()-1; l++){
            idH+=sizevalue[l]*dim[l+1];
        }
        values[idH]=v;
        return true;
    }

    public int rg(){
        return dimension.length;
    }

    public int[] getDimension() {
        return dimension;
    }

    public void setDimension(int[] dimension) {
        this.dimension = dimension;
    }

    public float[] getValues() {
        return values;
    }

    public void setValues(float[] values) {
        this.values = values;
    }

    public int getSizevalue() {
        return sizevalue[this.dimension.length-1];
    }

    @Override
    public String toString() {
        if(rg()==1){
            String lines="";
                for (int i = 0; i < dimension[0]; i++) {
                    lines+=formatNumber(values[i])+" ";
                }

            return lines+"\n";
        }
        if(rg()==2){
            String lines="";
            for (int j = 0; j < dimension[0]; j++) {
                for (int i = 0; i < dimension[1]; i++) {
                    lines+=formatNumber(values[j+dimension[0]*i])+" ";
                }
                lines+="\n";
            }
            return lines;
        }
        String lines="";
        int nbssmat=getSizevalue()/this.sizevalue[1];
        int[]di=new int[rg()-2];

        for (int i = 0; i < nbssmat; i++) {
            String d="(";
            for (int j = 0; j < rg()-2; j++) {
                d+=(di[j]+1)+",";
            }
            d+=")    ";
            lines+=complete(d,this.dimension[1]*5+4);

            di[0]++;
            for (int k = 0; k < rg()-2; k++) {
                if(di[k]>=this.dimension[k+2]){
                    di[k]-=this.dimension[k+2];
                    if(k+1<rg()-2)di[k+1]++;
                }
            }
        }
        lines+="\n";

         for (int l = 0; l < this.dimension[0]; l++) {

            di = new int[rg() - 2];
            di[0]=-1;
            for (int i = 0; i < nbssmat; i++) {
                di[0]++;
                for (int k = 0; k < rg() - 2; k++) {
                    if (di[k] >= this.dimension[k + 2]) {
                        di[k] -= this.dimension[k + 2];
                        if (k + 1 < rg() - 2) di[k + 1]++;
                    }
                }
                int place = l;
                for (int j = 0; j < rg() - 2; j++) {
                    place += di[j] * this.sizevalue[j+1];
                }
                for (int k = 0; k < this.dimension[1]; k++) {
                    int placec = place + k * this.sizevalue[0];
                    lines += formatNumber(values[placec]) + " ";
                }

                lines += "    ";
            }
            lines += "\n";
        }
        return lines;
    }

    public static String formatNumber(float f){
        String l=f+"";
        while (l.length()<4){
            l=" "+l;
        }
        return l.substring(0,4);
    }

    public static String complete(String f,int s){
        while (f.length()<s){
            f=" "+f;
        }
       return f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HyperVector that = (HyperVector) o;
        return Arrays.equals(dimension, that.dimension) &&
                Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(dimension);
        result = 31 * result + Arrays.hashCode(values);
        result = 31 * result + Arrays.hashCode(sizevalue);
        return result;
    }
}
