package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageMapper {
    public static double[][][] converse_image_to_double(String path) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));
        double[][][] img_double = new double[3][image.getWidth()][image.getHeight()];
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                Color c = new Color(image.getRGB(i,j));
                img_double[0][i][j]=c.getRed()/256.0;
                img_double[1][i][j]=c.getGreen()/256.0;
                img_double[2][i][j]=c.getBlue()/256.0;
            }
        }
        return img_double;
    }
    public static BufferedImage converse_double_to_image(double[][][] img_double) throws IOException {
        BufferedImage image = new BufferedImage(img_double[0].length,img_double[0][0].length,BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                Color c = new Color(toColor(img_double[0][i][j]),toColor(img_double[1][i][j]),toColor(img_double[2][i][j]));
                image.setRGB(i,j,c.getRGB());
            }
        }
        return image;
    }

    private static int toColor(double c){
        return (int)(Math.max(Math.min(c,1),0)*255);
    }
}
