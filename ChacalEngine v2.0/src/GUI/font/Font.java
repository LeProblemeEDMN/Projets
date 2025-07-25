package GUI.font;

import main.MainLoop;
import main.Pipeline;
import main.World;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;
import toolbox.maths.Vector3;

import javax.sound.midi.Soundbank;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class Font {
    public int texture;
    public HashMap<Character, Vector2f> offsetText = new HashMap<Character,Vector2f>();
    public HashMap<Character, Vector3> sizeText = new HashMap<Character,Vector3>();

    public Font(int texture, HashMap<Character, Vector2f> offsetText, HashMap<Character, Vector3> sizeText) {
        this.texture = texture;
        this.offsetText = offsetText;
        this.sizeText = sizeText;
    }

    public Message getMessage(String sentence, Vector2f center,Vector2f size,Vector3 color,boolean oneUse) {
        float letter_size_x = size.x / sentence.length();

        float[][] texture_data = process(sentence, new Vector2f(center.x - letter_size_x * sentence.length() / 2.0f, center.y - size.y / 2), new Vector2f(size.x, size.y));
        int VAOid = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(VAOid);

        int vbo1 = MainLoop.LOADER.storeDataInAttributeListWithoutManagingCleanUp(0, 2, texture_data[1]);
        int vbo2 = MainLoop.LOADER.storeDataInAttributeListWithoutManagingCleanUp(1, 2, texture_data[0]);

        GL30.glBindVertexArray(0);
        return new Message(this,sentence,VAOid,vbo1,vbo2,color,oneUse);
    }

    public float[][] process(String sentence,Vector2f origin,Vector2f size) {
        int length = sentence.length();
        //check for aligning the text
        float max_size = -999;//mzx height of the characters
        float total_size = 0;//size of the string
        float min_position=999;//min height of the characters
        for (int i = 0; i < length; i++) {
            char c = sentence.charAt(i);
            Vector3 o_size = sizeText.get('%');
            if(offsetText.containsKey(c)){
                o_size=sizeText.get(c);
            }
            total_size += o_size.x;
            max_size = Math.max(max_size, o_size.z);
            min_position = Math.min(min_position,o_size.z-o_size.y);
        }

        float[][] array=new float[2][12*length];
        float displacement=origin.x;//origin of the letter
        //float size_x=size.x/length;
        for(int i=0;i<length;i++){
            char c=sentence.charAt(i);
            Vector2f o_letter=offsetText.get('%');
            Vector3 o_size=sizeText.get('%');

            if(offsetText.containsKey(c)){
                o_letter=offsetText.get(c);
                o_size=sizeText.get(c);
            }
            int id=1;

            float size_y=o_size.y/(max_size-min_position)*size.y;//height of the character
            //y position of the bottom character on the screen
            float origin_y=origin.y+(o_size.z-o_size.y-min_position)/(max_size - min_position)*size.y;
            //width of the cahracter on the screen coordinates
            float size_x=size.x*(o_size.x/total_size);

            array[id][i*12]=displacement;
            array[id][i*12+1]=origin_y;
            array[id][i*12+2]=displacement+size_x;
            array[id][i*12+3]=origin_y;
            array[id][i*12+4]=displacement;
            array[id][i*12+5]=origin_y+size_y;

            array[id][i*12+6]=displacement;
            array[id][i*12+7]=origin_y+size_y;
            array[id][i*12+8]=displacement+size_x;
            array[id][i*12+9]=origin_y;
            array[id][i*12+10]=displacement+size_x;
            array[id][i*12+11]=origin_y+size_y;

            id=0;

            array[id][i*12]=o_letter.x;
            array[id][i*12+1]=o_letter.y+o_size.y;
            array[id][i*12+2]=o_letter.x+o_size.x;
            array[id][i*12+3]=o_letter.y+o_size.y;
            array[id][i*12+4]=o_letter.x;
            array[id][i*12+5]=o_letter.y;


            array[id][i*12+6]=o_letter.x;
            array[id][i*12+7]=o_letter.y;
            array[id][i*12+8]=o_letter.x+o_size.x;
            array[id][i*12+9]=o_letter.y+o_size.y;
            array[id][i*12+10]=o_letter.x+o_size.x;
            array[id][i*12+11]=o_letter.y;

            displacement+=size_x;
        }

        return array;
    }

    public int getTexture() {
        return texture;
    }
}
