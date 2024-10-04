//dense Layer
kernel void dense(global const float* weights, global const float* bias,global const float* input,global const float* sum, global float* result_w, global float* result_b, global float* result_g, global const float* errorLastLayer,int const act, int const m, int const l, int const n, int const lr) {
   const int x = get_global_id(0);
   const int y = get_global_id(1);

   float learningrate = pow(10.0f,(float)lr);
    learningrate=0.01f;
    //propage erreur
   float error = errorLastLayer[x*n + y];
   float s = sum[x*n + y];
   if(act==1 && s<0){
        error= 0;
   }else if(act==2){
        float y= 1/(1+exp(-s));
        error *= y*(1-y);
   }
    //calcule error bias
    result_b[x] += (bias[x] -learningrate*error)/n;
   //calcule erreur des poids
    for (int i=0; i<l; i++){
        result_w[x*l+i] =(weights[x*l+i] - learningrate*error * input[i*n+y])/n;
    }

    //calcule prochaine erreur
    for (int i=0; i<l; i++){
        result_g[i*n+y] += error * weights[x*l+i];
    }

}