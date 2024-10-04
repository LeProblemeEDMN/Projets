package loading;

import entity.BoundingBox;
import entity.Triangle;
import entity.Vector3;
import org.lwjgl.util.vector.*;
import utils.MathsUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Loader {
    public static BoundingBox loadOBJ(String objFileName, Vector3 pos, Vector3 rotation, float scale) {
        FileReader isr = null;
        File objFile = new File( objFileName );
        try {
            isr = new FileReader(objFile);
        } catch (FileNotFoundException e) {
            System.err.println("File not found in res folder! "+ objFileName);
            System.exit(-1);
        }
        BufferedReader reader = new BufferedReader(isr);
        String line="";
        List<Vector3> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Vector3> normals = new ArrayList<Vector3>();
        List<Integer> indices = new ArrayList<Integer>();
        Vector3 minVec=new Vector3();
        Vector3 maxVec=new Vector3();

        Matrix4f transfo= MathsUtils.createTransformationMatrix(new Vector3f(pos.x,pos.y,pos.z), rotation.x, rotation.y, rotation.z, scale);

        try {
            while (true) {

                line = reader.readLine();
                if (line.startsWith("v ")) {

                    String[] currentLine = line.split(" ");
                    Vector4f vertex = new Vector4f( Float.valueOf(currentLine[currentLine.length-3]),
                             Float.valueOf(currentLine[currentLine.length-2]), Float.valueOf(currentLine[currentLine.length-1]),1);
                    vertex=Matrix4f.transform(transfo,vertex,null);
                    Vector3 a=new Vector3(vertex.x, vertex.y, vertex.z);

                    vertices.add(a);
                    getMaxAndMin(minVec, maxVec, a);

                } else if (line.startsWith("vt ")) {
                    String[] currentLine = line.split(" ");
                    Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    String[] currentLine = line.split(" ");
                    Vector4f normal = new Vector4f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]),0);
                    normal=Matrix4f.transform(transfo,normal,null);
                    Vector3 a=new Vector3(normal.x, normal.y, normal.z);
                    normals.add(a);
                } else if (line.startsWith("f ")) {
                    break;
                }
            }

            List<Triangle>triangles=new ArrayList<>();
            textures.add(0,new Vector2f(0,0));
            while (line != null && line.startsWith("f ")) {
                String[] currentLine = line.split(" ");

                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");

                //PREND PREMIERE NORMALE
                Triangle t=new Triangle(vertices.get(Integer.parseInt(vertex1[0])-1),vertices.get(Integer.parseInt(vertex2[0])-1),vertices.get(Integer.parseInt(vertex3[0])-1));//,normals.get(Integer.parseInt(vertex1[2])-1)
                t.setTextA(textures.get(Integer.parseInt(vertex1[1])-1));
                t.setTextB(textures.get(Integer.parseInt(vertex2[1])-1));
                t.setTextC(textures.get(Integer.parseInt(vertex3[1])-1));

                triangles.add(t);

                line = reader.readLine();
            }
            reader.close();

            BoundingBox box=new BoundingBox(minVec.x, maxVec.x, minVec.y, maxVec.y, minVec.z, maxVec.z);
            box.triangles=triangles;
            box.calculateBB();

            return box;
            //return triangles;
        } catch (IOException e) {

            System.err.println("Error reading the file "+e);
            System.exit(-1);
        }
    return null;
    }
    public static void getMaxAndMin(Vector3 min,Vector3 max,Vector3 vec) {
        if(min.x>vec.x) {
            min.x=vec.x;
        }else if(max.x<vec.x){
            max.x=vec.x;
        }
        if(min.y>vec.y) {
            min.y=vec.y;
        }else if(max.y<vec.y){
            max.y=vec.y;
        }
        if(min.z>vec.z) {
            min.z=vec.z;
        }else if(max.z<vec.z){
            max.z=vec.z;
        }
    }
    public static void getMaxAndMinTriangle(Vector3 min,Vector3 max,Vector3 vecA,Vector3 vecB,Vector3 vecC) {
       getMaxAndMin(min,max,vecA);
        getMaxAndMin(min,max,vecB);
        getMaxAndMin(min,max,vecC);
    }
}
