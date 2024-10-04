//multiply matrix A and B of size m*l and l*n
kernel void matrix_mul(global const float* A, global const float* B, global float* result, int const m, int const l, int const n) {
   const int x = get_global_id(0);
   const int y = get_global_id(1);

   float result_v = 0;
   for (int i = 0; i<l; i++){
        result_v += A[x*l + i] * B[i*n + y];
   }

   result[x*n + y] = result_v;
}