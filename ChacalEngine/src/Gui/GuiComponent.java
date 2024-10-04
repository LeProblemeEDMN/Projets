package Gui;

import org.lwjgl.util.vector.Vector2f;

import RenderEngine.gui.GuiTexture;
import RenderEngine.gui.font.GUIText;
import toolbox.maths.Vector3;

public class GuiComponent {
	public GuiComponent(String phrase, Vector2f coord, Vector2f size, float maxLineLength,
			float fontSize,Vector3 col) {
		super();
		this.phrase = phrase;
		this.coord = coord;
		this.size = size;
		this.maxLineLength = maxLineLength;
		this.fontSize = fontSize;
		this.color=col;
		this.type=ComponentType.STRING;
	}
	public GuiComponent(int img, Vector2f coord, Vector2f size) {
		
		this.img = img;
		this.coord = coord;
		this.size = size;
		this.type=ComponentType.IMAGE;
	}

	public ComponentType type;
	public int img;
	public String phrase;
	public Vector3 color;
	public Vector2f coord;
	public Vector2f size;
	
	public GUIText guiText;
	public GuiTexture texture;
	public float maxLineLength;
	public float fontSize;
	public static enum ComponentType{
		STRING,
		IMAGE,
	}
}
