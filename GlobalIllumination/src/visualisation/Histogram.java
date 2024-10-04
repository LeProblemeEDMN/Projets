package visualisation;

public class Histogram {
    public int[] numbers_ind;
    public float[] samples_lim;
    public int max,nb_samples;

    public Histogram(int samples){
        nb_samples=samples;
    }
    public void compute(float[] x,float max ,float min){
        samples_lim=new float[nb_samples+1];
        numbers_ind=new int[nb_samples];

        float intervalle=max-min;
        max+=intervalle*0.001;
        min-=intervalle*0.001;
        intervalle=max-min;

        float step=intervalle/nb_samples;

        for (int i = 0; i < nb_samples; i++) samples_lim[i]=min+i*step;
        samples_lim[nb_samples]=max;

        for (int i = 0; i < x.length; i++) {
            int id=(int)((x[i]-min)/step);
            id = Math.max(0, Math.min(nb_samples - 1, id));
            numbers_ind[id]++;
        }
        max=0;
        for (int i = 0; i < nb_samples; i++)max= Math.max(max,numbers_ind[i]);
    }


    public void compute(float[] x){
        float max=-9999;
        float min=9999;
        for (float xi:x) {
            max=Math.max(xi,max);
            min=Math.min(xi,min);
        }
        compute(x,max,min);
    }

    public float[] getSamples_lim() {
        return samples_lim;
    }

    public int getMax() {
        return max;
    }

    public int getNb_samples() {
        return nb_samples;
    }

    public int[] getSum_ind() {
        int[] sum_ind = new int[nb_samples];
        sum_ind[0] = numbers_ind[0];
        for (int i = 1; i < nb_samples; i++) {
            sum_ind[i] = numbers_ind[i] + sum_ind[i - 1];
        }
        return sum_ind;
    }

    public int[] getNumbers_ind() {
        return numbers_ind;
    }
}
