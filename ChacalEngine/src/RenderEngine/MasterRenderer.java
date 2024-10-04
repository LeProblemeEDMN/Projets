package RenderEngine;

import Entity.Entity;
import Loader.TexturedModel;
import Main.MainLoop;
import Main.MainRender;
import RenderEngine.sky.SkyboxRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;
import toolbox.maths.AxisAlignedBB;
import toolbox.maths.Frustum;
import toolbox.maths.ProgramStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {
	public Map<TexturedModel, List<Entity>>entities=new HashMap<TexturedModel, List<Entity>>();
	public Map<TexturedModel, List<Entity>>entitiesNormalMap=new HashMap<TexturedModel, List<Entity>>();
	public Map<TexturedModel, List<Entity>>allEntities=new HashMap<TexturedModel, List<Entity>>();
	
	EntityRenderer entityRenderer;
	EntityRendererNormalMap entityRendererNormalMap;
	SkyboxRenderer skyboxRenderer;
	
	public MasterRenderer() {
		entityRenderer=new EntityRenderer();
		skyboxRenderer=new SkyboxRenderer(MainLoop.LOADER, MainRender.CAMERA.getProjectionMatrix());
		entityRendererNormalMap=new EntityRendererNormalMap();
		enableCulling();
	}
	
	public void render(List<Entity>entitiesList) {
		ProgramStats.startStat(ProgramStats.renderPreparation);
		entities.clear();
		entitiesNormalMap.clear();
		allEntities.clear();
		Frustum frustum=Frustum.getFrustum(MainRender.CAMERA.getViewMatrix(), MainRender.CAMERA.getProjectionMatrix());
		for (Entity entity : entitiesList) {
			AxisAlignedBB aabb=entity.getAxisAlignedBB();
			if(MainRender.CAMERA.getPosition().squareDistanceTo(entity.getPosition())<entity.renderingDistance*entity.renderingDistance) {
				if (entity.inFrustum(frustum)) {
					processEntity(entity);
				}
			}
		}
		allEntities.putAll(entities);
		allEntities.putAll(entitiesNormalMap);
		Prepare();
		makeRender();
	}
	
	public void makeRender() {

		ProgramStats.startAndRemoveStat(ProgramStats.sky);
		skyboxRenderer.render(MainRender.CAMERA.getViewMatrix());
		
		ProgramStats.startAndRemoveStat(ProgramStats.entityRender);
		entityRenderer.initRender();
		entityRenderer.render(entities);
		
		//System.out.println(entitiesNormalMap.keySet().size());
		ProgramStats.startAndRemoveStat(ProgramStats.normalEntityRender);
		entityRendererNormalMap.initRender();
		entityRendererNormalMap.render(entitiesNormalMap);

		ProgramStats.removeStat();
	}
	
	
	
	public void processEntity(Entity entity) {
		for (int i = 0; i < entity.getListTexturedModel().size(); i++) {
			
		TexturedModel model=entity.getTexturedModel(i);
	if(model.getTexture().getNormalMap()==0) {
		List<Entity>batch=entities.get(model);
		if(batch!=null) {
			batch.add(entity);
		}else {
			List<Entity>newBatch=new ArrayList<Entity>();
			newBatch.add(entity);
			entities.put(model, newBatch);
		}
	}else {
	//	System.out.println("passe");
		List<Entity>batch=entitiesNormalMap.get(model);
		if(batch!=null) {
			batch.add(entity);
		}else {
			List<Entity>newBatch=new ArrayList<Entity>();
			newBatch.add(entity);
			entitiesNormalMap.put(model, newBatch);
		}
	}
		}
	}
	
	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	
	}
	public void Prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT| GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(Main.MainRender.SKY_COLOR.x,Main.MainRender.SKY_COLOR.y, Main.MainRender.SKY_COLOR.z, 	2);

	}
	public void cleanUp() {
		entities.clear();
		entityRenderer.cleanUp();
		skyboxRenderer.cleanUp();
		entitiesNormalMap.clear();
		entityRendererNormalMap.cleanUp();
	}
	public EntityRenderer getEntityRenderer() {
		return entityRenderer;
	}
	public EntityRendererNormalMap getEntityRendererNormalMap() {
		return entityRendererNormalMap;
	}
	public Map<TexturedModel, List<Entity>> getEntitiesNormalMap() {
		return entitiesNormalMap;
	}
	public Map<TexturedModel, List<Entity>> getEntities() {
		return entities;
	}
	public Map<TexturedModel, List<Entity>> getAllEntities() {
		return allEntities;
	}
	public void resize() {
		entityRenderer.resize();
		entityRendererNormalMap.resize();
		skyboxRenderer.updateProjectionMatrix();
	}
}
