package GUI;

import GUI.elements.ButtonGUI;
import GUI.elements.GUIImage;
import GUI.elements.GuiSlider;
import GUI.elements.GuiSliderDiscrete;
import GUI.font.FontManager;
import GUI.guis.OptionGuis;
import GUI.shader.ElementColorShader;
import loading.Loader;
import main.Constantes;
import main.MainLoop;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import rendering.postprocessing.PostProcessing;
import toolbox.InputManager;
import toolbox.maths.Vector3;

import java.util.ArrayList;
import java.util.List;

public class GuiManager {

    public static List<GUI>guis=new ArrayList<GUI>();
    private static ElementColorShader elementShader;

    public static OptionGuis guiOptions;

    public static void init(){
        elementShader=new ElementColorShader();
        GuiSlider.sliderTexture= MainLoop.LOADER.loadTexture("GUI/slider.png");
        GuiSlider.cursorTexture= MainLoop.LOADER.loadTexture("GUI/curseur.png");

        elementShader.init();
        guiOptions=new OptionGuis();
        guis.add(guiOptions);

    }

    public static void update(float mouseX,float mouseY,boolean mousePressed){
        if(InputManager.information.isClicked())guis.get(0).setActive(!guis.get(0).isActive());
        for(GUI gui:guis){
            if(gui.isActive()){
                gui.update(mouseX,1-mouseY,mousePressed);
            }
        }

    }

    public static void render(){
        PostProcessing.start();
        elementShader.start();
        for(GUI gui:guis){
            if(gui.isActive()){
                for(Element e:gui.getElements()){
                    e.render(elementShader);
                }
            }
        }
        elementShader.stop();
        PostProcessing.end();
    }

    public static  void renderElement(){
        GL30.glBindVertexArray(PostProcessing.quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }

    public static void cleanUp(){
        for(GUI gui:guis){
            for(Element e:gui.getElements()){
                e.cleanUp();
            }
        }
    }

    public static void resize(int width,int height){
        for(GUI gui:guis){
            for(Element e:gui.getElements()){
                e.resize(width,height);
            }
        }
    }

}
