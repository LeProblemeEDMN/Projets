package scenes.mainMenu;

import GUI.GUI;
import GUI.GuiManager;
import GUI.elements.ButtonGUI;
import GUI.elements.GUIImage;
import GUI.font.FontManager;
import entity.Camera;
import entity.Entity;
import entity.Light.PointLight;
import loading.Loader;
import loading.Texture.ModelTexture;
import loading.TexturedModel;
import main.Pipeline;
import org.joml.Vector2f;
import scenes.Scene;
import toolbox.MouseBinding;
import toolbox.maths.Vector3;

public class MainMenuScene extends Scene {

    public static float TILE_SIZE = 20;

    float distance=100;
    float roll_increment=10;
    float baseSpeedRy=2;
    float speedRy=baseSpeedRy;
    float factor=0.4f;
    ButtonGUI options;
    Entity[][] damier;
    public MainMenuScene(Loader loader){
        canMouseBeGrabbed=true;
        pointLigths.add(new PointLight( new Vector3(1f,1f,0.8f).getMul(3),new Vector3(1,0,0),new Vector3(100000,350000,-10000)));

        ModelTexture blackWood=new ModelTexture("bois_sombre");
        blackWood.setTransparance(true);
        ModelTexture whiteWood=new ModelTexture("bois_clair");
        whiteWood.setTransparance(true);
        TexturedModel black_tile=new TexturedModel(blackWood,"res/LOD/cube");
        TexturedModel white_tile=new TexturedModel(whiteWood,"res/LOD/cube");

        int damier_size=5;
        float tile_size=TILE_SIZE;
        damier=new Entity[damier_size][damier_size];
        for(int x=0;x<damier_size;x++){
            for(int y=0;y<damier_size;y++){
                TexturedModel model=(x+y)%2==0?black_tile:white_tile;
                damier[x][y]=new Entity(model,new Vector3(tile_size*x-tile_size*0.5f*damier_size,-tile_size,tile_size*y-tile_size*0.5f*damier_size),0,0,0,tile_size);
                entities.add(damier[x][y]);
            }
        }

        TexturedModel elephant=new TexturedModel(new ModelTexture("elephant"),"res/LOD/elephant");
        entities.add(new Entity(elephant,new Vector3(tile_size,tile_size*0.25f,0),0,90,0,tile_size*0.5f));

        TexturedModel montagne=new TexturedModel(new ModelTexture("montagne"),"res/LOD/montagne");
        entities.add(new Entity(montagne,new Vector3(0,tile_size*0.25f,-tile_size),0,0,0,tile_size*0.5f));
        entities.add(new Entity(montagne,new Vector3(0,tile_size*0.25f,tile_size),0,0,0,tile_size*0.5f));
        entities.add(new Entity(montagne,new Vector3(0,tile_size*0.25f,0),0,0,0,tile_size*0.5f));

        /*int ARM_base_texture=loader.loadTexture("ARMbase.png");
        TexturedModel cubeT=new TexturedModel(new ModelTexture(loader.loadTexture("sol_base.jpg"),ARM_base_texture),"res/LOD/cube");
        cubeT.getTexture().setTransparance(true);
        Entity ground=new Entity(cubeT, new Vector3(-500/2,-500-tile_size/2,-500/2), 0, 0, 0, 500);
        ground.setLarge(true);
        entities.add(ground);*/

        set();

        GUI g=new GUI();
        GuiManager.guis.add(g);
        g.setActive(true);
        g.getElements().add(new GUIImage(new Vector2f(0.87f,0.5f),new Vector2f(0.26f,1f),new Vector3(0.2,0.2,0.2),0.5f));
        ButtonGUI play =new ButtonGUI(new Vector2f(0.85f,0.85f),new Vector2f(0.2f,0.1f),new Vector3(106.0f/255,87.0f/255,133.0f/255), FontManager.fonts.get("cambria"),"Play",new Vector3(1,1,1));
        play.setOnRelease(()-> play.color=new Vector3(106.0f/255,87.0f/255,133.0f/255));
        play.setOnClick(()-> play.color=new Vector3(106.0f/255,87.0f/255,133.0f/255).getMul(0.5f));
        g.getElements().add(play);

        options =new ButtonGUI(new Vector2f(0.85f,0.7f),new Vector2f(0.2f,0.1f),new Vector3(106.0f/255,87.0f/255,133.0f/255), FontManager.fonts.get("cambria"),"Options",new Vector3(1,1,1));
        options.setOnRelease(()-> options.color=new Vector3(106.0f/255,87.0f/255,133.0f/255));
        options.setOnClick(this::optionsClicked);
        g.getElements().add(options);

    }

    public void optionsClicked(){
        options.color=new Vector3(106.0f/255,87.0f/255,133.0f/255).getMul(0.5f);
        GuiManager.guiOptions.setActive(true);
    }

    @Override
    public void update(float dt) {

        float d=-Pipeline.camera.getPosition().y/Pipeline.mousePicker.getCurrentRay().y;
        if(d>0){
            Vector3 intersect=Pipeline.camera.getPosition().getAdd(Pipeline.mousePicker.getCurrentRay().getMul(d));
            int id_x=(int)(intersect.x/TILE_SIZE+2.5);
            int id_z=(int)(intersect.z/TILE_SIZE+2.5);
            if(id_x>=0 && id_z>=0 && id_z<5 && id_x<5){
                damier[id_x][id_z].getPosition().y=Math.min(-TILE_SIZE*0.75f,damier[id_x][id_z].getPosition().y+2*0.1f*dt*TILE_SIZE);
            }
        }
        for(int x=0;x<damier.length;x++){
            for(int y=0;y<damier.length;y++){
                damier[x][y].getPosition().y=Math.max(-TILE_SIZE,damier[x][y].getPosition().y-0.1f*dt*TILE_SIZE);
                damier[x][y].update();
            }
        }

        //camera update
        Camera cam = Pipeline.camera;

        distance=Math.max(10,Math.min(distance+roll_increment*MouseBinding.getMouseWheelVelocity(),200));
        MouseBinding.resetMouseWheelVelocity();

        if(MouseBinding.isPressedLeft()) {
            cam.rotation.x = cam.rotation.x + (float) (MouseBinding.getMouseY() - cam.lp[1]);
            speedRy =speedRy+ (float) (MouseBinding.getMouseX() - cam.lp[0])*0.3f;
            if (cam.rotation.x > 90) cam.rotation.x = 90;
            if (cam.rotation.x < 10) cam.rotation.x = 10;

        }
        cam.rotation.y+=dt*speedRy;

        speedRy-=factor*dt*(speedRy-baseSpeedRy);
        cam.computeDirection();
        cam.setPosition(cam.direction_view.getMul(-distance));

        cam.lp[0] = MouseBinding.getMouseX();
        cam.lp[1] = MouseBinding.getMouseY();

        /*if(InputManager.escape.isPressed())glfwSetInputMode(DisplayManager.window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
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

        }*/

    }
}
