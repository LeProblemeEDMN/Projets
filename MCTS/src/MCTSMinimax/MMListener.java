package MCTSMinimax;

import MCTS.GameP4;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class MMListener {
    public static boolean PLAYGAME=true;
    private static MCTSMinPlayer p1 = new MCTSMinPlayer();

    public static void train(String saveDir){
        long tl =System.currentTimeMillis();
        p1 = new MCTSMinPlayer(saveDir);
        System.out.println("firstNode = "+p1.getFirstNode().getVisited());

        MCTSMinimaxConstant.LEARNING_RATE = (float) Math.sqrt(4);
        MCTSMinimaxConstant.SIMULATIONS_PER_MOVE = 500;
        MCTSMinimaxConstant.MINIMAX_DEPTH = 1;
        MCTSMinimaxConstant.bestMove=false;
        MCTSMinimaxConstant.AVOID_TRAINING = false;

        System.out.println("Load in "+(System.currentTimeMillis() - tl)/1000+" seconds");
        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.SHORT);
        P4Frame frame = new P4Frame();//interface graphique
        for (int j = 0; j < 0; j++) {
            //p1 = new MCTSMinPlayer(saveDir);
            for (int i = 0; i <30; i++) {
                long t = System.currentTimeMillis();
                System.out.println("Epoch " + i+j*3);
                train(p1, 30, saveDir,frame);
                System.out.println("   Time:" + (System.currentTimeMillis() - t) / 1000 + " " + shortDateFormat.format(new Date()));
            }
        }
       if(!PLAYGAME)return;

        //play vs human
        while (true) {
            Scanner s = new Scanner(System.in);
            GameP4 game = new GameP4();
            p1.setupGame(game);
            frame.setColor(game);

            MCTSMinimaxConstant.LEARNING_RATE = 1.41f;// 1.41f;
            MCTSMinimaxConstant.SIMULATIONS_PER_MOVE = 1350;
            MCTSMinimaxConstant.MINIMAX_DEPTH = 1;
            MCTSMinimaxConstant.bestMove = true;
            MCTSMinimaxConstant.AVOID_TRAINING = true;


            while (game.checkWin() == 0 && game.getAllState().length>0) {
                if (game.turn == 1){
                   game = p1.turn(true);
                   frame.setColor(game);
                   frame.choice =false;
                }
                else if (game.turn == -1) {
                    //game.display();
                    //int id = s.nextInt();
                    while (!frame.choice) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    game.play(frame.selected);
                    frame.choice =false;
                    p1.setNextStep(game.hashMap(),game.turn);
                    frame.setColor(game);
                }
            }
            game.display();
            p1.saveMCTS(saveDir);
            System.out.println("WIN:" + game.checkWin());
        }
    }

    public static void train(MCTSMinPlayer p1, int nbGame, String saveDir,P4Frame frame ){
        int p1Win = 0;
        int p2Win =0;

        for (int i = 0; i < nbGame; i++) {
            GameP4 game = new GameP4();
            int nbT = 0;

            p1.setupGame(game);
            while (nbT<42 && game.checkWin()==0){
                game=p1.turn(false);
                frame.setColor(game);
                if(game==null)break;
                nbT++;
            }
            if(game==null)continue;
            int winner = game.checkWin();
            p1.endGame();
            if(winner==1)p1Win++;
            if(winner==-1)p2Win++;
            System.out.println(i);
        }
        //System.out.println(p1.getFirstNode()+" "+p1.getFirstNode().nextNodes+" "+p1.getFirstNode().nextNodes.get(0).nextNodes.get(0));
        System.out.println("   P1:"+p1Win+" P2:"+p2Win);
        System.out.println("First Node:"+p1.getFirstNode().getVisited()+" "+p1.getFirstNode().getWin());
        long t =System.currentTimeMillis();

        p1.saveMCTS(saveDir);
        System.out.println("   Saved in "+(System.currentTimeMillis() - t)/1000+" seconds");
    }
}
