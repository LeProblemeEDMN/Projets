package GPU.NN;

import GPU.GPUMatrix;
import Neuron.Dense;
import matrix.Matrix;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class GPUNetwork {
    //last layer must a dense one

    private int sizeFirstBuffer;
    private List<GPULayer> layers = new ArrayList<>();
    public GPUNetwork(int...size){
        sizeFirstBuffer = 1;
        for (int i = 0; i < size.length; i++) {
            sizeFirstBuffer *= size[i];
        }
        layers.add(new FirstLayer(size));
    }

    public void add(GPULayer l){
        layers.add(l);
        if(l instanceof GPUDense){
            GPUDense d =(GPUDense) l;
           d.init(layers.get(layers.size()-2));
        }
    }

    public FloatBuffer predict(FloatBuffer input,int batch_size){
        layers.get(0).out = input;
        for (int i = 1; i < layers.size(); i++) {
            layers.get(i).forward(batch_size);
        }
        return layers.get(layers.size() - 1).out;
    }

    public List<Matrix> predict(List<Matrix> inputs){
        int batch_size = inputs.size();
        FloatBuffer buffer = BufferUtils.createFloatBuffer(batch_size*sizeFirstBuffer);
       for (int i = 0; i < sizeFirstBuffer; i++) {
            for (int j = 0; j < batch_size; j++) {
                buffer.put(i*batch_size+j,inputs.get(j).getValue(i,0));
            }
        }

        layers.get(0).out = buffer;
        for (int i = 1; i < layers.size(); i++) {
            layers.get(i).forward(batch_size);
        }
        GPUDense lastLayer =(GPUDense)layers.get(layers.size() - 1);
        Matrix result = GPUMatrix.bufferToMatrix(lastLayer.out,lastLayer.number_neurons,batch_size);

        List<Matrix> results = new ArrayList<>();
        for (int i = 0; i < batch_size; i++) {
            Matrix r = new Matrix(result.getLin(),1);
            for (int j = 0; j <lastLayer.number_neurons; j++) {
                r.setValue(j,0,result.getValue(j,i));
            }
            results.add(r);
        }
        return null;
    }

    public int getSizeFirstBuffer() {
        return sizeFirstBuffer;
    }

    public List<GPULayer> getLayers() {
        return layers;
    }
}
