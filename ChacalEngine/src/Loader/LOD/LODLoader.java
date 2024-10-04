package Loader.LOD;

import Loader.NormalObjLoader.VertexNM;
import Loader.RawModel;
import Main.MainLoop;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import toolbox.maths.AxisAlignedBB;
import toolbox.maths.Vector3;

import javax.sound.midi.Soundbank;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LODLoader {
    public static RawModel LOD(String path) {
        FileReader isr = null;
        File objFile = new File(path);
        try {
            isr = new FileReader(objFile);
        } catch (FileNotFoundException e) {
            System.err.println("File not found in res; don't use any extention");
        }
        BufferedReader reader = new BufferedReader(isr);
        String line="init line";
        List<Point> vertices = new ArrayList<Point>();
        List<Vector2f> textures = new ArrayList<Vector2f>();
        List<Vector3> normals = new ArrayList<Vector3>();
        List<Integer>indices=new ArrayList<>();
        List<Triangle>triangles=new ArrayList<>();

        Vector3 min=new Vector3(999,999,999),max=new Vector3(-999,-999,-999);
        try {
            while (true) {
                line = reader.readLine();
                if (line.startsWith("v ")) {
                    String[] currentLine = line.split(" ");
                    Vector3 vertex = new Vector3((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]),
                            (float) Float.valueOf(currentLine[3]));
                    Point point=new Point(vertex,vertices.size());
                    vertices.add(point);

                    getMaxAndMin(min, max, vertex);

                } else if (line.startsWith("vt ")) {
                    String[] currentLine = line.split(" ");
                    Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]));
                    textures.add(texture);
                } else if (line.startsWith("vn ")) {
                    String[] currentLine = line.split(" ");
                    Vector3 normal = new Vector3((float) Float.valueOf(currentLine[1]),
                            (float) Float.valueOf(currentLine[2]),
                            (float) Float.valueOf(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) {
                    break;
                }

            }
            while (line != null && line.startsWith("f ")) {
                //System.out.println(line);
                String[] currentLine = line.split(" ");
                String[] vertex1 = currentLine[1].split("/");
                String[] vertex2 = currentLine[2].split("/");
                String[] vertex3 = currentLine[3].split("/");
                Point v0 = processVertex(vertex1, vertices, indices,textures,normals);
                Point v1 = processVertex(vertex2, vertices, indices,textures,normals);
                Point v2 = processVertex(vertex3, vertices, indices,textures,normals);

                Triangle triangle=new Triangle(v0,v1,v2);
                triangles.add(triangle);
                //calculateTangents(v0, v1, v2, textures);//NEW
                if(currentLine.length>=5){
                    String[] vertex4 = currentLine[4].split("/");
                    Point v4=processVertex(vertex4, vertices, indices,textures,normals);

                    v0=processVertex(vertex1, vertices, indices,textures,normals);
                    v2=processVertex(vertex3, vertices, indices,textures,normals);
                    triangle=new Triangle(v4,v0,v2);
                    triangles.add(triangle);
                    // calculateTangents(v4, v0, v2, textures);
                }
                line = reader.readLine();
            }
            reader.close();
        }catch (IOException e) {
            System.err.println("Error reading the file LOD");
        }
        float size=triangles.size();
        doLOD(vertices,triangles);
        System.out.println("GAIN:  "+((float)triangles.size()/size)+" "+(triangles.size()-size));
        for (Triangle t:triangles) {
            indices.add(t.getA().getId());
            indices.add(t.getB().getId());
            indices.add(t.getC().getId());
        }

        float[] verticesArray = new float[vertices.size() * 3];
        float[] texturesArray = new float[vertices.size() * 2];
        float[] normalsArray = new float[vertices.size() * 3];
        float furthest = convertDataToArrays(vertices, verticesArray,
                texturesArray, normalsArray);
        int[] indicesArray = convertIndicesListToArray(indices);
        System.out.println(verticesArray);
        RawModel model= MainLoop.LOADER.loadToVOA(verticesArray, texturesArray, normalsArray, indicesArray,path);
        model.setAabb(new AxisAlignedBB(min, max));
        return model;
    }

    private static void doLOD(List<Point>vertices,List<Triangle>triangles){
        List<Polygon>polygons=new ArrayList<>();
        for (Point v:vertices) {
            Polygon p=Polygon.createPolygon(v,triangles);
            if(p!=null)polygons.add(p);
        }
        //System.out.println(triangles.size());
        for (Polygon p: polygons) {
            p.simplifyPolygon(triangles);
        }
    }


    private static Point processVertex(String[] vertex, List<Point> vertices,
                                          List<Integer> indices,List<Vector2f>textures,List<Vector3>norms) {
        int index = Integer.parseInt(vertex[0]) - 1;
        Point currentVertex = vertices.get(index);
        int textureIndex = Integer.parseInt(vertex[1]) - 1;
        int normalIndex = Integer.parseInt(vertex[2]) - 1;
        if (!currentVertex.isLock()) {
            currentVertex.updateText(textures.get(textureIndex).x,textures.get(textureIndex).y);
            currentVertex.updateNorm(norms.get(normalIndex));
        }
       // indices.add(index);
        return currentVertex;
    }

    private static int[] convertIndicesListToArray(List<Integer> indices) {
        int[] indicesArray = new int[indices.size()];
        for (int i = 0; i < indicesArray.length; i++) {
            indicesArray[i] = indices.get(i);
        }
        return indicesArray;
    }

    private static float convertDataToArrays(List<Point> vertices,  float[] verticesArray, float[] texturesArray,
                                             float[] normalsArray) {
        float furthestPoint = 0;
        for (int i = 0; i < vertices.size(); i++) {
            Point currentVertex = vertices.get(i);
            /*if (currentVertex.getPosition(). > furthestPoint) {
                furthestPoint = currentVertex.getLength();
            }*/
            Vector3 position = currentVertex.getPosition();
            //Vector2f textureCoord = currentVertex.;
            Vector3 normalVector = currentVertex.norm;
            //Vector3f tangent = currentVertex.getAverageTangent();
            verticesArray[i * 3] = position.x;
            verticesArray[i * 3 + 1] = position.y;
            verticesArray[i * 3 + 2] = position.z;
            texturesArray[i * 2] = currentVertex.tx;
            texturesArray[i * 2 + 1] = 1 - currentVertex.ty;
            normalsArray[i * 3] = normalVector.x;
            normalsArray[i * 3 + 1] = normalVector.y;
            normalsArray[i * 3 + 2] = normalVector.z;
            /*tangentsArray[i * 3] = tangent.x;
            tangentsArray[i * 3 + 1] = tangent.y;
            tangentsArray[i * 3 + 2] = tangent.z;*/

        }
        return furthestPoint;
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
}
