package MCTSGame;

import MCTS.GameP4;
import MCTSMinimax.*;
import Minimax.MinimaxNode;
import Minimax.MinimaxPruning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MCTSNode {
    public static Random RDM = new Random();

    protected List<int[]> nextNodesKey;
    protected MCTSNode previousNode;

    private int win =0;
    private int visited = 0;
    private int[] key;
    private MCTSPlayer player;

    private boolean canWin = false;
    private int turn = 1;
    protected List<GameMinimax> minimaxRolloutNodesList = new ArrayList<>();

    public MCTSNode(int[] key, MCTSPlayer player, int turn) {
        this.key = key;
        this.player = player;
        if(key!=null)evaluateKey();

        this.turn = turn;

    }

    public MCTSNode(int nodeId, int win, int visited, boolean canWin, int[] key, MCTSPlayer player) {
        this.key = key;
        this.player = player;
        this.visited = visited;
        this.win = win;
        this.canWin = canWin;
    }

    //ajoute des nouvelles étapes
    public void expand(){
        GameState p4 = player.createMap(key);
        p4.turn = this.turn;
        int[][] states = p4.getAllState();
        nextNodesKey = new ArrayList<>();
        for (int i = 0; i < states.length; i++) {
            MCTSNode nN = player.getNode(states[i]);
            if(nN==null){
                nN = new MCTSNode(states[i],player,-this.turn);
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

    public MCTSNode play() {
        if (this.nextNodesKey == null || this.nextNodesKey.size() == 0) this.expand();

        if (this.visited<100000 || !MCTSMinimaxConstant.AVOID_TRAINING){
            int nbTest = 0;
            while (nbTest < MCTSMinimaxConstant.SIMULATIONS_PER_MOVE) {
                MCTSNode promisingNode = findPromisingNode();
                if (promisingNode.nextNodesKey == null || promisingNode.nextNodesKey.size() == 0)
                    promisingNode.expand();

                MCTSNode nodeToExplore = promisingNode;//node du tour de l'adversaire
                nodeToExplore.setPreviousNode(this);
                simulateRandomPlayout(nodeToExplore);
                nbTest++;
            }

        }
        //RECHERCHE MEILLEURE
        if(MCTSMinimaxConstant.bestMove)MCTSMinimaxConstant.LEARNING_RATE/=2;
        MCTSNode bestNode = findPromisingNode();
        if(MCTSMinimaxConstant.bestMove)MCTSMinimaxConstant.LEARNING_RATE*=2;

        //SI MEILLEURE EST NULLE CHERCHE AVEC MINIMAX
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
            MCTSNode NN = player.getNode( bestNode.getKey());
            bestNode = NN;

        }

        return bestNode;
    }
    //fait une partie aleatoire avec le mcts en guide
    private int simulateRandomPlayout(MCTSNode node) {
        MCTSNode finalNode = node;

        int currentTurn = node.turn;

        GameP4 p4 = GameP4.createMap(node.key);
        p4.turn = -node.turn;

        int[] currentMap = node.key;
        int nb=0;
        //partie
        while (p4.checkWin()==0 && nb<80){
            if(finalNode.minimaxRolloutNodesList.size()==0) {
                boolean canContinue = finalNode.fillRolloutNodes();
                if(!canContinue)return 0;
            }

            int a=RDM.nextInt(finalNode.minimaxRolloutNodesList.size());
            currentMap = finalNode.minimaxRolloutNodesList.get(a).getKey();

            currentTurn= -currentTurn;
            p4 = GameP4.createMap(currentMap);

            //ajoute Node
            MCTSNode NN = player.getNode(currentMap);
            if(NN==null){
                NN = new MCTSNode(currentMap,player,currentTurn);
                if(nb<3)player.addNode(currentMap, NN);
            }
            NN.setPreviousNode(finalNode);
            finalNode = NN;
            nb++;
        }

        if(nb>=79){
            if(RDM.nextFloat()>0.0){
                finalNode.reward(0);
                finalNode.reward(1);
            }
            return 0;
        }
        finalNode.reward(1);

        return p4.checkWin();
    }

    //cherche les nodes qui ne sont pas un dead end avec le minimax
    public boolean fillRolloutNodes() {
        //cherche avec le minimax pour eviter les coup bêtes.
        GameMinimax minimaxNode = new GameMinimax(key,0,turn,player);

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
            if(player.createMap(((GameMinimax)child).getKey()).checkWin()!=0){
                //0 car c'est la node suivante qui gagne
                minimaxRolloutNodesList.clear();
                minimaxRolloutNodesList.add((GameMinimax) child);
                return true;
            }

            if(alpha<eva) {
                alpha = eva;
                minimaxRolloutNodesList.clear();
                minimaxRolloutNodesList.add((GameMinimax)child);
            }else if(alpha==eva){
                minimaxRolloutNodesList.add((GameMinimax)child);
            }
        }
        return true;
    }

    //cherche la meilleure node avec les donnée dispo
    public MCTSNode findPromisingNode(){
        //System.out.println(this.nextNodesKey.size());
        MCTSNode bestNode = player.getNode(nextNodesKey.get(0));
        float bestScore = calculateScore(bestNode);

        for (int i = 1; i < nextNodesKey.size(); i++) {
            MCTSNode current = player.getNode(nextNodesKey.get(i));
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
        GameState state = player.createMap(key);
        if(state.checkWin()!=0)canWin=true;
    }

    //calcule le score de la node qui sert à déterminer si elle doit être visitée
    public float calculateScore(MCTSNode node){
        return (float)node.getWin()/(1+node.getVisited()) + MCTSMinimaxConstant.LEARNING_RATE*(float)Math.sqrt( Math.log(this.getVisited())/(1+node.getVisited()));
    }
    public void reward(int reward){
        //reward(reward,true);
        win+=reward;
        visited++;
        reward = 1-reward;
        MCTSNode node = this.previousNode;
        int i=0;
        while (node!=null && i<750){
            node.win+=reward;
            node.visited++;
            reward = 1-reward;
            if(node==node.previousNode)break;
            node = node.previousNode;

            i++;
        }

    }
    //compteur pr eviter le stack overflow
    private static int rewardLim = 100;
    //augmente le score de la node
    public void reward(int reward,boolean first){
        //avoid stack overflow
        if(first)rewardLim=80;
        win+=reward;
        visited++;
        rewardLim--;

        if(previousNode!= null && rewardLim>0)previousNode.reward(1-reward,false);
    }


    public MCTSNode getChild(int[] cs){
        visited++;
        if(nextNodesKey == null){
            nextNodesKey = new ArrayList<>();
        }
        for (int i = 0; i < nextNodesKey.size(); i++) {
            if(Arrays.equals(nextNodesKey.get(i), cs)){
                return player.getNode(nextNodesKey.get(i));
            }
        }
        MCTSNode nN = new MCTSNode(cs,player,-this.turn);
        nextNodesKey.add(cs);
        player.addNode(cs,nN);
        return nN;
    }

    public MCTSNode getChildWithoutExpand(int[] cs){
        for (int i = 0; i < nextNodesKey.size(); i++) {
            if(Arrays.equals(nextNodesKey.get(i), cs)){
                return player.getNode(nextNodesKey.get(i));
            }
        }
        return null;
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

    public boolean canWin() {
        return canWin;
    }


    public int getTurn() {
        return turn;
    }

    public List<int[]> getNextNodesKey() {
        return nextNodesKey;
    }

    public void setNextNodesKey(List<int[]> nextNodesKey) {
        this.nextNodesKey = nextNodesKey;
    }

    public MCTSNode getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(MCTSNode previousNode) {
        this.previousNode = previousNode;
    }

    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    public int getVisited() {
        return visited;
    }

    public void setVisited(int visited) {
        this.visited = visited;
    }

    public int[] getKey() {
        return key;
    }

    public void setKey(int[] key) {
        this.key = key;
    }

    public MCTSPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MCTSPlayer player) {
        this.player = player;
    }

    public boolean isCanWin() {
        return canWin;
    }

    public void setCanWin(boolean canWin) {
        this.canWin = canWin;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<GameMinimax> getMinimaxRolloutNodesList() {
        return minimaxRolloutNodesList;
    }

    public void setMinimaxRolloutNodesList(List<GameMinimax> minimaxRolloutNodesList) {
        this.minimaxRolloutNodesList = minimaxRolloutNodesList;
    }
}
