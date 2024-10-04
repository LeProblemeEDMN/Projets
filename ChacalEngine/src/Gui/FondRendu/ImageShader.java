package Gui.FondRendu;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;

import Loader.RawModel;
import Main.MainLoop;
import ShaderEngine.ShaderAttrib;
import ShaderEngine.ShaderProgram;
import toolbox.maths.Vector3;

public class ImageShader extends ShaderProgram{
	private static final float[] POSITIONS = { 0, 1,0, 0, 1, 1, 1,0};	
	private static RawModel quad;
	public ImageShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
		start();
		image.loadInt(0);
		stop();
		quad = MainLoop.LOADER.loadToVAO(POSITIONS, 2,"null");
	}

	private static final String VERTEX_FILE = "/Gui/FondRendu/vertex.txt";
	private static final String FRAGMENT_FILE = "/Gui/FondRendu/fragment.txt";
	
	public ShaderAttrib translation=new ShaderAttrib("translation", this);
	public ShaderAttrib size=new ShaderAttrib("size", this);
	public ShaderAttrib color=new ShaderAttrib("color", this);
	public ShaderAttrib needInvert=new ShaderAttrib("needInvert", this);
	public ShaderAttrib image=new ShaderAttrib("colourTexture", this);
	public ShaderAttrib imagePercent=new ShaderAttrib("haveImage", this);
	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}
	public void render(Vector3 colorVec,int image,float pourcent,Vector2f sizeVec,Vector2f translationVec,boolean needInvert) {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D,image);
		color.loadVector3(colorVec);
		imagePercent.loadFloat(pourcent);
		translation.loadVector2D(translationVec);
		size.loadVector2D(sizeVec);
		this.needInvert.loadBoolean(needInvert);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
	}
	public void start2(){
		GL11.glDepthMask(false);
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		start();
	}
	
	public void end2(){
		stop();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL11.glDepthMask(true);
	}
}
