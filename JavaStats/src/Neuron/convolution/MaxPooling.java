package Neuron.convolution;

import Neuron.Layer;
import matrix.Matrix;

public class MaxPooling extends Layer {
    private int width, height;

    private int sx,sy;

    private int numberFilter;

    private int[][][] idPixelChoosen;

    public MaxPooling(int sx, int sy) {
        this.sx = sx;
        this.sy = sy;
    }

    @Override
    public void init(Layer layer) {
        super.init(layer);
        if(layer instanceof Convolution){
            Convolution fl = (Convolution) layer;
            width = fl.getSx()/2 + (fl.getSx()% sx ==0?0:1);
            height = fl.getSy()/2 + (fl.getSy()% sy ==0?0:1);
            numberFilter = fl.getNumberFilters();
        }else if(layer instanceof MaxPooling){
            MaxPooling fl = (MaxPooling) layer;
            width = fl.getWidth()/2 + (fl.getWidth()% sx ==0?0:1);
            height = fl.getHeight()/2 + (fl.getHeight()% sy ==0?0:1);
            numberFilter = fl.getNumberFilter();
        }

    }

    @Override
    public void forwardPropagation() {
        int depth = getPreviousLayer().getOutConv().length;

        idPixelChoosen = new int[depth][width][height];
        Matrix[] out =new Matrix[depth];
        for (int i = 0; i < depth; i++) {
            Matrix input = getPreviousLayer().getOutConv()[i];
            Matrix filter = new Matrix(width, height);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int px = x * sx;
                    int py = y * sy;

                    float max = -999;
                    int idMax = 0;
                    for (int j = px; j < Math.min(px + sx, input.getCol()); j++) {
                        for (int k = py; k < Math.min(py + sy, input.getLin()); k++) {
                            if(max < input.getValue(j, k)){
                                max = input.getValue(j, k);
                                idMax = (j - px) + (k - py) * sx;
                            }
                        }
                    }
                    filter.setValue(x, y, max);
                    idPixelChoosen[i][x][y] = idMax;
                }
            }
            out[i] = filter;
        }
        setOut(out);
    }

    @Override
    public Matrix[] backwardPropagation(Matrix[] errorGradientNext){
        Matrix[] output = new Matrix[errorGradientNext.length];
        for (int i = 0; i < errorGradientNext.length; i++) {
            Matrix outLayer = new Matrix(getPreviousLayer().getOut().getCol(), getPreviousLayer().getOut().getLin());
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int idPixelSelect = idPixelChoosen[i][x][y];
                    int px = x * sx + idPixelSelect % sx;
                    int py = y * sy + idPixelSelect / sx;
                    outLayer.setValue(px, py, errorGradientNext[i].getValue(x, y));
                }
            }
            output[i] = outLayer;
        }
        setErrorGradient(output);
        return output;
    }

    @Override
    public void updateLayer(float lr) {
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getNumberFilter() {
        return numberFilter;
    }

    @Override
    public String save() {
        return "MAXPOOLING "+Integer.toString(sx)+" "+Integer.toString(sy);
    }
    public static MaxPooling createMP(String line){
        String[] split = line.split(" ");
        int sx = Integer.parseInt(split[1]);
        int sy = Integer.parseInt(split[2]);
        return new MaxPooling(sx, sy);
    }
}
