package Chess;


import Connect4.Connect4Player;
import Connect4.GameP4;
import MCTSMinimax.MCTSMinimaxConstant;
import MCTSMinimax.P4Frame;

import java.text.DateFormat;
import java.util.Scanner;

public class ChessListener {
    public static boolean PLAYGAME=true;
    private static ChessPlayer p1 = new ChessPlayer();

    public static void train(String saveDir){
        long tl =System.currentTimeMillis();
        p1 = new ChessPlayer(saveDir);

        System.out.println(p1.getFirstNode());
        System.out.println("firstNode = "+p1.getFirstNode().getVisited());

        System.out.println("Load in "+(System.currentTimeMillis() - tl)/1000+" seconds");
        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.SHORT);
        FenetreEchec frame = new FenetreEchec();//interface graphique

       if(!PLAYGAME)return;
        JoueurHumain player =new JoueurHumain("Moi",frame,false);
        //play vs human
        while (true) {
            Scanner s = new Scanner(System.in);
            Plateau game = new Plateau();
            p1.setupGame(game);
            frame.affiche(game);
            System.out.println(game);
            MCTSMinimaxConstant.LEARNING_RATE = 1.41f;// 1.41f;
            MCTSMinimaxConstant.SIMULATIONS_PER_MOVE = 200;
            MCTSMinimaxConstant.MINIMAX_DEPTH = 1;
            MCTSMinimaxConstant.bestMove = true;
            MCTSMinimaxConstant.AVOID_TRAINING = true;

            int turn=0;
            while (game.checkWin() == 0 && game.getAllState().length>0 && turn <80) {
                if (game.turn == 1){
                   game = (Plateau) p1.turn(true);
                   frame.affiche(game);
                }
                /*else if (game.turn == -1){
                    game = (Plateau) p1.turn(false);
                    frame.affiche(game);
                }*/
                else if (game.turn == -1) {
                    game = player.decision(game.deplacementPossible(false),game);
                    p1.setNextStep(game.hashMap(),game.turn);
                    frame.affiche(game);
                }
                p1.saveMCTS(saveDir);
                turn++;
            }
            System.out.println("WIN:" + game.checkWin());
        }
    }
}
