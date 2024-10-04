package GPU.NN;

import GPU.GPUMatrix;
import Neuron.Layer;
import Neuron.activationFunction.ActivFunction;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class GPUDense extends GPULayer{

    protected int number_neurons;
    protected int input_size;

    protected FloatBuffer weights;
    protected FloatBuffer bias;
    protected FloatBuffer sum;

    public GPUDense(int number_neurons, ActivFunction function) {
        this.number_neurons = number_neurons;
        this.activFunction = function;
        this.input_size = 1;
    }

    public void init(GPULayer lastLayer){
        this.last = lastLayer;
        if(lastLayer instanceof GPUDense) this.input_size = ((GPUDense) lastLayer).number_neurons;
        if(lastLayer instanceof FirstLayer) this.input_size = ((FirstLayer) lastLayer).size[0];
        bias  = GPUMatrix.matrixToBuffer(Layer.randomMatrix(number_neurons,1));
        weights  = GPUMatrix.matrixToBuffer(Layer.randomMatrix(number_neurons,this.input_size));
    }

    public void forward(int batch_size){
        FloatBuffer buf = GPUNetworkComputation.dense(weights,bias,last.out,number_neurons,this.input_size,batch_size,activFunction);

        out = buf.slice(0,number_neurons*batch_size);
        sum = buf.slice(number_neurons*batch_size,number_neurons*batch_size);
    }

}
