package visualisation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Graph {

    private static int compteurFrame=0;

    private static int w=720,h=480;

    public static  Figure figure=new Figure(w,h);

    private static String savePath="";
    private static boolean needtosave=false;

    public static void show(){
        figure.draw();
        if(needtosave) figure.getImage().saveFig(savePath);
        needtosave = false;

        JFrame frame=new JFrame("Chacal Plot " +compteurFrame);
        frame.setSize(figure.getW(),figure.getH());
        Image img=figure.getImage().getImage().getScaledInstance(figure.getW(),figure.getH(), BufferedImage.SCALE_SMOOTH);
        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        compteurFrame++;

        figure=new Figure(w,h);

    }

    public static void setSize(int width,int height){
        w=width;
        h=height;
        figure.resize(w,h);
    }

    public static void saveFig(String PATH){
        savePath = PATH;
        needtosave = true;
    }

    public static void setTitle(String title){
        figure.setTitle(title);
    }

    public static void showLegend(boolean show){
        figure.setShowLegendPlot(show);
    }
}
