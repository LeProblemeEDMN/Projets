package MCTS;


public class MCTSPlayer {

    public int NODE_COMPTER=0;

    private Node firstNode;
    private Node currentNode;

    private GameP4 gameP4;
    private int code;
    public float learningRate = 2;
    public String name;

    public MCTSPlayer() {
        this.firstNode = new Node(null,null,this);
        currentNode = firstNode;
    }

    public MCTSPlayer(String file,String name) {
        this.name = name;
        this.firstNode = Node.readMCTS(file, this);
        currentNode = firstNode;
    }

    public void setupGame(GameP4 gameP4){
        this.gameP4 = gameP4;
        firstNode.setKey(gameP4.hashMap());
        this.currentNode = firstNode;
    }

    public void turn(boolean showInfo){
        if(showInfo){
            System.out.println("current:"+currentNode);
        }
        currentNode = currentNode.play();
        if(showInfo) System.out.println("choose : "+currentNode+"    "+currentNode.nextNodes);
        gameP4.nextPlay(currentNode.getKey());
    }
    public void setNextStep(int[] map){
        if(currentNode!=null) {
            int[] cs = gameP4.hashMap();
            currentNode = currentNode.getChild(cs);
        }else{
            currentNode= firstNode;
        }
    }
    public void endGame(){
        currentNode.reward(1);
    }

    public GameP4 getGameP4() {
        return gameP4;
    }

    public Node getFirstNode() {
        return firstNode;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

}
