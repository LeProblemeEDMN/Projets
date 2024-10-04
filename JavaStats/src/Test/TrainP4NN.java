package Test;

import Neuron.Dense;
import Neuron.NNetwork;
import Neuron.activationFunction.ReLu;
import Neuron.activationFunction.Sigmoid;
import Neuron.activationFunction.XAct;
import Neuron.convolution.Convolution;
import Neuron.convolution.Flatten;
import Neuron.costFunction.MSE;
import matrix.Matrix;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TrainP4NN {

    public static void train() throws IOException {
        List<Matrix[]>in = new ArrayList<>();
        List<Matrix> results = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(new File("res/training.txt")));
        String line = reader.readLine();
        while (line!=null){
            String[]parts = line.split(" ");
            Matrix map = new Matrix(7,6);
            map.setValues(createMap(stringToKey(parts[0])));
            in.add(new Matrix[]{map});
            int v= Integer.parseInt(parts[1]);
            int w= Integer.parseInt(parts[2]);

            Matrix r = new Matrix(1,1);
            r.setValue(0,0,(float)w/v);
            results.add(r);
            line = reader.readLine();
        }
        System.out.println(results.size());
        NNetwork convo = new NNetwork();

        convo.add(new Convolution(false, 1,1,8, ReLu.RELU));
        convo.add(new Convolution(false, 1,1,16, ReLu.RELU));
        convo.add(new Flatten());
        convo.add(new Dense(32, ReLu.RELU));
        convo.add(new Dense(1, Sigmoid.SIGMOID));
        convo.compile(MSE.MSE,7,6);

        convo=NNetwork.readNetwork("nn.txt");

        convo.getOptimizer().setBatch_size(32);
        convo.getOptimizer().setEpochs(10);
        convo.getOptimizer().setLearningRate(0.05f);

        convo.getOptimizer().train(in,results);
        convo.save("nn.txt");
    }

    public static int[] stringToKey(String part){
        String[] parts = part.split("/");
        if(parts.length!=7)return null;
        int[] i = new int[7];
        for (int j = 0; j < 7; j++) {
            i[j] = Integer.parseInt(parts[j]);
        }
        return i;
    }
    public static float[][] createMap(int[] mapKey){
        float[][] map = new float[7][6];

        for (int i = 0; i < 7; i++) {
            int column= mapKey[i];

            for (int j = 0; j < 6; j++) {
                map[i][j] = column%10 - 1;
                column/=10;
            }
        }
        return map;
    }
}
