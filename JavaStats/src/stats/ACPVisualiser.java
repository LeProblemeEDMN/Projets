package stats;

import datas_structures.Range;
import visualisation.Bar;
import visualisation.ColorsList;
import visualisation.Graph;
import visualisation.Scatter;

import java.awt.*;
import java.util.Arrays;

public class ACPVisualiser {

    public static String title_render_points = "Projection des individus";
    public static String title_variance_bar = "Diagramme des éboulis des valeurs propres";

    public static void renderPoints(ACP acp, float[][] ind, float size, String PATH_SAVE,Color color, int... axes){
        Color[] colors = new Color[ind[0].length];
        Arrays.fill(colors, color);
        renderPoints(acp, ind, size, PATH_SAVE, colors, axes);
    }

    public static void renderPoints(ACP acp, float[][] ind, float size, String PATH_SAVE,int[] classif, int... axes){
        Color[] colors = new Color[classif.length];
        for (int j = 0; j < classif.length; j++) {
            colors[j] = ColorsList.COLOR_LIST[classif[j]];
        }
        renderPoints(acp, ind, size, PATH_SAVE, colors, axes);
    }


    public static void renderPoints(ACP acp, float[][] ind, float size, String PATH_SAVE,Color[] classif, int... axes){
        for (int i = 0; i < axes.length/2; i++) {
            int planA = axes[2*i];
            int planB = axes[2*i + 1];
            Scatter.scatter(ind[planA], ind[planB], size, classif);

            Graph.setTitle(title_render_points);
            if(PATH_SAVE!= null && PATH_SAVE.length() > 0) Graph.saveFig(PATH_SAVE);
            Graph.figure.setLabelX("Axe F"+(planA+1) + " " + (float)((int)(1000 * acp.getPercentageVarianceExplained()[planA]))/10 + "%");
            Graph.figure.setLabelY("Axe F"+(planB+1) + " " + (float)((int)(1000 * acp.getPercentageVarianceExplained()[planB]))/10 + "%");

            Graph.show();
        }
    }

    public static void varianceBar(ACP acp){
        Graph.figure.setLabelX("Axes");
        Graph.figure.setLabelY("Variance");

        Graph.setTitle(title_variance_bar);

        float[] axes = Range.range(1.0f,acp.getPercentageVarianceExplained().length+2,1.0f);
        Bar.bar(axes,acp.getPercentageVarianceExplained(),Color.BLUE,"Valeur propres");

        Graph.show();
    }
}
