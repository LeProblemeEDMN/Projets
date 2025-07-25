package main;

import main.ConfigLoader;
/*import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;*/
import toolbox.MouseBinding;
import toolbox.maths.ProgramStats;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

//import static org.lwjgl.opengl.GL11.glViewport;

public class DisplayManager {
	/*
	Manage the display: the frame rate, the resizing...
	 */
	public static int FPS_CAP=75;
	private static float delta=0.00001f;
	private static float lastFrameTime=0;
	public static float totalsec=0f;
	static long nb=System.currentTimeMillis();
	private static int width,height;
	public static boolean must_resize=false;
	public static int getWidth() {
		return width;
	}


	public static int getHeight() {
		return height;
	}


	public static long window;//window id
	//initialise open gl
	public static void init(ConfigLoader configLoader) {
		FPS_CAP=configLoader.getIntParameter("fpsCap");

		if (!glfwInit()) {
			throw new IllegalStateException("GLFW n'a pas pu être initialisé");
		}

		// Paramètres de la fenêtre (version OpenGL, profil)
		glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API); // OpenGL comme API
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4); // Version 3
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5); // Version 3
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); // Profil OpenGL Core
		/*GLFW.glfwWindowHint(GLFW.GLFW_RED_BITS, 16);
		GLFW.glfwWindowHint(GLFW.GLFW_GREEN_BITS, 16);
		GLFW.glfwWindowHint(GLFW.GLFW_BLUE_BITS, 16);
		GLFW.glfwWindowHint(GLFW.GLFW_ALPHA_BITS, 16);
		GLFW.glfwWindowHint(GLFW.GLFW_DEPTH_BITS, 24); // Optionnel, mais souvent nécessaire pour les scènes 3D
		GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, 8);*/

		width=configLoader.getIntParameter("width");
		height=configLoader.getIntParameter("height");
		// Créer la fenêtre
		window = glfwCreateWindow(configLoader.getIntParameter("width"), configLoader.getIntParameter("height"), "Fenêtre LWJGL", NULL, NULL);
		if (window == NULL) {
			throw new RuntimeException("Échec de la création de la fenêtre GLFW");
		}
		glfwMakeContextCurrent(window);
		glfwShowWindow(window);

		//quand on redimensionne l fenetre le flag must_resize est mit à true et le resize est géré dans MainLoop.
		GLFW.glfwSetFramebufferSizeCallback(window, new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int width_n, int height_n) {
				width=width_n;
				height=height_n;
				must_resize=true;
				// Mettre à jour le viewport OpenGL
				GL11.glViewport(0, 0, width, height);
			}
		});
		//scroll callback
		GLFW.glfwSetScrollCallback(DisplayManager.window, new GLFWScrollCallback() {
			@Override public void invoke (long win, double dx, double dy) {
				MouseBinding.mouseWheelVelocity += (float) dy;
			}
		});

		// Initialiser OpenGL
		GL.createCapabilities();
		//GL11.glEnable(GL30.GL_FRAMEBUFFER_SRGB);
	}
	
	public static void update() {
		// Échanger les buffers
		glfwSwapBuffers(window);

		// Gérer les événements (clavier, souris, etc.)
		glfwPollEvents();
		try {
			long tps=System.currentTimeMillis()-nb;
			long sleep=(1000/FPS_CAP)-tps-1;

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

		glClear(GL_COLOR_BUFFER_BIT);
	}

	public static float getFrameTimeSecond() {
		return delta;
	}

	public void resize(int width, int height) {
		this.width=width;
		this.height=height;
	}

	public static boolean isClosed() {
		return glfwWindowShouldClose(window);
	}
	public static void closeDisplay() {
		glfwDestroyWindow(window);
		glfwTerminate();
	}

}
