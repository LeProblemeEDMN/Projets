package stats;

import datas_structures.DataColumn;
import datas_structures.DataFrame;

import java.nio.channels.FileLock;
import java.util.Arrays;

public class Stats {
    public static final int STATS_FLOATS=0,STATS_INTS=1,STATS_FLOATS_INTS=2;

    public static float median (float[]values){
        return percentile(values,0.5f)[0];
    }

    public static float Q1 (float[]values){
        return percentile(values,0.25f)[0];
    }

    public static float Q3 (float[]values){
        return percentile(values,0.75f)[0];
    }

    public static float std_dev(float[]values){
        float mean=mean(values);
        float var=0;
        int nbParcelle=0;
        for (int i = 0; i < values.length; i++) {
            if(!Float.isNaN(values[i])){
                var+=values[i]*values[i];
                nbParcelle++;
            }
        }
        var/=nbParcelle;
        var-=mean*mean;
        return (float)Math.sqrt(var);
    }

    public static float[] percentile(float[]values,float... percents){
        float[]valuesSort=values.clone();
        Arrays.sort(valuesSort);
        float[] percentile=new float[percents.length];
        for (int i = 0; i <percents.length; i++) {
            if(percents[i]>1 || percents[i]<0)percentile[i]= Float.NaN;
            else {
                int index=(int)(percents[i]*valuesSort.length);
                percentile[i]=valuesSort[Math.min(index,values.length-1)];
            }
        }
        return percentile;
    }

    public static float mean(float[]values){
        float tot=0;
        int nbVal=0;
        for (int i = 0; i < values.length; i++)
            if(!Float.isNaN(values[i])) {
                tot += values[i];
                nbVal++;
            }
        return tot/nbVal;
    }

    public float[]meanDataframe(DataFrame frame,boolean onLine,int type){
        return meanDataframe(frame, onLine, type,true);
    }
    /*
    onLine effectue action sur les lignes retourne un tableau du nombre de lignes
     */
    public static float[]meanDataframe(DataFrame frame,boolean onLine,int type,boolean ignoreNan){
        int length=onLine?frame.getNumber_line():frame.getNumber_col();
        boolean[] mask=new boolean[frame.getNumber_col()];
        for (int i = 0; i < frame.getNumber_col(); i++) {
            DataFrame.VAR_TYPE type_column=frame.getTypes_columns().get(i);
            mask[i]=((type==STATS_FLOATS || type==STATS_FLOATS_INTS) && type_column== DataFrame.VAR_TYPE.FLOAT) ||
                    ((type==STATS_INTS || type==STATS_FLOATS_INTS) && type_column== DataFrame.VAR_TYPE.INT);
        }

        float[] mean=new float[length];
        int[] number=new int[length];
        if(onLine){
            //mean des lignes
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < frame.getNumber_col(); j++) {
                    if(mask[j]) {
                        DataColumn column = frame.getColumn(j);
                        float value = column.getType() == DataFrame.VAR_TYPE.FLOAT ? column.getFloat(i) : column.getInt(i);

                        if (Float.isNaN(value) || value ==Integer.MAX_VALUE) {
                            if (!ignoreNan) {
                                //mean[i]+=0;
                                number[i]++;
                            }
                        } else {
                            mean[i] += value;
                            number[i]++;
                        }
                    }
                }
            }
        }else{
            //moyenne sur les colonnes boucle sur toutes
            for (int i = 0; i < length; i++) {
                //si dois faire le calcul sur celle ci
                if (mask[i]){
                    DataColumn column=frame.getColumn(i);
                    boolean isFLoat=column.getType()== DataFrame.VAR_TYPE.FLOAT;
                    //somme les valeurs en ignorant les NaN si demandé
                    for (int j = 0; j < frame.getNumber_line(); j++) {
                        float value=isFLoat?column.getFloat(j):column.getInt(j);

                        if(Float.isNaN(value) || value==Integer.MAX_VALUE){
                            if(!ignoreNan){
                                //mean[i]+=0;
                                number[i]++;
                            }
                        }else{
                            mean[i]+=value;
                            number[i]++;
                        }

                    }
                //si moyenne dois pas être faite
                }else mean[i]=Float.NaN;
            }
        }
        for (int i = 0; i < length; i++) {
            if(number[i]>0)mean[i]/=number[i];
            else mean[i]=Float.NaN;
        }
        return mean;
    }
}
