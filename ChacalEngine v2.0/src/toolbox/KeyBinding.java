package toolbox;

import main.DisplayManager;

import static org.lwjgl.glfw.GLFW.*;

public class KeyBinding {
	private int keyId;
	private String name;
	private boolean isPressed=false,isClicked=false;
	private long lastClickTime=System.currentTimeMillis();
	//private long updateTime=50;
	public KeyBinding(int keyId, String name) {
		this.keyId = keyId;
		this.name = name;
		InputManager.bindingsList.add(this);
	}
	public void update() {
		boolean isLastPressed=isPressed;
		isPressed= glfwGetKey(DisplayManager.window, keyId)==GLFW_PRESS;//Keyboard.isKeyDown(keyId);
		if(isLastPressed){

		}
		isClicked=false;
		if(!isLastPressed && isPressed ) {
			lastClickTime=System.currentTimeMillis();
			isClicked=true;
		}
	}
	
	public int getKeyId() {
		return keyId;
	}
	public void setKeyId(int keyId) {
		this.keyId = keyId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isPressed() {
		return isPressed;
	}
	public void setPressed(boolean isPressed) {
		this.isPressed = isPressed;
	}
	public boolean isClicked() {
		return isClicked;
	}
	public void setClicked(boolean isClicked) {
		this.isClicked = isClicked;
	}
	public void delete() {
		InputManager.bindingsList.remove(this);
	}
}
