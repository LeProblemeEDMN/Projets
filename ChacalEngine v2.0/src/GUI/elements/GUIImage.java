package GUI.elements;

import GUI.Element;
import GUI.GuiManager;
import GUI.shader.ElementColorShader;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import toolbox.maths.Vector3;

public class GUIImage extends Element {

    int texture=-1;
    Vector3 color;
    float alpha;

    public GUIImage(Vector2f position, Vector2f size, int texture, float alpha) {
        this.texture = texture;
        this.alpha = alpha;
        this.pos=new Vector2f(position.x-0.5f*size.x,position.y-0.5f*size.y);
        this.size=size;
    }

    public GUIImage(Vector2f position, Vector2f size,Vector3 color, float alpha) {
        this.color = color;
        this.alpha = alpha;
        this.pos=new Vector2f(position.x-0.5f*size.x,position.y-0.5f*size.y);
        this.size=size;
    }

    @Override
    public void render(ElementColorShader shader) {
        shader.scale.loadVector2D(size);
        shader.location.loadVector2D(pos);
        shader.alpha.loadFloat(alpha);
        if(texture==-1){
            shader.useText.loadFloat(0);
            shader.color.loadVector3(color);
            GuiManager.renderElement();
        }else{
            shader.useText.loadFloat(1);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D,texture);
            GuiManager.renderElement();
            shader.useText.loadFloat(0);
        }
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public void resize(int width, int height) {

    }
}
