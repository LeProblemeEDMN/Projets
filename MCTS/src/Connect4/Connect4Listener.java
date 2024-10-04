package Connect4;


import MCTSGame.MCTSNode;
import MCTSMinimax.MCTSMinPlayer;
import MCTSMinimax.MCTSMinimaxConstant;
import MCTSMinimax.P4Frame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Scanner;

public class Connect4Listener {
    public static boolean PLAYGAME=true;
    private static Connect4Player p1 = new Connect4Player();

    public static void train(String saveDir) throws IOException {
        long tl =System.currentTimeMillis();
        p1 = new Connect4Player(saveDir);



        System.out.println("firstNode = "+p1.getFirstNode().getVisited());

        System.out.println("Load in "+(System.currentTimeMillis() - tl)/1000+" seconds");
        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.SHORT);

        P4Frame frame = new P4Frame();//interface graphique



       /* for (int i = 0; i < 75; i++) {
            System.out.println("epoch:"+i);
            train(p1,20,saveDir,frame);
        }*/


        int total=0;
        BufferedWriter writer=new BufferedWriter(new FileWriter(new File("res/training.txt")));
        for(MCTSNode n:p1.getMap().values()){

            if(n.getVisited()>1000){
                total++;
                String keyToString=""+n.getKey()[0];
                for (int i = 1; i < 7; i++) keyToString+="/"+n.getKey()[i];
                writer.write(keyToString+" "+n.getVisited()+" "+(n.getTurn()==1?n.getWin():(n.getVisited()-n.getWin())));
                writer.newLine();
            }
        }
        System.out.println("TOTAL:"+total);
        writer.close();

       if(!PLAYGAME)return;

        //play vs human
        while (true) {
            Scanner s = new Scanner(System.in);
            GameP4 game = new GameP4();
            p1.setupGame(game);
            frame.setColor(game);
            MCTSMinimaxConstant.LEARNING_RATE = 1.41f;// 1.41f;
            MCTSMinimaxConstant.SIMULATIONS_PER_MOVE = 20000;
            MCTSMinimaxConstant.MINIMAX_DEPTH = 1;
            MCTSMinimaxConstant.bestMove = true;
            MCTSMinimaxConstant.AVOID_TRAINING = true;

            System.out.println("Debut");
            while (game.checkWin() == 0 && game.getAllState().length>0) {
                System.out.println(game.turn+"###############################");
                if (game.turn == 1){
                   game = (GameP4) p1.turn(true);
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
            p1.saveMCTS(saveDir);
            game.display();
            System.out.println("WIN:" + game.checkWin());
        }
    }
    public static void train(Connect4Player p1, int nbGame, String saveDir, P4Frame frame ){
        int p1Win = 0;
        int p2Win =0;
        MCTSMinimaxConstant.AVOID_TRAINING = false;
        for (int i = 0; i < nbGame; i++) {
            GameP4 game = new GameP4();
            int nbT = 0;

            p1.setupGame(game);
            while (nbT<42 && game.checkWin()==0){
                game=(GameP4) p1.turn(false);
                frame.setColor(game);
                if(game==null)break;
                nbT++;
            }
            if(game==null)continue;
            int winner = game.checkWin();
            p1.endGame();
            if(winner==1)p1Win++;
            if(winner==-1)p2Win++;
            //System.out.println(i);
        }
        System.out.println("   P1:"+p1Win+" P2:"+p2Win);
        System.out.println("First Node:"+p1.getFirstNode().getVisited()+" "+p1.getFirstNode().getWin());
        for (int[] n:p1.getFirstNode().getNextNodesKey()) {
            System.out.println("    "+p1.getNode(n));
        }

        long t =System.currentTimeMillis();

        p1.saveMCTS(saveDir);
        System.out.println("   Saved in "+(System.currentTimeMillis() - t)/1000+" seconds");
    }
}
