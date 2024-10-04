import GPU.GPUMatrix;
import GPU.NN.GPUDense;
import GPU.NN.GPUNetwork;
import GPU.NN.GPUNetworkComputation;
import GPU.MainCL;
import GPU.NN.GPUOptimizer;
import Neuron.Dense;
import Neuron.NNetwork;

import Neuron.activationFunction.ReLu;
import Neuron.activationFunction.Sigmoid;
import Neuron.activationFunction.XAct;
import Neuron.convolution.Convolution;
import Neuron.convolution.Flatten;
import Neuron.convolution.MaxPooling;
import Neuron.costFunction.MSE;
import Test.TrainP4NN;
import matrix.Matrix;
import matrix.MatrixHelper;
import optimisation.GradOptimizer;
import optimisation.Norm2Function;
import optimisation.Polynomial;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/*
matplot lib: subplot,show(),plot,bar,names,scattter
dataframe csv
fonction de base stat sur dataframe
ACP (QR)
lambda pr tri dataframe (bonus)
ttest kruskal-wallis test loi normale
generateur loi normale
test cluster (variance etc)
K-Means CAH
PLS-DA
regression linéaire
 */
public class main {

    public static Matrix createRDMMat(int l, int c,float max){
        Random random = new Random();
        Matrix A = new Matrix(l,c);
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < c; j++) {
                A.setValue(i,j,(random.nextFloat()-0.5f)*2*max);
            }
        }
        return A;
    }
    public static void main(String[]args) throws IOException{
        NNetwork convo = new NNetwork();
        convo.add(new Dense(1, ReLu.RELU));
        convo.compile(MSE.MSE,1);
        Random RDM=new Random();
        List<Matrix[]> inLines=new ArrayList<>();
        List<Matrix> outLines=new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            float v=RDM.nextFloat()*10;
        }
        
        List<Matrix[]> inTest=new ArrayList<>();
        List<Matrix> outTest=new ArrayList<>();
        List<Matrix[]> inTrain=new ArrayList<>();
        List<Matrix> outTrain=new ArrayList<>();
        for (int i = 0; i < test; i++) {
            int id=RDM.nextInt(inLines.size());
            inTest.add(inLines.get(id));
            outTest.add(outLines.get(id));
            outLines.remove(id);
            inLines.remove(id);
        }
        int s=inLines.size();
        for (int i = 0; i < s; i++) {
            int id=RDM.nextInt(inLines.size());
            inTrain.add(inLines.get(id));
            outTrain.add(outLines.get(id));
            outLines.remove(id);
            inLines.remove(id);
        }

    }
    public static void createDataset() throws IOException {

        int sampling_per_image=15;
        int sp=5;
        int sa=8;
        int decal=1;

        String path="D:/Images/2020-2021/";
        List<Matrix[]> inLines=new ArrayList<>();
        List<Matrix> outLines=new ArrayList<>();

        Random RDM=new Random();

        File dir=new File(path);
        for(File f:dir.listFiles()){
            BufferedImage img= ImageIO.read(f);

            if(f.getName().contains(".3gp"))continue;

            for (int i = 0; i < sampling_per_image; i++) {
                int x = RDM.nextInt(img.getWidth()-2*sp);
                int y = RDM.nextInt(img.getHeight()-2*sp);

                Matrix area=new Matrix(3*sa*sa,1);

                Matrix[] areaSmall=new Matrix[3];
                areaSmall[0]=new Matrix(sp,sp);areaSmall[1]=new Matrix(sp,sp);areaSmall[2]=new Matrix(sp,sp);
                for (int j = 0; j < 2*sp; j++) {
                    for (int k = 0; k < 2*sp; k++) {
                        Color c=new Color(img.getRGB(x+j,y+k));
                        areaSmall[0].setValue((j)/2,(k)/2,(c.getRed()/255f*0.25f));
                        areaSmall[1].setValue((j)/2,(k)/2,(c.getGreen()/255f*0.25f));
                        areaSmall[2].setValue((j)/2,(k)/2,(c.getBlue()/255f*0.25f));
                        if(j>=decal && j<=sa && k>=decal && k<=sa){
                            int i1 = 3 * (sa * (j - decal) + k - decal);
                            area.setValue(i1,0,c.getRed()/255f);
                            area.setValue(i1 +1,0,c.getGreen()/255f);
                            area.setValue(i1 +2,0,c.getBlue()/255f);
                        }
                    }
                }
                inLines.add(areaSmall);
                outLines.add(area);
            }
        }

        int test=(int)(0.15*inLines.size());
        System.out.println("FIN DATASET TAILLE:"+inLines.size());
        List<Matrix[]> inTest=new ArrayList<>();
        List<Matrix> outTest=new ArrayList<>();
        List<Matrix[]> inTrain=new ArrayList<>();
        List<Matrix> outTrain=new ArrayList<>();
        for (int i = 0; i < test; i++) {
            int id=RDM.nextInt(inLines.size());
            inTest.add(inLines.get(id));
            outTest.add(outLines.get(id));
            outLines.remove(id);
            inLines.remove(id);
        }
        int s=inLines.size();
        for (int i = 0; i < s; i++) {
            int id=RDM.nextInt(inLines.size());
            inTrain.add(inLines.get(id));
            outTrain.add(outLines.get(id));
            outLines.remove(id);
            inLines.remove(id);
        }

        NNetwork convo = new NNetwork();
        convo.add(new Convolution(true,1,1,32,ReLu.RELU));
        convo.add(new Convolution(true,1,1,64,ReLu.RELU));

        convo.add(new Flatten());
        convo.add(new Dense(6*sa*sa, ReLu.RELU));
        convo.add(new Dense(3*sa*sa, ReLu.RELU));

        convo.compile(MSE.MSE,sp,sp,3);
        //convo = NNetwork.readNetwork("res/carre.txt");
        //convo.getOptimizer().setUseAccuracy(0.5f);
        convo.getOptimizer().setLearningRate(0.05f);
        convo.getOptimizer().setMultiClassifAccuracy(false);
        convo.getOptimizer().setBatch_size(32);
        convo.getOptimizer().setEpochs(10);

        convo.getOptimizer().setTest(inTest, outTest);
        convo.getOptimizer().setRenderTraining(true);


        convo.getOptimizer().train(inTrain, outTrain);

        convo.getOptimizer().setLearningRate(0.015f);
        convo.getOptimizer().setEpochs(10);
        convo.getOptimizer().train(inTrain, outTrain);
        convo.save("res/update.txt");
    }
}
       /*
    Random rdm = new Random();
        int size = 5;
        for (int i = 0; i < 11000; i++) {
            float [][] values = new float[size][size];
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    values[j][k] = rdm.nextFloat();
                }
            }

            boolean haveSquare = rdm.nextBoolean();
            if(haveSquare){
                int length = rdm.nextInt((size-2))+2;
                int px = rdm.nextInt(size-length);
                int py = rdm.nextInt(size-length);
                for (int j = 0; j < length; j++) {
                    for (int k = 0; k < length; k++) {
                        values[j + px][k + py] += 2;
                    }
                }
            }else{
                for (int j = 0; j < rdm.nextInt(2 * size)+2; j++) {
                    int px = rdm.nextInt(size);
                    int py = rdm.nextInt(size);
                    values[px][py] += 2;
                }
            }

            Matrix in =new Matrix(size,size);
            in.setValues(values);

            Matrix[] array = {in};
            input.add(array);

            Matrix out =new Matrix(1,1);
            out.setValue(0,0,haveSquare? 1 :0);
            output.add(out);

        }
        //crée les donnée de test en enlevent les x dernier elements
        List<Matrix[]> inputTest = new ArrayList<>();
        List<Matrix> outputTest = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {

            inputTest.add(input.get(input.size() - 1));
            input.remove(input.size() - 1);
            outputTest.add(output.get(output.size() - 1));
            output.remove(output.size() - 1);
        }

        NNetwork convo = new NNetwork();
        convo.add(new Convolution(false,1,1,32,ReLu.RELU));
        convo.add(new Convolution(false,1,1,64,ReLu.RELU));
        convo.add(new MaxPooling(2,2));
        convo.add(new Flatten());
        convo.add(new Dense(15, ReLu.RELU));
        convo.add(new Dense(1, Sigmoid.SIGMOID));

        convo.compile(MSE.MSE,size,size);
        convo = NNetwork.readNetwork("res/carre.txt");
        convo.getOptimizer().setUseAccuracy(0.5f);
        convo.getOptimizer().setLearningRate(0.0f);
        convo.getOptimizer().setMultiClassifAccuracy(false);
        convo.getOptimizer().setBatch_size(32);
        convo.getOptimizer().setEpochs(10);

        convo.getOptimizer().setTest(inputTest, outputTest);
        convo.getOptimizer().setRenderTraining(true);


        convo.getOptimizer().train(input, output);
        convo.save("res/carre.txt");
        input.clear();
        output.clear();
        for (int i = 0; i < 10000; i++) {
            int a =Math.random()>=0.5?1:0;
            int b =Math.random()>=0.5?1:0;
            Matrix in = new Matrix(2,1);
            in.setValue(0,0,a);
            in.setValue(1,0,b);
            Matrix[] array = {in};
            input.add(array);

            Matrix out = new Matrix(1,1);
            out.setValue(0,0,a==b?1:0);
            output.add(out);
        }


    }
}
*/