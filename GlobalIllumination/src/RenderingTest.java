import entity.*;
import loading.Loader;
import org.lwjgl.util.vector.Vector2f;
import rendering_raytracing.Scene;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RenderingTest {
    public static Scene scene() throws IOException {
        List<Entity> entities = new ArrayList<>();
        List<Triangle> triangles = new ArrayList<>();
        triangles.add(new Triangle(new Vector3(-100,0,-100),new Vector3(100,0,-100),new Vector3(-100,0,100)));
        triangles.get(0).setTextA(new Vector2f());
        triangles.get(0).setTextB(new Vector2f(1,0));
        triangles.get(0).setTextC(new Vector2f(0,1));
        triangles.add(new Triangle(new Vector3(100,0,100),new Vector3(100,0,-100),new Vector3(-100,0,100)));
        triangles.get(1).setTextA(new Vector2f(1,1));
        triangles.get(1).setTextB(new Vector2f(1,0));
        triangles.get(1).setTextC(new Vector2f(0,1));
        List<Triangle> triangles2 = new ArrayList<>();

        triangles2.add(new Triangle(new Vector3(10,0,10),new Vector3(0,0,10),new Vector3(10,10,10)));
        triangles2.get(0).setTextA(new Vector2f(1,0));
        triangles2.get(0).setTextB(new Vector2f(0,0));
        triangles2.get(0).setTextC(new Vector2f(1,1));
        triangles2.add(new Triangle(new Vector3(0,10,10),new Vector3(0,0,10),new Vector3(10,10,10)));
        triangles2.get(1).setTextA(new Vector2f(0,1));
        triangles2.get(1).setTextB(new Vector2f(0,0));
        triangles2.get(1).setTextC(new Vector2f(1,1));
        List<Triangle> triangles3 = new ArrayList<>();
        triangles3.add(new Triangle(new Vector3(8,0,8),new Vector3(3,0,8),new Vector3(8,5,8)));
        triangles3.get(0).setTextA(new Vector2f(1,0));
        triangles3.get(0).setTextB(new Vector2f(0,0));
        triangles3.get(0).setTextC(new Vector2f(1,1));

        triangles3.add(new Triangle(new Vector3(3,5,8),new Vector3(3,0,8),new Vector3(8,5,8)));
        triangles3.get(1).setTextA(new Vector2f(0,1));
        triangles3.get(1).setTextB(new Vector2f(0,0));
        triangles3.get(1).setTextC(new Vector2f(1,1));

        BoundingBox face_bas = Loader.loadOBJ("res/face.txt",new Vector3(0,0,0),new Vector3(90,0,0),20);
        BoundingBox face_gauche = Loader.loadOBJ("res/face.txt",new Vector3(0,0,0),new Vector3(0,-90,0),20);
        BoundingBox face_droit = Loader.loadOBJ("res/face.txt",new Vector3(20,0,20),new Vector3(0,90,0),20);
        BoundingBox face_avant = Loader.loadOBJ("res/face.txt",new Vector3(20,0,20),new Vector3(0,180,0),20);
        BoundingBox face_arr = Loader.loadOBJ("res/face.txt",new Vector3(0,0,0),new Vector3(0,0,0),20);


        BoundingBox dragon = Loader.loadOBJ("res/dragon.obj",new Vector3(10,3,8),new Vector3(0,0,0),0.5f);

        Material brick = new Material(0,0.4f,0, ImageIO.read(new File("res/img.png")));
        Material mirror = new Material(0,0.8f,(float) Math.toRadians(4),ImageIO.read(new File("res/white.bmp")));
        Material glass = new Material(1,5,(float) Math.toRadians(2),ImageIO.read(new File("res/white.bmp")));
        Material white = new Material(0,0.4f,(float) Math.toRadians(1),ImageIO.read(new File("res/white.bmp")));
        Material red = new Material(0,0.4f,(float) Math.toRadians(1),ImageIO.read(new File("res/red.bmp")));

        //entities.add(new Entity(triangles3, glass));
        entities.add(new Entity(face_bas, white));
        entities.add(new Entity(face_avant, white));
        entities.add(new Entity(face_arr, white));
        entities.add(new Entity(face_gauche, white));
        entities.add(new Entity(face_droit, white));
         entities.add(new Entity(triangles2, white));
        entities.add(new Entity(dragon, red));
        List<Light>lights = new ArrayList<>();
        lights.add(new Light(new Vector3(-20000,100000,-15000),new Vector3(1.3,1.3,1),new Vector3(1,0,0),Float.MAX_VALUE));
        lights.get(0).lightRadius=15000;
        //lights.add(new Light(new Vector3(15,5,2),new Vector3(0.4,0.2,0.2),new Vector3(1,0.05,0.0002),Float.MAX_VALUE));


        Scene sc= new Scene();
        sc.lights=lights;
        sc.entities=entities;
        return sc;
    }
}
