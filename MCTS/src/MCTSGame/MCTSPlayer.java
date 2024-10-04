package MCTSGame;

import MCTS.GameP4;
import MCTSMinimax.Node;

import java.io.BufferedWriter;
import java.util.Arrays;
import java.util.HashMap;

public class MCTSPlayer {

    protected MCTSNode firstNode;
    protected HashMap<String,MCTSNode> map= new HashMap<>();
    protected MCTSNode currentNode;

    protected GameState game;

    public MCTSPlayer(int[] firstKey) {
        this.firstNode = new MCTSNode(firstKey,this,1);
        addNode(firstKey,firstNode);
        System.out.println(firstNode);
        currentNode = firstNode;
    }

    public void setupGame(GameState game){
        this.game = game;
        this.currentNode = firstNode;
        firstNode.setPreviousNode(null);
    }

    public GameState createMap(int[] key){
        System.out.println("CREATE MAP RETURN NULL");
        return null;
    }

    public GameState turn(boolean showInfo){

        if(currentNode==null)return null;
        MCTSNode newNode = currentNode.play();

        if(showInfo){
            System.out.println("current:"+currentNode);
            for (int[] n:getCurrentNode().nextNodesKey) {
                System.out.println("    "+getNode(n));
            }
        }
        newNode.setPreviousNode(currentNode);
        currentNode = newNode;
        if(currentNode==null)return null;

        if(showInfo){
            System.out.println("choose : "+currentNode+"    ");
            if(currentNode.nextNodesKey!=null)for (int i = 0; i < currentNode.nextNodesKey.size(); i++) {
                System.out.println("   "+getNode(currentNode.nextNodesKey.get(i)));
            }
            System.out.println();
        }

        int t =game.turn;
        game = this.createMap(currentNode.getKey());
        game.turn =-t;
        return game;
    }
    public void setNextStep(int[] map,int turn){
        if(currentNode!=null) {
            if(currentNode.nextNodesKey==null || currentNode.nextNodesKey.size()==0){
                currentNode.expand();
            }
            MCTSNode mapNode = getNode(map);
            if(mapNode==null) {
                mapNode = new MCTSNode(map,this,turn);
                addNode(map,mapNode);
            }
            if(currentNode!=mapNode){
                //Evite de boucler a l'infini
                currentNode.setPreviousNode(mapNode);
                //System.out.println(currentNode+" // "+mapNode);
            }

            currentNode.setTurn(turn);
            currentNode = mapNode;
            game=createMap(currentNode.getKey());
            game.turn=turn;
        }else{
            currentNode= firstNode;
        }
    }

    public void endGame(){
        if(currentNode!=null)
            currentNode.reward(1);
    }

    public GameState getState() {
        return game;
    }

    public MCTSNode getFirstNode() {
        return firstNode;
    }

    public MCTSNode getCurrentNode() {
        return currentNode;
    }

    public HashMap<String, MCTSNode> getMap() {
        return map;
    }

    public MCTSNode getNode(int[] key){
        return map.get(Arrays.toString(key));
    }
    public void addNode(int[] key , MCTSNode node){
        map.put(Arrays.toString(key), node);
    }

    private static int chunkSize = 200000;
    private static BufferedWriter nodeWriter;

    public void setCurrentNode(MCTSNode currentNode) {
        this.currentNode = currentNode;
    }

}
