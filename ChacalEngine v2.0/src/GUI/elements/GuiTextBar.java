package GUI.elements;

import GUI.Element;
import GUI.GuiManager;
import GUI.font.Font;
import GUI.font.FontManager;
import GUI.font.Message;
import GUI.shader.ElementColorShader;
import org.joml.Vector2f;
import toolbox.InputManager;
import toolbox.maths.Vector3;

import java.util.ArrayList;
import java.util.List;

public class GuiTextBar extends Element {
    Vector3 color;
    Vector3 font_color;
    String value;
    String typing_value;//
    boolean selected;
    private Message displayed_text;
    private Vector2f center;
    int cursor_pos;
    private Font font;
    public GuiTextBar(Vector2f pos, Vector2f size, Vector3 color, Font font, String msg, Vector3 colorText) {
        this.pos = new Vector2f(pos.x-0.5f*size.x,pos.y-0.5f*size.y);//the gui shader use the left corner no the center to draw the object
        this.size = size;
        this.color = color;
        this.center=pos;
        this.canBeInteractedWith=true;
        this.font_color=colorText;
        this.value=msg;
        this.font=font;

        displayed_text=font.getMessage(msg,pos,new Vector2f(size.x*0.95f,size.y*0.95f),colorText,false);

    }

    public void update(float mouseX, float mouseY, boolean mousePressed){
        boolean inside=pos.x<=mouseX && pos.y<=mouseY && pos.x+size.x>=mouseX && pos.y+size.y>=mouseY;

        if(selected && !InputManager.last_written_text.isEmpty()){
            List<Character> char_tab=new ArrayList<>();
            for (int i = 0; i < typing_value.length(); i++) char_tab.add(typing_value.charAt(i));

            for (int i = 0; i < InputManager.last_written_text.size(); i++) {
                int id=InputManager.last_written_text.get(i);
                if(id==InputManager.text_enter_pressed){
                    selected=false;
                    value="";

                    for (int j = 0; j < char_tab.size(); j++) value+=char_tab.get(j);
                    displayed_text.cleanUp();
                    displayed_text=font.getMessage(value,center,new Vector2f(size.x*0.95f,size.y*0.95f),font_color,false);
                    break;
                }else if(id==InputManager.text_left_arrow_key_pressed){
                    cursor_pos=Math.max(0,cursor_pos-1);
                }else if(id==InputManager.text_right_arrow_key_pressed){
                    cursor_pos=Math.min(char_tab.size(),cursor_pos+1);
                }else if(id==InputManager.text_delete_pressed){
                    if(cursor_pos>0){
                        char_tab.remove(cursor_pos-1);
                        cursor_pos--;
                    }
                }else{
                    char_tab.add(cursor_pos,(char)id);
                    cursor_pos++;
                }

            }
            typing_value="";
            System.out.println(typing_value);
            for (int i = 0; i < char_tab.size(); i++) typing_value+=char_tab.get(i);
        }

        if(!this.selected && (mousePressed && inside)){
            selected=true;
            typing_value=value+"";
            cursor_pos=value.length();
        }
        if(InputManager.escape.isClicked()){
            displayed_text.cleanUp();
            displayed_text=font.getMessage(value,center,new Vector2f(size.x*0.95f,size.y*0.95f),font_color,false);
            selected=false;
        }

    }
    @Override
    public void render(ElementColorShader shader) {
        if(selected){
            shader.alpha.loadFloat(1);
            shader.scale.loadVector2D(size);
            shader.location.loadVector2D(pos);
            shader.color.loadVector3(color);
            GuiManager.renderElement();
            String new_msg="";
            for (int i = 0; i < cursor_pos; i++)
                new_msg+=typing_value.charAt(i);
            if(System.currentTimeMillis()%1000<500){
                new_msg+=".";
            }else new_msg+=" ";

            for (int i = cursor_pos; i < typing_value.length(); i++)
                new_msg+=typing_value.charAt(i);
            Message current=font.getMessage(new_msg,center,new Vector2f(size.x*0.95f,size.y*0.95f),font_color,true);
            FontManager.addMessageToRender(current);


        }else {
            shader.alpha.loadFloat(1);
            shader.scale.loadVector2D(size);
            shader.location.loadVector2D(pos);
            shader.color.loadVector3(color);
            GuiManager.renderElement();
            if(displayed_text!=null) FontManager.addMessageToRender(displayed_text);
        }
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public void resize(int width, int height) {

    }
}
