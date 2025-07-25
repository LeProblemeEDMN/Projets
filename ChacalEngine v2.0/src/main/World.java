package main;

import GUI.GuiManager;
import GUI.font.FontManager;
import entity.Entity;
import entity.Light.Light;
import entity.Light.PointLight;
import entity.Light.SpotLight;
import loading.LOD.LODManager;
import loading.Loader;
import loading.RawModel;
import loading.TerrainLoader;
import loading.Texture.ModelTexture;
import loading.Texture.TextureUtils;
import loading.TexturedModel;
import org.lwjgl.system.MemoryUtil;
import scenes.SaintBrevin.SaintBrevinScene;
import scenes.Scene;
import scenes.game.GameScene;
import scenes.mainMenu.MainMenuScene;
import simulation.SoftBody;
import toolbox.InputManager;
import toolbox.KeyBinding;
import toolbox.MouseBinding;
import toolbox.MousePicker;
import toolbox.maths.Frustum;
import toolbox.maths.Vector3;

import java.nio.FloatBuffer;
import java.util.*;

import loading.ObjLoader.objFileLoader;
public class World {
    public static List<SpotLight>spots=new ArrayList<>();
    public static List<PointLight>pointLigths=new ArrayList<>();

    public static List<Entity>entities=new ArrayList<>();
    public static Map<TexturedModel, List<Entity>> entitesMap=new HashMap<TexturedModel, List<Entity>>();

    public static HashMap<TexturedModel,List<Entity>[]> lodSortedEntities=new HashMap<>();
    public static HashMap<List<Entity>,FloatBuffer> bufferSortedEntities=new HashMap<>();

    //the game is compsoed of multiple scenes (game ,main menu,mini games...)
    //only one is renderered at a time
    public static List<Scene> scenes=new ArrayList<>();
    public static Scene activeScene=null;//the scene to be displayed

    public static void update(){

        InputManager.update();

        GuiManager.update(MouseBinding.getMouseXscale(),MouseBinding.getMouseYscale(), MouseBinding.isPressedLeft());

        activeScene=scenes.get(0);
        activeScene.setActive(true);
        activeScene.flush();
        activeScene.update(DisplayManager.getFrameTimeSecond());

        Pipeline.camera.update();
        //sort entities by LOD depending of the distance to camera
        sortByLOD();


    }

    public static void fill(Loader loader){
        LODManager.loadAllLOD("res/LOD");
        TextureUtils.loadTextureDir();

        FontManager.init();
        //a faire apres le font amanger
        GuiManager.init();

        //scenes.add(new GameScene(loader));
        //scenes.add(new SaintBrevinScene(loader));
        scenes.add(new MainMenuScene(loader));
    }


    /*
    For each textured model make a number of model LODList of entities containing all the entities
    using this LOD.
    It also make a floatbuffer containing the transformation matrix of each models for each LOD
    in order to use instance rendering.
     */
    public static void sortByLOD(){
        //clear the buffer to free the memory
        for(FloatBuffer b : bufferSortedEntities.values()){
            if(b!=null)MemoryUtil.memFree(b);
        }
        bufferSortedEntities.clear();
        lodSortedEntities.clear();

        Frustum frustum = Frustum.getFrustum(Pipeline.getViewMat(), Pipeline.getProjMat());
        for(TexturedModel model : entitesMap.keySet()){
            List<Entity>[] entities_LOD=new List[model.getNumberLOD()];
            for(int i=0;i<entities_LOD.length;i++)
                entities_LOD[i]=new ArrayList<>();
            //check if the model can be seen
            for (Entity e: entitesMap.get(model)) {
                if(Pipeline.camera.can_be_seen(e) || e.isLarge()){//Pipeline.camera.getPosition().squareDistanceTo(e.getPosition())<e.renderingDistance*e.renderingDistance) {
                    if (e.inFrustum(frustum) || e.isLarge()) {
                        int l_id=model.getLODId(e.screen_percent(Pipeline.camera.getPosition()));
                        entities_LOD[l_id].add(e);
                    }
                }
            }
            lodSortedEntities.put(model,entities_LOD);

            //load the transformation matrix in the floatbuffer
            for(int i=0;i<entities_LOD.length;i++) {
                if (entities_LOD[i].isEmpty()) continue;
                FloatBuffer buffer =  MemoryUtil.memAllocFloat(entities_LOD[i].size() * 16); // 16 floats par matrice
                int j =0;
                for (Entity entity : entities_LOD[i]) {
                    entity.getTransformationMatrix().get(j*16, buffer);
                    j++;
                }
                bufferSortedEntities.put(entities_LOD[i],buffer);
            }

        }
    }

}
