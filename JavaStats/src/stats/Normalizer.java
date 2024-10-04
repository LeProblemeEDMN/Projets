package stats;

public class Normalizer {
    private float[] means;
    private float[] std;
    private boolean oneFeatures, initialized = false;

    //fit the normalizer with one features to prepare the normalization
    public void fit(float[] x){
        oneFeatures = true;
        initialized = true;

        means = new float[1];
        means[0] = Stats.mean(x);

        std = new float[1];
        std[0] = Stats.std_dev(x);
    }

    //fit the normalizer with multiple features to prepare the normalization
    public void fit(float[][] x){
        oneFeatures = false;
        initialized = true;

        means = new float[x.length];
        std = new float[x.length];
        for (int i = 0; i < x.length; i++) {
            means[i] = Stats.mean(x[i]);
            std[i] = Stats.std_dev(x[i]);
        }
    }

    public float[] transform(float[] x){
        if(!isOneFeatures()){
            System.err.println("Normalizer is not single features cannot return transform of an 1D array");
            return x;
        }
        float[] x_transform = new float[x.length];
        for (int i = 0; i < x.length; i++) {
            x_transform[i] = (x[i] - means[0]) / std[0];
        }
        return x_transform;
    }

    public float[][] transform(float[][] x){
        if(isOneFeatures()){
            System.err.println("Normalizer is single features cannot return transform of an 2D array");
            return x;
        }
        float[][] x_transform = new float[x.length][10];
        for (int i = 0; i < x.length; i++) {
            float[] x_part = x[i];
            float[] x_part_transform = new float[x_part.length];
            for (int j = 0; j < x_part.length; j++) {
                x_part_transform[j] = (x_part[j] - means[i]) / std[i];
            }
            x_transform[i] = x_part_transform;
        }
        return x_transform;
    }

    public float getMean() {
        if(isOneFeatures())
            return means[0];
        else {
            System.err.println("Normalizer is not single features cannot return the mean");
            return 0;
        }
    }

    public float getPstDev() {
        if(isOneFeatures())
            return std[0];
        else {
            System.err.println("Normalizer is not single features cannot return the pst-dev");
            return 0;
        }
    }

    public float[] getMeans() {
        if(isOneFeatures()){
            System.err.println("Normalizer is single features cannot return the means array");
            return null;
        }else return means;
    }

    public void setMeans(float[] means) {
        this.means = means;
    }

    public float[] getStdDev() {
        if(isOneFeatures()){
            System.err.println("Normalizer is single features cannot return the std-dev array");
            return null;
        }else return std;
    }

    public void setStd(float[] std) {
        this.std = std;
    }

    public boolean isOneFeatures() {
        return oneFeatures;
    }

    public void setOneFeatures(boolean oneFeatures) {
        this.oneFeatures = oneFeatures;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
