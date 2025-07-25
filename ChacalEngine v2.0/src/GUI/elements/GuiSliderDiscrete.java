package GUI.elements;

import GUI.font.Font;
import main.Constantes;
import org.joml.Vector2f;
import toolbox.maths.Vector3;

public class GuiSliderDiscrete extends GuiSlider{
    String[] keys;
    String actual;
    public GuiSliderDiscrete(Vector2f position, Vector2f size, float percentSizeCounter, float percentYSizeCursor, Font font, Vector3 textColor, Vector3 backgroundCountercolor, String[] keys,Constantes cst) {
        super(position, size, percentSizeCounter, percentYSizeCursor, font, textColor, backgroundCountercolor, 0, keys.length-1, cst);
        this.keys = keys;
        actual=keys[0];
    }

    @Override
    public void update(float mouseX, float mouseY, boolean mousePressed) {
        boolean inside=pos.x<=mouseX && pos.y<=mouseY && pos.x+sizeSlider.x>=mouseX && pos.y+sizeSlider.y>=mouseY;
        if(inside && mousePressed){
            value= Math.round((mouseX-pos.x)/sizeSlider.x*(max-min)+min);
            actual=keys[Math.round(value)];
            if(this.constante_value!=null)constante_value.value_string=actual;
        }

    }

    @Override
    public String getValueString() {
        return actual;
    }
}
