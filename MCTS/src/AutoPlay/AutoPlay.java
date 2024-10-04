package AutoPlay;

import Connect4.Connect4Player;
import Connect4.GameP4;
import MCTSGame.MCTSPlayer;
import MCTSMinimax.MCTSMinimaxConstant;
import MCTSMinimax.P4Frame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class AutoPlay {
    private Robot robot;
    private boolean rightBegin = true, isInit = false;
    private BufferedImage pseudo;
    private Connect4Player player;
    private P4Frame frame = new P4Frame();
    private Scanner wait=new Scanner(System.in);


    public AutoPlay(){
        MCTSMinimaxConstant.LEARNING_RATE = 1.41f;// 1.41f;
        MCTSMinimaxConstant.SIMULATIONS_PER_MOVE = 2650;
        MCTSMinimaxConstant.MINIMAX_DEPTH = 1;
        MCTSMinimaxConstant.MINIMAX_DEPTH_DEFAULT = 9;
        MCTSMinimaxConstant.bestMove = true;
        MCTSMinimaxConstant.AVOID_TRAINING = true;
        player=new Connect4Player("res/MCTSMinimax2");
        try {
            pseudo = ImageIO.read(new File("res/pseudo.png"));
            robot = new Robot();
        } catch (AWTException | IOException e) {
            e.printStackTrace();
        }
    }

    public void  update(){
        try {

            //Dimension de l'écran
            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            //capture d'écran
            //BufferedImage screen = ImageIO.read(new File("res/screenshot.jpg"));
            BufferedImage screen = robot.createScreenCapture(new Rectangle(dimension.width, dimension.height));
            boolean rightToPlay = new Color(screen.getRGB(1230,930)).getGreen()<100;

            Color gridColor= new Color(screen.getRGB(710,410));
            if(gridColor.getRed()>240){
                System.out.println("PAS COMMENCE");
                for (int i = 0; i < 13; i++) {
                    robot.mouseMove(950,270+50*i);
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    Thread.sleep(100);
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                }

                player.setCurrentNode(player.getFirstNode());
                Thread.sleep(2500);
                isInit=false;
                return;
            }



            int goodPixel=0;
            for (int i = 0; i < pseudo.getWidth(); i++) {
                for (int j = 0; j < pseudo.getHeight(); j++) {
                    Color pseudoCol= new Color(pseudo.getRGB(i,j));
                    Color realCol= new Color(screen.getRGB(1195+i,988-32+j));
                    if(realCol.getRed()<100 && pseudoCol.getRed()<100){
                        goodPixel++;
                    }else if(realCol.getRed()>100 && pseudoCol.getRed()>100){
                        goodPixel++;
                    }
                }
            }
            boolean isOnRightSide=goodPixel>0.85*pseudo.getHeight()*pseudo.getWidth();
            if(!isInit){
                rightBegin=rightToPlay;
               isInit=true;
                System.out.println(rightBegin+" "+isOnRightSide+" #######################################################");
            }

            if(!(rightToPlay==isOnRightSide))return;
            //System.out.println(isOnLeftSide+" "+leftBegin);
            GameP4 game= new GameP4();
            for (int i = 0; i < 7; i++) {
                int x=660+100*i;
                for (int j = 0; j < 6; j++) {
                    int y=260+j*100;
                    Color c=new Color(screen.getRGB(x,y));
                    if(c.getRed()<100){
                        boolean vert=c.getGreen()>100;
                        game.map[i][5-j]=!rightBegin==vert?1:-1;
                    }
                }
            }
           // System.out.println((rightBegin==isOnRightSide?1:-1)+" "+rightToPlay);
            player.setNextStep(game.hashMap(),rightBegin==isOnRightSide?1:-1);
           // System.out.println(player.getCurrentNode());
           // game.display();
            frame.setColor(game);
            GameP4 newGgame = (GameP4) player.turn(true);
            //newGgame.display();
           // System.out.println("JOUE:"+player.getCurrentNode());
            frame.setColor(newGgame);

            int[]previousState=game.hashMap();
            int[]newState=newGgame.hashMap();
            for (int i = 0; i < 7; i++) {
                if(previousState[i]==newState[i])continue;
                Thread.sleep(800);
                robot.mouseMove(660+100*i,360);
                robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                Thread.sleep(100);
                robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                robot.mouseMove(0,0);
            }
            player.saveMCTS("res/MCTSMinimax2");

        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
