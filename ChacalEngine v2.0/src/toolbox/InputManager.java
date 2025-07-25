package toolbox;

import main.DisplayManager;
import org.lwjgl.glfw.GLFWCharCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;


public class InputManager {
	public static List<KeyBinding>bindingsList=new ArrayList<>();
	public static KeyBinding up,down,right,left,sneak,jump,sprint,escape,grab,bridge,information,inventory,add,sub;
	public static KeyBinding A_letter,E_letter;
	public static List<Integer> written_text=new ArrayList<>();
	public static List<Integer> last_written_text=new ArrayList<>();
	public static int text_left_arrow_key_pressed=-1;
	public static int text_right_arrow_key_pressed=-2;
	public static int text_enter_pressed=-3;
	public static int text_delete_pressed=-4;
	public static void init() {
		MouseBinding.init();
		up=new KeyBinding(GLFW_KEY_W, "up_key");
		down=new KeyBinding(GLFW_KEY_S, "down_key");
		right=new KeyBinding(GLFW_KEY_D, "right_key");
		left=new KeyBinding(GLFW_KEY_A, "left_key");
		
		jump=new KeyBinding(GLFW_KEY_SPACE, "jump_key");
		sneak=new KeyBinding(GLFW_KEY_F, "sneak_key");
		sprint=new KeyBinding(GLFW_KEY_R, "sprint_key");
		
		escape=new KeyBinding(GLFW_KEY_ESCAPE, "escape_key");
		grab=new KeyBinding(GLFW_KEY_TAB, "grab_key");
		bridge=new KeyBinding(GLFW_KEY_F10, "bridge_key");
		information=new KeyBinding(GLFW_KEY_F3, "information_key");
		inventory=new KeyBinding(GLFW_KEY_E, "inventory_key");
		add=new KeyBinding(GLFW_KEY_KP_ADD, "add");
		sub=new KeyBinding(GLFW_KEY_KP_SUBTRACT, "sub");

		//azerty keys
		A_letter=new KeyBinding(GLFW_KEY_Q, "A_key");
		E_letter=new KeyBinding(GLFW_KEY_E, "E_key");
		glfwSetKeyCallback(DisplayManager.window, new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				System.out.println(key+" "+scancode+" "+action+" "+mods);
				if(key==257 && action==1){
					written_text.add(text_enter_pressed);
				}
				else if(key==263 && action==1){
					written_text.add(text_left_arrow_key_pressed);
				}
				else if(key==262 && action==1){
					written_text.add(text_right_arrow_key_pressed);
				}else if(key==259 && action==1){
					written_text.add(text_delete_pressed);
				}

			}
		});
		glfwSetCharCallback(DisplayManager.window, new GLFWCharCallback() {
			@Override
			public void invoke(long window, int codepoint) {
				written_text.add(codepoint);
			}
		});
	}
	public static void update() {
		last_written_text.clear();
		last_written_text.addAll(written_text);
		written_text.clear();
		MouseBinding.update();
		for (KeyBinding keyBinding : bindingsList) {
			keyBinding.update();
		}
	}
}
