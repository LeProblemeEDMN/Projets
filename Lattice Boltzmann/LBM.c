#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include <omp.h>


/*
Problèmes:
Temperature qui augmente sur les bords
Changer Nx change grandement les comportement (beaucoup plus dense)
 -> surement du au fait que la force doit être proportionnelle à dx (ce qui est logique pour que le travail soit le même).

A implem 
condition aux bords
temperture (par sure que ce soit réaliste pr l'instant).
*/
#define Nx 200
#define Ny 200
#define MAX_X 100.0
#define MAX_Y 100.0
#define M 9
#define PI 3.14159265359
#define G -0.4
#define EPSILON 1e-14
#define BOUNDARY 0
#define RHO_0 100
#define T_0 100
#define COEF_DILATATION 1.5e-0
#define CAP_CALORIFIQUE_AIR 1005
#define CAP_CALORIFIQUE_SOLIDE 500

struct Datas
{
    double* temperature;
    double* densities;
    double* speed_x;
    double* speed_y;
    char* map;
    
    double* f;
    double* g;

    double* temp;
};
struct LBM_parameters{
    double dt;
    double dx;
    double c;
    double Cs;
    double ics2;
    double omega;

    int* directions_int;
    double* directions;
    double* directions_weights;
};


double get_time();
float uniform_random(float min, float max);
void dump_temperature_to_file(struct Datas* datas, const char *filename);
void compute_forces(struct Datas* datas,struct LBM_parameters* params);
void compute_collisions(struct Datas* datas,struct LBM_parameters* params);
void compute_macroscopic(struct Datas* datas,struct LBM_parameters* params);
void advect(struct Datas* datas,struct LBM_parameters* params);
void read_init_file(struct Datas* datas, struct LBM_parameters* params);

static inline int spatial_array(int x,int y){
    return x+y*Nx;
}
static inline int phase_array(int x,int y,int i){
    return x*M+y*Nx*M+i;
}

int main (int argc, char **argv){
    //timing parameters
    double T_max=120;
    double dt=0.055;
    double t=0;
    int N=(int)((T_max-t)/dt);
    int number_of_save=150;
    int time_between=N/number_of_save;
    struct LBM_parameters params;
    params.dt=dt;
    params.dx=MAX_X/Nx;
    params.c=params.dx/params.dt;
    params.Cs=params.c/sqrt(3);
    params.ics2=1.0/(params.Cs*params.Cs);
    params.omega=1.0/(1,57e-5/(params.dt*params.Cs*params.Cs)+0.5);
    printf("Omega:%f\n",params.omega);

    struct Datas datas;
    datas.temperature=calloc(Nx*Ny,sizeof(double));
    datas.densities=calloc(Nx*Ny,sizeof(double));
    datas.speed_x=calloc(Nx*Ny,sizeof(double));
    datas.speed_y=calloc(Nx*Ny,sizeof(double));
    datas.map=calloc(Nx*Ny,sizeof(char));
    datas.f=calloc(Nx*Ny*M,sizeof(double));
    datas.g=calloc(Nx*Ny*M,sizeof(double));
    datas.temp=calloc(Nx*Ny*M,sizeof(double));

    params.directions=calloc(M*2,sizeof(double));
    params.directions_weights=calloc(M,sizeof(double));
    params.directions_int=calloc(M*2,sizeof(int));
    params.directions_int[0]=0;
    params.directions_int[1]=0;
    params.directions[0]=0;
    params.directions[1]=0;
    params.directions_weights[0]=4.0/9;
    //TODO implem pr que e_idelta_t soit entier
    for (int i = 1; i < M; i++){
        params.directions_int[2*i]=(int)(round(cos(2*PI*(i-1)/8)));
        params.directions_int[2*i+1]=(int)(round(sin(2*PI*(i-1)/8)));
        params.directions_weights[i]=pow(4,1-abs(params.directions_int[2*i])-abs(params.directions_int[2*i+1]))/9.0;
        
        params.directions[2*i]=params.directions_int[2*i]*params.c;
        params.directions[2*i+1]=params.directions_int[2*i+1]*params.c;
    }
    
    read_init_file(&datas,&params);

    double begin=get_time();
    compute_macroscopic(&datas,&params);
    printf("values:%f %f %f\n",datas.densities[0],datas.densities[spatial_array(0,1)],datas.densities[spatial_array(1,0)],datas.densities[spatial_array(1,1)]);
    printf("Initiale, %f %f \n",datas.densities[0],datas.temperature[0]);
    printf("Fin de l'alloc\n");
    int index_save=0;
    for (int i = 0; i < N; i++){
        double tot=0;

        compute_collisions(&datas,&params);

        compute_forces(&datas,&params);

        advect(&datas,&params);

        compute_macroscopic(&datas,&params);
        
        double max=-1;
        int mx=0;
        int my=0;
        for (int y = 0; y < Ny; y++){
        for (int x = 0; x < Nx; x++){
            if( datas.map[spatial_array(x,y)]!=0)continue;
            tot+=datas.densities[spatial_array(x,y)];
            if(fabs(100-datas.densities[spatial_array(x,y)])>max){
                max=fabs(100-datas.densities[spatial_array(x,y)]);
                mx=x;
                my=y;
            }
        }
        }
        printf("values:%f %f %d %d %f\n",datas.densities[spatial_array(mx,my)],tot,mx,my,t);
        if(i%time_between==0){
            
            char buf[256];
            snprintf(buf, sizeof(buf), "t/result_temperature_%d.txt", index_save);
            dump_temperature_to_file(&datas,buf);
            index_save++;
        }
       /* if(max>1000){
            break;
        }*/
        t+=dt;
    }
    printf("Time:%f\n",get_time()-begin);

    free(datas.temperature);
    free(datas.densities);
    free(datas.speed_x);
    free(datas.speed_y);
    free(datas.map);
    free(datas.f);
    free(datas.g);
    free(params.directions_weights);
    free(params.directions);
    free(params.directions_int);
    free(datas.temp);

}

void advect(struct Datas* datas,struct LBM_parameters* params){
    //tester de l'enlever

    #pragma omp parallel for collapse(2)
    for (int y = 0; y < Ny; y++){
        for (int x = 0; x < Nx; x++){
            int index=spatial_array(x,y);
            if(datas->map[index]!=0)continue;
                for (int i = 1; i < M; i++){
                
                int index2=phase_array(x,y,i);

                int n_x=x+params->directions_int[2*i];
                int n_y=y+params->directions_int[2*i+1];
                if(BOUNDARY==1 && x==Nx-1 && n_x==Nx){//eneleve le rebond sur le bord droit
                    continue;
                }
                if(n_x<0 || n_x>=Nx || n_y<0 || n_y>=Ny || datas->map[spatial_array(n_x,n_y)]!=0){
                    int new_i=1+(i+3)%8;
                    int indexNext=phase_array(x,y,new_i);
                    datas->temp[indexNext]+=datas->f[index2];
                    continue;
                }
                int indexNext=phase_array(n_x,n_y,i);
                datas->temp[indexNext]+=datas->f[index2];
            }
        }
    }
    #pragma omp parallel for collapse(2)
    for (int y = 0; y < Ny; y++){
        for (int x = 0; x < Nx; x++){
            for (int i = 1; i < M; i++){
                int index2=phase_array(x,y,i);
                datas->f[index2]=datas->temp[index2];
                datas->temp[index2]=0;

            }
        }
    }
    
    //for g
    #pragma omp parallel for collapse(2)
    for (int y = 0; y < Ny; y++){
        for (int x = 0; x < Nx; x++){
            int index=spatial_array(x,y);
            //if(datas->map[index]!=0)continue;
            for (int i = 1; i < M; i++){
                
                int index2=phase_array(x,y,i);
                int n_x=x+params->directions_int[2*i];
                int n_y=y+params->directions_int[2*i+1];
                if(BOUNDARY==1 && x==Nx-1 && n_x==Nx){//enleve le rebond sur le bord droit
                    continue;
                }
                if(n_x<0 || n_x>=Nx || n_y<0 || n_y>=Ny){// || datas->map[spatial_array(n_x,n_y)]!=0
                    int new_i=1+(i+3)%8;
                    int indexNext=phase_array(x,y,new_i);
                    datas->temp[indexNext]+=datas->g[index2];
                    continue;
                }
                int indexNext=phase_array(n_x,n_y,i);
                datas->temp[indexNext]+=datas->g[index2];
            }
        }
    }
    #pragma omp parallel for collapse(2)
    for (int y = 0; y < Ny; y++){
        for (int x = 0; x < Nx; x++){
            for (int i = 1; i < M; i++){
                int index=spatial_array(x,y);
                int index2=phase_array(x,y,i);
                datas->g[index2]=datas->temp[index2];
                datas->temp[index2]=0;
            }
        }
    }

}

void compute_forces(struct Datas* datas,struct LBM_parameters* params){
    
    
    #pragma omp parallel for collapse(2)
    for (int y = 0; y < Ny; y++){
        for (int x = 0; x < Nx; x++){
            int index=spatial_array(x,y);
            if(datas->map[index]!=0)continue;

            double f_x=0;
        /*    
            if(y>170 && x>75 && x<125){
                f_x=-8;
            }*/



            if(BOUNDARY==1 && x==0){
                datas->f[phase_array(x,y,1)]=RHO_0*params->directions_weights[1]*36/5;
                datas->f[phase_array(x,y,2)]=RHO_0*params->directions_weights[2]*36/5;
                datas->f[phase_array(x,y,8)]=RHO_0*params->directions_weights[8]*36/5;
                datas->g[phase_array(x,y,1)]=T_0*params->directions_weights[1]*36/5;
                datas->g[phase_array(x,y,2)]=T_0*params->directions_weights[2]*36/5;
                datas->g[phase_array(x,y,8)]=T_0*params->directions_weights[8]*36/5;
                continue;
            }

            double tot_pos=0;
            double tot_neg=0;
            for (int i = 0; i < M; i++){
                double f_y=G*(1-COEF_DILATATION*(datas->temperature[index]-T_0/RHO_0));
                int index2=phase_array(x,y,i);

                double v=datas->densities[index]*params->directions_weights[i]*params->dt*params->ics2*(f_x*(params->directions[2*i])
                        +f_y*(params->directions[2*i+1]));
                datas->f[index2]+=v;
                if(y<50 && x>75 && x<125){
                    datas->g[index2]+=params->directions_weights[i]*params->dt*10;
                }
                tot_pos+=fmax(0,datas->f[index2]);
                tot_neg+=fmax(0,-datas->f[index2]);
            }

            //fix les f devenant negatif tt en maintenant la quantité de matière.
            for (int i = 0; i < M; i++){
                int index2=phase_array(x,y,i);
                datas->f[index2]=fmax(0,datas->f[index2]-fabs(datas->f[index2])/tot_pos*tot_neg);
            }
        }
    }

}

void compute_collisions(struct Datas* datas, struct LBM_parameters* params){
    #pragma omp parallel for collapse(2)
    for (int y = 0; y < Ny; y++){
        for (int x = 0; x < Nx; x++){
            int index=spatial_array(x,y);
            
            double last_term=0.5*(datas->speed_x[index]*datas->speed_x[index]+datas->speed_y[index]*datas->speed_y[index])*params->ics2;

            double tot_pos_f=0;
            double tot_neg_f=0;

            double tot_pos_g=0;
            double tot_neg_g=0;

            for (int i = 0; i < M; i++){
                int index2=phase_array(x,y,i);
                double dot=(params->directions[2*i]*datas->speed_x[index]+params->directions[2*i+1]*datas->speed_y[index])*params->ics2;

                if(datas->map[index]==0){
                    double f_eq=params->directions_weights[i]*datas->densities[index]*(1+dot+0.5*dot*dot-last_term);
                    datas->f[index2]+=params->omega*(f_eq-datas->f[index2]);
                    tot_pos_f+=fmax(0,datas->f[index2]);
                    tot_neg_f+=fmax(0,-datas->f[index2]);
                }
    
                double g_eq=params->directions_weights[i]*datas->temperature[index]*datas->densities[index]*(1+dot);
                if(datas->map[index]!=0)g_eq=params->directions_weights[i]*datas->temperature[index]*RHO_0*(1+dot);
                //if(y==100 && x==115)printf("eq:%f %f\n",g_eq,datas->temperature[index]);
                datas->g[index2]+=params->omega*(g_eq-datas->g[index2]);

                tot_pos_g+=fmax(0,datas->g[index2]);
                tot_neg_g+=fmax(0,-datas->g[index2]);
            }

            //conserve la quantité de matière tout en evitant les valeurs négatives
            for (int i = 0; i < M; i++){
                int index2=phase_array(x,y,i);
                
                if(datas->map[index]==0)datas->f[index2]=fmax(0,datas->f[index2]-fabs(datas->f[index2])/tot_pos_f*tot_neg_f);
                
                datas->g[index2]=fmax(0,datas->g[index2]-fabs(datas->g[index2])/tot_pos_g*tot_neg_g);
                
            }

        }
    }
   

}

void compute_macroscopic(struct Datas* datas,struct LBM_parameters* params){
    double f_x=0;
    double f_y=G;
    //utiliser plus de mémoire pr temporel
    #pragma omp parallel for collapse(2)
    for (int y = 0; y < Ny; y++){
        for (int x = 0; x < Nx; x++){
            int index=spatial_array(x,y);
            
            datas->densities[index]=0;
            datas->speed_x[index]=0;
            datas->speed_y[index]=0;
            datas->temperature[index]=0;
        }
    }
    
    //#pragma omp parallel for collapse(2)
    for (int y = 0; y < Ny; y++){
        for (int x = 0; x < Nx; x++){
            for (int i = 0; i < M; i++){
                int index=spatial_array(x,y);
                int index2=phase_array(x,y,i);
                datas->densities[index]+=datas->f[index2];
                datas->speed_x[index]+=datas->f[index2]*params->directions[2*i];
                datas->speed_y[index]+=datas->f[index2]*params->directions[2*i+1];
                datas->temperature[index]+=datas->g[index2];
            }
        }
    }
    
    //#pragma omp parallel for collapse(2)
    for (int y = 0; y < Ny; y++){
        for (int x = 0; x < Nx; x++){
            int index=spatial_array(x,y);
            
           if(datas->map[index]!=0){
            datas->speed_x[index]=0;
            datas->speed_y[index]=0;
            datas->densities[index]=0;
            datas->temperature[index]=datas->temperature[index]/RHO_0;
            if(y==100 && x==115)printf("%f\n",datas->temperature[index]);
           }else{
            
            datas->temperature[index]=datas->temperature[index]/(datas->densities[index]+EPSILON);
            
            datas->speed_x[index]=(datas->speed_x[index]+f_x*params->dt*0.5)/(datas->densities[index]+EPSILON);
            datas->speed_y[index]=(datas->speed_y[index]+f_y*params->dt*0.5)/(datas->densities[index]+EPSILON);
           }
        }
    }
}

void dump_temperature_to_file(struct Datas* datas, const char *filename) {
    FILE *file = fopen(filename, "w");
    if (file == NULL) {
        perror("Erreur lors de l'ouverture du fichier");
        return;
    }

    for (int y = 0; y < Ny; y++) {
        for (int x = 0; x < Nx; x++) {
            fprintf(file, "%lf %lf ", datas->temperature[spatial_array(x, y)],datas->densities[spatial_array(x, y)]);
        }

        fprintf(file, "\n");
    }

    fclose(file);
}

void read_init_file(struct Datas* datas, struct LBM_parameters* params){
    FILE *fichier;
    // Chemin du fichier à ouvrir en mode lecture ("r" pour read)
    const char *chemin_fichier = "simMask/duck200.txt";
    fichier = fopen(chemin_fichier, "r");
    // Vérifier si l'ouverture du fichier a réussi
    if (fichier == NULL) {
        fprintf(stderr, "Erreur lors de l'ouverture du fichier.\n");
        return 1; // Quitter le programme avec code d'erreur
    }

    #pragma omp parallel for collapse(2)
    for (int y = 0; y < Ny; y++){
        for (int x = 0; x < Nx; x++){
            for (int i = 0; i < M; i++){
                int index2=phase_array(x,y,i);
                datas->temp[index2]=0;
                datas->f[index2]=params->directions_weights[i]*RHO_0;
                datas->g[index2]=params->directions_weights[i]*T_0;
            }
            int index=spatial_array(x,y);
            datas->map[index]=0;

        }
    }

    double value=0;
    int count=0;
    while (fscanf(fichier, "%lf", &value) == 1) {
        if(count>=Nx*Ny)break;
        int i=count/Nx;
        int j=Nx-1-count%Nx;
        if(value<0.5 ){//&& i>5 && i<Nx-5 && j>5 && j<Ny-5){
            datas->map[spatial_array(i,j)]=1;
            for (int i = 0; i < M; i++){
                int index2=phase_array(i,j,i);
                datas->f[index2]=0;
                //datas->g[index2]=0;
            }
        }
        count++;
    }
}

float uniform_random(float min, float max) {
    return min + ((float)rand() / RAND_MAX) * (max - min);
}

double get_time()
{
    struct timespec now;
    clock_gettime(CLOCK_REALTIME, &now);
    return now.tv_sec + now.tv_nsec*1e-9;
}

