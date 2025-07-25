package loading.LOD;

import org.joml.Vector2f;
import org.joml.Vector3f;
import toolbox.maths.Vector3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

public class LOD {
    public static void LOD(String filenameIn,String filenameOut,int nbremove){
        /*
        Utilise l'algo Edge Collapse pour reduire le nompre de triangles du modèle.
        A chaque itération le plus petit arc est enleve et les deux points le formant sont fusionné.
        A chaque fois on retire tt les triangles utilisant cet arc (1 en général il semblerait) et 1 sommet.

        Upgrade-> ne pas recalculer tt les arc à chaque fois. -> en chosir K d'un coup et faire tt les modifs avant de retrier les arcs.
         */
        List<Vertex> vertices=new ArrayList<>();
        List<Triangle> triangles=new ArrayList<>();

        load(filenameIn, vertices, triangles);
        System.out.println(triangles.size());
        List<Vertex> removed=new ArrayList<>();

        //compute the neighbors of each vertex
        HashMap<Vertex,List<Vertex>> neighbours=new HashMap<>();
        for (Vertex v : vertices) neighbours.put(v, new ArrayList<>());
        for (Triangle t : triangles) {
            addNeighs(t.a,t.b,neighbours.get(t.c));
            addNeighs(t.b,t.c,neighbours.get(t.a));
            addNeighs(t.a,t.c,neighbours.get(t.b));
        }
        int totalRemoval=0;
        int initTriangle=triangles.size();
        List<Edge> edge=new ArrayList<>();
        int idLookMax=-1;
        int idLook=0;
        for (int nb = 0; nb < nbremove; nb++) {

            //look for the shortest edge
            if(idLook>=idLookMax){
                System.out.println("Iteration:"+nb+"/"+nbremove+" LOD triangles: "+(initTriangle-totalRemoval)+" Initial triangles"+initTriangle+" triangles ("+(float)totalRemoval/initTriangle*100+"%)");
                edge.clear();
                for (Map.Entry<Vertex,List<Vertex>> e:neighbours.entrySet()){
                    Vertex a=e.getKey();
                    //on test si la pos x de a est plus petite que b sinon on ajouterai l'arc a->b et l'arc b->a (permet aussi d'avoir 2 fois moins d'arc a trier).
                    for (Vertex b : e.getValue())if(a.pos.x<=b.pos.x)edge.add(new Edge(a,b));
                }
                edge.sort(new Comparator<Edge>() {
                    @Override
                    public int compare(Edge o1, Edge o2) {
                        return Float.compare(o1.length,o2.length);
                    }
                });
                idLook=0;
                idLookMax=(int)(edge.size()*0.05f)+1;
            }
            Vertex a_m = edge.get(idLook).a;
            Vertex b_m = edge.get(idLook).b;
            boolean f_a=removed.contains(a_m);
            while ((f_a || removed.contains(b_m)) && idLook<idLookMax){
                idLook++;
                if(!f_a)neighbours.get(a_m).remove(b_m);
                a_m = edge.get(idLook).a;
                b_m = edge.get(idLook).b;
                f_a=removed.contains(a_m);
            }
            if(idLook>idLookMax){continue;}
            idLook++;

            List<Vertex> neighbours_a=neighbours.get(a_m);
            List<Triangle> removeTriangles=new ArrayList<>();
            for (Triangle t : triangles) {
                int count =(t.a.equals(a_m) ?1:0) + (t.a.equals(b_m) ?1:0)+(t.b.equals(a_m) ?1:0) + (t.b.equals(b_m) ?1:0)+(t.c.equals(a_m) ?1:0) + (t.c.equals(b_m) ?1:0);
                if(count>=2){
                    removeTriangles.add(t);
                }
            }
            //on evite le crash
            if(removed.contains(b_m) || neighbours_a==null){
                if(neighbours_a!=null)neighbours_a.remove(b_m);
                //System.out.println("ERROR");
                continue;
            }

            //interpole le point
            Vertex nV=new Vertex(a_m.pos.getAdd(b_m.pos).getMul(0.5f));
            nV.set(a_m.normal.getAdd(b_m.normal).getMul(0.5f),a_m.uv.getAdd(b_m.uv).getMul(0.5f));

            //neigbours of the new vertex
            List<Vertex> nV_neigh=new ArrayList<>();
            nV_neigh.addAll(neighbours_a);
            for (Vertex v:neighbours.get(b_m)) {
                if(v!=a_m && !nV_neigh.contains(v)){
                    nV_neigh.add(v);
                }
            }

            //remove the triangle
            triangles.removeAll(removeTriangles);

            //les points aux mêmes coordonées sont déplacé également
            for(Vertex v:vertices){
                if(v.pos.x<=a_m.pos.x && v.pos.equalv3(a_m.pos) && v!=a_m){
                    v.pos.copy(nV.pos);
                }else if(v.pos.equalv3(b_m.pos) && v!=b_m){
                    v.pos.copy(nV.pos);
                }
            }

            //rempalce les sommets qu'on a supprimé par le nouveaux vertex
            for (Triangle t : triangles) {
                if (t.a.equals(a_m)|| t.a.equals(b_m)){
                    t.a=nV;
                    neighbours.get(t.c).add(nV);
                    neighbours.get(t.b).add(nV);
                }
                if (t.b.equals(a_m)|| t.b.equals(b_m)){

                    t.b=nV;
                    neighbours.get(t.a).add(nV);
                    neighbours.get(t.c).add(nV);
                }
                if (t.c.equals(a_m)|| t.c.equals(b_m)){

                    neighbours.get(t.a).add(nV);
                    neighbours.get(t.b).add(nV);
                    t.c=nV;
                }
            }

            //on enleve a_m et b_m des liste des voisins
            for(Vertex v:neighbours_a){
                List<Vertex> vl=neighbours.get(v);
                if(vl!=null)vl.remove(a_m);
            }
            for(Vertex v:neighbours.get(b_m)){
                List<Vertex> vl=neighbours.get(v);
                if(vl!=null)vl.remove(b_m);
            }

            vertices.remove(a_m);
            vertices.remove(b_m);
            vertices.add(nV);


            removed.add(a_m);
            removed.add(b_m);
            neighbours.put(nV,nV_neigh);
            //on s'assure que ya pas de voisins deja enleve
            nV_neigh.removeAll(removed);
            neighbours.remove(a_m);
            neighbours.remove(b_m);
            totalRemoval+=removeTriangles.size();

        }
        List<Vertex> usedVertices=new ArrayList<>();
        for (Triangle t : triangles) {
            if(!usedVertices.contains(t.a)){usedVertices.add(t.a);}
            if(!usedVertices.contains(t.b)){usedVertices.add(t.b);}
            if(!usedVertices.contains(t.c)){usedVertices.add(t.c);}
        }

        write(filenameOut, usedVertices, triangles);
        System.out.println("LOD removed "+totalRemoval+" remaining:"+triangles.size()+" triangles ("+(float)totalRemoval/initTriangle*100+"%)");
    }




    /*
    Sauvegarde les vertex et triangle sous la frome d'un fichier .obj
     */
    public static void write(String filename, List<Vertex> vertices,List<Triangle> triangles){
        HashMap<Vertex,Integer> map_int=new HashMap<>();
        StringBuffer pos_string=new StringBuffer();
        StringBuffer normal_string=new StringBuffer();
        StringBuffer texture_string=new StringBuffer();
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex=vertices.get(i);
            map_int.put(vertex,i);
            pos_string.append("v "+vertex.pos.x+" "+vertex.pos.y+" "+vertex.pos.z+"\n");
            normal_string.append("vn "+vertex.normal.x+" "+vertex.normal.y+" "+vertex.normal.z+"\n");
            texture_string.append("vt "+vertex.uv.x+" "+vertex.uv.y+"\n");
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(pos_string.toString());
            writer.write(normal_string.toString());
            writer.write(texture_string.toString());

            for(Triangle triangle:triangles){
                String line="f";
                int id=map_int.get(triangle.a)+1;
                line+=" "+id+"/"+id+"/"+id;

                id=map_int.get(triangle.b)+1;
                line+=" "+id+"/"+id+"/"+id;
                id=map_int.get(triangle.c)+1;
                line+=" "+id+"/"+id+"/"+id+"\n";
                writer.write(line);
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    /*
     Charge un .obj sous la forme de triangles et de vertex
        */
    public static void load(String filename, List<Vertex> vertices,List<Triangle> triangles) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            //on commence par recupere tout les coordonnées spatiales et de texture et les normales de tout les sommets
            List<Vector3> positions = new ArrayList<Vector3>();
            List<Vector3> normals = new ArrayList<Vector3>();
            List<Vector3> textures = new ArrayList<Vector3>();
            String line="";
            while (true) {
                line = reader.readLine();
                if (line.startsWith("v ")) {
                    String[] currentLine = line.split(" ");
                    Vector3 vertex = new Vector3( Float.valueOf(currentLine[currentLine.length-3]),
                           Float.valueOf(currentLine[currentLine.length-2]), Float.valueOf(currentLine[currentLine.length-1]));
                    positions.add(vertex);

                } else if (line.startsWith("vt ")) {
                    String[] currentLine = line.split(" ");
                    Vector3 texture = new Vector3(Float.valueOf(currentLine[1]),
                            Float.valueOf(currentLine[2]),0);
                    textures.add(texture);

                } else if (line.startsWith("vn ")) {
                    String[] currentLine = line.split(" ");
                    Vector3 normal = new Vector3(Float.valueOf(currentLine[1]),
                            Float.valueOf(currentLine[2]), Float.valueOf(currentLine[3]));
                    normals.add(normal);
                } else if (line.startsWith("f ")) {

                    break;
                }
            }
            for (Vector3 p : positions)
                vertices.add(new Vertex(p));
            //on crée les triangles
            while (line != null && line.startsWith("f ")) {
                String[] currentLine = line.split(" ");

                Vertex a= process(currentLine[1].split("/"),vertices,normals,textures);
                Vertex b= process(currentLine[2].split("/"),vertices,normals,textures);
                Vertex c= process(currentLine[3].split("/"),vertices,normals,textures);
                triangles.add(new Triangle(a,b,c));
                //si le .obj utilise des carrée on les represente par deux triangles.
                if(currentLine.length>=5) {
                    Vertex d= process(currentLine[1].split("/"),vertices,normals,textures);
                    triangles.add(new Triangle(d,a,c));
                }

                line = reader.readLine();
            }

            reader.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void addNeighs(Vertex a,Vertex b,List<Vertex> neigh){
        if(!neigh.contains(a)){
            neigh.add(a);
        }
        if(!neigh.contains(b)){neigh.add(b);}
    }

    //set a vertex normal and texture. If the texture already set and different create a new vertex
    public static Vertex process(String[] vertex1,List<Vertex> vertices,List<Vector3> normals,List<Vector3> textures) {
        int idP=Integer.parseInt(vertex1[0])-1;
        int idT=Integer.parseInt(vertex1[1])-1;
        int idN=Integer.parseInt(vertex1[2])-1;
        Vertex V=vertices.get(idP);
        if(!V.isSet ){
            //System.out.println(normals.size()+ " "+textures.size()+" "+idP+" "+idT+" "+vertex1[1]);
            V.set(normals.get(idN),textures.get(idT));
        }else if(!V.normal.equalv3(normals.get(idN)) || !V.uv.equalv3(textures.get(idT))){
            V=new Vertex(V.pos);

            V.set(normals.get(idN),textures.get(idT));
            vertices.add(V);
        }
        return V;
    }
}

class Edge{
    Vertex a,b;
    float length;

    public Edge(Vertex a, Vertex b) {
        this.a = a;
        this.b = b;
        length=a.pos.squareDistanceTo(b.pos);
    }
}