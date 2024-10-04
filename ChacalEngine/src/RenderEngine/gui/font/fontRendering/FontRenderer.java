package RenderEngine.gui.font.fontRendering;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import RenderEngine.gui.font.FontType;
import RenderEngine.gui.font.GUIText;

public class FontRenderer {

	private FontShader shader;

	public FontRenderer() {
		shader = new FontShader();
	}

	public void cleanUp(){
		shader.cleanUp();
	}
	
	private void prepare(){GL11.glEnable(GL11.GL_BLEND);
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
	GL11.glDisable(GL11.GL_DEPTH_TEST);
	shader.start();
	}
	public void render(Map<FontType, List<GUIText>> texts) {
		prepare();
		for (FontType font:texts.keySet()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, font.getTextureAtlas());
			for(GUIText guiText:texts.get(font)) {
				renderText(guiText);
			}
		}
		endRendering();
	}
	public void renderTexts(FontType type, List<GUIText> texts) {
		prepare();
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, type.getTextureAtlas());
			for(GUIText guiText:texts) {
				renderText(guiText);
			}
		
		endRendering();
	}
	private void renderText(GUIText text){
		//System.out.println(text.getPosition()+" "+text.getVertexCount());
		GL30.glBindVertexArray(text.getMesh());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		shader.loadColor(text.getColour());
		shader.loadTranslation(text.getPosition());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, text.getVertexCount());
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	private void endRendering(){
		shader.stop();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
	}

}
