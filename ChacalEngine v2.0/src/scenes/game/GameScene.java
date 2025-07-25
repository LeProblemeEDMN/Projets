package scenes.game;

import entity.Camera;
import entity.Entity;
import entity.Light.PointLight;
import entity.Light.SpotLight;
import loading.Loader;
import loading.RawModel;
import loading.TerrainLoader;
import loading.Texture.ModelTexture;
import loading.TexturedModel;
import main.DisplayManager;
import main.Pipeline;
import scenes.Scene;
import toolbox.InputManager;
import toolbox.MouseBinding;
import toolbox.maths.Vector3;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class GameScene extends Scene {
    public GameScene(Loader loader){
        canMouseBeGrabbed=true;

        float base_intensity=5000;
        //soleil
        //pointLigths.add(new PointLight( new Vector3(1f,1f,0.8f),new Vector3(1,0,0),new Vector3(1000,150000,-1000)));
        pointLigths.add(new PointLight( new Vector3(1f,1f,0.8f).getMul(3),new Vector3(1,0,0),new Vector3(100000,350000,-10000)));
        //0 135 || 10 120 || 20 90 || 30 70 || 50 50
        Random random=new Random(10);
        for (int i = 0; i < 0; i++) {
            pointLigths.add(new PointLight( new Vector3(random.nextFloat(),random.nextFloat(),random.nextFloat()).getMul(base_intensity),new Vector3(1,0.0,1),new Vector3(random.nextFloat()*200-100,random.nextFloat()*100,random.nextFloat()*200-100)));
            spots.add(new SpotLight(new Vector3(random.nextFloat(),random.nextFloat(),random.nextFloat()).getMul(base_intensity),new Vector3(1,0.0,1),new Vector3(random.nextFloat()*300-150,random.nextFloat()*150,random.nextFloat()*300-150),new Vector3(random.nextFloat()*360,random.nextFloat()*360,0),60+random.nextFloat()*10));
        }

        //charge les objets du monde

        //pas d'ambient occlusion shineDamper=20 et pas du metal.
        int ARM_base_texture=loader.loadTexture("ARMbase.png");

        RawModel terrain= TerrainLoader.loadHeightMap("res/height_map.png",400,100,3);

        TexturedModel cubeT=new TexturedModel(new ModelTexture(loader.loadTexture("jadeColor.png"),ARM_base_texture),terrain);
        cubeT.getTexture().setTransparance(true);

        float size=0;

        ModelTexture g=new ModelTexture("elephant");

        ModelTexture r=new ModelTexture(loader.loadTexture("red.png"),ARM_base_texture);
        r.setTransparance(true);
        TexturedModel cube_vert=new TexturedModel(g,"res/LOD/elephant");
        cube_vert.getTexture().setTransparance(true);
        Entity ground=new Entity(cubeT, new Vector3(-size/2,-size-20,-size/2), 0, 0, 0, 1);
        ground.setLarge(true);
        entities.add(ground);

        Random rand = new Random(1);

        for (int i = 0; i < 1; i++) {
                Entity e=new Entity(cube_vert,new Vector3( rand.nextDouble(-150,150), rand.nextDouble(-0,150)*0+15,rand.nextDouble(-150,150)),0,0,0,15);
                entities.add(e);
        }
        this.set();
    }

    @Override
    public void update(float dt) {
        Camera cam= Pipeline.camera;
        if(InputManager.escape.isPressed())glfwSetInputMode(DisplayManager.window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        if(InputManager.grab.isPressed()){
            glfwSetInputMode(DisplayManager.window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            cam.lp[0]= MouseBinding.getMouseX();
            cam.lp[1]=MouseBinding.getMouseY();
        }
        //on test si le curseur est grabbed
        if(glfwGetInputMode(DisplayManager.window, GLFW_CURSOR)==GLFW_CURSOR_DISABLED) {

            cam.rotation.x=cam.rotation.x+(float)(MouseBinding.getMouseY() -cam.lp[1]);
            cam.rotation.y=cam.rotation.y+(float)(MouseBinding.getMouseX()-cam.lp[0]);
            cam.lp[0]=MouseBinding.getMouseX();
            cam.lp[1]=MouseBinding.getMouseY();

            if(cam.rotation.x>90) cam.rotation.x=90;
            if(cam.rotation.x<-90) cam.rotation.x=-90;
            float vit=80*DisplayManager.getFrameTimeSecond();
            if(InputManager.up.isPressed()) {
                cam.getPosition().add(cam.getForward(-vit));
            }
            if(InputManager.down.isPressed()) {
                cam.getPosition().add(cam.getForward(vit));
            }
            if(InputManager.right.isPressed()) {
                cam.getPosition().add(cam.getRight(vit));
            }
            if(InputManager.left.isPressed()) {
                cam.getPosition().add(cam.getRight(-vit));
            }
            if(InputManager.jump.isPressed()) {
                cam.getPosition().y+=vit;
            }
            if(InputManager.sneak.isPressed()) {
                cam.getPosition().y-=vit;
            }

        }
    }
}
