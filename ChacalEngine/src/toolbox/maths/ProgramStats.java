package toolbox.maths;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.Sys;


public class ProgramStats {
	public static long totalPoint=0;
	public static long totalFrame=0;
	public static long sleepTime=0;
	
	public static Stat total=new Stat();
	public static Stat mainGame=new Stat();
	public static Stat mainRender=new Stat();
	public static Stat displayManager=new Stat();
	
	public static Stat mainPreparation=new Stat();
	public static Stat shadow=new Stat();
	public static Stat normalRender=new Stat();
	public static Stat gui=new Stat();
	public static Stat postProcessing=new Stat();
	
	public static Stat renderPreparation=new Stat();
	public static Stat sky=new Stat();
	public static Stat normalEntityRender=new Stat();
	public static Stat entityRender=new Stat();
	
	public static List<Stat> stats=new ArrayList<>();
	public static void startStat(Stat s) {
		stats.add(s);
		s.start();
	}
	public static void removeStat() {
		if(stats.size()>0) {
		Stat stat=stats.get(stats.size()-1);
		stat.end();
		stats.remove(stat);
		}
	}
	public static void startAndRemoveStat(Stat s) {
		removeStat();
		startStat(s);
	}
	public static void printTrace() {
		float secondes=((float)total.getTotalms()/1000.0f);
		float sleepT=((float)sleepTime/1000.0f);
		float gameTime=secondes-sleepT;
		float totalEntityTimeRender=0;
		System.out.println("Temps total: "+secondes+"s");
		System.out.println("Nombre d'images total: "+totalFrame+" Fps moyen: "+Math.floor((float)totalFrame*10/secondes)/10);
		System.out.println("Sleep Time: "+sleepT+" "+Math.floor((sleepT/secondes)*1000)/10.0+"% "+" Fps sans Sleep Time: "+Math.floor((float)totalFrame*10/(secondes-sleepT))/10);
		System.out.println();
		
		totalPoint=totalPoint/3;
		
		float v=((float)mainGame.getTotalms()/1000.0f);
		System.out.println("Main Game: "+v+"s "+Math.floor(((float)v/gameTime)*1000)/10.0+"%");
		float rs=((float)mainRender.getTotalms()/1000.0f);
		System.out.println("Main Render: "+rs+"s "+Math.floor((rs/gameTime)*1000)/10.0+"%");
		
		v=((float)mainPreparation.getTotalms()/1000.0f);
		System.out.println("	Preparation du MainRender: "+v+"s "+Math.floor((v/rs)*1000)/10.0+"%");
		v=((float)shadow.getTotalms()/1000.0f);
		System.out.println("	Shadow: "+v+"s "+Math.floor((v/rs)*100)+"%");
		float master=((float)normalRender.getTotalms()/1000.0f);
		System.out.println("	MasterRenderer: "+master+"s "+Math.floor((master/rs)*1000)/10.0+"%");
		
		v=((float)renderPreparation.getTotalms()/1000.0f);
		System.out.println("		Preparation du MasterRenderer: "+v+"s "+Math.floor((v/master)*1000)/10.0+"%");
		v=((float)sky.getTotalms()/1000.0f);
		System.out.println("		Temps de rendu du ciel: "+v+"s "+Math.floor((v/master)*1000)/10.0+"%");
		v=((float)entityRender.getTotalms()/1000.0f);
		totalEntityTimeRender+=v;
		System.out.println("		Temps de rendu des entit�es: "+v+"s "+Math.floor((v/master)*1000)/10.0+"%");
		v=((float)normalEntityRender.getTotalms()/1000.0f);
		totalEntityTimeRender+=v;
		System.out.println("		Temps de rendu des entit�es NM: "+v+"s "+Math.floor((v/master)*1000)/10.0+"%");
		
		v=((float)gui.getTotalms()/1000.0f);
		System.out.println("	GUIs: "+v+"s "+Math.floor((v/rs)*1000)/10.0+"%");

		v=((float)postProcessing.getTotalms()/1000.0f);
		System.out.println("	PostProcessing: "+v+"s "+Math.floor((v/rs)*1000)/10.0+"%");
	
		v=((float)displayManager.getTotalms()/1000.0f);
		System.out.println("DisplayManager: "+v+"s "+Math.floor(((float)v/gameTime)*1000)/10.0+"%");
		
		float millionpoint=(float)(totalPoint/100000000)+((float)(totalPoint%100000000)/100000000.0f);	
		System.out.println();
		
		System.out.println("Total des triangles:"+Math.floor(millionpoint*100)/100.0+" centaines de millions. Temps de rendu de cent millions de triangles: "+(int)Math.floor(totalEntityTimeRender*1000/millionpoint)+" ms");
		
	}
}
 class Stat{
	long totalms=0;
	long begintime=0;
	
	public void start() {
		begintime=System.currentTimeMillis();
	}
	public void end() {
		totalms+=(System.currentTimeMillis()-begintime);
	}
	public long getTotalms() {
		return totalms;
	}
}
