import AutoPlay.AutoPlay;
import Chess.ChessListener;
import Chess.Pieces;
import Chess.Plateau;
import Connect4.Connect4Listener;
import Connect4.Connect4Player;
import MCTS.ListenerMinimax;
import MCTSMinimax.MCTSMinPlayer;
import MCTSMinimax.MMListener;
import MCTSMinimax.Node;
import MCTSMinimax.P4Frame;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import MCTSGame.MCTSNode;

public class Main {

    public static void explore(Plateau plateau,int depth,boolean b){

        List<Plateau> p = plateau.deplacementPossible(b);
        for (Plateau pl:p) {
            if(depth==0)pl.deplacementPossible(!b);
            else {
                explore(pl,depth-1,!b);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        long t = System.currentTimeMillis();

Connect4Listener.train("res/MCTSMinimax2");
        //Connect4Player p1 = new Connect4Player("res/MCTSMinimax2");
        //List<MCTSNode> nodes=p1.getMap().values().stream().toList();

        /*int total=0;
        BufferedWriter writer=new BufferedWriter(new FileWriter(new File("res/training.txt")));
        for(MCTSNode n:nodes){
            float wr = (float) n.getWin()/n.getVisited();
            if(n.getVisited()>1000 && (wr<0.3f||wr>0.7f)){
                total++;
                String keyToString=""+n.getKey()[0];
                for (int i = 1; i < 7; i++) keyToString+="/"+n.getKey()[i];
                writer.write(keyToString+" "+n.getVisited()+" "+(n.getTurn()==1?n.getWin():(n.getVisited()-n.getWin())));
                writer.newLine();
            }
        }
        System.out.println("TOTAL:"+total);
        writer.close();*/


        //Connect4Listener.train("res/MCTSMinimax2");
        /*AutoPlay autoPlay=new AutoPlay();
        Thread.sleep(5000);
        while (true){
            autoPlay.update();
            Thread.sleep(50);
        }*/

        //ChessListener.train("res/Chess");
      /*  Connect4Listener.train("res/MCTSMinimax");
        Plateau plateau = new Plateau();
        explore(plateau,2,true);
        System.out.println(System.currentTimeMillis()-t);*/
    }
}
