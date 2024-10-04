package GPU.NN;

import Neuron.costFunction.CostFunction;
import matrix.Matrix;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.List;

public class GPUOptimizer {

    public static void train(List<Matrix> inputs, List<Matrix> outputs, int batch_size,int epochs,float lr, GPUNetwork network, CostFunction function){
        int batches = inputs.size()/batch_size;
        FloatBuffer[] input_buffer = new FloatBuffer[batches];
        FloatBuffer[] output_buffer = new FloatBuffer[batches];

        long t = System.currentTimeMillis();
        for (int b = 0; b < batches; b++) {
            //input
            FloatBuffer buffer = BufferUtils.createFloatBuffer(batch_size*network.getSizeFirstBuffer());
            for (int i = 0; i < network.getSizeFirstBuffer(); i++) {
                for (int j = 0; j < batch_size; j++) {
                    buffer.put(i*batch_size+j,inputs.get(b*batch_size + j).getValue(i,0));
                }
            }
            input_buffer[b] = buffer;
            //output
            buffer = BufferUtils.createFloatBuffer(batch_size*outputs.get(0).getLin());
            for (int i = 0; i < outputs.get(0).getLin(); i++) {
                for (int j = 0; j < batch_size; j++) {
                    buffer.put(i*outputs.get(0).getLin()+j,outputs.get(b*batch_size + j).getValue(i,0));
                }
            }
            output_buffer[b] = buffer;
        }
        System.out.println("Buffer prepare en "+(System.currentTimeMillis()-t)+" ms.");

        int nb_layers = network.getLayers().size();

        for (int e = 0; e < epochs; e++) {
            float meanLoss =0;
            for (int b = 0; b < batches; b++) {
                FloatBuffer result = network.predict(input_buffer[b], batch_size);
                FloatBuffer errorBuffer = BufferUtils.createFloatBuffer(batch_size*outputs.get(0).getLin());
                float error = 0;
                for (int i = 0; i < outputs.get(0).getLin(); i++) {
                    for (int j = 0; j < batch_size; j++) {
                        float v_r = output_buffer[b].get(i*outputs.get(0).getLin()+j);
                        float v_nn = result.get(i*outputs.get(0).getLin()+j);
                        error+=0.5f*(v_nn-v_r)*(v_nn-v_r)/outputs.get(0).getLin();
                        errorBuffer.put(i*outputs.get(0).getLin()+j,v_r-v_nn);
                    }
                }
                meanLoss+=error/batches;

                for (int i = nb_layers-1; i >0 ; i--) {
                    errorBuffer = GPUNetworkComputation.backWard((GPUDense) network.getLayers().get(nb_layers-1), errorBuffer,batch_size,lr);
                }
            }
            System.out.println("Epochs: "+e+" Loss:"+meanLoss);
        }
    }
}
