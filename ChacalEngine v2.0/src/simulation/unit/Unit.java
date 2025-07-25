package simulation.unit;

import toolbox.maths.Vector3;

import java.lang.annotation.Target;

public class Unit {
    private int maxModels,modelAlive;
    private Model[] model;
    private int numberRow,numberLines;

    private float positionUnit;
    private Vector3 target;
    private float[] pathPoints;
    private final float lookAhead=20;
    public void update(float dt){
        //purepursuit to find targetPoint

       /* positionUnit=0;
        modelAlive=0;
        for (int i = 0; i < maxModels; i++)
            if(model[i].isAlive()){
                model[i].update(dt,pathPoints[pathPoints.length-1]);
                positionUnit.add(model[i].getPosition());
                modelAlive++;
            }
        positionUnit.getMul(1.0f/modelAlive);
*/
    }
}
