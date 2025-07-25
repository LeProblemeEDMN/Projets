package GUI.elements;

import GUI.Element;
import GUI.GuiManager;
import GUI.font.Font;
import GUI.font.FontManager;
import GUI.shader.ElementColorShader;
import main.Constantes;
import main.DisplayManager;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import toolbox.maths.Vector3;

public class GuiSlider extends Element {
    //un slider est composé de deux parties le slider et l'affichage de la valeur;
    public static int sliderTexture;
    public static int cursorTexture;
    public static int counterTexture;
    protected float min;
    protected float max;
    protected float value;

    protected Vector2f sizeSlider;
    private Vector2f sizeCursor;
    private Vector2f sizeCounter;
    private Font font;
    private Vector3 textColor,backgroundCountercolor;

    protected Constantes constante_value;

    public GuiSlider(Vector2f position, Vector2f size, float percentSizeCounter, float percentYSizeCursor, Font font, Vector3 textColor, Vector3 backgroundCountercolor, float min, float max, Constantes cst) {
        /*position: position of the center of the slider
          size: size fo the whole slider
          percentSizeCounter: percent size (on the x-axis) of the counter (i.e. sizeCounter.x+sizeSlider.x=size.x and size.x*percentSizeCounter=sizeCounter.x)
          percentYSizeCursor: size of the cursor compared to the size of the slider.

          cst: a constante to which the value of the slider will be written
         */
        float aspectRatio= (float) DisplayManager.getWidth()/DisplayManager.getHeight();
        this.font = font;
        this.textColor = textColor;
        this.backgroundCountercolor = backgroundCountercolor;
        this.min = min;
        this.max = max;
        this.value=min;
        this.canBeInteractedWith=true;

        this.pos=new Vector2f(position.x-0.5f*size.x,position.y-0.5f*size.y);
        this.sizeSlider=new Vector2f(size.x*(1-percentSizeCounter),size.y);
        this.sizeCursor=new Vector2f(size.y*percentYSizeCursor/aspectRatio,size.y*percentYSizeCursor);
        this.sizeCounter=new Vector2f(size.x*percentSizeCounter,size.y);
        this.constante_value=cst;
    }

    public void update(float mouseX, float mouseY, boolean mousePressed){
        boolean inside=pos.x<=mouseX && pos.y<=mouseY && pos.x+sizeSlider.x>=mouseX && pos.y+sizeSlider.y>=mouseY;
        if(inside && mousePressed){
            value= (mouseX-pos.x)/sizeSlider.x*(max-min)+min;
            if(this.constante_value!=null)constante_value.value=value;
        }
    }

    @Override
    public void render(ElementColorShader shader) {
        shader.alpha.loadFloat(1);
        //render the slider background
        shader.scale.loadVector2D(sizeSlider);
        shader.location.loadVector2D(pos);
        shader.useText.loadFloat(1);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,sliderTexture);
        GuiManager.renderElement();

        //render the slider cursor
        shader.scale.loadVector2D(sizeCursor);
        float percentSlider=(value-min)/(max-min);
        shader.location.loadVector2D(new Vector2f(pos.x+sizeSlider.x*percentSlider-sizeCursor.x*0.5f,pos.y+0.5f*(sizeSlider.y-sizeCursor.y)));
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,cursorTexture);
        GuiManager.renderElement();

        //render the value background
        shader.useText.loadFloat(0);
        shader.scale.loadVector2D(sizeCounter);
        shader.location.loadVector2D(new Vector2f(pos.x+sizeSlider.x,pos.y+0.5f*(sizeSlider.y-sizeCounter.y)));
        shader.color.loadVector3(backgroundCountercolor);
        GuiManager.renderElement();
        int error = GL11.glGetError();
        if (error != 0) {
            System.out.println("OpenGL error 4: " + error);
        }
        FontManager.addMessageToRender(font.getMessage(getValueString(),new Vector2f(pos.x+sizeSlider.x+sizeCounter.x*0.5f,pos.y+0.5f*sizeSlider.y),sizeCounter,textColor,true));
    }

    public String getValueString(){
        return value+"";
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public void resize(int width, int height) {
        this.sizeCursor=new Vector2f(sizeCursor.y*height/width,sizeCursor.y);
    }

    public float getValue() {
        return value;
    }
}
