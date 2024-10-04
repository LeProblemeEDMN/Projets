package MCTS;

import Minimax.MinimaxNode;


import java.util.ArrayList;
import java.util.List;

public class NodeP4Minimax implements MinimaxNode {

    private static int[][] posValues={
            {3,4,5,7,5,4,3},
            {4,6,8,10,8,6,4},
            {5,8,11,13,11,8,5},
            {5,8,11,13,11,8,5},
            {4,6,8,10,8,6,4},
            {3,4,5,7,5,4,3}};

    private int[] key;
    private int depth;
    private int turn;

    public NodeP4Minimax(int[] key, int depth, int turn) {
        this.key = key;
        this.depth = depth;
        this.turn = turn;
    }

    public NodeP4Minimax(int[] key) {
        this.key = key;
    }

    @Override
    public List<MinimaxNode> getNextNodes() {
        GameP4 game = GameP4.createMap(key);
        game.turn = turn;
        int[][]states= game.getAllState();
        List<MinimaxNode>nodes=new ArrayList<>();
        for (int i = 0; i < states.length; i++) {
            nodes.add(new NodeP4Minimax(states[i],depth+1,-turn));
        }
        return nodes;
    }

    @Override
    public boolean isLeafNode() {
        int[][]states= GameP4.createMap(key).getAllState();
        return states.length==0 || GameP4.createMap(key).checkWin()!=0;
    }

    @Override
    public float evalNode() {
        GameP4 game = GameP4.createMap(key);
        int score = game.checkWin();//1 si IA win -1 si player win 0 sinon
        if(score!=0)
            return score*(500-depth*2);//score sert à mettre positif si joueur gagne inverse la depth pour que les victoire imminentes soit importante
        else{
            int eval = 0;
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 6; j++) {
                    if(game.map[i][j]==turn){
                        eval+=posValues[j][i];
                        for (int k = Math.max(0,i-1); k <= Math.min(6,i+1); k++) {
                            for (int l = Math.max(0,j-1); l <= Math.min(5,j+1); l++) {
                                if(game.map[k][l]!=0)eval+=game.map[k][l]*game.map[i][j];
                            }
                        }
                    }
                }
            }
            return turn*eval;
        }


    }

    public int[] getKey() {
        return key;
    }

    public void setKey(int[] key) {
        this.key = key;
    }
}
