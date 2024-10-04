package toolbox;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

public class InputManager {
	public static List<KeyBinding>bindingsList=new ArrayList<>();
	public static KeyBinding up,down,right,left,sneak,jump,sprint,escape,grab,bridge,information,inventory,add,sub;
	public static void init() {
		MouseBinding.init();
		up=new KeyBinding(Keyboard.KEY_Z, "up_key");
		down=new KeyBinding(Keyboard.KEY_S, "down_key");
		right=new KeyBinding(Keyboard.KEY_D, "right_key");
		left=new KeyBinding(Keyboard.KEY_Q, "left_key");
		
		jump=new KeyBinding(Keyboard.KEY_A, "jump_key");
		sneak=new KeyBinding(Keyboard.KEY_F, "sneak_key");
		sprint=new KeyBinding(Keyboard.KEY_R, "sprint_key");
		
		escape=new KeyBinding(Keyboard.KEY_ESCAPE, "escape_key");
		grab=new KeyBinding(Keyboard.KEY_TAB, "grab_key");
		bridge=new KeyBinding(Keyboard.KEY_F10, "bridge_key");
		information=new KeyBinding(Keyboard.KEY_F3, "information_key");
		inventory=new KeyBinding(Keyboard.KEY_E, "inventory_key");
		add=new KeyBinding(Keyboard.KEY_ADD, "add");
		sub=new KeyBinding(Keyboard.KEY_SUBTRACT, "sub");
	}
	public static void update() {
		MouseBinding.update();
		for (KeyBinding keyBinding : bindingsList) {
			keyBinding.update();
		}
	}
}
