package GUI.font;

import GUI.font.shader.FontShader;
import main.MainLoop;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import toolbox.maths.Vector3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FontManager {

    public static HashMap<String,Font> fonts=new HashMap<>();
    public static HashMap<Font, List<Message>> fonts_messages=new HashMap<>();
    private static FontShader shader;

    public static void init(){
        FontManager.loadFont("font/cambria.jpg","res/font/cambria.fnt","cambria");
        shader=new FontShader();
        shader.init();
    }

    public static void renderMessages(){
        shader.start();

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        for (Font font : fonts.values()){
            List<Message> messages=fonts_messages.get(font);
            if(messages.isEmpty())continue;

            GL13.glActiveTexture(GL13.GL_TEXTURE1);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D,font.getTexture());
            for(Message message : messages){
                GL30.glBindVertexArray(message.getVAOid());
                GL20.glEnableVertexAttribArray(0);
                GL20.glEnableVertexAttribArray(1);

                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, message.sentence.length()*6);//
                int error = GL11.glGetError();
                if (error != 0) {
                    System.out.println("OpenGL Error before rendering: " + error);
                }
                GL20.glDisableVertexAttribArray(1);
                GL20.glDisableVertexAttribArray(0);
                GL30.glBindVertexArray(0);
                if(message.isOneUse())message.cleanUp();
            }
            messages.clear();
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL11.glDepthMask(true);

        shader.stop();
    }

    public static void addMessageToRender(Message message){
        fonts_messages.get(message.font).add(message);
    }

    public static void loadFont(String imagePath, String dataPath,String fontName) {
        System.out.println("font");
        int imgText= MainLoop.LOADER.loadTexture(imagePath);
        HashMap<Character, Vector2f> offsetText = new HashMap<Character,Vector2f>();
        HashMap<Character, Vector3> sizeText = new HashMap<Character,Vector3>();

        try {
            BufferedReader reader=new BufferedReader(new FileReader(dataPath));
            String line1= reader.readLine();
            String[] line2= reader.readLine().split(" ");
            String line3= reader.readLine();
            float width_texture= (float) Integer.parseInt(line2[3].replace("scaleW=",""));
            float height_texture= (float)Integer.parseInt(line2[4].replace("scaleH=",""));
            float base= (float)Integer.parseInt(line2[2].replace("base=",""));
            int nb_character=Integer.parseInt(reader.readLine().replace("chars count=",""));
            for(int i=0;i<nb_character;i++) {
                String line= reader.readLine();

                char carac=(char)getValue(line,"id=");

                Vector2f offset=new Vector2f(getValue(line,"x=")/width_texture,getValue(line,"y=")/height_texture);

                Vector3 size=new Vector3(getValue(line,"width=")/width_texture,getValue(line,"height=")/height_texture,-getValue(line,"yoffset=")/height_texture);
                offsetText.put(carac,offset);
                sizeText.put(carac,size);
            }
            Font f=new Font(imgText,offsetText,sizeText);
            fonts.put(fontName,f);
            fonts_messages.put(f,new ArrayList<>());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static int getValue(String text,String key){
        return Integer.parseInt(text.split(key)[1].split(" ")[0]);
    }


    public static FontShader getShader() {
        return shader;
    }

}
