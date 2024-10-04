package RenderEngine.gui.font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Loader.Loader;
import Main.MainLoop;
import RenderEngine.gui.font.fontRendering.FontRenderer;

public class TextMaster {
	private static Loader loader;
	public static Map<FontType, List<GUIText>> texts=new HashMap<FontType, List<GUIText>>();
	private static FontRenderer renderer;
	
	public static  void init(Loader loaderer) {
		loader=loaderer;
		renderer=new FontRenderer();
	}
	public static void CleanUp() {
		renderer.cleanUp();
	}
	public static void loadText(GUIText tGuiText) {
		FontType fontType=tGuiText.getFont();
		TextMeshData data=fontType.loadText(tGuiText);
		int vao=MainLoop.LOADER.loadToVOA(data.getVertexPositions(), data.getTextureCoords());
		tGuiText.setMeshInfo(vao, data.getVertexCount());
		
		List<GUIText>textBatch=texts.get(fontType);
		if(textBatch==null) {
			textBatch=new ArrayList<GUIText>();
			texts.put(fontType, textBatch);
		}
		textBatch.add(tGuiText);
	}
	public static void removeText(GUIText tGuiText) {
		List<GUIText>textBatch=texts.get(tGuiText.getFont());
		textBatch.remove(tGuiText);
		if(textBatch.isEmpty()) {
			texts.remove(tGuiText.getFont());
		}
	}
	public static void render() {
		renderer.render(texts);
	}
}
