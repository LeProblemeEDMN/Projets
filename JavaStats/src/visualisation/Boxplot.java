package visualisation;

import stats.Stats;

import java.awt.*;
import java.util.Arrays;

public class Boxplot {
    //outliers

    public static void boxplot(float[]values){
        float[]specialVal= Stats.percentile(values,0.05f,0.25f,0.5f,0.75f,0.95f,0,1);
        GraphicShape rect=new GraphicShape(-20,specialVal[1],20,specialVal[3],false,Color.black,GraphicShape.RECTANGLE);
        Graph.figure.getShapes().add(rect);
        GraphicShape medianLine=new GraphicShape(-20,specialVal[2],20,specialVal[2],false,new Color(226,174,65),GraphicShape.LINE);
        Graph.figure.getShapes().add(medianLine);
        GraphicShape fiveLine=new GraphicShape(-20,specialVal[0],20,specialVal[0],false,Color.black,GraphicShape.LINE);
        Graph.figure.getShapes().add(fiveLine);
        GraphicShape ninetyfiveLine=new GraphicShape(-20,specialVal[4],20,specialVal[4],false,Color.black,GraphicShape.LINE);
        Graph.figure.getShapes().add(ninetyfiveLine);
        GraphicShape fiveTotwentyfive=new GraphicShape(0,specialVal[0],0,specialVal[1],false,Color.black,GraphicShape.LINE);
        Graph.figure.getShapes().add(fiveTotwentyfive);
        GraphicShape seventyfiveToninetyfive=new GraphicShape(0,specialVal[3],0,specialVal[4],false,Color.black,GraphicShape.LINE);
        Graph.figure.getShapes().add(seventyfiveToninetyfive);

        float intervalle=specialVal[6]-specialVal[5];
        for (int i = 0; i < values.length; i++) {
            if(values[i]<specialVal[0]|| values[i]>specialVal[4]){
                GraphicShape outlier=new GraphicShape(-1,values[i]-intervalle/100,1,values[i]+intervalle/100,false,Color.gray,GraphicShape.CIRCLE);
                Graph.figure.getShapes().add(outlier);
            }
        }

        Graph.figure.setMinX(-50);
        Graph.figure.setMaxX(50);
    }

    public Boxplot(){
        GraphicShape shape=new GraphicShape();
        shape.type=GraphicShape.RECTANGLE;
        shape.color= Color.CYAN;
        shape.fill=false;
        shape.dx=-30;
        shape.dy=0;
        shape.ex=30;
        shape.ey=200;

    }

}
