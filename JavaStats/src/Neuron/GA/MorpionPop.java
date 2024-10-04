package Neuron.GA;

import Neuron.Dense;
import Neuron.NNetwork;
import Neuron.activationFunction.ReLu;
import Neuron.activationFunction.Sigmoid;
import Neuron.costFunction.MBCE;
import matrix.Matrix;

import java.util.Random;

public class MorpionPop extends  Population{

    @Override
    public NNetwork getNNetwork() {
        NNetwork network = new NNetwork();
        network.add(new Dense(14, ReLu.RELU));
        network.add(new Dense(9, Sigmoid.SIGMOID));

        network.compile(MBCE.MBCE,18);
        return network;
    }
    private Random random = new Random();
    @Override
    protected float[] calculateFitnessnetwork(NNetwork a, NNetwork b) {
   /*     int[][] map = new int [3][3];
        int caseRest = 9;
        int nbPoseA = 0;
        int nbPoseB = 0;
        boolean tourA = true;
        int nbTurn = 0;

        while (caseRest >0 && nbTurn<50){
            Matrix mat = new Matrix(18,1);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if(map[i][j] == 1)mat.setValue(i*3 + j,0, 1);
                    if(map[i][j] == -1)mat.setValue(i*3 + j + 9,0, 1);
                }
            }
            Matrix output;
            Matrix[] arr2={mat};
            if(tourA){
                output = a.predict(arr2);
            }else output = b.predict(arr2);

            int idMax =0;
            float max =output.getValue(0,0);
            for (int i = 1; i < 9; i++) {
                if(max < output.getValue(i,0)){
                    idMax = i;
                    max = output.getValue(i,0);
                }
            }
            int x = idMax/3;
            int y = idMax%3;

            if(map[x][y] == 0 && tourA){
                map[x][y] = 1;
                nbPoseA++;
                caseRest--;
            }else if(map[x][y] == 0){
                map[x][y] = -1;
                nbPoseB++;
                caseRest--;
            }

            float victoire = checkVictoire(map);
            if(victoire>=0){
                render(map);
                float[] scores = {1000-nbPoseA*10,10 *nbPoseB};
                if(victoire==0) {
                    scores[0] = nbPoseA*10;
                    scores[1] = 1000- 10 *nbPoseB;
                }
                return scores;
            }
            nbTurn++;
            tourA =!tourA;
        }
        float[] scores = {100,100};
        return scores;*/
        return null;
    }

    @Override
    protected float calculateFitnessnetwork(NNetwork network) {
        int[][] map = new int [3][3];
        int caseRest = 9;
        int nbPose = 0;
        boolean tourNN = true;
        int nbTurn = 0;
        while (caseRest >0 && nbTurn<50){
            if(tourNN){
                Matrix mat = new Matrix(18,1);
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if(map[i][j] == 1)mat.setValue(i*3 + j,0, 1);
                        if(map[i][j] == -1)mat.setValue(i*3 + j + 9,0, 1);
                    }
                }
                Matrix[] arr = {mat};
                Matrix output = network.predict(arr);
                int idMax =0;
                float max =output.getValue(0,0);
                for (int i = 1; i < 9; i++) {
                    if(max < output.getValue(i,0)){
                        idMax = i;
                        max = output.getValue(i,0);
                    }
                }
                int x = idMax/3;
                int y = idMax%3;

                if(map[x][y] == 0){
                    map[x][y] = 1;
                    nbPose++;
                    caseRest--;
                }

                tourNN = false;
            }else{
                //place aléatoirement
                boolean pose=false;
                while (!pose){
                    int x = random.nextInt(3);
                    int y = random.nextInt(3);
                    if(map[x][y] ==0){
                        map[x][y] = -1;
                        pose = true;
                    }
                }
                caseRest--;
                tourNN = true;
            }
            float vict = checkVictoire(map);
            if(vict>=0)render(map);
            if(vict>0)return vict-10*nbPose;
            if(vict==0)return 10*nbPose;

            nbTurn++;
        }

        return 100 +10*nbPose;
    }



    private float checkVictoire(int[][]map){
        boolean victoire =false;
        boolean fin= false;
        //check victoire
        for (int i = 0; i < 3; i++) {
            if((map[i][0] == map[i][1] && map[i][0] == map[i][2]) && map[i][0]!=0){
                fin = true;
                victoire = map[i][0] ==1;
            }
            else if((map[0][i] == map[1][i] && map[0][i] == map[2][i]) && map[0][i]!=0){
                fin = true;
                victoire = map[0][i] ==1;
            }
        }
        if(!fin){

            if(map[0][0] == map[1][1] &&map[0][0] == map[2][2] && map[0][0]!=0 ){
                fin = true;
                victoire = map[0][0] ==1;
            }else if(map[2][0] == map[1][1] &&map[2][0] == map[0][2] && map[2][0]!=0){
                fin = true;
                victoire = map[2][0] ==1;
            }
        }
        if(fin){
            return victoire? 1000: 0;
        }
        return -1;
    }

    private void render(int[][]map){
        for (int i = 0; i < 3; i++) {
            String line ="";
            for (int j = 0; j < 3; j++) {
                line += map[i][j] +" ";
            }
            System.out.println(line);
        }
        System.out.println();
    }
}
