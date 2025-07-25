package GUI.font;

import main.MainLoop;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import toolbox.maths.Vector3;

public class Message {
    protected String sentence;
    protected int VAOid,vbo1,vbo2;
    protected Vector3 color;
    protected boolean oneUse;
    protected Font font;


    public Message(Font f,String sentence, int VAOid, int vbo1, int vbo2, Vector3 color, boolean oneUse) {
        this.sentence = sentence;
        this.VAOid = VAOid;
        this.vbo1 = vbo1;
        this.vbo2 = vbo2;
        this.color = color;
        this.oneUse = oneUse;
        font =f;
    }

    public Vector3 getColor() {
        return color;
    }

    public void setColor(Vector3 color) {
        this.color = color;
    }

    public int getVAOid() {
        return VAOid;
    }

    public boolean isOneUse() {
        return oneUse;
    }

    public Font getFont() {
        return font;
    }

    public void cleanUp(){
        GL15.glDeleteBuffers(vbo1);
        GL15.glDeleteBuffers(vbo2);
        GL30.glDeleteVertexArrays(VAOid);
    }
}
