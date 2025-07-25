package GUI.elements;

import GUI.Element;
import GUI.GuiManager;
import GUI.font.Font;
import GUI.font.FontManager;
import GUI.font.Message;
import GUI.shader.ElementColorShader;
import org.joml.Vector2f;
import toolbox.maths.Vector3;




public class ButtonGUI extends Element {
    public Vector3 color;

    public boolean pressed=false;

    private Message textButton;
    private Action onClick;
    private Action onRelease;
    public ButtonGUI(Vector2f pos, Vector2f size, Vector3 color) {
        this.pos = new Vector2f(pos.x-0.5f*size.x,pos.y-0.5f*size.y);
        this.size = size;
        this.color = color;
        this.canBeInteractedWith=true;
    }

    public ButtonGUI(Vector2f pos, Vector2f size, Vector3 color, Font font,String msg,Vector3 colorText) {
        this.pos = new Vector2f(pos.x-0.5f*size.x,pos.y-0.5f*size.y);//the gui shader use the left corner no the center to draw the object
        this.size = size;
        this.color = color;
        this.canBeInteractedWith=true;

        textButton=font.getMessage(msg,pos,new Vector2f(size.x*0.95f,size.y*0.95f),colorText,false);

    }

    public void setOnClick(Action onClick) {
        this.onClick = onClick;
    }

    public void setOnRelease(Action onRelease) {
        this.onRelease = onRelease;
    }

    public void update(float mouseX, float mouseY, boolean mousePressed){
        boolean inside=pos.x<=mouseX && pos.y<=mouseY && pos.x+size.x>=mouseX && pos.y+size.y>=mouseY;
        if(!this.pressed && (mousePressed && inside))onClick.execute();
        if(this.pressed && !(mousePressed && inside))onRelease.execute();
        this.pressed = mousePressed && inside;
    }

    /*public void onClick(){
        System.out.println("Button Clicked");
        color=new Vector3(1,1,1);
    }
    public void onRelease(){
        System.out.println("Button Released");
        color=new Vector3(0.5,0.5,0.5);
    }*/

    @Override
    public void render(ElementColorShader shader) {
        shader.alpha.loadFloat(1);
        shader.scale.loadVector2D(size);
        shader.location.loadVector2D(pos);
        shader.color.loadVector3(color);
        GuiManager.renderElement();
        if(textButton!=null)FontManager.addMessageToRender(textButton);
    }

    @Override
    public void cleanUp() {
        if(textButton!=null)textButton.cleanUp();
    }

    @Override
    public void resize(int width, int height) {

    }
}
