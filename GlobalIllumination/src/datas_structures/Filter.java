package datas_structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Filter {
    public static final int SUPERIOR=0,INFERIOR=1,EQUALS=2, SUPERIOR_EQUALS=3, INFERIOR_EQUALS=4;
    public static int[]compareString(DataFrame frame,String column_name,String key, boolean ignoreCase){
        return compareString(frame.getColumn(column_name),key,ignoreCase);
    }

    public static int[] not(int[]a,int range){
        a=a.clone();
        Arrays.sort(a);
        List<Integer>notA=new ArrayList<>();
        int indexA=0;
        for (int i = 0; i < range; i++) {
            while (indexA<a.length && a[indexA]<i)indexA++;
            if(indexA>=a.length || a[indexA]!=i)notA.add(i);
        }
        int[] result=new int[notA.size()];
        for (int i = 0; i < notA.size(); i++) {
            result[i] = notA.get(i);
        }
        return result;
    }

    public static int[]compareString(DataColumn column,String key, boolean ignoreCase){
        if(column.getType()!= DataFrame.VAR_TYPE.STRING){
            System.err.println("Column "+column.getColumnName()+" can't be compare with a string");
            return null;
        }
        List<Integer>lines=new ArrayList<>();
        for (int i = 0; i < column.getLength(); i++) {
            if(ignoreCase && key.equalsIgnoreCase(column.getString(i)))lines.add(i);
            else if(key.equals(column.getString(i)))lines.add(i);
        }
        int[] result=new int[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            result[i] = lines.get(i);
        }
        return result;
    }

    public static int[]compare(DataFrame frame,String column_name,float seuil, int type){
        return compare(frame.getColumn(column_name),seuil,type);
    }
    public static int[]compare(DataColumn column,float seuil, int type){
        List<Integer>lines=new ArrayList<>();
        boolean isFloat=column.getType()== DataFrame.VAR_TYPE.FLOAT;
        boolean isInt=column.getType()== DataFrame.VAR_TYPE.INT;
        for (int i = 0; i < column.getLength(); i++) {
            float value=isFloat?column.getFloat(i) : (isInt? column.getInt(i) : column.getString(i).length());
            if(SUPERIOR==type && value>seuil)lines.add(i);
            else if(INFERIOR==type && value<seuil)lines.add(i);
            else if(EQUALS==type && value==seuil)lines.add(i);
            else if(SUPERIOR_EQUALS==type && value>=seuil)lines.add(i);
            else if(INFERIOR_EQUALS==type && value<=seuil)lines.add(i);
        }
        int[] result=new int[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            result[i] = lines.get(i);
        }
        return result;
    }

    public static int[]and(int[] a,int[]b){
        List<Integer>finalList=new ArrayList<>();
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if(a[i]==b[j]){
                    finalList.add(a[i]);
                    break;
                }
            }
        }
        int[] result=new int[finalList.size()];
        for (int i = 0; i < finalList.size(); i++) {
            result[i] = finalList.get(i);
        }
        return result;
    }
    public static int[]or(int[] a,int[]b){
        List<Integer>finalList=new ArrayList<>();
        for (int j = 0; j < b.length; j++) finalList.add(b[j]);

        for (int i = 0; i < a.length; i++) {
            if(!finalList.contains(a[i]))finalList.add(a[i]);
        }
        int[] result=new int[finalList.size()];
        for (int i = 0; i < finalList.size(); i++) {
            result[i] = finalList.get(i);
        }
        return result;
    }
}
