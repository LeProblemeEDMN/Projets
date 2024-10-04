package Neuron;

import matrix.Matrix;
import stats.ACP;
import stats.ACPVisualiser;
import visualisation.Graph;
import visualisation.Plot;

import java.awt.*;
import java.util.List;

public class NetworkViz {

    public static float[][] populationToArray(List<Matrix> mats){
        float[][] array =new float[mats.get(0).getLin()][mats.size()];
        for (int i = 0; i < mats.size(); i++) {
            for (int j = 0; j < mats.get(0).getLin(); j++) {
                array[j][i] = mats.get(i).getValue(j,0);
            }
        }
        return array;
    }
    public static int[] labels(List<Matrix> mats,float seuil) {
        int[] lab = new int[mats.size()];
        for (int j = 0; j < mats.size(); j++) {
            Matrix pred = mats.get(j);
            float max = pred.getValue(0, 0);
            int idMax = 0;
            for (int i = 0; i < pred.getLin(); i++) {
                if (max < pred.getValue(i, 0)) {
                    max = pred.getValue(i, 0);
                    idMax = i;
                }
            }
            if (max > seuil) lab[j] = 1 + idMax;
            else lab[j] = 0;
        }
       return lab;
    }

    public static void ACP_datas(float[][] ind, int[] lab){
        ACP acp = new ACP();
        acp.fit(ind, true);
        ACPVisualiser.renderPoints(acp, ind,5,null,lab,2,3);

    }

    public static void renderLearning(float[] train, float[] test, String name){
        float[] x =new float[train.length];
        for (int i = 0; i < train.length; i++) {
            x[i] = i;
        }
        Plot.plot(x,train, Color.blue,name +" training");
        if(test != null) Plot.plot(x,test, Color.red,name +" test");

        Graph.showLegend(true);
        Graph.setTitle("Evolution of "+ name);
        Graph.figure.setLabelX("Epochs");
        Graph.figure.setLabelY(name);
        Graph.show();
    }
}
