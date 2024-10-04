package upscaling;

import MatrixException.ArithmeticMatrixException;
import utils.DCT;
import utils.DCT2dim;
import utils.ImageMapper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Upscaler {



    public static void upscale(String inputPath,String outputPath,int period,int border,double scaleFactor) throws IOException, ArithmeticMatrixException {
        int newPeriod=(int)(period*scaleFactor);
        double[][][] image= ImageMapper.converse_image_to_double(inputPath);
        int w=image[0].length;
        int h=image[0][0].length;

        int newWidth=newPeriod*(w/period)+(int)(scaleFactor*(w%period));
        int newHeight=newPeriod*(h/period)+(int)(scaleFactor*(h%period));
        double[][][] newImg = new double[3][newWidth][newHeight];

        int totalPart=(1+w/period)*(1+h/period)*3;
        int part=0;
        long begin=System.currentTimeMillis();

        for (int channel = 0; channel < 3; channel++) {
            System.out.println("Channel:"+channel);
            for (int i = 0; i < w/period; i++) {
                for (int j = 0; j < h/period; j++) {
                    double[][] sub_im = sub_img(image,channel,i*period-border,j*period-border,period+2*border,period+2*border);
                    DCT2dim dct=new DCT2dim(period+2*border);
                    dct.computeCoeffs(sub_im);
                    replace(newImg,calculate_part_upscale(dct,period,period,scaleFactor,border),channel,i*newPeriod,j*newPeriod);
                    part++;
                    if(part%2000==0) System.out.println(getTimeInformation(totalPart,part,begin));
                }
                double[][] sub_im = sub_img(image,channel,i*period-border,h-h%period-border,period+2*border,h%period+2*border);
                DCT2dim dct=new DCT2dim(period+2*border);
                dct.computeCoeffs(sub_im);
                replace(newImg,calculate_part_upscale(dct,period,h%period,scaleFactor,border),channel,i*newPeriod,h/period*newPeriod);
                part++;
                if(part%2000==0) System.out.println(getTimeInformation(totalPart,part,begin));
            }

            for (int j = 0; j < h/period; j++) {
                double[][] sub_im = sub_img(image,channel,w-w%period-border,j*period-border,w%period+2*border,period+2*border);
                DCT2dim dct=new DCT2dim(period+2*border);
                dct.computeCoeffs(sub_im);
                replace(newImg,calculate_part_upscale(dct,w%period,period,scaleFactor,border),channel,w/period*newPeriod,j*newPeriod);
                part++;
                if(part%2000==0) System.out.println(getTimeInformation(totalPart,part,begin));
            }
            double[][] sub_im = sub_img(image,channel,w-w%period-border,h-h%period-border,w%period+2*border,h%period+2*border);
            DCT2dim dct=new DCT2dim(Math.max(w%period,h%period)+2*border);
            dct.computeCoeffs(sub_im);
            replace(newImg,calculate_part_upscale(dct,w%period,h%period,scaleFactor,border),channel,w/period*newPeriod,h/period*newPeriod);
        }

        ImageIO.write(ImageMapper.converse_double_to_image(newImg),"png",new File(outputPath));
    }

    private static String getTimeInformation(int total,int made,long begin){
        float percent=(float) made/total;
        int per=(int)(100*percent);
        float time_sec=(float) (System.currentTimeMillis()-begin)/1000;
        return per+"% Ellapsed:"+(int)time_sec+" Estimated:"+(int)(time_sec/percent*(1-percent));
    }

    private static double[][] calculate_part_upscale(DCT2dim dct,int periodX,int periodY,double scaleFactor,int border){
        int nx=(int)(scaleFactor*periodX);
        int ny=(int)(scaleFactor*periodY);

        double[][] part = new double[nx][ny];
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                part[i][j]=dct.getDCTValue((double) i*periodX/nx+border,(double) j*periodY/ny+border);
            }
        }
        return part;
    }
    private static void replace(double[][][] ori,double[][] place,int channel,int offsetX,int offsety){
        for (int i = 0; i < place.length; i++) {
            for (int j = 0; j < place[0].length; j++) {
                ori[channel][offsetX+i][offsety+j]=place[i][j];
            }
        }
    }
    private static double[][] sub_img(double[][][] ori,int channel,int offsetX,int offsetY,int lengthX,int lengthY){
        double[][] result=new double[lengthX][lengthY];
        for (int i = 0; i < lengthX; i++) {
            for (int j = 0; j < lengthY; j++) {
                if(offsetX+i<0 || offsetY+j<0)result[i][j]=ori[channel][Math.max(0,Math.min(ori[channel].length-1,offsetX+i))][Math.max(0,Math.min(ori[channel][0].length-1,offsetY+j))];
                else if(offsetX+i>=ori[channel].length || offsetY+j>=ori[channel][0].length)result[i][j]=ori[channel][Math.min(ori[channel].length-1,offsetX+i)][Math.min(ori[channel][0].length-1,offsetY+j)];
                else{
                    result[i][j]=ori[channel][offsetX+i][offsetY+j];
                }
            }
        }
        return result;
    }
    public static void upscale_simple(String inputPath,String outputPath,int period,int border,double scaleFactor) throws IOException, ArithmeticMatrixException {
        int newPeriod=(int)(period*scaleFactor);
        double[][][] image= ImageMapper.converse_image_to_double(inputPath);
        int w=image[0].length;
        int h=image[0][0].length;

        int newWidth=newPeriod*(w/period)+(int)(scaleFactor*(w%period));
        int newHeight=newPeriod*(h/period)+(int)(scaleFactor*(h%period));

        double[][][] newImg = new double[3][newWidth][newHeight];
        for (int channel = 0; channel < 3; channel++) {
            System.out.println("Channel:"+channel);
            for (int i = 0; i < w/period; i++) {
                for (int j = 0; j < h/period; j++) {

                    double[][] sub_im = sub_img(image,channel,i*period,j*period,period,period);
                    DCT2dim dct=new DCT2dim(period);
                    dct.computeCoeffs(sub_im);
                    replace(newImg,calculate_part_upscale(dct,period,period,scaleFactor,border),channel,i*newPeriod,j*newPeriod);
                }
                double[][] sub_im = sub_img(image,channel,i*period,h-h%period,period,h%period);
                DCT2dim dct=new DCT2dim(period);
                dct.computeCoeffs(sub_im);
                replace(newImg,calculate_part_upscale(dct,period,h%period,scaleFactor,border),channel,i*newPeriod,h/period*newPeriod);
            }

            for (int j = 0; j < h/period; j++) {
                double[][] sub_im = sub_img(image,channel,w-w%period,j*period,w%period,period);
                DCT2dim dct=new DCT2dim(period);
                dct.computeCoeffs(sub_im);
                replace(newImg,calculate_part_upscale(dct,w%period,period,scaleFactor,border),channel,w/period*newPeriod,j*newPeriod);
            }
            double[][] sub_im = sub_img(image,channel,w-w%period,h-h%period,w%period,h%period);
            DCT2dim dct=new DCT2dim(Math.max(w%period,h%period));
            dct.computeCoeffs(sub_im);
            replace(newImg,calculate_part_upscale(dct,w%period,h%period,scaleFactor,border),channel,w/period*newPeriod,h/period*newPeriod);
        }

        ImageIO.write(ImageMapper.converse_double_to_image(newImg),"png",new File(outputPath));
    }
}
