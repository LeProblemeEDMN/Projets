package MCTSMinimax;

import MCTS.GameP4;
import Minimax.MinimaxNode;
import java.util.ArrayList;
import java.util.List;

public class P4Minimax implements MinimaxNode {
    private int[] key;
    private int depth;
    private int turn;

    public P4Minimax(int[] key, int depth, int turn) {
        this.key = key;
        this.depth = depth;
        this.turn = turn;
    }

    public P4Minimax(int[] key) {
        this.key = key;
    }

    @Override
    public List<MinimaxNode> getNextNodes() {
        GameP4 game = GameP4.createMap(key);
        game.turn = turn;
        int[][]states= game.getAllState();
        List<MinimaxNode>nodes=new ArrayList<>();
        for (int i = 0; i < states.length; i++) {
            nodes.add(new P4Minimax(states[i],depth+1,-turn));
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
            return (score==turn?1:-1)*(500);//score sert à mettre positif si joueur gagne inverse la depth pour que les victoire imminentes soit importante
        else{
            return 0;
        }
    }

    public int[] getKey() {
        return key;
    }

    public void setKey(int[] key) {
        this.key = key;
    }

    public int getTurn() {
        return turn;
    }
}
