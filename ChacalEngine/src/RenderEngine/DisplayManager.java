package RenderEngine;

import static org.lwjgl.opengl.GL11.glViewport;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;


import Loader.ConfigLoader;
import toolbox.maths.ProgramStats;

public class DisplayManager {
	public static int FPS_CAP=75;
	private static float delta=0.00001f;
	private static float lastFrameTime=0;
	public static float totalsec=0f;
	static long nb=System.currentTimeMillis();
	public static void init(ConfigLoader configLoader) {
		FPS_CAP=configLoader.getIntParameter("fpsCap");
		ContextAttribs attribs=new ContextAttribs(3, 2)
		.withForwardCompatible(true)
		.withProfileCore(true);

		try {
			Display.setDisplayMode(new DisplayMode(configLoader.getIntParameter("width"), configLoader.getIntParameter("height")));
			Display.setTitle(configLoader.getStringParameter("name"));
			Display.setResizable(true);

			
			Display.sync(60);
			Display.create(new PixelFormat().withDepthBits(24),attribs);

			//glEnable(GL13.GL_MULTISAMPLE);
			glViewport(0, 0, configLoader.getIntParameter("width"), configLoader.getIntParameter("height"));
			//GL11.glEnable(GL13.GL_MULTISAMPLE);
		} catch (LWJGLException e) {
			System.out.println("Erreur creation Display");
			e.printStackTrace();
		}

	}
	
	public static void update() {	
		
		Display.update(true);

		try {
			long tps=System.currentTimeMillis()-nb;
			long sleep=(1000/FPS_CAP)-tps;
			if(sleep>0) {	
				ProgramStats.removeStat();
				ProgramStats.sleepTime+=sleep;
					Thread.sleep(sleep);
			}else {
				ProgramStats.removeStat();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		delta=(float)(System.currentTimeMillis()-nb)/1000f;
		totalsec+=delta;
		nb=System.currentTimeMillis();
	}
	public static float getFrameTimeSecond() {
		return delta;
	}
	
	public static boolean isClosed() {
		return Display.isCloseRequested();
	}
	public static void closeDisplay() {
		Display.destroy();
	}
	private static long getCurrentTime() {
		return Sys.getTime()*1000/Sys.getTimerResolution();

	}
	
    private static ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException
    {
        BufferedImage bufferedimage = ImageIO.read(imageStream);
        int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), (int[])null, 0, bufferedimage.getWidth());
        System.out.println(aint.length);
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);

        for (int i : aint)
        {
            bytebuffer.putInt(i << 8 | i >> 24 & 255);
        }

        bytebuffer.flip();
        return bytebuffer;
    }

}
