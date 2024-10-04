package Main;

import java.util.ArrayList;
import java.util.List;

import Loader.NormalObjLoader.NormalMappedObjLoader;
import Loader.Texture.ModelTexture;
import Loader.TexturedModel;
import Entity.Ball;
import Entity.Plan;
import Loader.ObjLoader.objFileLoader;
import PostProcessing.PostProcessing;
import org.lwjgl.Sys;
import org.lwjgl.util.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import Loader.ConfigLoader;
import Loader.Loader;
import RenderEngine.DisplayManager;
import RenderEngine.gui.font.GUIText;
import toolbox.maths.MathHelper;
import toolbox.maths.Maths;
import toolbox.maths.ProgramStats;

public class MainLoop {
	public static ConfigLoader CONFIG;
	public static Loader LOADER;
	
	private static long time=System.currentTimeMillis();
	private static int fps=0;
	/*
	 * 1er mouvement perso tuto
	 *( god rays)
	 */
	public static void main(String[]args) {
		LOADER = new Loader();
		CONFIG=new ConfigLoader("res/config.txt");

		DisplayManager.init(CONFIG);

		Register.Init(LOADER);
		MainGame.Init();
		MainRender.Init(CONFIG);

		MainRender.PostInit();
		int i=0;
		while(!DisplayManager.isClosed()||i<3){

			MainGame.Update();
			MainRender.Update();
			DisplayManager.update();
			//fps();
			i++;
		}
		DisplayManager.closeDisplay();

	}
	public static void fps() {
		fps++;
		ProgramStats.totalFrame++;
		long t=System.currentTimeMillis();
		longfps.add(t);
		for (int i = 0; i < longfps.size(); i++) {
			if(t-longfps.get(i)>1000) {
				longfps.remove(i);
			}
		}
		Register.text.remove();
		Register.text=new GUIText(longfps.size()+" "+PostProcessing.combineFilter.exposure, 2, Register.type, new Vector2f(0, 0), 1f, false,true);
		//Register.text=new GUIText("vitesse:"+MainGame.ballPhysic.get(0).getVitesse().length(), 2, Register.type, new Vector2f(0, 0), 1f, false,true);
		Register.text.setColour(1, 1,0);
		if((System.currentTimeMillis()-time)>1000) {
			time=System.currentTimeMillis();
			System.out.println(fps+" fps");
			fps=0;
		}
	}
	
	public static void CleanUp() {
		MainGame.CleanUp();
		MainRender.CleanUp();
		LOADER.cleanUp();
	}
	public static List<Long >longs=new ArrayList<>();
	public static List<Long >longfps=new ArrayList<>();
}
