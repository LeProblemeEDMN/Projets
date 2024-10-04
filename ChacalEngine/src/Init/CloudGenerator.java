package Init;

import Init.Clouds.CloudKernel;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import toolbox.maths.Vector3;

import java.nio.FloatBuffer;
import java.util.Random;

public class CloudGenerator {
    public static int WIDTH_CLOUD=250,HEIGHT_CLOUD=250,DEPTH_CLOUD=250;

    public static int cloudTexture(){
        float maxDsit=(float)Math.sqrt(WIDTH_CLOUD*WIDTH_CLOUD+HEIGHT_CLOUD*HEIGHT_CLOUD+DEPTH_CLOUD*DEPTH_CLOUD);
        int nbPoint=10;
        Random rdm=new Random();
        //float[]points={300,400,230,50,100,150};
        float[]points_red=new float[nbPoint*3];
        float[]points_green=new float[nbPoint*3];
        float[]points_blue=new float[nbPoint*3];
        for (int i = 0; i < nbPoint; i++) {
            points_red[3*i]=rdm.nextInt(WIDTH_CLOUD);
            points_red[3*i+1]=rdm.nextInt(HEIGHT_CLOUD);
            points_red[3*i+2]=rdm.nextInt(DEPTH_CLOUD);
            points_green[3*i]=rdm.nextInt(WIDTH_CLOUD);
            points_green[3*i+1]=rdm.nextInt(HEIGHT_CLOUD);
            points_green[3*i+2]=rdm.nextInt(DEPTH_CLOUD);
            points_blue[3*i]=rdm.nextInt(WIDTH_CLOUD);
            points_blue[3*i+1]=rdm.nextInt(HEIGHT_CLOUD);
            points_blue[3*i+2]=rdm.nextInt(DEPTH_CLOUD);
        }

        float[] points_final_red =new float[points_red.length*27];
        float[] points_final_green =new float[points_red.length*27];
        float[] points_final_blue =new float[points_red.length*27];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    int id=(i*9+j*3+k)*points_red.length;
                    for (int l = 0; l < points_red.length/3; l++) {
                        points_final_red[id+3*l]=points_red[l*3]+(i-1)*WIDTH_CLOUD;
                        points_final_red[id+3*l+1]=points_red[l*3+1]+(j-1)*HEIGHT_CLOUD;
                        points_final_red[id+3*l+2]=points_red[l*3+2]+(k-1)*DEPTH_CLOUD;
                        points_final_green[id+3*l]=points_green[l*3]+(i-1)*WIDTH_CLOUD;
                        points_final_green[id+3*l+1]=points_green[l*3+1]+(j-1)*HEIGHT_CLOUD;
                        points_final_green[id+3*l+2]=points_green[l*3+2]+(k-1)*DEPTH_CLOUD;
                        points_final_blue[id+3*l]=points_blue[l*3]+(i-1)*WIDTH_CLOUD;
                        points_final_blue[id+3*l+1]=points_blue[l*3+1]+(j-1)*HEIGHT_CLOUD;
                        points_final_blue[id+3*l+2]=points_blue[l*3+2]+(k-1)*DEPTH_CLOUD;
                    }
                }
            }
        }
        FloatBuffer buffer= CloudKernel.generate(WIDTH_CLOUD,HEIGHT_CLOUD,DEPTH_CLOUD, points_final_red,points_final_green,points_final_blue,(int)maxDsit);
        int texID = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL12.GL_TEXTURE_3D, texID);
        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_WRAP_R, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL12.glTexImage3D(GL12.GL_TEXTURE_3D, 0, GL11.GL_RGB, WIDTH_CLOUD, HEIGHT_CLOUD, DEPTH_CLOUD,0,
                GL11.GL_RGB, GL11.GL_FLOAT,buffer);
        return texID;
    }
}
