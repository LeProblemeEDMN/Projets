package toolbox;

import org.lwjgl.input.Mouse;

public class MouseBinding {
	private static int nbRoll=0;
	private static boolean isPressedLeft=false,isClickedLeft=false,isPressedRight=false,isClickedRight=false;
	private static long lastClickTimeLeft=System.currentTimeMillis(),lastClickTimeRight=System.currentTimeMillis();
	private static long updateTime=200;
	public static void init() {
		nbRoll=Mouse.getDWheel();
	}
	public static void update() {
		isClickedRight=false;
		isClickedLeft=false;
		boolean isLPLeft=isPressedLeft;
		boolean isLPRight=isPressedRight;
		isPressedLeft=Mouse.isButtonDown(0);
		isPressedRight=Mouse.isButtonDown(1);
		nbRoll=Mouse.getDWheel();
		if(isPressedRight && ( !isLPRight || System.currentTimeMillis()-lastClickTimeRight>updateTime)) {
			lastClickTimeRight=System.currentTimeMillis();
			isClickedRight=true;
		}
		if(isPressedLeft && (!isLPLeft || System.currentTimeMillis()-lastClickTimeLeft>updateTime)) {
			lastClickTimeLeft=System.currentTimeMillis();
			isClickedLeft=true;
		}
	}
	public static int getNbRoll() {
		return nbRoll;
	}
	public static void setNbRoll(int nb) {
		nbRoll = nb;
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
	public boolean isPressedRight() {
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
	
}
