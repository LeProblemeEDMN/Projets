package Gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;

import Gui.GuiComponent.ComponentType;
import Gui.FondRendu.ImageShader;
import Loader.ConfigLoader;
import Loader.RawModel;
import Main.MainLoop;
import Main.MainRender;
import RenderEngine.gui.GuiTexture;
import RenderEngine.gui.font.FontType;
import RenderEngine.gui.font.GUIText;
import ShaderEngine.ShaderAttrib;
import toolbox.maths.Vector3;

public class GuiScreenRenderer {
	
	
	public static guiScreen screen=null;
	public static ImageShader imageRenderer;
	private static FontType fontType;
	public static GuiTextMaster textMaster;
	public static RenderEngine.gui.GuiRenderer imgGuiRenderer;
	
	private static List<GUIText>guiTexts=new ArrayList<>();
	private static List<GuiTexture>guiImg=new ArrayList<>();
	
	public static void init(ConfigLoader config) {
		
		//pas de taille car pas de fbo
		imageRenderer=new ImageShader();
		textMaster=new GuiTextMaster(MainLoop.LOADER);
		imgGuiRenderer=new RenderEngine.gui.GuiRenderer(MainLoop.LOADER);
		fontType=new FontType(MainLoop.LOADER.loadTexture(config.getStringParameter("fontTexture")), new File(config.getStringParameter("fontFNT")));
		
	
	}
	
	public static void render() {
		
		if(screen!=null) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			

			screen.update();
			imageRenderer.start2();
		imageRenderer.render(new Vector3(), screen.getTextureBackground(),0.0f, new Vector2f(1, 1), new Vector2f(0, 0),false);
		for (guiObject obj : screen.getObjects()) {
			processGuiObject(obj);
		}
		for (guiObject obj : screen.getObjects()) {
			if(obj.haveImgInBackgournd) imageRenderer.render(obj.backGroundcolor, obj.imgBackgorund,obj.textureVisibility, obj.size, obj.position,false);
		}
		imageRenderer.end2();
		//guiTexts.add(Register.text);
		textMaster.render(guiTexts);
		imgGuiRenderer.Render(guiImg);
			
		guiTexts.clear();
		guiImg.clear();
		}
	}
	public static boolean haveGuiOpen() {
		return screen!=null;
	}
	
	public static void processGuiObject(guiObject object) {
		for (GuiComponent component : object.components) {
			if(component.type==ComponentType.IMAGE) {
				guiImg.add(component.texture);
			}else {
				guiTexts.add(component.guiText);
			}
		}
	}
	

	public static void initScreen(guiScreen screen) {
		for (guiObject obj : screen.getObjects()) {
			for (GuiComponent component : obj.components) {
				if(component.type==ComponentType.IMAGE) {
					component.texture=new GuiTexture(component.img, obj.convertPosition(component), obj.convertSize(component));
				}else {
					component.guiText=new GUIText(component.phrase, component.fontSize, fontType,  obj.convertPosition(component), component.maxLineLength,false,false);
					//component.guiText=new GUIText(component.phrase,component.fontSize, fontType, new Vector2f(1f, -1f), 1f, false,false);
					component.guiText.setColour(component.color.x, component.color.y, component.color.z);
					textMaster.loadText(component.guiText);
				}
			}
		}
	}
	public static void loadComponent(GuiComponent component,guiObject obj) {
		if(component.type==ComponentType.IMAGE) {
			component.texture=new GuiTexture(component.img, obj.convertPosition(component), obj.convertSize(component));
		}else {
			component.guiText=new GUIText(component.phrase, component.fontSize, fontType,  obj.convertPosition(component), component.maxLineLength,false,false);
			component.guiText.setColour(component.color.x, component.color.y, component.color.z);
			textMaster.loadText(component.guiText);
		}
	}
}
