package visualisation;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class Figure {
    private float maxX,minX,maxY,minY;
    private boolean autolimit=true,firstSet=false;
    private int w,h;
    private Image image;
    private int planX,planY,planWidth,planHeight;

    private List<GraphicShape>shapes=new ArrayList<>();

    private Color axisColor=Color.GRAY;

    private int gradX=10,gradY=10;
    private int labelSize=20,axesSize=10,titleSize=20,legendSize=12;

    private boolean showLegend=true;


    private String title,labelX,labelY;

    private Font fontTitle,fontAxes,fontLabels, fontLegend;

    private List<Color> legendColors=new ArrayList<>();
    private List<String> legendNames=new ArrayList<>();
    private boolean showLegendPlot=true;

    public Figure(int w,int h){
        image=new Image(w,h);
        this.w=w;
        this.h=h;
    }

    public void resize(int w,int h){
        image=new Image(w,h);
        this.w=w;
        this.h=h;
    }

    private boolean isInFigure(GraphicShape shape){
        return (Math.max(shape.dx,shape.ex)<=maxX ||Math.min(shape.dx,shape.ex)>=minX) && (Math.max(shape.dy,shape.ey)<=maxY ||Math.min(shape.dy,shape.ey)>=minY);
    }

    private int imageCoordX(float x){
        float xPercent=(x-minX)/(maxX-minX);
        return planX+(int)(planWidth*xPercent);
    }
    private int imageCoordY(float y){
        float yPercent=(y-minY)/(maxY-minY);
        return planY+(int)(planHeight*yPercent);
    }

    public void prepareDraw(){
        if(minX==maxX){
            minX=9999;
            maxX=-9999;
            for (GraphicShape s:shapes) {
                minX=Math.min(s.dx,minX);
                maxX=Math.max(s.ex,maxX);
            }
            float difX=maxX-minX;
            float difY=maxY-minY;
            minX-=difX*0.05f;
            maxX+=difX*0.05f;
        }
        if(minY==maxY){
            minY=9999;
            maxY=-9999;
            for (GraphicShape s:shapes) {
                minY=Math.min(s.dy,minY);
                maxY=Math.max(s.ey,maxY);
            }
            float difY=maxY-minY;
            minY-=difY*0.05f;
            maxY+=difY*0.05f;
        }

        //set the position of the elements
        planX=w/100;
        planY=h/100;
        if(showLegend){
            planY+=axesSize;
            planX+=3*axesSize;
        }

        if(labelX!=null)planY+=labelSize;
        if(labelY!=null)planX+=labelSize;

        planWidth=w-planX;
        planHeight=h-planY;

        if (title!=null)planHeight-=titleSize;
        fontTitle=new Font ("TimesRoman", Font.BOLD, titleSize);
        fontLabels=new Font ("TimesRoman", Font.BOLD, labelSize);
        fontAxes=new Font ("TimesRoman", Font.BOLD, axesSize);
        fontLegend=new Font ("TimesRoman", Font.BOLD, legendSize);
    }

    public void draw(){
        prepareDraw();
        image.getGraphics().setStroke(new BasicStroke(imageCoordY((maxY-minY)/150)-imageCoordY(0)));

        for (GraphicShape shape:shapes) {
            if(isInFigure(shape)){
                if(shape.type==GraphicShape.RECTANGLE){
                    int dx=imageCoordX(shape.dx);
                    int dy=imageCoordY(shape.dy);;
                    Shape rect=new Rectangle(dx,h-imageCoordY(shape.ey),imageCoordX(shape.ex)-dx,imageCoordY(shape.ey)-dy);
                    image.setColor(shape.color);
                    image.drawShape(rect,shape.fill);
                }else if(shape.type==GraphicShape.CIRCLE){
                    int dx=imageCoordX(shape.dx);
                    int dy=imageCoordY(shape.dy);
                    Shape circle=new Ellipse2D.Float(dx,h-dy,imageCoordX(shape.ex)-dx,imageCoordY(shape.ey)-dy);
                    image.setColor(shape.color);
                    image.drawShape(circle,shape.fill);
                }else{
                    image.setColor(shape.color);
                    image.getGraphics().drawLine(imageCoordX(shape.dx),h-imageCoordY(shape.dy),imageCoordX(shape.ex),h-imageCoordY(shape.ey));

                }
            }
        }

        //draw axis
        image.setColor(axisColor);
        image.getGraphics().drawLine(planX,h-planY,planX+planWidth,h-planY);
        image.getGraphics().drawLine(planX,h-planY,planX,h-planHeight-planY);
        float offsetX=0,sizeX=(maxX-minX)/gradX;
        float offsetY=0,sizeY=(maxY-minY)/gradY;
        if(minX<0 && maxX>0){
            float d=-minX/sizeX-(int)(-minX/sizeX);
            offsetX=(minX)+d*sizeX;
        }
        if(minY<0 && maxY>0){
            float d=-minY/sizeY+(int)(-minY/sizeY);
            offsetY=minY+d*sizeY;
        }

        //coord X
        image.getGraphics().setFont(fontAxes);
        while (offsetX<maxX){
            image.write(""+(float)((int)(offsetX*100))/100,imageCoordX(offsetX),h-planY+axesSize);
            offsetX+=sizeX;
        }
        //coord Y
        while (offsetY<maxY){
            String value=""+(float)((int)(offsetY*100))/100;
            image.write(value,planX-(int)(axesSize*value.length()*0.6f),h-imageCoordY(offsetY));
            offsetY+=sizeY;
        }

        //label X
        image.getGraphics().setFont(fontLabels);
        if(labelX!=null){
            System.out.println(labelX);
            image.write(labelX,planX+planWidth/2-(labelX.length()*labelSize/3),h-planY+axesSize+labelSize);
        }
        //label Y
        if(labelY!=null){
            int x=planX-axesSize-labelSize;
            int y=h-planY-planHeight/2+(labelY.length()*labelSize/3);
            image.writeVertically(labelY,-x,-y);
            //image.writeVertically(labelY,0,-50);
        }
        image.getGraphics().setFont(fontTitle);
        if(title!=null){
            image.write(title,w/2-(title.length()*labelSize/3),titleSize);

        }

        if(showLegend)drawLegend();
    }

    //draw la legend carre de couleur puis le nom de l'objet associe
    private  void drawLegend(){
        int items=legendNames.size();
        float l=maxX-minX;
        image.getGraphics().setFont(fontLegend);
        for (int i = 0; i < items; i++) {
            //rectange de couleur
            Shape rect=new Rectangle(imageCoordX(minX+l/100),h-imageCoordY(maxY)+(i-1)*(legendSize+3),imageCoordX(minX + l/100 + l/50) - imageCoordX(minX + l/100),legendSize);
            image.setColor(legendColors.get(i));
            image.drawShape(rect,true);

            //legende le y est le centre du label (legendSize/2)
            image.setColor(Color.BLACK);
            image.write(legendNames.get(i),imageCoordX(minX + l/100 + l/46f),h-imageCoordY(maxY)+(i)*(legendSize+3) - legendSize/2);
        }
    }

    public List<GraphicShape> getShapes() {
        return shapes;
    }

    public Image getImage() {
        return image;
    }

    public void setLabelX(String labelX) {
        this.labelX = labelX;
    }

    public void setLabelY(String labelY) {
        this.labelY = labelY;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public boolean isShowLegendPlot() {
        return showLegendPlot;
    }

    public void setShowLegendPlot(boolean showLegendPlot) {
        this.showLegendPlot = showLegendPlot;
    }

    public Color getAxisColor() {
        return axisColor;
    }

    public List<Color> getLegendColors() {
        return legendColors;
    }

    public List<String> getLegendNames() {
        return legendNames;
    }

    public void addLegend(String legend, Color color){
        legendNames.add(legend);
        legendColors.add(color);
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }
}
