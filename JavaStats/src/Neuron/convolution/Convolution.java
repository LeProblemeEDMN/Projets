package Neuron.convolution;

import Neuron.FirstLayer;
import Neuron.Layer;
import Neuron.activationFunction.ActivFunction;
import matrix.Matrix;
import matrix.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Convolution extends Layer {
    private List<Matrix[]> filters;
    private Matrix bias;
    private boolean keepSize =false;
    private int size, k;
    private int step;
    
    private int numberFilters;

    private ActivFunction actFunction;

    private Matrix[] sums;
    
    private List<Matrix[]> gradientFilters;
    private Matrix gradientBias;
    private Layer nextLayer;

    private int sx,sy;

    public Convolution(boolean keepSize, int k, int step, int numberFilters, ActivFunction actFunction) {
        this.keepSize = keepSize;
        this.k = k;
        this.step = step;
        this.numberFilters = numberFilters;
        this.actFunction = actFunction;
        this.size = 2 * k + 1;
    }
        //UTILISE PAS KEEP SIZE------------------------------
    @Override
    public void init(Layer layer) {
        super.init(layer);
        int previousSize;
        if(!iniated) {
            //calcule valeurs des tailles
            if (layer instanceof FirstLayer) {
                FirstLayer fl = (FirstLayer) layer;
                sx = (fl.getSizeInputArray()[0] - 2 * k) / step;
                sy = (fl.getSizeInputArray()[1] - 2 * k) / step;
                if(keepSize){
                    sx=fl.getSizeInputArray()[0];
                    sy=fl.getSizeInputArray()[1];
                }
            } else if (layer instanceof Convolution) {
                Convolution fl = (Convolution) layer;
                sx = (fl.getSx() - 2 * k) / step;
                sy = (fl.getSy() - 2 * k) / step;
                if(keepSize){
                    sx=fl.getSx();
                    sy=fl.getSy();
                }
            }



            previousSize = layer.getOutConv().length;

            filters = new ArrayList<>();
            for (int i = 0; i < numberFilters; i++) {
                Matrix[] filter = new Matrix[previousSize];
                for (int j = 0; j < previousSize; j++) {
                    filter[j] = Layer.randomMatrix(size, size);
                }
                filters.add(filter);
            }

            bias = Layer.randomMatrix(numberFilters, 1);
        }else {
            previousSize = layer.getOutConv().length;
        }
        //Sert à dire au prochain layer de combien de layer est ocnstitué ce convo
        setOut(new Matrix[numberFilters]);

        gradientFilters = new ArrayList<>();
        gradientBias = new Matrix(numberFilters,1);

        for (int i = 0; i < numberFilters; i++) {
            Matrix[] gradFilter = new Matrix[previousSize];
            for (int j = 0; j < previousSize; j++) {
                gradFilter[j] = new Matrix(size, size);
            }
            gradientFilters.add(gradFilter);

        }

    }

    @Override
    public void forwardPropagation() {
        Matrix[] inputs = getPreviousLayer().getOutConv();

        //ajoute des 0 aux bord de la matrice pr que l'image finale ai la meme taille qeu celle de sortie
       /* if(keepSize){
            for (int i = 0; i < inputs.length; i++) {
                inputs[i] = resizeMatrix(inputs[i]);
            }
        }*/

        //defini la taille des matrices de sortie
        int sOutx = (inputs[0].getLin()-2 * k)/step;
        int sOuty = (inputs[0].getCol()-2 * k)/step;
        //calcule les convolutions pour chaque filtre
        Matrix[] outs = new Matrix[numberFilters];
        for (int i = 0; i < numberFilters; i++) {
            Matrix filterResult = new Matrix(sOutx, sOuty);
            Matrix[]filter = filters.get(i);

            //remplis les valeur pour le filtre
            for (int j = 0; j < sOutx; j++) {
                for (int l = 0; l < sOuty; l++) {
                    //position du centre du filtre
                    int indexCenterX = keepSize? 0 :k + j*step;
                    int indexCenterY = keepSize? 0 :k + l*step;

                    filterResult.setValue(j, l, applyFilter(inputs, filter, bias.getValue(i, 0), indexCenterX, indexCenterY));
                }
            }
            outs[i] = filterResult;
        }

        sums = outs;

        //utilise la fonction d'activaiton sur tt les resultat obtenu.
        Matrix[] activationOutput = new Matrix[numberFilters];
        for (int i = 0; i < numberFilters; i++) {
            activationOutput[i] = actFunction.actLayer(outs[i]);
        }
        setOut(activationOutput);
    }

    //applique un filtre sur une zone pour obtenir la valeur de la convolution
    private float applyFilter(Matrix[]input,Matrix[]filter, float bias, int x, int y){
        int beginX = x - k;
        int beginY = y - k;

        float total = bias;
        for (int i = 0; i < filter.length; i++) {
            for (int j = 0; j < filter[0].getLin(); j++) {
                for (int l = 0; l < filter[0].getCol(); l++) {
                    if(beginX + j>=0 && beginY+l>=0 && beginX + j<input[i].getLin() && beginY+l<input[i].getCol())
                        total += input[i].getValue(beginX + j, beginY  + l) * filter[i].getValue(j, l);
                }
            }
        }
        return total;
    }

    //calcule les erreur pr chaqeu case de l'input à a partir du grad précedent
    @Override
    public Matrix[] backwardPropagation(Matrix[] lastErrorGradient) {

        //ajoute erreur de activ function
        for (int i = 0; i < lastErrorGradient.length; i++) {
            Matrix errorM= lastErrorGradient[i];
            Matrix sum= sums[0];
            for (int j = 0; j < errorM.getLin(); j++) {
                for (int l = 0; l < errorM.getCol(); l++) {
                    errorM.setValue(j, l, errorM.getValue(j, l) * actFunction.dActivation(sum.getValue(j, l)));
                }
            }
        }
        setErrorGradient(lastErrorGradient);

        //calcule weights et bias error
        for (int i = 0; i < numberFilters; i++) {
            Matrix errorFilter = lastErrorGradient[i];
            //pour le bias somme de tt les erreurs fais derivée
            for (int j = 0; j < errorFilter.getCol(); j++) {
                for (int l = 0; l < errorFilter.getLin(); l++) {
                    gradientBias.setValue(i,0, gradientBias.getValue(i,0) + errorFilter.getValue(l, j));
                }
            }

            Matrix[] gradientFilter = gradientFilters.get(i);
            for (int l = 0; l < gradientFilter.length; l++) {
                Matrix gf = gradientFilter[l];
                Matrix outLayer = getPreviousLayer().getOutConv()[l];
                for (int x = 0; x < gf.getCol(); x++) {
                    for (int y = 0; y < gf.getLin(); y++) {
                        float deriv = 0;
                        //pour chaque poids de la convo fais la somme pr derivée
                        for (int u = 0; u < errorFilter.getLin(); u++) {
                            for (int v = 0; v < errorFilter.getCol(); v++) {
                                //calcule la position du pixel si keepsize=fals eajoute k car tailte sortie reduite
                                int px = u + (keepSize?0:k) - k - x;
                                int py = v + (keepSize?0:k) - k - y;
                                //check si bian dans la zone pr éviter erreu bete
                                if(px>=0 && px<outLayer.getCol() && py>=0 && py<outLayer.getLin())deriv += errorFilter.getValue(u, v) * outLayer.getValue(px, py);
                            }
                        }
                    }
                }
            }

        }

        //calcule erreur pr layer suivant
        Matrix[]previousGradient = new Matrix[getPreviousLayer().getOutConv().length];
        for (int j = 0; j < previousGradient.length; j++) {
            previousGradient[j] = new Matrix(getPreviousLayer().getOut().getLin(), getPreviousLayer().getOut().getCol());
        }
            
        //boucle sert les couches du filtre
        for (int j = 0; j < previousGradient.length; j++) {
            for (int x = 0; x < getPreviousLayer().getOut().getLin(); x++) {
                for (int y = 0; y < getPreviousLayer().getOut().getCol(); y++) {
                    previousGradient[j].setValue(x,y, calculateDeriveConvo(j, x, y, lastErrorGradient));
                }
            }
        }
        return previousGradient;
    }

    public float calculateDeriveConvo(int posInLayer,int posX,int posY, Matrix[] lastErrorGradient){
        float deriv = 0;
        for (int i = 0; i < numberFilters; i++) {
            for (int m = 0; m < size; m++) {
                for (int n = 0; n < size; n++) {
                    int x = m + posX + (keepSize?0:-k);
                    int y = n + posY + (keepSize?0:-k);

                    if(x>=0 && x<lastErrorGradient[i].getLin() && y>=0 && y<lastErrorGradient[i].getCol()) deriv += filters.get(i)[posInLayer].getValue(m, n) * lastErrorGradient[i].getValue(x, y);
                }
            }
        }
        return deriv;
    }

    @Override
    public void updateLayer(float lr) {
        for (int i = 0; i < numberFilters; i++) {
            Matrix[] filter = filters.get(i);
            Matrix[] gradFilter = gradientFilters.get(i);
            for (int j = 0; j < filter.length; j++){
                filter[j].add(gradFilter[j].mul(lr));
            }
        }
        bias = bias.add(gradientBias.mul(lr));

        gradientFilters = new ArrayList<>();
        gradientBias = new Matrix(numberFilters,1);

        for (int i = 0; i < numberFilters; i++) {
            Matrix[] gradFilter = new Matrix[getPreviousLayer().getOutConv().length];
            for (int j = 0; j < gradFilter.length; j++) {
                gradFilter[j] = new Matrix(size, size);
            }
            gradientFilters.add(gradFilter);

        }
    }

    private Matrix resizeMatrix(Matrix input){
        Matrix input_resize = new Matrix(input.getLin() + 2 * k, input.getCol() + 2 * k);
        for (int i = 0; i < input.getLin(); i++) {
            for (int j = 0; j < input.getCol(); j++) {
                input_resize.setValue(k + i, k+ j, input.getValue(i,j));
            }
        }
        return input_resize;
    }

    public int getSx() {
        return sx;
    }

    public int getSy() {
        return sy;
    }

    public int getNumberFilters() {
        return numberFilters;
    }

    @Override
    public String save() {
        int previousSize = filters.get(0).length;//taille des filtres
        String line ="CONVOLUTION "+actFunction.getName()+" "+keepSize+" "+k+" "+numberFilters+" "+step+" "+previousSize+" "+sx+" "+sy;
        for (int i = 0; i < numberFilters; i++) {
            line += " " + bias.getValue(i,0);
        }
        //boucle sur les filtre
        for (int i = 0; i < numberFilters; i++) {
            //boucle sur chaque couche du filtre
            for (int j = 0; j < previousSize; j++) {
                Matrix cFiltre = filters.get(i)[j];
                //ecrit la matrice
                for (int l = 0; l < size; l++) {
                    for (int m = 0; m < size; m++) {
                        line += " "+cFiltre.getValue(l, m);
                    }
                }
            }
        }
        return line;
    }

    public static Convolution createConvo(String line){
        String[] split = line.split(" ");

        ActivFunction actFunction = null;
        for (int i = 0; i < ActivFunction.ACTIVAITON_FUNCTIONS.length; i++)
            if(split[1].equals( ActivFunction.ACTIVAITON_FUNCTIONS[i].getName())) actFunction = ActivFunction.ACTIVAITON_FUNCTIONS[i];

        boolean keepSize = Boolean.parseBoolean(split[2]);
        int k = Integer.parseInt(split[3]);
        int size = 2*k +1;
        int numberFilters = Integer.parseInt(split[4]);
        int step = Integer.parseInt(split[5]);
        int previousSize = Integer.parseInt(split[6]);
        int sx = Integer.parseInt(split[7]);
        int sy = Integer.parseInt(split[8]);


        Convolution convolution = new Convolution(keepSize, k, step, numberFilters, actFunction);
        convolution.sx = sx;
        convolution.sy = sy;
        convolution.filters = new ArrayList<>();
        convolution.iniated = true;
        int id = 9;

        Matrix bias = new Matrix(numberFilters,1);
        for (int i = 0; i < numberFilters; i++) {
            bias.setValue(i,0,Float.parseFloat(split[id]));
            id++;
        }
        convolution.bias = bias;
        //boucle sur les filtre
        for (int i = 0; i < numberFilters; i++) {
            Matrix[] filter = new Matrix[previousSize];
            //boucle sur chaque couche du filtre
            for (int j = 0; j < previousSize; j++) {
                Matrix cFiltre = new Matrix(size,size);
                //ecrit la matrice
                for (int l = 0; l < size; l++) {
                    for (int m = 0; m < size; m++) {
                        cFiltre.setValue(l,m,Float.parseFloat(split[id]));
                        id++;
                    }
                }
                filter[j] = cFiltre;
            }
            convolution.filters.add(filter);
        }

        return convolution;
    }
}
