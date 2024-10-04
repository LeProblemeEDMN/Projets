package visualisation;

import java.awt.*;

public class Histrogram2D {

    private static Color caseColor(float fullness){
        return new Color(fullness*0.9f+0.1f,0.1f,0.1f);
    }
    public static void render(Histrogram2D hist){
        render(hist.getSamples_lim_x(), hist.getSamples_lim_y(), hist.getNumbers_ind(), hist.getMax());
    }
    public static void render(float[]lims_x,float[]lims_y,int[][] nb_ind,int max){
        for (int i = 0; i < lims_x.length-1; i++) {
            for (int j = 0; j < lims_y.length-1; j++) {
                GraphicShape colorRect=new GraphicShape(lims_x[i],lims_y[j],lims_x[i + 1],lims_y[j + 1],true,caseColor(Math.min(1,(float)nb_ind[i][j]/max)),GraphicShape.RECTANGLE);
                Graph.figure.getShapes().add(colorRect);
            }
        }
    }


    public int[][] numbers_ind;
    public float[] samples_lim_x,samples_lim_y;
    public int max,nb_samples_x,nb_samples_y;

    public  Histrogram2D(int nb_samples_x, int nb_samples_y) {
        this.nb_samples_x = nb_samples_x;
        this.nb_samples_y = nb_samples_y;
    }

    public void compute(float[] x,float[]y){
        samples_lim_x=new float[nb_samples_x+1];
        samples_lim_y=new float[nb_samples_y+1];
        numbers_ind=new int[nb_samples_x][nb_samples_y];

        float maxX=-9999;
        float minX=9999;
        for (float xi:x) {
            maxX=Math.max(xi,maxX);
            minX=Math.min(xi,minX);
        }

        float maxY=-9999;
        float minY=9999;
        for (float yi:y) {
            maxY=Math.max(yi,maxY);
            minY=Math.min(yi,minY);
        }
        float intervalleX=maxX-minX;
        maxX+=intervalleX*0.001;
        minX-=intervalleX*0.001;
        intervalleX=maxX-minX;

        float intervalleY=maxY-minY;
        maxY+=intervalleY*0.001;
        minY-=intervalleY*0.001;
        intervalleY=maxY-minY;

        float step_x=intervalleX/nb_samples_x;
        float step_y=intervalleY/nb_samples_y;

        for (int i = 0; i < nb_samples_x; i++) samples_lim_x[i]=minX+i*step_x;
        samples_lim_x[nb_samples_x]=maxX;

        for (int i = 0; i < nb_samples_y; i++) samples_lim_y[i]=minY+i*step_y;
        samples_lim_y[nb_samples_y]=maxY;

        for (int i = 0; i < x.length; i++) {
            int id_x=(int)((x[i]-minX)/step_x);
            int id_y=(int)((y[i]-minY)/step_y);
            numbers_ind[id_x][id_y]++;
        }
        max=0;
        for (int i = 0; i < nb_samples_x; i++)
            for (int j = 0; j < nb_samples_y; j++) {
                max= Math.max(max,numbers_ind[i][j]);
            }
    }

    public int getMax() {
        return max;
    }

    public float[] getSamples_lim_x() {
        return samples_lim_x;
    }

    public float[] getSamples_lim_y() {
        return samples_lim_y;
    }

    public int[][] getNumbers_ind() {
        return numbers_ind;
    }

    public int getNb_samples_x() {
        return nb_samples_x;
    }

    public int getNb_samples_y() {
        return nb_samples_y;
    }
}
