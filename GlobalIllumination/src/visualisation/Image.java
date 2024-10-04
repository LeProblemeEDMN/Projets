package visualisation;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Image {
    public static final int DPI=150;
    private BufferedImage image;
    private int width,height;
    private Font font;
    private Graphics2D graphics;


    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        image=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        graphics=image.createGraphics();

        graphics.fill(new Rectangle(0,0,width,height));

        setColor(Color.BLACK);
        font=new Font ("TimesRoman", Font.BOLD, 10);
        graphics.setFont(font);
    }

    public void setColor(Color color){
        graphics.setColor(color);
    }

    public void write(String sentence,int x,int y){
        graphics.drawString(sentence,x,y);
    }

    public void writeVertically(String sentence,int x,int y){
        AffineTransform defaultAt = graphics.getTransform();
        AffineTransform at = new AffineTransform();
        //at.setToRotation(-Math.PI / 2,width/2,height/2);
        at.rotate(-Math.PI / 2);
        Point2D a=at.deltaTransform(new Point2D.Float(x,y),null);
        System.out.println(a);
        graphics.setTransform(at);
        graphics.drawString(sentence,(int)a.getX(),(int)a.getY());
        graphics.setTransform(defaultAt);
    }
    public void setFont(Font font) {
        this.font = font;
        image.getGraphics().setFont(font);
    }

    public void saveFig(String PATH){
        try {
            ImageIO.write(image,"png", new File(PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawShape(Shape shape,boolean fill){
        if(fill)graphics.fill(shape);
        else graphics.draw(shape);
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    public BufferedImage getImage() {
        return image;
    }
}
