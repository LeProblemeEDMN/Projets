package Neuron.GA;

import Neuron.Dense;
import Neuron.NNetwork;
import Neuron.activationFunction.ReLu;
import Neuron.activationFunction.Sigmoid;
import Neuron.costFunction.MBCE;
import matrix.Matrix;

import java.util.Random;

public class MoveGame extends Population{

    private int size=10;

    private float flowPow=3f;

    @Override
    public NNetwork getNNetwork() {
        NNetwork network = new NNetwork();
       // network.add(new Dense(10, ReLu.RELU));
        network.add(new Dense(4, Sigmoid.SIGMOID));

        network.compile(MBCE.MBCE,4);
        return network;
    }
    private Random random = new Random();
    @Override
    protected float calculateFitnessnetwork(NNetwork network) {
        int turn =100;
        int apple = 100;
        int posx =2;
        int posy =2;
        int posax =5;
        int posay =5;
        int step=0;
        while (turn>0){
            Matrix input =new Matrix(4,1);
           // input.setValue(posax*size+posay,0,1);
            input.setValue(0,0,(float)posx/size);
            input.setValue(1,0,(float)posy/size);
            input.setValue(2,0,(float)posax/size);
            input.setValue(3,0,(float)posay/size);
            //input.setValue(posx*size+posy +size*size,0,1);
            Matrix[] arr = {input};
            Matrix prediction = network.predict(arr);
            int idMax =0;
            float max =prediction.getValue(0,0);
            for (int i = 1; i < 4; i++) {
                if(max < prediction.getValue(i,0)){
                    idMax = i;
                    max = prediction.getValue(i,0);
                }
            }
            if(idMax ==0)posx++;
            if(idMax ==1)posx--;
            if(idMax ==2)posy++;
            if(idMax ==3)posy--;

            //sort carte
            if(posx<0 || posx>=size || posy<0 || posy>=size){
                //System.out.println("sort " +((float)Math.pow((100-apple)*10,2)-step));
                return (float)(float)Math.pow((100-apple)*10,flowPow)-step;
            }

            if(posax == posx && posay == posy){
                apple--;
                turn+=30;
                if(apple==0)return 8000+ (float)Math.pow((100-apple)*10,flowPow)-step;
                posax = random.nextInt(size);
                posay = random.nextInt(size);
            }
            step++;
            turn--;
        }
        //fin de tour
        //System.out.println("fin tour "+apple+"  " +(1000 + (float)Math.pow((100-apple)*10,2)-step )+" ");
        return 1000 + (float)Math.pow((100-apple)*10,flowPow)-step;
    }

    @Override
    protected float[] calculateFitnessnetwork(NNetwork a, NNetwork b) {
        return new float[0];
    }
}
