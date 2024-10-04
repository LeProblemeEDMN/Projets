package RenderEngine;

import Entity.Entity;
import Loader.RawModel;
import Loader.TexturedModel;
import Main.MainGame;
import Main.MainRender;
import ShaderEngine.entity.EntityShader;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;
import toolbox.maths.MathHelper;
import toolbox.maths.Vector3;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class EntityRenderer {
	EntityShader shader;
	private float sizeWindMap=140;
	private float windMultiplicator=1;
	private BufferedImage windMap;
	private Vector3 moveWind=new Vector3(4,0,0),posWind=new Vector3();


	public EntityRenderer() {
		shader=new EntityShader();
		shader.init(MainGame.spots);
		try {
			//windMap= ImageIO.read(new File("res/windmap.png"));
			windMap= ImageIO.read(new File("res/waterDUDV.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void initRender() {
		shader.start();
		shader.loadViewMatrix(MainRender.CAMERA.getViewMatrix());
		shader.loadSpotAttenuation(MainGame.spots);
	}
	
	
	public void render(Map<TexturedModel, List<Entity>> entities) {

		posWind.add(moveWind.getMul(DisplayManager.getFrameTimeSecond()));

		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, MainRender.shadowMapMasterRenderer.getShadowMap());
		shader.toShadowMapSpace.loadMatrix4f(MainRender.shadowMapMasterRenderer.getToShadowMapSpaceMatrix());
		for (TexturedModel model : entities.keySet()) {
			RawModel rawModel = model.getRawModel();
			bindModel(rawModel);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getTextureId());
			shader.reflectivity.loadFloat(model.getTexture().getReflectivity());
			shader.shineDamper.loadFloat(model.getTexture().getShineDamper());
			shader.useFakeLightning.loadBoolean(model.getTexture().isUseFakeLightning());
			
			if(model.getTexture().isTransparance())  MasterRenderer.disableCulling();

			if(model.isGrass()){
				shader.isGrass.loadBoolean(true);
			}

			for (Entity entity : entities.get(model)) {

				prepareInstance(entity);

				if(entity.isHaveCubeMap()) {

					shader.materialValue.loadVector2D(new Vector2f(model.getTexture().getReflectionMaterial(), model.getTexture().getRefractionMaterial()));
					GL13.glActiveTexture(GL13.GL_TEXTURE1);
					GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP,entity.getMap().getTexture());

				}else {
					shader.materialValue.loadVector2D(new Vector2f(0, 0));
				}

				if(model.isGrass()){
						float px = (float) MathHelper.mod(entity.getPosition().getX() + posWind.x, sizeWindMap) / sizeWindMap;
						float pz = (float) MathHelper.mod(entity.getPosition().getZ(), sizeWindMap) / sizeWindMap;
						//System.out.println((int)(px*windMap.getWidth())+" "+(int)(pz*windMap.getHeight()));

						int ix = (int) (px * windMap.getWidth());
						int iy = (int) (pz * windMap.getHeight());
						float rx = px * windMap.getWidth() - ix;
						float ry = pz * windMap.getHeight() - iy;
						float weight = 0;

						Color colorWind = new Color(windMap.getRGB(ix, iy));

						float dx = (float) colorWind.getRed() / 255f * (1 - rx) * (1 - ry);
						float dy = (float) colorWind.getGreen() / 255f * (1 - rx) * (1 - ry);

						int nx = ix + 1 >= windMap.getWidth() ? 0 : ix + 1;
						int ny = iy + 1 >= windMap.getHeight() ? 0 : iy + 1;

						colorWind = new Color(windMap.getRGB(nx, iy));
						dx += (float) colorWind.getRed() / 255f * rx * (1 - ry);
						dy += (float) colorWind.getGreen() / 255f * rx * (1 - ry);

						colorWind = new Color(windMap.getRGB(ix, ny));
						dx += (float) colorWind.getRed() / 255f * (1 - rx) * ry;
						dy += (float) colorWind.getGreen() / 255f * (1 - rx) * ry;

						colorWind = new Color(windMap.getRGB(nx, ny));

						dx += (float) colorWind.getRed() / 255f * rx * ry;
						dy += (float) colorWind.getGreen() / 255f * rx * ry;


						shader.windMove.loadVector3(new Vector3(dx - 0.5f, 0, dy - 0.5f));
						//shader.windMove.loadVector3(new Vector3((float)colorWind.getRed()/255f-0.5f,-0.1f,(float)colorWind.getGreen()/255f-0.5f));

				}


				GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(),
						GL11.GL_UNSIGNED_INT, 0);
			}
			if(model.getTexture().isTransparance()) {
				MasterRenderer.enableCulling();
			}
			if(model.isGrass()){
				shader.isGrass.loadBoolean(false);
			}
		}
		/*if(tot>0)
		System.out.println(tot);*/
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	shader.stop();
	}
	
	private void prepareInstance(Entity entity) {	
		///Matrix4f modelMatrix = Maths.createTransfromationMatrix(entity.getPosition().getOglVec(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.transformationMatrix.loadMatrix4f(entity.getTransformationMatrix());
		shader.loadSpotInUse(entity.spotIds);
		shader.loadPointInUse(entity.pointIds);
	}
	
	private void bindModel(RawModel rawModel) {
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
	}
	public void cleanUp() {
		shader.cleanUp();
	}
	public EntityShader getShader() {
		return shader;
	}
	public void resize() {
		shader.start();
		shader.projectionMatrix.loadMatrix4f(MainRender.CAMERA.getProjectionMatrix());
		shader.stop();
	}
}
