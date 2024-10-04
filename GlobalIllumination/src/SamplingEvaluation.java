import entity.Vector3;
import stats.Stats;
import visualisation.Boxplot;
import visualisation.Graph;
import visualisation.Plot;
import visualisation.Scatter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SamplingEvaluation {

    public static float evaluation(Color c1,Color c2){
        Vector3 vc1 =new Vector3(c1.getRed()/255f,c1.getGreen()/255f,c1.getBlue()/255f);
        Vector3 vc2 =new Vector3(c2.getRed()/255f,c2.getGreen()/255f,c2.getBlue()/255f);
        return vc1.sub(vc2).length();
    }

    public static void evaluate(String fileDirA,String fileDirB,String referencePath,String figDir) throws IOException {
        Comparator<File>fileComp=new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                String name1=o1.getName().split("_")[1];
                String name2=o2.getName().split("_")[1];
                return (Integer.parseInt(name1.substring(0,name1.length()-4))<Integer.parseInt(name2.substring(0,name2.length()-4)))?-1:1;
            }
        };

        File dirA= new File(fileDirA);
        BufferedImage reference = ImageIO.read(new File(referencePath));
        File[] imagesFileA =dirA.listFiles();

        List<File>files=Arrays.asList(imagesFileA);
        files.sort(fileComp);
        imagesFileA=files.toArray(imagesFileA);


        List<BufferedImage>imagesA=new ArrayList<>();
        List<float[]>differencesA=new ArrayList<>();

        List<Integer> timesA=new ArrayList<>();
        for (int i = 0; i < imagesFileA.length; i++) {
            String name=imagesFileA[i].getName().split("_")[1];
            timesA.add(Integer.parseInt(name.substring(0,name.length()-4)));
            imagesA.add(ImageIO.read(imagesFileA[i]));
            differencesA.add(evaluateImage(imagesA.get(imagesA.size()-1),reference));
        }
        File dirB= new File(fileDirB);
        File[] imagesFileB =dirB.listFiles();
        files=Arrays.asList(imagesFileB);
        files.sort(fileComp);
        imagesFileB=files.toArray(imagesFileB);

        List<BufferedImage>imagesB=new ArrayList<>();
        List<float[]>differencesB=new ArrayList<>();
        List<Integer> timesB=new ArrayList<>();
        for (int i = 0; i < imagesFileB.length; i++) {
            String name=imagesFileB[i].getName().split("_")[1];
            timesB.add(Integer.parseInt(name.substring(0,name.length()-4)));
            imagesB.add(ImageIO.read(imagesFileB[i]));
            differencesB.add(evaluateImage(imagesB.get(imagesB.size()-1),reference));
        }
        System.out.println("Fin loading et préprocessing data");
        //----------------------------------------------
        Color[] colors={Color.RED,Color.GREEN,Color.BLUE,Color.CYAN};
        float[] quartile={0.5f,0.75f,0.95f,0.99f};//,0.95f,0.99f
        float[][] mapA=new float[quartile.length][Math.min(imagesA.size(),imagesB.size())];
        float[] time=new float[Math.min(imagesA.size(),imagesB.size())];
        for (int i = 0; i < mapA[0].length; i++) {
            float[]qus= Stats.percentile(differencesA.get(i),quartile);
            for (int j = 0; j < quartile.length; j++) {
                mapA[j][i]=qus[j];
            }
            time[i]=timesA.get(i);
        }

        System.out.println(Arrays.toString(time));
        Color[] colorsB={Color.GRAY,Color.BLACK,Color.YELLOW,Color.ORANGE};
        float[][] mapB=new float[quartile.length][Math.min(imagesA.size(),imagesB.size())];
        for (int i = 0; i < mapB[0].length; i++) {
            float[]qus= Stats.percentile(differencesB.get(i),quartile);
            for (int j = 0; j < quartile.length; j++) {
                mapB[j][i]=qus[j];
            }
        }

        for (int i = 0; i <quartile.length; i++) {
            //Scatter.scatter(time,mapA[i],2,colors[i]);

            //Graph.figure.addLegend("Simple "+(int)(100*quartile[i])+"%", colors[i]);
           // Scatter.scatter(time,mapB[i],2,colorsB[i]);
            Plot.plot(time,mapB[i],colorsB[i],"Opti "+(int)(100*quartile[i])+"%");
            Plot.plot(time,mapA[i],colors[i],"Simple "+(int)(100*quartile[i])+"%");
           // Graph.figure.addLegend("Opti "+(int)(100*quartile[i])+"%", colorsB[i]);
        }
        Graph.showLegend(true);
        Graph.show();

    }

    public static float[] evaluateImage(BufferedImage img,BufferedImage reference){
        float[] R=new float[img.getWidth()*img.getHeight()];
        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                R[i*img.getHeight()+j]=evaluation(new Color(img.getRGB(i,j)),new Color(reference.getRGB(i,j)));
            }
        }
        return R;
    }
}
