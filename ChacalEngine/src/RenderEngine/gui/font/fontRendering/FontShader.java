package RenderEngine.gui.font.fontRendering;

import org.lwjgl.util.vector.Vector2f;

import ShaderEngine.ShaderProgram;
import toolbox.maths.Vector3;

public class FontShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/RenderEngine/gui/font/fontRendering/fontVertex.txt";
	private static final String FRAGMENT_FILE = "/RenderEngine/gui/font/fontRendering/fontFragment.txt";
	int location_color;
	int location_translation;
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		getAllUniformLocations();
	}

	@Override
	protected void getAllUniformLocations() {
		location_color=super.getUniformlocation("color");
		location_translation=super.getUniformlocation("translation");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
@Override
public void cleanUp() {
	// TODO Auto-generated method stub
	super.cleanUp();
}
public void loadColor(Vector3 color) {
	super.loadVector(location_color, color);

}
public void loadTranslation(Vector2f translation) {
	super.load2DVector(location_translation, translation);
}
}
