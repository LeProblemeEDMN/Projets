package Gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Loader.Loader;
import RenderEngine.gui.font.FontType;
import RenderEngine.gui.font.GUIText;
import RenderEngine.gui.font.TextMeshData;
import RenderEngine.gui.font.fontRendering.FontRenderer;
	
public class GuiTextMaster {
	private static Loader loader;
	//public static Map<FontType, List<GUIText>> texts=new HashMap<FontType, List<GUIText>>();
	private static FontRenderer renderer;
	
	public GuiTextMaster(Loader loaderer) {
		loader=loaderer;
		renderer=new FontRenderer();
	}
	public void CleanUp() {
		renderer.cleanUp();
	}
	public void loadText(GUIText tGuiText) {
		FontType fontType=tGuiText.getFont();
		TextMeshData data=fontType.loadText(tGuiText);
		int vao=loader.loadToVOA(data.getVertexPositions(), data.getTextureCoords());
		tGuiText.setMeshInfo(vao, data.getVertexCount());
	}
	public void render(List<GUIText> texts) {
		if(texts.size()>0) {
		renderer.renderTexts(texts.get(0).getFont(),texts);
		}
	}
}
