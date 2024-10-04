package rendering_raytracing;

import entity.*;

import stats.Normalizer;
import stats.Stats;
import utils.Constants;
import utils.MousePicker;
import visualisation.*;
import visualisation.Image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Renderer {
    public int sx,sy;
    public JFrame frame;
    public JLabel lbl;

    public boolean renderSpeed=true;

    public Renderer(int sx, int sy) {
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

        int[][] pass = new int[Constants.WIDTH][Constants.HEIGHT];

        int[][] sk = new int[Constants.WIDTH][Constants.HEIGHT];

        List<Vector3>[][] pastVecs = new ArrayList[Constants.WIDTH][Constants.HEIGHT];
        int pastLength=6;
        float[][] diffVec = new float[Constants.WIDTH][Constants.HEIGHT];
        long beginTime=System.currentTimeMillis();
        long saveTime=System.currentTimeMillis();

        while (true) {
            long t=System.currentTimeMillis();

            float maxDiff=0;
            int skip=0;
            for (int i = 0; i < image.getWidth()/2; i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    sk[i][j]=0;
                    //SKIP
                    if(pass[i][j]>pastLength*3 && Math.random()>0.05f+400*Math.pow(diffVec[i][j],1.5f)){
                        skip++;
                        sk[i][j]=1;
                        maxDiff=Math.max(maxDiff,diffVec[i][j]);
                        continue;
                    }

                    Droite d = new Droite();
                    d.O = picker.pos;
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



                    if(pass[i][j]==1)pastVecs[i][j]=new ArrayList<>();


                    pastVecs[i][j].add(currentcolor);

                    Vector3 petiteMoy=new Vector3();
                    pastVecs[i][j].forEach(petiteMoy::add);
                    petiteMoy.mul(1.0f/pastVecs[i][j].size());
                    diffVec[i][j]=petiteMoy.getSub(color).length();
                    maxDiff=Math.max(maxDiff,diffVec[i][j]);

                    if(pass[i][j]> Math.sqrt(pastVecs[i][j].size())){//
                        pastVecs[i][j].remove(0);
                    }

                }
            }

            if(maxDiff>0){
                for (int i = 0; i < image.getWidth()/2; i++) {
                    for (int j = 0; j < image.getHeight(); j++) {
                        if(sk[i][j]==0) image.setRGB(i+image.getWidth()/2, image.getHeight() - 1 - j, new Color((int) Math.min(255,255 * diffVec[i][j]/maxDiff), (int) Math.min(255,255 * diffVec[i][j]/maxDiff), (int) Math.min(255,255 * diffVec[i][j]/maxDiff)).getRGB());
                        else image.setRGB(i+image.getWidth()/2, image.getHeight() - 1 - j,Color.RED.getRGB());
                    }
                }
            }

            ImageIcon icon = new ImageIcon(image);
            lbl.setIcon(icon);
            System.out.println("PASSAGE:"+pass[0][0] +" skip:"+(float)(2*skip)/(image.getWidth()*image.getHeight()));
            System.out.println("TIME:"+(System.currentTimeMillis()-t));

            if(System.currentTimeMillis()-saveTime>30000){
                BufferedImage writeimage = new BufferedImage(Constants.WIDTH,Constants.HEIGHT,BufferedImage.TYPE_INT_RGB);
                for (int i = 0; i < image.getWidth()/2; i++) {
                    for (int j = 0; j < image.getHeight(); j++) {
                        writeimage.setRGB(i,j,image.getRGB(i,j));
                    }

                    }
                System.out.println("SAVE "+(int)((System.currentTimeMillis()-beginTime)/1000));
                ImageIO.write(writeimage,"png",new File(dir+"/time_"+(int)((System.currentTimeMillis()-beginTime)/1000)+".png"));
                saveTime=System.currentTimeMillis();
                if(System.currentTimeMillis()-beginTime>60000000){
                    return;
                }
            }
        }
    }

    public static void renderPixel(Scene sc, MousePicker picker){
        int s=30000;
        float[]r=new float[s];
        float[]g=new float[s];
        float[]b=new float[s];

        int nbSamp=2400;
        float[]x=new float[nbSamp];
        float[]y=new float[nbSamp];
        //NORMALITE
        for (int l = 0; l < 600; l++) {
            int X=(int)(720*Math.random());
            int Y=(int)(480*Math.random());
            for (int i = 0; i < s; i++) {
                Droite d = new Droite();
                d.O = picker.pos;
                d.D = picker.calculateMouseRay(((float) (X+Math.random())) / 720 * 2 - 1, ((float) (Y+0*Math.random())) / 480 * 2 - 1);

                Vector3 currentcolor = sc.computeRay(d, 5,false);


                r[i]=currentcolor.x;
                g[i]=currentcolor.y;
                b[i]=currentcolor.z;

            }

            Normalizer norm=new Normalizer();
            norm.fit(r);
            r=norm.transform(r);
            norm.fit(g);
            g=norm.transform(g);
            norm.fit(b);
            b=norm.transform(b);


            for (int i = 0; i < nbSamp; i++) {
                int id= Math.max(0, Math.min((int)(nbSamp*(r[i]+3)/6),nbSamp-1));

                y[id]++;
                id= Math.max(0, Math.min((int)(nbSamp*(g[i]+3)/6),nbSamp-1));
                y[id]++;
                id= Math.max(0, Math.min((int)(nbSamp*(b[i]+3)/6),nbSamp-1));
                y[id]++;
            }


            /*for (int j = 0; j < 3; j++) {
                Histogram h=new Histogram(nbSamp);
                h.compute(j==0?r:(j==1?g:b));



                for (int i = 0; i <nbSamp; i++) {
                    x[i]=h.samples_lim[i];
                    y[i]+=h.numbers_ind[i];

                }

            }*/
        }
        x[0]=-3;
        for (int i = 1; i < nbSamp; i++) {
           x[i]=x[i-1]+6.0f/nbSamp;
        }
        Plot.plot(x,y,Color.BLACK);
        Graph.show();
    }

}
