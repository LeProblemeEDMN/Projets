package loading;

import main.MainLoop;
import org.joml.Vector2f;
import org.joml.Vector3f;
import toolbox.maths.AxisAlignedBB;
import toolbox.maths.Vector3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TerrainLoader {
    public static RawModel loadHeightMap(String fileName,float size,float amplitude,float nb_tile){
        /*
        Create a terrain mesh from a heightMap. The mesh will be a square on the  x-z axis and the y component will be
        given by the height map
        fileName: name of the image. The red channel will be the height of the model.
        size: size of the squre mesh.
        amplitude: amplitude of the height difference (0 in the heightmap is an ehight of 0 and 255 an height of amplitude).
        nb_tile: number of repetition of the texture per axis

         */
        float[][] heights;
        int width=0;
        int height=0;
        float minHeight=amplitude;
        float maxHeight=0;
        //read the height from the image
        try {
            BufferedImage img= ImageIO.read(new File(fileName));

            width=img.getWidth()-1;
            height=img.getHeight()-1;
            heights=new float[img.getWidth()-1][img.getHeight()-1];
            for (int i = 0; i < img.getWidth()-1; i++) {
                for (int j = 0; j < img.getHeight()-1; j++) {
                    heights[i][j]=(new Color(img.getRGB(i, j)).getRed()+new Color(img.getRGB(i+1, j)).getRed()+new Color(img.getRGB(i, j+1)).getRed()+new Color(img.getRGB(i+1, j+1)).getRed())/(4*255.0f)*amplitude;
                    minHeight=Math.min(heights[i][j],minHeight);
                    maxHeight=Math.max(heights[i][j],maxHeight);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //create the arrays
        int number_triangles=(width-1)*(height-1)*2;

        float[] positions=new float[number_triangles*3*3];
        float[] normals=new float[number_triangles*3*3];
        float[] tangents=new float[number_triangles*3*3];
        float[] textures=new float[number_triangles*3*2];
        int[] indices=new int[number_triangles*3];

        float dx=size/width;
        float dy=size/height;
        float offset=size/2;
        int id_triangle=0;
        float iw=1.0f/width*nb_tile;
        float ih=1.0f/height*nb_tile;

        //fill the arrays
        for (int i = 0; i < width-1; i++) {
            for (int j = 0; j < height-1; j++) {

                Vector3 pmm=new Vector3(dx*i-offset,heights[i][j],dy*j-offset);
                Vector3 pmp=new Vector3(dx*i-offset,heights[i][j+1],dy+dy*j-offset);
                Vector3 ppp=new Vector3(dx+dx*i-offset,heights[i+1][j+1],dy+dy*j-offset);
                Vector3 ppm=new Vector3(dx+dx*i-offset,heights[i+1][j],dy*j-offset);

                //process the first triangle
                //compute the normal
                Vector3 normal=pmm.getSub(pmp).cross(pmm.getSub(ppm)).normalize();
                if(normal.y<0)normal=normal.getMul(-1);

                //add the positions
                addVectorToArray(positions,pmm,id_triangle*3);
                addVectorToArray(positions,pmp,id_triangle*3+3);
                addVectorToArray(positions,ppm,id_triangle*3+6);
                //add the normal
                addVectorToArray(normals,normal,id_triangle*3);
                addVectorToArray(normals,normal,id_triangle*3+3);
                addVectorToArray(normals,normal,id_triangle*3+6);

                Vector3 tangent=ppm.getSub(pmm);
                addVectorToArray(tangents,tangent,id_triangle*3);
                addVectorToArray(tangents,tangent,id_triangle*3+3);
                addVectorToArray(tangents,tangent,id_triangle*3+6);

                textures[2*id_triangle]=(i*iw);
                textures[2*id_triangle+1]=j*ih;
                textures[2*id_triangle+2]=i*iw;
                textures[2*id_triangle+3]=(j+1)*ih;
                textures[2*id_triangle+4]=(i+1)*iw;
                textures[2*id_triangle+5]=j*ih;
                id_triangle+=3;


                //process the second triangle
                //compute the normal
                 normal=ppp.getSub(pmp).cross(ppp.getSub(ppm)).normalize();
                if(normal.y<0)normal=normal.getMul(-1);
                //add the positions
                addVectorToArray(positions,ppp,id_triangle*3);
                addVectorToArray(positions,pmp,id_triangle*3+3);
                addVectorToArray(positions,ppm,id_triangle*3+6);
                //add the normal
                addVectorToArray(normals,normal,id_triangle*3);
                addVectorToArray(normals,normal,id_triangle*3+3);
                addVectorToArray(normals,normal,id_triangle*3+6);

                 tangent=ppm.getSub(pmm);
                addVectorToArray(tangents,tangent,id_triangle*3);
                addVectorToArray(tangents,tangent,id_triangle*3+3);
                addVectorToArray(tangents,tangent,id_triangle*3+6);

                textures[2*id_triangle]=(i+1)*iw;
                textures[2*id_triangle+1]=(j+1)*ih;
                textures[2*id_triangle+2]=i*iw;
                textures[2*id_triangle+3]=(j+1)*ih;
                textures[2*id_triangle+4]=(i+1)*iw;
                textures[2*id_triangle+5]=j*ih;
                id_triangle+=3;
            }
        }
        for(int j=0;j<number_triangles*3;j++) {
            indices[j]=j;
        }
        RawModel model= MainLoop.LOADER.loadToVOA(positions, textures, normals,tangents, indices);
        model.setAabb(new AxisAlignedBB(new Vector3(-offset,offset,minHeight), new Vector3(offset,offset,maxHeight)));
        return model;
    }

    public static void addVectorToArray(float[] arr, Vector3 v,int b){
        arr[b]=v.x;
        arr[b+1]=v.y;
        arr[b+2]=v.z;
    }
}
