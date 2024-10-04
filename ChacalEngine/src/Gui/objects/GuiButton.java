package Gui.objects;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import Gui.GuiComponent;
import Gui.guiObject;
import toolbox.InputManager;
import toolbox.MouseBinding;
import toolbox.maths.Vector3;

public class GuiButton extends guiObject{
	private boolean isClicked,isPressedLastFrame;
	private Vector3 baseColor=new Vector3(0.5, 0.5, 0.5);
	private Vector3 pressedColor=new Vector3(0.7, 0.7, 0.7);
	
	public GuiButton(Vector3 baseColor, Vector3 pressedColor, Vector2f position, Vector2f size, int imgBackgorund,
			float textureVisibility) {
		super(position, size, new Vector3(), imgBackgorund, textureVisibility);
		this.baseColor = baseColor;
		this.pressedColor = pressedColor;
	}
	public GuiButton(Vector3 siVraiBase, Vector3 siVraiPressed,boolean condition, Vector2f position, Vector2f size, int imgBackgorund,
			float textureVisibility) {
		this(siVraiBase, siVraiPressed, position, size, imgBackgorund, textureVisibility);
		if(!condition) {
			this.pressedColor=siVraiBase;
			this.baseColor=siVraiPressed;
		}
	}


	@Override
	public void update(Vector2f mouseCoord) {
		isClicked=false;
		boolean isPressed=false;
		if(mouseCoord.x>=this.position.x && mouseCoord.x<=(this.position.x+this.size.x)
		   && mouseCoord.y>=this.position.y && mouseCoord.y<=(this.position.y+this.size.y)) {
			isPressed=MouseBinding.isPressedLeft();
			this.backGroundcolor=pressedColor;
		}else {
			this.backGroundcolor=baseColor;
		}
		if(isPressedLastFrame && !isPressed) {
			isClicked=true;
		}
		
		isPressedLastFrame=isPressed;
	}
	public boolean isClicked() {
		return isClicked;
	}
public Vector3 getBaseColor() {
		return baseColor;
	}
public void setBaseColor(Vector3 baseColor) {
		this.baseColor = baseColor;
	}
public Vector3 getPressedColor() {
		return pressedColor;
	}
public void setPressedColor(Vector3 pressedColor) {
		this.pressedColor = pressedColor;
	}
	public void invertColor() {
		Vector3 batch=getBaseColor();
		setBaseColor(getPressedColor());
		setPressedColor(batch);
	}
}
