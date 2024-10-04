package MCTS;

import Minimax.MinimaxNode;
import Minimax.MinimaxPruning;

import java.util.List;
import java.util.Random;

public class MinimaxPlayer {

    private Random random= new Random();
    private int code;
    public String name;

    private NodeP4Minimax firstNode;
    private NodeP4Minimax currentNode;

    public MinimaxPlayer() {
        this.firstNode = new NodeP4Minimax(new GameP4().hashMap(),0,1);
        currentNode = firstNode;
    }

    public void setupGame(GameP4 gameP4){
        firstNode.setKey(gameP4.hashMap());
        this.currentNode = firstNode;
    }

    public int[] turn(boolean showInfo){

        NodeP4Minimax bestNode =null;

        float maxEva = -999999f;
        List<MinimaxNode> nodes = currentNode.getNextNodes();
        System.out.println(nodes.size());
        float alpha=-999999f;
        boolean rdm=true;
        int i =6;
        for (MinimaxNode child:nodes) {
            float eva= MinimaxPruning.minimax(child, 9, alpha-1, 999999f, false);
            maxEva= Math.max(maxEva, eva);

            if(GameP4.createMap(((NodeP4Minimax)child).getKey()).checkWin()!=0)return ((NodeP4Minimax)child).getKey();

            i--;
            if(alpha<eva) {
                alpha = eva;
                bestNode = (NodeP4Minimax) child;
            }
            if(alpha!=0)rdm=false;
        }
        System.out.println();
        if(!rdm) return bestNode.getKey();
        else return ((NodeP4Minimax)nodes.get(random.nextInt(nodes.size()))).getKey();
    }
    public void setNextStep(int[] map){
        currentNode = new NodeP4Minimax(map,0,1);
    }

    public NodeP4Minimax getFirstNode() {
        return firstNode;
    }

    public NodeP4Minimax getCurrentNode() {
        return currentNode;
    }
}
