package GPU.NN;

import GPU.CLObject;
import Neuron.activationFunction.ActivFunction;
import Neuron.activationFunction.ReLu;
import Neuron.activationFunction.Sigmoid;
import Neuron.activationFunction.XAct;
import matrix.Matrix;

import java.nio.FloatBuffer;

public class GPUNetworkComputation {

    protected static CLObject kernelDense;
    protected static CLObject kernelBackward;

    public static void init(){
        kernelDense = new CLObject("src/GPU/NN/denseLayer.cls","dense");
        kernelDense.setDebug(false);
        kernelBackward = new CLObject("src/GPU/NN/denseLayerBackward.cls","dense");
        kernelBackward.setDebug(false);
    }

    public static FloatBuffer dense(FloatBuffer weights, FloatBuffer bias, FloatBuffer input, int m, int l, int n, ActivFunction act){

        kernelDense.setResult(m*n*2,8,3);
        kernelDense.addEntry(weights,0);
        kernelDense.addEntry(bias,1);
        kernelDense.addEntry(input,2);
        kernelDense.getMemoryInt().put(5,m);
        kernelDense.getMemoryInt().put(6,l);
        kernelDense.getMemoryInt().put(7,n);

        if(act.getName() == XAct.X_ACT.getName())kernelDense.getMemoryInt().put(4,0);
        if(act.getName() == ReLu.RELU.getName())kernelDense.getMemoryInt().put(4,1);
        if(act.getName() == Sigmoid.SIGMOID.getName())kernelDense.getMemoryInt().put(4,2);

        kernelDense.execute_dim(m,n);

        FloatBuffer output = kernelDense.getWriteToMap().get(3);
        return output;
    }
    //(global const float* weights, global const float* bias,global const float* input,global const float* sum, global float* result_w, global float* result_b, global float* result_g
//global const float* errorLastLayer,int const act, int const m, int const l, int const n, float const lr)
    public static FloatBuffer backWard(GPUDense dense, FloatBuffer lastError,int batch_size, float learningRate){
        kernelBackward.getMemory().clear();
        kernelBackward.getMemoryInt().clear();
        kernelBackward.getWriteToMap().clear();
        kernelBackward.getResults().clear();
        kernelBackward.getResultSize().clear();

        kernelBackward.addEntry(dense.weights,0);
        kernelBackward.addEntry(dense.bias,1);
        kernelBackward.addEntry(dense.last.out,2);
        kernelBackward.addEntry(dense.sum,3);

        kernelBackward.setResult(dense.number_neurons*dense.input_size,8,4);
        kernelBackward.setResult(dense.number_neurons,8,5);
        kernelBackward.setResult(dense.number_neurons*batch_size,8,6);

        kernelBackward.addEntry(lastError,7);

        kernelBackward.getMemoryInt().put(9,dense.number_neurons);
        kernelBackward.getMemoryInt().put(10,dense.input_size);
        kernelBackward.getMemoryInt().put(11,batch_size);
        kernelBackward.getMemoryInt().put(12,(int)Math.log10(learningRate));

        if(dense.activFunction.getName() == XAct.X_ACT.getName())kernelBackward.getMemoryInt().put(8,0);
        if(dense.activFunction.getName() == ReLu.RELU.getName())kernelBackward.getMemoryInt().put(8,1);
        if(dense.activFunction.getName() == Sigmoid.SIGMOID.getName())kernelBackward.getMemoryInt().put(8,2);

        kernelBackward.execute_dim(dense.number_neurons, dense.input_size);

        dense.weights = kernelBackward.getWriteToMap().get(4);
        for (int i = 0; i < dense.weights.capacity(); i++) {
            System.out.print(dense.weights.get(i)+" ");
        }
        System.out.println();
        dense.bias = kernelBackward.getWriteToMap().get(5);
        return kernelBackward.getWriteToMap().get(6);
    }
}
