package RenderEngine.shadow;

import PostProcessing.DLSS.DLSS;
import PostProcessing.PostProcessing;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import Entity.Camera;
import Entity.Entity;
import Entity.Light.PointLight;
import Entity.Light.SpotLight;
import Loader.TexturedModel;
import Main.MainRender;
import Main.Register;
import RenderEngine.gui.GuiTexture;
import toolbox.maths.AxisAlignedBB;
import toolbox.maths.Frustum;
import toolbox.maths.Vector3;



/**
 * This class is in charge of using all of the classes in the shadows package to
 * carry out the shadow render pass, i.e. rendering the scene to the shadow map
 * texture. This is the only class in the shadows package which needs to be
 * referenced from outside the shadows package.
 * 
 * @author Karl
 *
 */
public class ShadowMapMasterRenderer {

	public static final int SHADOW_MAP_SIZE = 2048;

	public static int SIZE=300;
	private ShadowFrameBuffer shadowFbo;
	private ShadowBox shadowBox;
	private Matrix4f projectionMatrix = new Matrix4f();
	private Matrix4f lightViewMatrix = new Matrix4f();
	private Matrix4f projectionViewMatrix = new Matrix4f();
	private Matrix4f offset = createOffset();
	private Camera cam;
	private EntityShadowRender entityShadowRender;
	private Map<TexturedModel, List<Entity>> entities=new HashMap<>();
	private DLSS dlss=new DLSS();
	/**
	 * Creates instances of the important objects needed for rendering the scene
	 * to the shadow map. This includes the {@link ShadowBox} which calculates
	 * the position and size of the "view cuboid", the simple renderer and
	 * shader program that are used to render objects to the shadow map, and the
	 * {@link ShadowFrameBuffer} to which the scene is rendered. The size of the
	 * shadow map is determined here.
	 * 
	 * @param camera
	 *            - the camera being used in the scene.
	 */
	public ShadowMapMasterRenderer(Camera camera) {
		cam=camera;
		shadowBox = new ShadowBox(lightViewMatrix, camera);
		
//		shadowFbo.needToViewPort=false;
		if(shadowFbo.needToViewPort) {
	shadowFbo = new ShadowFrameBuffer(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE);
		}else {
		shadowFbo = new ShadowFrameBuffer(Display.getWidth(), Display.getHeight());
		}
		entityShadowRender=new EntityShadowRender();
		//MainRender.guis.add(new GuiTexture(shadowFbo.getShadowMap(), new Vector2f(-0.6f, -0.6f), new Vector2f(0.15f, 0.15f)));
		dlss.resize(2*SHADOW_MAP_SIZE,SHADOW_MAP_SIZE*2);
	}

	/**
	 * Carries out the shadow render pass. This renders the entities to the
	 * shadow map. First the shadow box is updated to calculate the size and
	 * position of the "view cuboid". The light direction is assumed to be
	 * "-lightPosition" which will be fairly accurate assuming that the light is
	 * very far from the scene. It then prepares to render, renders the entities
	 * to the shadow map, and finishes rendering.
	 * 
	 * @param entities
	 *            - the lists of entities to be rendered. Each list is
	 *            associated with the {@link TexturedModel} that all of the
	 *            entities in that list use.
	 * @param sun
	 *            - the light acting as the sun in the scene.
	 */
	public void render(PointLight sun,List<Entity>e) {
		prepare(sun.getPosition().getMul(-1).getOglVec(), shadowBox);
		Frustum frustum=Frustum.getFrustum(lightViewMatrix, projectionMatrix);
		entities.clear();
		for (Entity entity : e) {
			AxisAlignedBB aabb=entity.getAxisAlignedBB();
			
			if(frustum.cubeInFrustum((float)aabb.minX, (float)aabb.minY, (float)aabb.minZ, (float)aabb.maxX, (float)aabb.maxY, (float)aabb.maxZ)) {
			prepareEntity(entity);
			}
		}
		
		//MainRender.animatedModelRenderer.renderShadow(Register.animatedModel, projectionViewMatrix);
	       
		entityShadowRender.render(entities,projectionViewMatrix);
		finish();

		/*PostProcessing.start();
		dlss.render(shadowFbo.getShadowMap());
		PostProcessing.end();*/
	}

	/**
	 * This biased projection-view matrix is used to convert fragments into
	 * "shadow map space" when rendering the main render pass. It converts a
	 * world space position into a 2D coordinate on the shadow map. This is
	 * needed for the second part of shadow mapping.
	 * 
	 * @return The to-shadow-map-space matrix.
	 */
	public Matrix4f getToShadowMapSpaceMatrix() {
		return Matrix4f.mul(offset, projectionViewMatrix, null);
	}

	/**
	 * Clean up the shader and FBO on closing.
	 */
	public void cleanUp() {
		shadowFbo.cleanUp();
		entityShadowRender.cleanUp();
	}

	/**
	 * @return The ID of the shadow map texture. The ID will always stay the
	 *         same, even when the contents of the shadow map texture change
	 *         each frame.
	 */
	public int getShadowMap() {
		return shadowFbo.getShadowMap();
		//return dlss.getOutputTexture();
	}

	/**
	 * @return The light's "view" matrix.
	 */
	protected Matrix4f getLightSpaceTransform() {
		return lightViewMatrix;
	}

	/**
	 * Prepare for the shadow render pass. This first updates the dimensions of
	 * the orthographic "view cuboid" based on the information that was
	 * calculated in the {@link SHadowBox} class. The light's "view" matrix is
	 * also calculated based on the light's direction and the center position of
	 * the "view cuboid" which was also calculated in the {@link ShadowBox}
	 * class. These two matrices are multiplied together to create the
	 * projection-view matrix. This matrix determines the size, position, and
	 * orientation of the "view cuboid" in the world. This method also binds the
	 * shadows FBO so that everything rendered after this gets rendered to the
	 * FBO. It also enables depth testing, and clears any data that is in the
	 * FBOs depth attachment from last frame. The simple shader program is also
	 * started.
	 * 
	 * @param lightDirection
	 *            - the direction of the light rays coming from the sun.
	 * @param box
	 *            - the shadow box, which contains all the info about the
	 *            "view cuboid".
	 */
	private void prepare(Vector3f lightDirection, ShadowBox box) {
		
		updateOrthoProjectionMatrix(SIZE,SIZE, SIZE);
		updateLightViewMatrix(lightDirection,MainRender.CAMERA.getPosition().getOglVec());
		Matrix4f.mul(projectionMatrix, lightViewMatrix, projectionViewMatrix);
	
		shadowFbo.bindFrameBuffer();
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

	}

	/**
	 * Finish the shadow render pass. Stops the shader and unbinds the shadow
	 * FBO, so everything rendered after this point is rendered to the screen,
	 * rather than to the shadow FBO.
	 */
	private void finish() {
		shadowFbo.unbindFrameBuffer();
	}

	/**
	 * Updates the "view" matrix of the light. This creates a view matrix which
	 * will line up the direction of the "view cuboid" with the direction of the
	 * light. The light itself has no position, so the "view" matrix is centered
	 * at the center of the "view cuboid". The created view matrix determines
	 * where and how the "view cuboid" is positioned in the world. The size of
	 * the view cuboid, however, is determined by the projection matrix.
	 * 
	 * @param direction
	 *            - the light direction, and therefore the direction that the
	 *            "view cuboid" should be pointing.
	 * @param center
	 *            - the center of the "view cuboid" in world space.
	 */
	private void updateLightViewMatrix(Vector3f direction, Vector3f center) {
		direction.normalise();
		center.negate();
		lightViewMatrix.setIdentity();
		float pitch = (float) Math.acos(new Vector2f(direction.x, direction.z).length());
		Matrix4f.rotate(pitch, new Vector3f(1, 0, 0), lightViewMatrix, lightViewMatrix);
		float yaw = (float) Math.toDegrees(((float) Math.atan(direction.x / direction.z)));
		yaw = direction.z > 0 ? yaw - 180 : yaw;
		Matrix4f.rotate((float) -Math.toRadians(yaw), new Vector3f(0, 1, 0), lightViewMatrix,
				lightViewMatrix);
		Matrix4f.translate(center, lightViewMatrix, lightViewMatrix);
	}

	/**
	 * Creates the orthographic projection matrix. This projection matrix
	 * basically sets the width, length and height of the "view cuboid", based
	 * on the values that were calculated in the {@link ShadowBox} class.
	 * 
	 * @param width
	 *            - shadow box width.
	 * @param height
	 *            - shadow box height.
	 * @param length
	 *            - shadow box length.
	 */
	private void updateOrthoProjectionMatrix(float width, float height, float length) {
		projectionMatrix.setIdentity();
		projectionMatrix.m00 = 2f / width;
		projectionMatrix.m11 = 2f / height;
		projectionMatrix.m22 = -2f / length;
		projectionMatrix.m33 = 1;
	}

	/**
	 * Create the offset for part of the conversion to shadow map space. This
	 * conversion is necessary to convert from one coordinate system to the
	 * coordinate system that we can use to sample to shadow map.
	 * 
	 * @return The offset as a matrix (so that it's easy to apply to other matrices).
	 */
	private static Matrix4f createOffset() {
		Matrix4f offset = new Matrix4f();
		offset.translate(new Vector3f(0.5f, 0.5f, 0.5f));
		offset.scale(new Vector3f(0.5f, 0.5f, 0.5f));
		return offset;
	}
	public static Vector2f getTexturesize() {
		if(ShadowFrameBuffer.needToViewPort) {
			return new Vector2f(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE);
		}else {
			return new Vector2f(Display.getWidth(), Display.getHeight());
		}
	}
	public void resize() {
		shadowFbo.cleanUp();
		shadowFbo = new ShadowFrameBuffer(Display.getWidth(), Display.getHeight());
	}
	public void prepareEntity(Entity e) {
		for (int i = 0; i < e.getListTexturedModel().size(); i++) {
		TexturedModel model=e.getTexturedModel(i);
		List<Entity>batch=entities.get(model);
		if(batch!=null) {
			batch.add(e);
		}else {
			List<Entity>newBatch=new ArrayList<Entity>();
			newBatch.add(e);
			entities.put(model, newBatch);
		}}
	}
}
