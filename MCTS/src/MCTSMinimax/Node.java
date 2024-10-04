package MCTSMinimax;

import MCTS.GameP4;
import Minimax.MinimaxNode;
import Minimax.MinimaxPruning;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Node {


    protected List<int[]> nextNodesKey;
    protected Node previousNode;

    private int win =0;
    private int visited = 0;
    private int[] key;
    private MCTSMinPlayer player;
    private int nodeId;

    private boolean canWin = false;

    private int turn = 1;


    private int idChild;
    
    private List<P4Minimax> minimaxRolloutNodesList = new ArrayList<>();
    
    public Node(int[] key, MCTSMinPlayer player,int turn) {
        this.key = key;
        this.player = player;
        if(key!=null)evaluateKey();
        this.nodeId=player.NODE_COMPTER;
        player.NODE_COMPTER++;

        this.turn = turn;

        idChild =0;
    }

    public Node(int nodeId, int win, int visited, boolean canWin, int[] key, MCTSMinPlayer player) {
        this.key = key;
        this.player = player;
        this.nodeId=nodeId;
        this.visited = visited;
        this.win = win;
        this.canWin = canWin;
    }

    public void expand(){
        GameP4 p4 = GameP4.createMap(key);
        p4.turn = this.turn;
        int[][] states = p4.getAllState();
        nextNodesKey = new ArrayList<>();
        for (int i = 0; i < states.length; i++) {
            Node nN = player.getNode(states[i]);
            if(nN==null){
                nN = new Node(states[i],player,-this.turn);
                player.addNode(states[i], nN);
            }
            nextNodesKey.add(states[i]);

            if(nN.canWin()){
                nextNodesKey.clear();
                nextNodesKey.add(states[i]);
                break;
            }
        }
    }

    public static Random RDM = new Random();

    public Node play(){
        if (this.nextNodesKey==null || this.nextNodesKey.size() == 0)this.expand();
        if(this.nextNodesKey.size() == 0){
            GameP4.createMap(this.key).display();
            System.out.println(GameP4.createMap(this.key).checkWin());
            System.out.println("End Tree");
            return null;
        }
        for (int i = 0; i < ( this.getVisited()>30000 && MCTSMinimaxConstant.AVOID_TRAINING? 0:MCTSMinimaxConstant.SIMULATIONS_PER_MOVE); i++) {
            Node promisingNode = findPromisingNode();
            if (promisingNode.nextNodesKey==null || promisingNode.nextNodesKey.size() == 0)promisingNode.expand();

            Node nodeToExplore = promisingNode;//node du tour de l'adversaire
            nodeToExplore.setPreviousNode(this);
            int winner = simulateRandomPlayout(nodeToExplore);
        }
        if(MCTSMinimaxConstant.bestMove)MCTSMinimaxConstant.LEARNING_RATE/=2;
        Node bestNode = findPromisingNode();
        if(MCTSMinimaxConstant.bestMove)MCTSMinimaxConstant.LEARNING_RATE*=2;
        
        if(bestNode.getWin()==0 || ((float)bestNode.getWin()/bestNode.getVisited()<0.07 && bestNode.getVisited()<100)) {

            //cherche avec le minimax pour eviter les coup bêtes.
            FittestMinimax minimaxNode = new FittestMinimax(key,0,turn);
            FittestMinimax bestNodeMin =null;

            List<MinimaxNode> nodes = minimaxNode.getNextNodes();

            float alpha=-999999f;
            for (MinimaxNode child:nodes) {
                float eva= MinimaxPruning.minimax(child, MCTSMinimaxConstant.MINIMAX_DEPTH_DEFAULT, alpha-1, 999999f, false);

                if(alpha<eva || (alpha==eva && RDM.nextInt(10)<3)) {
                    alpha = eva;
                    bestNodeMin = (FittestMinimax) child;
                }
            } 
            //ajoute Node
            Node NN = player.getNode( bestNode.getKey());
            bestNode = NN;
            //System.out.println("SWITCh "+bestNode);
        }
        
        return bestNode;
    }

    private int simulateRandomPlayout(Node node) {

        Node finalNode = node;

        int currentTurn = node.turn;

        GameP4 p4 = GameP4.createMap(node.key);
        p4.turn = -node.turn;

        int[] currentMap = node.key;
        int nb=0;
        while (p4.checkWin()==0){
            if(finalNode.minimaxRolloutNodesList.size()==0) {
            	boolean canContinue = finalNode.fillRolloutNodes();
            	if(!canContinue)return 0;
            }
            
            int a=RDM.nextInt(finalNode.minimaxRolloutNodesList.size());
            currentMap = finalNode.minimaxRolloutNodesList.get(a).getKey();
            
            currentTurn= -currentTurn;
            p4 = GameP4.createMap(currentMap);

            //ajoute Node
            Node NN = player.getNode(currentMap);
            if(NN==null){
                NN = new Node(currentMap,player,currentTurn);
                if(nb<3)player.addNode(currentMap, NN);
            }
            NN.setPreviousNode(finalNode);
            finalNode = NN;
            nb++;
        }
        finalNode.reward(1);
        return p4.checkWin();
    }
    //return si la partie peut continuer
    public static int nb=0;
    public boolean fillRolloutNodes() {
        //cherche avec le minimax pour eviter les coup bêtes.
        P4Minimax minimaxNode = new P4Minimax(key,0,turn);

        nb++;
        List<MinimaxNode> nodes = minimaxNode.getNextNodes();
        if(nodes.size()==0) {
            if(RDM.nextInt(2)==1){//une chance sur deux de faire 0.5 en moyenne pr une égalité sur tout l'arbre
                this.reward(1);
                this.reward(0);
            }
            return false;
        }//si plus de coup possuible quitte pour faire un égalité

        float alpha=-999999f;
        for (MinimaxNode child:nodes) {
            float eva= MinimaxPruning.minimax(child, MCTSMinimaxConstant.MINIMAX_DEPTH, alpha-1, 999999f, false);

            //si une node entraine une victoire directe alors return celle ci.
            if(GameP4.createMap(((P4Minimax)child).getKey()).checkWin()!=0){
                //0 car c'est la node suivante qui gagne
            	minimaxRolloutNodesList.clear();
            	minimaxRolloutNodesList.add((P4Minimax)child);
                return true;
            }

            if(alpha<eva) {
                alpha = eva;
                minimaxRolloutNodesList.clear();
            	minimaxRolloutNodesList.add((P4Minimax)child);
            }else if(alpha==eva){
            	minimaxRolloutNodesList.add((P4Minimax)child);
            }
        }
        return true;
    }

    public Node findPromisingNode(){

        Node bestNode = player.getNode(nextNodesKey.get(0));
        float bestScore =calculateScore(bestNode);

        for (int i = 1; i < nextNodesKey.size(); i++) {
            Node current = player.getNode(nextNodesKey.get(i));
            float s = calculateScore(current);

            if(s > bestScore){
                bestScore = s;
                bestNode = current;
            }else if(current.canWin()){
                bestScore = 99999;
                bestNode = current;
            }
        }
        return bestNode;
    }

    //sert à savoir si ce move fait gagner la partie
    private void evaluateKey(){
        GameP4 p4 = GameP4.createMap(key);
        if(p4.checkWin()!=0)canWin=true;
    }

    //calcule le score de la node qui sert à déterminer si elle doit être visitée
    public float calculateScore(Node node){
        return (float)node.getWin()/(1+node.getVisited()) + MCTSMinimaxConstant.LEARNING_RATE*(float)Math.sqrt( Math.log(this.getVisited())/(1+node.getVisited()));
    }

    //augmente le score de la node
    public void reward(int reward){
        win+=reward;
        visited++;
        if(previousNode!= null)previousNode.reward(1-reward);
    }


    public Node getChild(int[] cs){
        visited++;
        if(nextNodesKey == null){
            nextNodesKey = new ArrayList<>();
        }
        for (int i = 0; i < nextNodesKey.size(); i++) {
            if(Arrays.equals(nextNodesKey.get(i), cs)){
                return player.getNode(nextNodesKey.get(i));
            }
        }
        Node nN = new Node(cs,player,-this.turn);
        nextNodesKey.add(cs);
        player.addNode(cs,nN);
        return nN;
    }

    public Node getChildWithoutExpand(int[] cs){
        for (int i = 0; i < nextNodesKey.size(); i++) {
            if(Arrays.equals(nextNodesKey.get(i), cs)){
                return player.getNode(nextNodesKey.get(i));
            }
        }
        return null;
    }

    public int getWin() {
        return win;
    }

    public GameP4 getGame() {
        return player.getGameP4();
    }

    public int getVisited() {
        return visited;
    }

    public int[] getKey() {
        return key;
    }

    public void setKey(int[] key) {
        this.key = key;
        if(key!=null)evaluateKey();
    }

    @Override
    public String toString() {
        String l ="Node{" +
                "win=" + win +
                ", visited=" + visited+" winrate:"+(int)(100*(float)win/visited)+" key:"+Arrays.toString(key);
                if(previousNode!=null && player!=null)l+="} score:" + previousNode.calculateScore(this);
                else l+="previous null";
        return l;
    }

    public void setVisited(int visited) {
        this.visited = visited;
    }

    public boolean canWin() {
        return canWin;
    }

    public void setNextNodesKey(List<int[]> nextNodesKey) {
        this.nextNodesKey = nextNodesKey;
    }

    /*  public static Node readMCTS(String dir, MCTSMinPlayer player){
        try {
            BufferedReader mainFile =new BufferedReader(new FileReader(dir+"/MCTSFile.txt"));
            String[] firstLine = mainFile.readLine().split(" ");
            player.NODE_COMPTER = Integer.parseInt(firstLine[0]);
            int nbSubFile = Integer.parseInt(mainFile.readLine().split(" ")[0]);

            System.out.println("Begin load "+player.name);
            Node n = createTreeFromFile(dir+"/Node_0/Part_", player, Integer.parseInt(firstLine[1]));
            List<Node> beginNodes = new ArrayList<>();
            beginNodes.addAll(n.nextNodes);
            for (int i = 0; i < nbSubFile; i++) {
                String line = mainFile.readLine();
                String[] tab = line.split(" ");
                int nodeId = Integer.parseInt(tab[0]);
                int previousNode = Integer.parseInt(tab[1]);
                int numberFileInDirectory = Integer.parseInt(tab[2]);

                System.out.println("    "+player.name+" "+(int)((float)i/nbSubFile*100)+"%");
                Node child = createTreeFromFile(dir+"/Node_"+nodeId+"/Part_", player,numberFileInDirectory);
                for (int j = 0; j < beginNodes.size(); j++) {
                    if(beginNodes.get(j).getNodeId()==previousNode){
                        if(beginNodes.get(j).nextNodes==null) beginNodes.get(j).nextNodes = new ArrayList<>();
                        beginNodes.get(j).nextNodes.add(child);
                        child.setPreviousNode(beginNodes.get(j));
                    }
                }
            }
            return n;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Node createTreeFromFile(String path, MCTSMinPlayer player, int nbFile){
        try {
            //id de la première node
            int firstId =0;
            HashMap<Integer,Node>map = new HashMap<>();
            HashMap<Integer,String[]>lines = new HashMap<>();
            for (int i = 0; i < nbFile; i++) {
                BufferedReader reader =new BufferedReader(new FileReader(path+i+".txt"));
                String line = reader.readLine();

                if(i==0) firstId = Integer.parseInt(line.split(" ")[0]);
                while (line != null){
                    if(line.length() > 0) {
                        String[] tab = line.split(" ");
                        int nodeId = Integer.parseInt(tab[0]);
                        lines.put(nodeId,tab);
                        int[] key = new int[7];
                        for (int j = 0; j < 7; j++) key[j] = Integer.parseInt(lines.get(nodeId)[1 + j]);
                        int win = Integer.parseInt(lines.get(nodeId)[8]);

                        int visited = Integer.parseInt(lines.get(nodeId)[9]);
                        boolean canwin = Boolean.parseBoolean(lines.get(nodeId)[10]);

                        int turn = Integer.parseInt(lines.get(nodeId)[12]);
                        int ssNode = 0;
                        map.put(nodeId, new Node(nodeId, win, visited, canwin, key, ssNode, player));
                        map.get(nodeId).setTurn(turn);
                    }
                    line = reader.readLine();
                }
            }

            //add child node
            Iterator<Integer> iterator = lines.keySet().iterator();
            while (iterator.hasNext()){
                int nodeId = iterator.next();
                String[] tab = lines.get(nodeId);
                if(tab!=null && tab.length>13){
                    int nbChild = Integer.parseInt(tab[13]);
                    Node n = map.get(nodeId);
                    n.nextNodes = new ArrayList<>();
                    for (int i = 0; i < nbChild; i++) {
                        Node c = map.get(Integer.parseInt(tab[14 + i]));
                        if(c != null) {
                            c.setPreviousNode(n);
                            n.nextNodes.add(c);
                        }
                    }
                }
            }
            return map.get(firstId);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public int getNodeId() {
        return nodeId;
    }

    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
        turn = -previousNode.turn;
        idChild =0;
        if(previousNode!=null){
            for (int i = 0; i < 7; i++) {
                if(key[i]!=previousNode.key[i])idChild=i;
            }
        }
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public Node getPreviousNode() {
        return previousNode;
    }

    public List<int[]> getNextNodesKey() {
        return nextNodesKey;
    }

    public int getTurn() {
        return turn;
    }

    public void setWin(int win) {
        this.win = win;
    }

}
