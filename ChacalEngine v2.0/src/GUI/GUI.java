package GUI;

import GUI.elements.ButtonGUI;
import GUI.elements.GuiSlider;
import GUI.elements.GuiTextBar;
import rendering.postprocessing.PostProcessing;

import java.util.ArrayList;
import java.util.List;


public class GUI {


    private List<Element> elements=new ArrayList<Element>();
    private boolean isActive=false;


    public List<Element> getElements() {
        return elements;
    }

    public void update(float mouseX,float mouseY,boolean mousePressed){
        if(!isActive)return;
        for(Element e:elements){
            if(e.canBeInteractedWith)
                process(e,mouseX,mouseY,mousePressed);
        }
    }

    public void process(Element e,float mouseX,float mouseY,boolean mousePressed){
        if(e instanceof ButtonGUI){
            ButtonGUI b=(ButtonGUI) e;
            b.update(mouseX,mouseY,mousePressed);
        }else if(e instanceof GuiSlider){
            GuiSlider b=(GuiSlider) e;
            b.update(mouseX,mouseY,mousePressed);
        }else if(e instanceof GuiTextBar){
            GuiTextBar b=(GuiTextBar) e;
            b.update(mouseX,mouseY,mousePressed);
        }else {
            System.err.println("Interaction non gérée");
        }
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
