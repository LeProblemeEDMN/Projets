package MCTSGame;

import MCTS.GameP4;
import MCTSMinimax.P4Minimax;
import Minimax.MinimaxNode;

import java.util.ArrayList;
import java.util.List;

public class GameMinimax implements MinimaxNode {
    protected int key[];
    private int depth;
    private int turn;
    private MCTSPlayer player;

    public GameMinimax(int[] key, int depth, int turn, MCTSPlayer player) {
        this.key = key;
        this.depth = depth;
        this.turn = turn;
        this.player = player;
    }

    public int[] getKey() {
        return key;
    }

    @Override
    public List<MinimaxNode> getNextNodes() {
        GameState game = player.createMap(key);
        game.turn = turn;
        int[][]states= game.getAllState();
        List<MinimaxNode>nodes=new ArrayList<>();
        for (int i = 0; i < states.length; i++) {
            nodes.add(new GameMinimax(states[i],depth+1,-turn,player));
        }
        return nodes;
    }

    @Override
    public boolean isLeafNode() {
        int[][]states= player.createMap(key).getAllState();
        return states.length==0 || player.createMap(key).checkWin()!=0;
    }

    @Override
    public float evalNode() {
        return 0;
    }
}
