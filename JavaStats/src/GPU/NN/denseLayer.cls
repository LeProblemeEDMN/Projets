//dense Layer
kernel void dense(global const float* weights, global const float* bias,global const float* input, global float* result,int const act, int const m, int const l, int const n) {
   const int x = get_global_id(0);
   const int y = get_global_id(1);

   float result_v = bias[x*n + y];
   for (int i = 0; i<l; i++){
        result_v += weights[x*l + i] * input[i*n + y];
   }
   result[m*n+x*n + y] = result_v;
   result[x*n + y] = 1;
   if(act==0){
    result[x*n + y] = result_v;
   }else if(act==1 && result_v>0){
        result[x*n + y] = result_v;
   }else if(act==2){
        result[x*n + y] = 1/(1+exp(-result_v));
   }
}