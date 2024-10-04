package Gui.objects;

import org.lwjgl.util.vector.Vector2f;

import Gui.GuiComponent;
import Gui.GuiScreenRenderer;
import Gui.guiObject;
import Main.MainGame;
import Main.MainLoop;
import toolbox.MouseBinding;
import toolbox.maths.Vector3;

public class GuiSlider extends guiObject{
	private int value=0;
	private int maxValue=0;
	private int minValue=0;
	private int difValue=0;
	private int texture=0;
	/*private Vector3 baseColor=new Vector3(0.5, 0.5, 0.5);
	private Vector3 pressedColor=new Vector3(0.7, 0.7, 0.7);*/
	private GuiComponent componentValue;
	private GuiComponent slider;
	private boolean isClicked=false;
	public GuiSlider(Vector3 backColor, Vector2f position, Vector2f size, int imgBackgorund,
			float textureVisibility,int minValue,int maxValue,int imgSlider) {
		super(position, size, backColor, imgBackgorund, textureVisibility);
		texture=imgSlider;
		slider=new GuiComponent(texture, new Vector2f(0, 0), new Vector2f((size.x/size.y), 1));
		componentValue=new GuiComponent(""+value, new Vector2f(0.5f, 0),  new Vector2f(0, 1), 10, 2, new Vector3());
		this.maxValue=maxValue;
		this.minValue=minValue;
		this.difValue=maxValue-minValue;
		components.add(slider);
		components.add(componentValue);
	}
	public GuiSlider(Vector3 backColor, Vector2f position, Vector2f size,int minValue,int maxValue,int imgSlider) {
		this(backColor, position, size, 0, 1, minValue, maxValue, imgSlider);
	}
	public GuiSlider( Vector2f position, Vector2f size, int imgBackgorund,int minValue,int maxValue,int imgSlider) {
		this(new Vector3(), position, size, imgBackgorund, 0, minValue, maxValue, imgSlider);
	}
	public void setValue(int value) {
		components.remove(slider);
		components.remove(componentValue);
		if(value>maxValue)value=maxValue;
		if(value<minValue)value=minValue;
		this.value = value;
		slider=new GuiComponent(texture, new Vector2f((float)(value-minValue)/(float)difValue, 0.5f), new Vector2f((size.y/size.x), 1));
		componentValue=new GuiComponent(""+value, new Vector2f(0.5f, 1),  new Vector2f((size.y/size.x), 0.4f), 10, 2, new Vector3());
		components.add(slider);
		components.add(componentValue);
		GuiScreenRenderer.loadComponent(slider, this);
		GuiScreenRenderer.loadComponent(componentValue, this);
		System.out.println(slider.texture.getScale()+" "+slider.texture.getPosition());
		
	}
	@Override
	public void update(Vector2f mouseCoord) {
		
		super.update(mouseCoord);
		isClicked=false;
		if(mouseCoord.x>=this.position.x && mouseCoord.x<=(this.position.x+this.size.x)
				   && mouseCoord.y>=this.position.y && mouseCoord.y<=(this.position.y+this.size.y)) {
			if (MouseBinding.isClickedLeft()) {
				isClicked=true;
				float pos=mouseCoord.x-this.position.x;
				pos/=this.size.x;
				pos*=difValue;
				//System.out.println(pos);
				setValue((int)pos+minValue);
			}
		}
	}
	public boolean isClicked() {
		return isClicked;
	}
	public int getValue() {
		return value;
	}
}
