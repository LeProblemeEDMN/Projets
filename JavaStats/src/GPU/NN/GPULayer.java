package GPU.NN;

import Neuron.activationFunction.ActivFunction;

import java.nio.FloatBuffer;

public class GPULayer {
    protected FloatBuffer out;
    protected ActivFunction activFunction;
    protected GPULayer last;

    public void forward(int batch_size){}

}
