package GUI;

import GUI.shader.ElementColorShader;
import org.joml.Vector2f;

public abstract class Element {
    public Vector2f pos;
    public Vector2f size;
    public boolean canBeInteractedWith=false;//can we interact or is this element just a decoration

    public abstract void render(ElementColorShader shader);

    public abstract void cleanUp();

    public abstract void resize(int width, int height);
}
