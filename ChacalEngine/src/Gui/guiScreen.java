package Gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;

public class guiScreen {
	private List<guiObject>objects=new ArrayList<>();
	private int textureBackground;
	private Vector2f mouseCoord;
	private boolean unGrabMouse=true;
	private long openTime=0;
	private boolean open=false;
	public guiScreen(boolean unGrabMouse) {
		this.unGrabMouse=unGrabMouse;
	}
	
	public void update() {
		mouseCoord=getNormalizedDeviceCoord();
		//System.out.println(mouseCoord);
		for (guiObject guiObject : objects) {
			guiObject.update(mouseCoord);
		}
	}
	public void addObject(guiObject obj) {
		objects.add(obj);
	}
	public void openGui() {
		if(GuiScreenRenderer.screen!=this&&(System.currentTimeMillis()-openTime>200)) {
		if(unGrabMouse)Mouse.setGrabbed(false);
		GuiScreenRenderer.screen=this;
		open=true;
		openTime=System.currentTimeMillis();
		}
	}
	public void closeGui() {
		if(System.currentTimeMillis()-openTime>200) {
		GuiScreenRenderer.screen=null;
		open=false;
		if(unGrabMouse)Mouse.setGrabbed(true);
		openTime=System.currentTimeMillis();
		}
	}
	public List<guiObject> getObjects() {
		return objects;
	}
	public int getTextureBackground() {
		return textureBackground;
	}
	protected Vector2f getNormalizedDeviceCoord() {
		float x=Mouse.getX();
		float y=Mouse.getY();
		x=(x)/Display.getWidth();
		y=(y)/Display.getHeight();
		return new Vector2f(x,y);
		
	}
	public void setTextureBackground(int textureBackground) {
		this.textureBackground = textureBackground;
	}
	public boolean isOpen() {
		return open;
	}
}
