package toolbox;

import main.DisplayManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.windows.MOUSEINPUT;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

public class MouseBinding {
	public static float mouseWheelVelocity=0;
	private static boolean isPressedLeft=false,isClickedLeft=false,isPressedRight=false,isClickedRight=false;

	private static float mouseX=0,mouseY=0,mouseXscale=0,mouseYscale=0;

	private static long lastClickTimeLeft=System.currentTimeMillis(),lastClickTimeRight=System.currentTimeMillis();
	private static long updateTime=200;
	public static void init() {

	}
	public static void update() {
		isClickedRight=false;
		isClickedLeft=false;

		//getPosition
		double[] xpos = new double[1];
		double[] ypos = new double[1];
		glfwGetCursorPos(DisplayManager.window, xpos, ypos);
		mouseX=(float) xpos[0];
		mouseY=(float) ypos[0];
		mouseXscale=mouseX/DisplayManager.getWidth();
		mouseYscale=mouseY/DisplayManager.getHeight();

		//check is clicked
		isPressedLeft=GLFW.glfwGetMouseButton(DisplayManager.window, 0)==GL11.GL_TRUE;
		isPressedRight=GLFW.glfwGetMouseButton(DisplayManager.window, 1)==GL11.GL_TRUE;

		boolean isLPLeft=isPressedLeft;
		boolean isLPRight=isPressedRight;

		if(isPressedRight && ( !isLPRight || System.currentTimeMillis()-lastClickTimeRight>updateTime)) {
			lastClickTimeRight=System.currentTimeMillis();
			isClickedRight=true;
		}
		if(isPressedLeft && (!isLPLeft || System.currentTimeMillis()-lastClickTimeLeft>updateTime)) {
			lastClickTimeLeft=System.currentTimeMillis();
			isClickedLeft=true;
		}
	}
	public static float getMouseWheelVelocity() {
		return mouseWheelVelocity;
	}
	public static void resetMouseWheelVelocity() {
		mouseWheelVelocity=0;
	}

	public static boolean isPressedLeft() {
		return isPressedLeft;
	}
	public static void setPressedLeft(boolean isPressedLef) {
		isPressedLeft = isPressedLef;
	}
	public static boolean isClickedLeft() {
		return isClickedLeft;
	}
	public void setClickedLeft(boolean isClickedLef) {
		this.isClickedLeft = isClickedLef;
	}
	public static boolean isPressedRight() {
		return isPressedRight;
	}
	public  static  void setPressedRight(boolean isPressedRigh) {
		isPressedRight = isPressedRigh;
	}
	public static boolean isClickedRight() {
		return isClickedRight;
	}
	public static void setClickedRight(boolean isClickedRigh) {
		isClickedRight = isClickedRigh;
	}
	public static long getUpdateTime() {
		return updateTime;
	}
	public static void setUpdateTime(long updateTime) {
		updateTime = updateTime;
	}

	public static float getMouseY() {
		return mouseY;
	}

	public static float getMouseX() {
		return mouseX;
	}

	public static float getMouseYscale() {
		return mouseYscale;
	}

	public static float getMouseXscale() {
		return mouseXscale;
	}
}
