package Gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import Gui.GuiComponent.ComponentType;
import toolbox.maths.Vector3;

public class guiObject {
	
	public Vector2f position;
	public Vector2f size;
	public Vector3 backGroundcolor;
	public int imgBackgorund;
	public float textureVisibility;
	public boolean haveImgInBackgournd=false;
	
	public List< GuiComponent>components=new ArrayList<>();
	public guiObject(Vector2f position, Vector2f size, Vector3 backGroundcolor, int imgBackgorund,
			float textureVisibility) {
		super();
		this.position = position;
		this.size = size;
		this.backGroundcolor = backGroundcolor;
		this.imgBackgorund = imgBackgorund;
		this.textureVisibility = textureVisibility;
		this.haveImgInBackgournd = (textureVisibility>0);
	}

	public void update(Vector2f mouseCoord) {
		
	}
	public void addComponent(GuiComponent component) {
		components.add(component);
	}
	public Vector2f convertPosition(GuiComponent component) {
		if(component.type==ComponentType.IMAGE) {
			return new Vector2f((position.x+(component.coord.x*size.x))*2-1, -1+(position.y+(component.coord.y*size.y))*2);
		}else {
			return new Vector2f((position.x+(component.coord.x*size.x))*2, -2+(position.y+(component.coord.y*size.y))*2);
		}
	}
	public Vector2f convertSize(GuiComponent component) {
		return new Vector2f((component.size.x*size.x), ((component.size.y*size.y)));
	}
}
