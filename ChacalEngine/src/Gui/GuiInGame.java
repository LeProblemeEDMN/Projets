package Gui;

import org.lwjgl.util.vector.Vector2f;


import Gui.FondRendu.ImageShader;
import Main.MainLoop;
import RenderEngine.DisplayManager;
import toolbox.maths.Vector3;

public class GuiInGame {
	public static boolean LAGOMETER_ACTIV=false;
	
	public static void renderBuffered(ImageShader imageRenderer) {
		imageRenderer.start2();
		MainLoop.longs.add(System.currentTimeMillis());
		long nb=-1;
		
		for (int i = 0; i < MainLoop.longs.size(); i++) {
			long long1=MainLoop.longs.get(i);
			if(System.currentTimeMillis()-3000>long1) {
				MainLoop.longs.remove(long1);
			}
														}	

	 float sizeX=0.5f/MainLoop.longs.size();
	 float sizeY=0.5f/100;
		int red=1000/(DisplayManager.FPS_CAP/2),orange=1000/(DisplayManager.FPS_CAP-2);
		int nbImage=MainLoop.longs.size();
		for (int i = 0; i <nbImage; i++) {
			long long1=MainLoop.longs.get(i);
			if(nb==-1) {
				nb=long1;
			}else {
				long dif=long1-nb;
				  Vector3 color=new Vector3(0, 1, 0);
				  if(dif>orange) {
					  color=new Vector3(1, 0.5f, 0);
					  if(dif>red) {
						  color=new Vector3(1, 0, 0);
					  }
				  }
				 imageRenderer.render(color, 0, 1.0f, new Vector2f(sizeX, sizeY*dif), new Vector2f(sizeX*i,0), false);
				 nb=long1;
			}
		}
		imageRenderer.end2();
	}
}
