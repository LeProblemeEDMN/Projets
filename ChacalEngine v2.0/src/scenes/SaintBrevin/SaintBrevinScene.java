package scenes.SaintBrevin;

import entity.Camera;
import entity.Entity;
import entity.Light.PointLight;
import entity.Light.SpotLight;
import loading.Loader;
import loading.RawModel;
import loading.TerrainLoader;
import loading.Texture.ModelTexture;
import loading.TexturedModel;
import main.Constantes;
import main.DisplayManager;
import main.Pipeline;
import rendering.shadow.CascadedShadowMap;
import scenes.Scene;
import toolbox.InputManager;
import toolbox.MouseBinding;
import toolbox.maths.Vector3;

import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class SaintBrevinScene extends Scene {
    private static float shift_x=10;
    private static float shift_y=0;
    private static float shift_z=0;
    public SaintBrevinScene(Loader loader) {
        canMouseBeGrabbed = true;
        CascadedShadowMap.FAR_SKY=25;

        float base_intensity = 5000;
        //soleil
        //pointLigths.add(new PointLight( new Vector3(1f,1f,0.8f),new Vector3(1,0,0),new Vector3(1000,150000,-1000)));
        //pointLigths.add(new PointLight(new Vector3(1f, 1f, 0.8f).getMul(3), new Vector3(1, 0, 0), new Vector3(-100000, 350000, 100000*0)));
        pointLigths.add(new PointLight( new Vector3(1f,1f,0.8f).getMul(3),new Vector3(1,0,0),new Vector3(-50000,350000,30000)));


        //charge les objets du monde

        //pas d'ambient occlusion shineDamper=20 et pas du metal.
        int ARM_base_texture = loader.loadTexture("ARMbase.png");

        ModelTexture r = new ModelTexture(loader.loadTexture("red.png"), ARM_base_texture);
        r.setTransparance(true);

        ModelTexture white = new ModelTexture(loader.loadTexture("white.png"), ARM_base_texture);
        white.setTransparance(true);

        ModelTexture blue = new ModelTexture(loader.loadTexture("blue.png"), ARM_base_texture);
        blue.setTransparance(true);

        ModelTexture bois_clair=new ModelTexture("bois_clair");
        bois_clair.setTransparance(true);
        ModelTexture bois_sombre=new ModelTexture("bois_sombre");
        bois_sombre.setTransparance(true);

        TexturedModel cube_bois_clair=new TexturedModel(bois_clair,"res/LOD/cube");
        TexturedModel cube_bois_sombre=new TexturedModel(bois_sombre,"res/LOD/cube");
        TexturedModel cube_blanc=new TexturedModel(white,"res/LOD/cube");
        TexturedModel cube_blue=new TexturedModel(blue,"res/LOD/cube");


        float size = 0;

        for (int i = -50; i < 50; i++) {
            for (int j = -50; j < 50; j++) {
                Entity ground = new Entity(cube_bois_clair, new Vector3(i*3, -1*3, j*3), 0, 0, 0, 1*3);
                entities.add(ground);
            }
        }
       /* Entity ground = new Entity(cube_bois_clair, new Vector3(3, -1*3, 3), 0, 0, 0, 1*3);
        entities.add(ground);*/
        float e=0.1f;
        float he=e/2;
        //mu gauche
        add_wall(cube_blanc,7.4f,0,e,5.1f);
        //mur droite
        add_wall(cube_blanc,-e,0,e,5.93f);
        //mur fond
        add_wall(cube_blanc,0,-e,7.4f,e);

        //cuisine
        add_element(cube_bois_sombre,6.8f,0.6f,0,0.6f,0.9f,1);
        add_element(cube_bois_sombre,7.4f-3.65f,0,0,3.65f,0.6f,1);
        //separation cuisine couloir
        add_wall(cube_blue,7.4f-3.65f-0.67f,0,0.67f,1.79f);

        //lavabo toilette
        add_element(cube_bois_sombre,0.75f,1.32f,0.8f,0.45f,0.23f,0.2f);
        //add_element(cube_bois_sombre,0.4f,0.54f,0.8f,0.45f,0.23f,0.2f);
        //separation toilette piece vide
        add_wall(cube_blanc,1.94f-0.25f,0.98f,0.25f,0.84f);
        add_wall(cube_blanc,0,0.98f,1.94f,e);
        //bloc toilettes
        add_wall(cube_blanc,0,0.41f,0.54f,0.57f);
        //separation piece vide salon
        add_wall(cube_blanc,0,2.67f,1.94f,0.7f);
        //separation salon grd piece
        add_wall(cube_blanc,3.08f,2.52f,e,3.4f);
        //baie vitré
        add_wall(cube_blanc,3.08f+e,5.1f,1.3f,e);
        add_wall(cube_blanc,6.4f,5.1f,1f,e);
        //fenetre salon
        add_wall(cube_blanc,0,5.92f,0.94f,e);
        add_wall(cube_blanc,2.1f,5.92f,0.98f,e);
        add_element(cube_bois_sombre,0.94f,5.92f,0,1.12f,e,1);
        add_element(cube_bois_sombre,0.94f,5.92f,2,1.12f,e,0.5f);
        //escalier
        Entity esc=new Entity(cube_bois_sombre,new Vector3(3.08f+e,2.72f,2.67f-0.83f),37.2348f,0.0f,0.0f,new Vector3(0.67f,e,4f));
        esc.setLarge(true);
        entities.add(esc);

        // second floor
        //left wall
        add_wall_2(cube_blanc,-e,-0.79f-e,e,2*e+5.79f+0.79f);

        //douche
        add_wall_2(cube_blanc,0,-e,0.79f,e);
        add_wall_2(cube_blanc,0,-0.79f,1.39f,e);
        add_wall_2(cube_blanc,1.39f,-0.79f,e,0.79f);

        //back wall
        add_wall_2(cube_blanc,1.39f,-e,5.94f,e);
        //double penderie
        add_wall_2(cube_blue,1.39f+1.74f,0,1.23f,1.68f);

        //separation sdb chambre1
        add_wall_2(cube_blanc,0,1.13f,0.34f,1.35f);
        add_wall_2(cube_blanc,0,1.13f,0.87f+0.99f+e,e);
        //TODO angle
        add_wall_2(cube_blanc,2.27f,2.48f,0.86f,e);
        add_wall_2(cube_blanc,2.27f,2.48f-0.75f,e,0.75f);

        //separation lavabo wc
        add_element_2(cube_blanc,0.87f,1.13f-0.57f,0,e,0.57f,1);

        //separation chambre 1 couloir
        add_wall_2(cube_blanc,3.08f,2*e+5.79f-2.48f,e,2.48f);
        //mur fond chambre 1
        add_element_2(cube_blanc,0,2*e+5.79f,0,3.08f,e,0.98f);

        //mur droite
        add_wall_2(cube_blanc,7.33f,0,e,4.63f);
        add_wall_2(cube_blanc,7.33f-0.49f,4.63f-0.7f,e,0.7f);
        add_wall_2(cube_blanc,7.33f-0.49f,4.63f,0.49f,e);
        add_wall_2(cube_blanc,7.33f-1.04f,4.63f-0.7f,0.55f,e);
        add_wall_2(cube_blanc,4.14f,4.63f-0.7f,0.4f,e);
        add_wall_2(cube_blanc,4.14f,4.63f-0.7f-1.51f,e,1.51f);
        Entity roof=new Entity(cube_bois_sombre,new Vector3(shift_x,2.44f+shift_y,2.17f+shift_z),23.80f,0.0f,0.0f,new Vector3(3.08f,e,4f));
        roof.setLarge(true);
        entities.add(roof);

        //veranda
        add_element(cube_bois_sombre,3.08f+e,2.8f,0,0.55f,1.2f,0.78f);
        //auvent
        add_element(cube_bois_sombre,7.4f-0.49f,2.5f,0,0.49f,1.51f,0.92f);
        //table
        add_element(cube_bois_sombre,4.6f,2.0f,0f,1.6f,0.71f,0.76f);

        //add_element_2(cube_bois_sombre,0.45f,1.13f+e,0f,1.4f,1.90f,0.7f);
        add_element_2(cube_bois_sombre,0f,2.7f+e,0f,1.9f,1.40f,0.7f);

        add_element_2(cube_bois_sombre,7.33f-2f,0+e,0f,1.4f,1.90f,0.7f);

        this.set();
    }

    public void add_wall(TexturedModel model,float x,float y,float lx,float ly){
        Entity e=new Entity(model,new Vector3(x,-1,y),0,0,0,new Vector3(lx,3.5,ly));
        e.setLarge(true);
        entities.add(e);
    }

    public void add_element(TexturedModel model,float x,float y,float z,float lx,float ly,float lz){
        Entity e=new Entity(model,new Vector3(x,z,y),0,0,0,new Vector3(lx,lz,ly));
        e.setLarge(true);
        entities.add(e);
    }

    public void add_wall_2(TexturedModel model,float x,float y,float lx,float ly){
        Entity e=new Entity(model,new Vector3(x+shift_x,-1+shift_y,y+shift_z),0,0,0,new Vector3(lx,3.44,ly));
        e.setLarge(true);
        entities.add(e);
    }

    public void add_element_2(TexturedModel model,float x,float y,float z,float lx,float ly,float lz){
        Entity e=new Entity(model,new Vector3(x+shift_x,z+shift_y,y+shift_z),0,0,0,new Vector3(lx,lz,ly));
        e.setLarge(true);
        entities.add(e);
    }
    boolean right_pressed_last=false;
    Entity selected_entity=null;
    @Override
    public void update(float dt) {
        Camera cam = Pipeline.camera;
       /* if (InputManager.escape.isPressed()) glfwSetInputMode(DisplayManager.window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        if (InputManager.grab.isPressed()) {
            glfwSetInputMode(DisplayManager.window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            cam.lp[0] = MouseBinding.getMouseX();
            cam.lp[1] = MouseBinding.getMouseY();
        }*/
        if(MouseBinding.isPressedRight()){
            if(!right_pressed_last){
                glfwSetInputMode(DisplayManager.window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                cam.lp[0] = MouseBinding.getMouseX();
                cam.lp[1] = MouseBinding.getMouseY();
            }
            right_pressed_last=true;
        }else{
            if(right_pressed_last)glfwSetInputMode(DisplayManager.window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            right_pressed_last=false;
        }
        //on test si le curseur est grabbed
        if (glfwGetInputMode(DisplayManager.window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED) {

            cam.rotation.x = cam.rotation.x + (float) (MouseBinding.getMouseY() - cam.lp[1]);
            cam.rotation.y = cam.rotation.y + (float) (MouseBinding.getMouseX() - cam.lp[0]);
            cam.lp[0] = MouseBinding.getMouseX();
            cam.lp[1] = MouseBinding.getMouseY();

            if (cam.rotation.x > 90) cam.rotation.x = 90;
            if (cam.rotation.x < -90) cam.rotation.x = -90;
        }
        float vit = 5 * DisplayManager.getFrameTimeSecond();



        if(MouseBinding.isPressedLeft()){
            if(selected_entity==null){
                //find the entity on which the cursor is pointing
                Vector3 ray=Pipeline.mousePicker.getCurrentRay();
                float max_distance=Float.POSITIVE_INFINITY;
                for (Entity e:getEntities()){
                    float inter=e.getAxisAlignedBB().intersectRay(cam.getPosition(),ray);
                    if(inter>=0 && max_distance>inter){
                        max_distance=inter;
                        selected_entity=e;
                    }
                }
              /*  System.out.println("refind"+" "+max_distance);
                System.out.println(cam.getPosition());
                System.out.println(selected_entity.getAxisAlignedBB());*/
            }
            if(selected_entity!=null){
                //System.out.println(selected_entity.getAxisAlignedBB());
                if (InputManager.up.isPressed()) {
                    selected_entity.getPosition().add(cam.getForward(-vit));
                }
                if (InputManager.down.isPressed()) {
                    selected_entity.getPosition().add(cam.getForward(vit));
                }
                if (InputManager.right.isPressed()) {
                    selected_entity.getPosition().add(cam.getRight(vit));
                }
                if (InputManager.left.isPressed()) {
                    selected_entity.getPosition().add(cam.getRight(-vit));
                }
                if (InputManager.jump.isPressed()) {
                    selected_entity.getPosition().y += vit*0.2f;
                }
                if (InputManager.sneak.isPressed()) {
                    selected_entity.getPosition().y -= vit*0.2f;
                }
                if (InputManager.A_letter.isPressed()) {
                    selected_entity.setRotY(selected_entity.getRotY()-vit*18);
                }
                if (InputManager.E_letter.isPressed()) {
                    selected_entity.setRotY(selected_entity.getRotY()+vit*18);
                }
                selected_entity.update();
            }
        }else{
            selected_entity=null;
            if (InputManager.up.isPressed()) {
                cam.getPosition().add(cam.getForward(-vit));
            }
            if (InputManager.down.isPressed()) {
                cam.getPosition().add(cam.getForward(vit));
            }
            if (InputManager.right.isPressed()) {
                cam.getPosition().add(cam.getRight(vit));
            }
            if (InputManager.left.isPressed()) {
                cam.getPosition().add(cam.getRight(-vit));
            }
            if (InputManager.jump.isPressed()) {
                cam.getPosition().y += vit*0.2f;
            }
            if (InputManager.sneak.isPressed()) {
                cam.getPosition().y -= vit*0.2f;
            }
        }


    }
}
