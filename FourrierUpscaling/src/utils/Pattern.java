package utils;

public class Pattern {
    public int sx,sy;
    public int[] getEmpl(int id){
        return new int[]{id/sy,id%sy};
    }
    public int getId(int[] empl){
        return empl[0]*sy+empl[1];
    }
}
