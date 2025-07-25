package GUI.elements;

import GUI.Element;
import GUI.font.Font;
import GUI.font.FontManager;
import GUI.font.Message;
import GUI.shader.ElementColorShader;
import org.joml.Vector2f;
import toolbox.maths.Vector3;

public class TextGUI extends Element {

    private String sentence;
    private Font font;
    private Message msg;
    private Vector3 color;

    public TextGUI(Vector2f position,Vector2f size,String sentence, Font font, Vector3 color) {
        this.sentence = sentence;
        this.pos=new Vector2f(position.x,position.y);
        this.font = font;
        this.color = color;
        msg=font.getMessage(sentence,this.pos,size,color,false);
    }

    @Override
    public void render(ElementColorShader shader) {
        FontManager.addMessageToRender(msg);
    }

    @Override
    public void cleanUp() {
        msg.cleanUp();
    }

    @Override
    public void resize(int width, int height) {

    }
}
