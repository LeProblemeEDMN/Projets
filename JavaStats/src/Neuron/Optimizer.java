package Neuron;

import Neuron.costFunction.CostFunction;
import matrix.Matrix;

import java.util.List;

public class Optimizer {

    private NNetwork network;

    private CostFunction costFunction;
    private int batch_size = 1;
    private int epochs = 1;
    private int step_per_epochs = -1;

    private float learningRate = 0.01f;

    private boolean multiClassifAccuracy = true;
    private float probaMin;

    private boolean useAccuracy = false;

    private List<Matrix[]> test_set_input;
    private List<Matrix> test_set_output;

    private float [] accTest,accTrain, lossTests,lossTrain;

    private boolean renderTraining = false;

    public Optimizer(NNetwork network, CostFunction costFunction) {
        this.network = network;
        this.costFunction = costFunction;
    }

    public void train(List<Matrix[]> input, List<Matrix> output) {
        int step = step_per_epochs;
        if(step<=0)step = input.size() / batch_size;

        //init les donnée pr viz
        lossTests = new float[epochs];
        lossTrain = new float[epochs];
        if(useAccuracy){
            accTest = new float[epochs];
            accTrain = new float[epochs];
        }

        int length = input.size();
        for (int i = 0; i < epochs; i++) {
            float loss = 0;
            float acc = 0;
            for (int j = 0; j < step; j++) {
                float lossBatch = 0;
                int accuracyTot =0;
                for (int k = 0; k < batch_size; k++) {
                    int idMat = (i * step * batch_size + j * batch_size + k) % length;
                    Matrix prediction = network.predict(input.get(idMat));

                    if(useAccuracy){
                        if(multiClassifAccuracy) accuracyTot += isGoodPredMulti(prediction, output.get(idMat).toVector()) ? 1:0;
                        //check que l'élément predit est du meme cote du seuil que l'element réel
                        else  accuracyTot += (prediction.getValue(0, 0)>probaMin && output.get(idMat).getValue(0, 0) > probaMin) ||
                                (prediction.getValue(0, 0)<probaMin && output.get(idMat).getValue(0, 0) < probaMin)  ? 1:0;
                    }
                    lossBatch += costFunction.loss(prediction.toVector(), output.get(idMat).toVector());
                    Matrix gradientLossCost = costFunction.gradientLoss(prediction.toVector(), output.get(idMat).toVector());
                    Matrix[] gradientLoss = {gradientLossCost};

                    for (int l = network.getLayers().size() - 1; l > 0; l--) {
                        gradientLoss = network.getLayers().get(l).backwardPropagation(gradientLoss);
                    }
                }
                for (int l = 1; l < network.getLayers().size(); l++) {
                    network.getLayers().get(l).updateLayer(-learningRate / batch_size);
                }
                loss += lossBatch / batch_size;
                acc += (float)accuracyTot/batch_size;
            }
            //sauvegarde pour viz
            lossTrain[i] = loss / step;
            if(useAccuracy)accTrain[i] = acc / step;

                //affiche les info de l'epoch
            System.out.print("Epoch: " + i + " Loss: " + loss / step);
            if(useAccuracy) System.out.print(" Accuracy:" +acc / step);
            //verif la loss et accuracy du test
            if(test_set_output != null){
                float lossTest = 0;
                int accuracyTest =0;
                for (int j = 0; j < test_set_input.size(); j++) {
                    Matrix prediction = network.predict(test_set_input.get(j));
                    lossTest += costFunction.loss(prediction.toVector(), test_set_output.get(j).toVector());
                    //fais l'accuracy si besoin est
                    if(useAccuracy){
                        if(multiClassifAccuracy) accuracyTest += isGoodPredMulti(prediction, test_set_output.get(j).toVector()) ? 1:0;
                        //check que l'élément predit est du meme cote du seuil que l'element réel
                        else  accuracyTest += (prediction.getValue(0, 0)>probaMin && test_set_output.get(j).getValue(0, 0) > probaMin) ||
                                (prediction.getValue(0, 0)<probaMin && test_set_output.get(j).getValue(0, 0) < probaMin)  ? 1:0;
                    }
                }
                lossTests[i] = lossTest / test_set_input.size();
                if(useAccuracy)accTest[i] = accuracyTest / step;

                System.out.print(" Test Loss: " + lossTest / test_set_input.size());
                if(useAccuracy) System.out.print(" Test Accuracy:" +(float)accuracyTest / test_set_input.size());
            }
            //retoru à la ligne de la finde l'epoch
            System.out.println();
        }

        if(renderTraining){
            NetworkViz.renderLearning(lossTrain,lossTests,"loss");
            if(useAccuracy)NetworkViz.renderLearning(accTrain,accTest,"accuracy");
        }
    }

    public void setTest(List<Matrix[]> input,List<Matrix> output){
        this.test_set_input = input;
        this.test_set_output = output;
    }

    private boolean isGoodPredMulti(Matrix pred, Matrix real){
        int idMax =0;
        float max = pred.getValue(0,0);
        for (int i = 0; i < pred.getLin(); i++) {
            if(max < pred.getValue(i,0)){
                max = pred.getValue(i,0);
                idMax = i;
            }
        }

        for (int i = 0; i < real.getLin(); i++) if( real.getValue(i,0) >  real.getValue(idMax,0)) return false;

        return pred.getValue(idMax,0)> probaMin;
    }

    public void setUseAccuracy(float probaMin) {
        this.useAccuracy = true;
        this.probaMin = probaMin;
    }

    public void setMultiClassifAccuracy(boolean multiClassifAccuracy) {
        this.multiClassifAccuracy = multiClassifAccuracy;
    }

    public NNetwork getNetwork() {
        return network;
    }

    public void setNetwork(NNetwork network) {
        this.network = network;
    }

    public CostFunction getCostFunction() {
        return costFunction;
    }

    public void setCostFunction(CostFunction costFunction) {
        this.costFunction = costFunction;
    }

    public int getBatch_size() {
        return batch_size;
    }

    public void setBatch_size(int batch_size) {
        this.batch_size = batch_size;
    }

    public int getEpochs() {
        return epochs;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }

    public int getStep_per_epochs() {
        return step_per_epochs;
    }

    public void setStep_per_epochs(int step_per_epochs) {
        this.step_per_epochs = step_per_epochs;
    }

    public float getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(float learningRate) {
        this.learningRate = learningRate;
    }

    public boolean isMultiClassifAccuracy() {
        return multiClassifAccuracy;
    }

    public float getProbaMin() {
        return probaMin;
    }

    public void setProbaMin(float probaMin) {
        this.probaMin = probaMin;
    }

    public boolean isUseAccuracy() {
        return useAccuracy;
    }

    public void setUseAccuracy(boolean useAccuracy) {
        this.useAccuracy = useAccuracy;
    }

    public boolean isRenderTraining() {
        return renderTraining;
    }

    public void setRenderTraining(boolean renderTraining) {
        this.renderTraining = renderTraining;
    }
}
