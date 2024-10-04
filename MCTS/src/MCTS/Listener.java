package MCTS;

import java.text.DateFormat;
import java.util.Date;
import java.util.Scanner;

public class Listener {
    public static boolean PLAYGAME=false;
    private static MCTSPlayer p1 = new MCTSPlayer();

    public static void train(String saveDir,String netDir){
        long tl =System.currentTimeMillis();

        //p1 = new MCTSPlayer("res/P1","P1");

        p1.learningRate = 3f;
        System.out.println("Load in "+(System.currentTimeMillis() - tl)/1000+" seconds");
        DateFormat shortDateFormat = DateFormat.getDateTimeInstance(
                DateFormat.SHORT,
                DateFormat.SHORT);

        for (int i = 0; i < 0; i++) {
           long t =System.currentTimeMillis();
            System.out.println("Epoch "+i);
            train(p1,200,saveDir);
           System.out.println("   Time:"+(System.currentTimeMillis()-t)/1000+" "+ shortDateFormat.format(new Date()));
        }
       if(!PLAYGAME)return;

        //play vs human
        while (true) {
            Scanner s = new Scanner(System.in);
            GameP4 game = new GameP4();
            p1.setupGame(game);
            p1.learningRate=1.41f;
            Node.nbSim = 200;
            while (game.checkWin() == 0) {
                if (game.turn == 1){

                    p1.turn(true);
                }
                else if (game.turn == -1) {
                    game.display();
                    int id = s.nextInt();
                    game.play(id);
                    p1.setNextStep(game.hashMap());
                }
            }

            System.out.println("WIN:" + game.checkWin());
        }
    }

    public static void train(MCTSPlayer p1,int nbGame,String saveDir){
        int p1Win = 0;
        int p2Win =0;

        for (int i = 0; i < nbGame; i++) {
            //System.out.println("-----------------------------------------------------");
           // System.out.println("-----------------------------------------------------");
           // System.out.println("-----------------------------------------------------");
            GameP4 game = new GameP4();
            int nbT = 0;

            p1.setupGame(game);
            while (nbT<42 && game.checkWin()==0){
                p1.turn(false);
                nbT++;
            }
            int winner = game.checkWin();
            p1.endGame();
            if(winner==1)p1Win++;
            if(winner==-1)p2Win++;
        }
        //System.out.println(p1.getFirstNode()+" "+p1.getFirstNode().nextNodes+" "+p1.getFirstNode().nextNodes.get(0).nextNodes.get(0));
        System.out.println("   P1:"+p1Win+" P2:"+p2Win);
        long t =System.currentTimeMillis();


        Node.saveTree(saveDir,p1.getFirstNode(), p1.NODE_COMPTER);
        System.out.println("   Saved in "+(System.currentTimeMillis() - t)/1000+" seconds");
    }
}
