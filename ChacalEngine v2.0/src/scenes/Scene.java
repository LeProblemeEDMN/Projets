package scenes;

import entity.Entity;
import entity.Light.PointLight;
import entity.Light.SpotLight;
import loading.TexturedModel;
import main.Pipeline;
import main.World;
import postProcessingEffect.DefferedRendering.DefferedRenderer;
import rendering.deffered_rendering.Deffered_workflow;
import rendering.postprocessing.PostProcessing;
import rendering.simple_rendering.Simple_workflow;

import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Scene {
    protected boolean canMouseBeGrabbed = false;

    protected boolean isActive=false;

    protected static List<SpotLight> spots=new ArrayList<>();
    protected static List<PointLight>pointLigths=new ArrayList<>();

    protected static List<Entity>entities=new ArrayList<>();
    protected static Map<TexturedModel, List<Entity>> entitesMap=new HashMap<TexturedModel, List<Entity>>();

    public void set(){
        for(Entity entity:entities){
            entity.setLights();
            TexturedModel model=entity.getTexturedModel();
            List<Entity>batch=entitesMap.get(model);
            if(batch!=null) {
                batch.add(entity);
            }else {
                List<Entity>newBatch=new ArrayList<Entity>();
                newBatch.add(entity);
                entitesMap.put(model, newBatch);
            }
        }
    }

    //update the scene
    public abstract void update(float dt);


    //function called the first time this scene is rendered (used to change the camera position for example)
    public void start() {
        if (Pipeline.haveRenderingWorkflow("simple")){
            Simple_workflow wr = (Simple_workflow) Pipeline.getRenderingWorkflow("simple");
            wr.loadLights(pointLigths, spots);
        }
        DefferedRenderer.instance.init(spots, pointLigths);
    }

    //set the world attributes (light entities...) to this scene attributes
    public void flush(){
        World.spots=spots;
        World.pointLigths=pointLigths;
        World.entities=entities;
        World.entitesMap=entitesMap;
    }

    public boolean isCanMouseBeGrabbed() {
        return canMouseBeGrabbed;
    }

    public void setCanMouseBeGrabbed(boolean canMouseBeGrabbed) {
        this.canMouseBeGrabbed = canMouseBeGrabbed;
    }

    public static List<SpotLight> getSpots() {
        return spots;
    }

    public static void setSpots(List<SpotLight> spots) {
        Scene.spots = spots;
    }

    public static List<PointLight> getPointLigths() {
        return pointLigths;
    }

    public static void setPointLigths(List<PointLight> pointLigths) {
        Scene.pointLigths = pointLigths;
    }

    public static List<Entity> getEntities() {
        return entities;
    }

    public static void setEntities(List<Entity> entities) {
        Scene.entities = entities;
    }

    public static Map<TexturedModel, List<Entity>> getEntitesMap() {
        return entitesMap;
    }

    public static void setEntitesMap(Map<TexturedModel, List<Entity>> entitesMap) {
        Scene.entitesMap = entitesMap;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        flush();
        if(active && !isActive)start();
        isActive = active;
    }
}
