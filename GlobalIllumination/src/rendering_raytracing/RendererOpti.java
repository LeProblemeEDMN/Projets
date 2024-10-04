package rendering_raytracing;

import entity.Droite;
import entity.Entity;
import entity.TriangleIntersection;
import entity.Vector3;
import stats.Normalizer;
import utils.Constants;
import utils.MousePicker;
import visualisation.Graph;
import visualisation.Plot;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RendererOpti {

    public static final float QUANTILE=1.96f,PROBA_RANDOM=0.015f,PROBA_CHOOSE=0.12f;

    public int sx,sy;
    public JFrame frame;
    public JLabel lbl;

    public boolean renderSpeed=true;

    public RendererOpti(int sx, int sy) {
        this.sx = sx;
        this.sy = sy;
        frame=new JFrame();
        lbl=new JLabel();
        frame.setLayout(new FlowLayout());
        frame.setSize(sx*2,sy);

        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Vector3 findColor(Droite d, List<Entity> entities){
        TriangleIntersection intersection=null;
        Entity entity=null;
        for (Entity e:entities) {
            TriangleIntersection i=e.intersect(d,intersection!=null?intersection.t:999999,false);
            if(i!=null){
                intersection=i;
                entity=e;
            }
        }
        if(intersection==null)return Constants.SKY_COLOR;
        Color c = entity.getRGB(intersection.textureCoord);
        return new Vector3(c.getRed()/255f,c.getGreen()/255f,c.getBlue()/255f);
    }

    public void simpleRender(Scene sc, MousePicker picker,String dir) throws IOException {
        BufferedImage image = new BufferedImage(Constants.WIDTH*2,Constants.HEIGHT,BufferedImage.TYPE_INT_RGB);
        Vector3[][] colors=new Vector3[Constants.WIDTH][Constants.HEIGHT];
        Vector3[][] colors_squared=new Vector3[Constants.WIDTH][Constants.HEIGHT];
        Vector3[][] colors_variance=new Vector3[Constants.WIDTH][Constants.HEIGHT];

        int[][] pass = new int[Constants.WIDTH][Constants.HEIGHT];

        float[][] values = new float[Constants.WIDTH][Constants.HEIGHT];

        List<Vector3>[][] pastVecs = new ArrayList[Constants.WIDTH][Constants.HEIGHT];
        int pastLength=3;

        long beginTime=System.currentTimeMillis();
        long saveTime=System.currentTimeMillis();
        float seuil=0;

        while (true) {
            long t=System.currentTimeMillis();

            List<PixelOpti> pixelsValues=new ArrayList<>();
            int skip=0;
            for (int i = 0; i < image.getWidth()/2; i++) {
                for (int j = 0; j < image.getHeight(); j++) {


                    /*if(pass[0][0]>10 && values[i][j]<seuil){
                        if(Math.random()>PROBA_RANDOM){
                            image.setRGB(i+image.getWidth()/2, image.getHeight() - 1 - j, Color.BLACK.getRGB());

                            skip++;
                            continue;
                        }
                    }*/

                    image.setRGB(i+image.getWidth()/2, image.getHeight() - 1 - j,Color.RED.getRGB());
                    Droite d = new Droite();
                    d.O = picker.pos;
                    //System.out.println(picker.pos);
                    d.D = picker.calculateMouseRay(((float) (i)) / image.getWidth() * 2 - 1, ((float) (j)) / image.getHeight() * 2 - 1);
                    d.D = picker.calculateMouseRay(((float) (i+Math.random())) / image.getWidth() * 2 - 1, ((float) (j+0*Math.random())) / image.getHeight() * 2 - 1);

                    Vector3 oldColor =colors[i][j];
                    if(oldColor==null)oldColor=new Vector3();
                    Vector3 currentcolor = sc.computeRay(d, 5,false);

                    Vector3 color=currentcolor.getAdd(oldColor.mul(pass[i][j]));
                    pass[i][j]++;
                    color.mul(1.0f/pass[i][j]);
                    image.setRGB(i, image.getHeight() - 1 - j, new Color((int) Math.min(255,255 * color.x), (int) Math.min(255,255 * color.y), (int) Math.min(255,255 * color.z)).getRGB());
                    colors[i][j]=color;

                    Vector3 oldColor2 =colors_squared[i][j];
                    if(oldColor2==null)oldColor2=new Vector3();
                    colors_squared[i][j]=currentcolor.getMul(currentcolor).getAdd(oldColor2.mul(pass[i][j]-1)).mul(1.0f/pass[i][j]);

                    colors_variance[i][j]=colors_squared[i][j].getSub(color.getMul(color));

                    values[i][j]=(float) (Math.sqrt(Math.max(0,colors_variance[i][j].x))+Math.sqrt(Math.max(0,colors_variance[i][j].y))+Math.sqrt(Math.max(0,colors_variance[i][j].z)))/ (float) Math.sqrt(pass[i][j]+Math.pow(10,-30))+QUANTILE*colors_variance[i][j].norm1()/pass[i][j];
                    //System.out.println(values[i][j]+" "+colors_variance[i][j]);
                    pixelsValues.add(new PixelOpti(i,j,values[i][j]));
                }
            }

            pixelsValues=pixelsValues.stream().sorted().collect(Collectors.toList());

            seuil=pixelsValues.get((int)(PROBA_CHOOSE* pixelsValues.size())).value;
            System.out.println(pixelsValues.get(0).value+" "+seuil);
            ImageIcon icon = new ImageIcon(image);
            lbl.setIcon(icon);
            System.out.println("PASSAGE:"+pass[0][0] +" skip:"+(float)(2*skip)/(image.getWidth()*image.getHeight()));
            System.out.println("TIME:"+(System.currentTimeMillis()-t));

            if(System.currentTimeMillis()-saveTime>30000 || pass[0][0]%2==0){
                BufferedImage writeimage = new BufferedImage(Constants.WIDTH,Constants.HEIGHT,BufferedImage.TYPE_INT_RGB);
                for (int i = 0; i < image.getWidth()/2; i++) {
                    for (int j = 0; j < image.getHeight(); j++) {
                        writeimage.setRGB(i,j,image.getRGB(i,j));
                    }

                    }
                System.out.println("SAVE "+(int)((System.currentTimeMillis()-beginTime)/1000));
                //ImageIO.write(writeimage,"png",new File(dir+"/time_"+(int)((System.currentTimeMillis()-beginTime)/1000)+".png"));
                ImageIO.write(writeimage,"png",new File(dir+"/time_"+pass[0][0]+".png"));

                saveTime=System.currentTimeMillis();
                if(System.currentTimeMillis()-beginTime>60000000){
                    return;
                }
            }
        }
    }

}

class PixelOpti implements Comparable{

    int x,y;
    float value;

    public PixelOpti(int x, int y, float value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof PixelOpti)return -Float.compare(value,((PixelOpti) o).value);
        return 0;
    }
}
