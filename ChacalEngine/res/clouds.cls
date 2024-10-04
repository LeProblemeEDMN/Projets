kernel void clouds(global float* result,global const float* points_red,global const float* points_green,global const float* points_blue, int const nb_p , int const sizex, int const sizey, int const sizez,int const div,int const power) {
const int itemId = get_global_id(0);
int z=itemId/(sizex*sizey);
int reste=itemId%(sizex*sizey);
int y=reste/sizex;
int x=reste%sizex;

float min_red=9999999;
float min_green=9999999;
float min_blue=9999999;
for(int i=0;i<nb_p;i++){
    float dist=(points_red[3*i]-x)*(points_red[3*i]-x)+(points_red[3*i+1]-y)*(points_red[3*i+1]-y)+(points_red[3*i+2]-z)*(points_red[3*i+2]-z);
    if(dist<min_red)min_red=dist;
    dist=(points_green[3*i]-x)*(points_green[3*i]-x)+(points_green[3*i+1]-y)*(points_green[3*i+1]-y)+(points_green[3*i+2]-z)*(points_green[3*i+2]-z);
    if(dist<min_green)min_green=dist;
    dist=(points_blue[3*i]-x)*(points_blue[3*i]-x)+(points_blue[3*i+1]-y)*(points_blue[3*i+1]-y)+(points_blue[3*i+2]-z)*(points_blue[3*i+2]-z);
    if(dist<min_blue)min_blue=dist;
}

result[itemId*3]=pow(1-sqrt(min_red)/div,power);
result[itemId*3+1]=pow(1-sqrt(min_green)/div,power);
result[itemId*3+2]=pow(1-sqrt(min_blue)/div,power*2);

}