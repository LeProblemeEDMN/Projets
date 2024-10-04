package datas_structures;

public class Range {
    public static int[]range(int beg,int end,int step){

        int length=Math.abs((end-beg)/step);
        if(Math.abs((end-beg)%step)!=0)length++;
        int[] range=new int[length];
        int i=0;
        if(step>0) {
            while (beg + i * step < end) {
                range[i] = beg + i * step;
                i++;
            }
        } else if(step<0){
            while (beg + i * step > end) {
                range[i] = beg + i * step;
                i++;
            }
        }
        return range;
    }

    public static float[]range(float beg,float end,float step){

        int length=(int)Math.abs((end-beg)/step);
        if(Math.abs((end-beg)%step)!=0)length++;
        float[] range=new float[length];
        int i=0;
        if(step>0) {
            while (beg + i * step < end) {
                range[i] = beg + i * step;
                i++;
            }
        } else if(step<0){
            while (beg + i * step > end) {
                range[i] = beg + i * step;
                i++;
            }
        }
        return range;
    }
}
