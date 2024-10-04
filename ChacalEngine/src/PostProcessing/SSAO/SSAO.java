package PostProcessing.SSAO;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import toolbox.maths.Vector3;

public class SSAO {
   
   public static List<Vector3f> getSSAOKernel(){
      List<Vector3f> kernel = new ArrayList<Vector3f>();
      for(int i = 0; i < 16; i++){
         Vector3 sample = new Vector3(((float)Math.random()) * 2 - 1, ((float)Math.random()) * 2 - 1, ((float)Math.random()));
         sample.normalize();
         sample.mul(((float)Math.random()));
         float scale = 1f/16f;
         scale = lerp(0.1f, 1f, scale * scale);
         sample.mul(scale);
         kernel.add(sample.getOglVec());
         
      }
      return kernel;
   }
   
   public static int createSSAONoiseTexture(){
      List<Vector3f> ssaoNoise = new ArrayList<Vector3f>();
      for(int i = 0; i < 16; i++){
         Vector3f noise = new Vector3f(((float)Math.random()) * 2 - 1, ((float)Math.random()) * 2 - 1, 0);
         noise.normalise();
         ssaoNoise.add(noise);
      }
      int noisetexture = GL11.glGenTextures();
    //  Loader.TEXTURE_LIST.add(noisetexture);
      FloatBuffer buffer = BufferUtils.createFloatBuffer(ssaoNoise.size() * 3);
      for(Vector3f v : ssaoNoise){
         buffer.put(v.x);
         buffer.put(v.y);
         buffer.put(v.z);
      }
      buffer.flip();
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, noisetexture);
      GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB16F, 4, 4, 0, GL11.GL_RGB, GL11.GL_FLOAT, buffer);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
      GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
      return noisetexture;
   }
   static public float lerp (float fromValue, float toValue, float progress) {
		return fromValue + (toValue - fromValue) * progress;
}
}
