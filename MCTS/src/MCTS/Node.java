package MCTS;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Node {
    public static boolean USE_NETWORK =false;

    protected List<Node> nextNodes;
    protected Node previousNode;

    private int win =0;
    private int visited = 0;
    private int[] key;
    private MCTSPlayer player;
    private int nodeId;

    private boolean canWin = false;

    public int sousnode = 0;
    private int turn = 1;

    private int idChild;

    public Node(Node previousNode, int[] key, MCTSPlayer player) {
        this.previousNode = previousNode;
        this.key = key;
        this.player = player;
        if(key!=null)evaluateKey();
        this.nodeId=player.NODE_COMPTER;
        player.NODE_COMPTER++;

        if(previousNode==null)turn = 1;
        else turn = -previousNode.getTurn();


        idChild =0;
        if(previousNode!=null){
            for (int i = 0; i < 7; i++) {
                if(key[i]!=previousNode.key[i])idChild=i;
            }
        }
    }

    public Node(int nodeId, int win, int visited, boolean canWin, int[] key,int sousnode, MCTSPlayer player) {
        this.key = key;
        this.player = player;
        this.nodeId=nodeId;
        this.visited = visited;
        this.win = win;
        this.canWin = canWin;
        this.sousnode = sousnode;
    }

    public void expand(int turn){
        GameP4 p4 = GameP4.createMap(key);
        p4.turn = turn;
        int[][] states = p4.getAllState();
        nextNodes = new ArrayList<>();
        for (int i = 0; i < states.length; i++) {
            Node nN = new Node(this, states[i],player);
            nextNodes.add(nN);
            if(nN.canWin()){
                nextNodes.clear();
                nextNodes.add(nN);
                break;
            }
        }
            addSN(nextNodes.size());
    }
    public static int nbSim = 40;
    public static Random RDM = new Random();
    public Node play(){
        if (this.nextNodes==null || this.nextNodes.size() == 0)this.expand(player.getGameP4().turn);
        for (int i = 0; i < nbSim; i++) {
            Node promisingNode = findPromisingNode();
            if (promisingNode.nextNodes==null || promisingNode.nextNodes.size() == 0)promisingNode.expand(-player.getGameP4().turn);
            Node nodeToExplore = promisingNode;
            if (promisingNode.nextNodes.size() > 0) {
                nodeToExplore = promisingNode.nextNodes.get(RDM.nextInt(promisingNode.nextNodes.size()));
            }
            int playoutResult = simulateRandomPlayout(nodeToExplore);
            nodeToExplore.reward(playoutResult);
        }
        Node bestNode = findPromisingNode();
        return bestNode;
    }

    private int simulateRandomPlayout(Node node) {
        GameP4 p4 = GameP4.createMap(node.key);
        p4.turn = -1;
        if(p4.checkWin()!=0)return 1;
        int result =-1;
        if(!USE_NETWORK) result = p4.randomPlay(0);
        if(result==0)return 1;
        //retourne 1 si autre gagne -1 si aucun gagne (donc tt deux perdent)
        return 0;
    }
    public Node findPromisingNode(){

        Node bestNode = nextNodes.get(0);
        float bestScore =calculateScore(bestNode);

        for (int i = 1; i < nextNodes.size(); i++) {
            float s = calculateScore(nextNodes.get(i));

            if(s > bestScore){
                bestScore = s;
                bestNode = nextNodes.get(i);
            }else if(nextNodes.get(i).canWin()){
                bestScore = 99999;
                bestNode = nextNodes.get(i);
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
        return (float)node.getWin()/(1+node.getVisited()) + player.learningRate*(float)Math.sqrt( Math.pow(this.getVisited(),0.5f)/(1+node.getVisited()));
    }

    //augmente le score de la node
    public void reward(int reward){
        win+=reward;
        visited++;
        if(previousNode!= null)previousNode.reward(1-reward);
    }

    public void addSN(int nb){
        sousnode += nb;
        if(previousNode != null)previousNode.addSN(nb);
    }

    public Node getChild(int[] cs){
        visited++;
        if(nextNodes == null){
            nextNodes = new ArrayList<>();
        }
        for (int i = 0; i < nextNodes.size(); i++) {
            if(Arrays.equals(nextNodes.get(i).getKey(), cs)){
                return nextNodes.get(i);
            }
        }
        Node nN = new Node(this,cs,player);
        nextNodes.add(nN);
        return nN;
    }

    public Node getChildWithoutExpand(int[] cs){
        for (int i = 0; i < nextNodes.size(); i++) {
            if(Arrays.equals(nextNodes.get(i).getKey(), cs)){
                return nextNodes.get(i);
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
                ", visited=" + visited+" winrate:"+(float)win/visited+ "key:"+Arrays.toString(key);
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

    private static int chunkSize = 200000;
    private static BufferedWriter nodeWriter;
    public int saveTree(String prefix,int recurLevel,int nodeCompteur) throws IOException {
        nodeCompteur++;
        BufferedWriter writer = nodeWriter;

        if(nodeCompteur%chunkSize==0){
            int partId= nodeCompteur/chunkSize;
            writer.close();
            nodeWriter = new BufferedWriter(new FileWriter(prefix+partId+".txt"));
            writer = nodeWriter;
        }
        writer.write(Integer.toString(nodeId)+" ");
        for (int i = 0; i < 7; i++) writer.write(Integer.toString(key[i])+" ");
        writer.write(Integer.toString(win)+" "+Integer.toString(visited)+" "+canWin+" "+Integer.toString(sousnode)+" "+Integer.toString(turn)+" ");
        if(nextNodes!=null) {
            writer.write(Integer.toString(nextNodes.size()));

            for (int i = 0; i < nextNodes.size(); i++){
                writer.write(" "+Integer.toString(nextNodes.get(i).getNodeId()));
            }
            if(recurLevel>1) {
                for (int i = 0; i < nextNodes.size(); i++) {
                    nodeWriter.newLine();
                    nodeCompteur= nextNodes.get(i).saveTree(prefix, recurLevel - 1,nodeCompteur);
                }
            }else writer.newLine();
        }else writer.newLine();

        return nodeCompteur;
    }

    public static void saveTree(String dir,Node node, int NODE_COMPTER){
        try {
            BufferedWriter mainFile = new BufferedWriter(new FileWriter(dir+"/MCTSFile.txt"));
            File file_node_0 = new File(dir+"/Node_0/");
            if(!file_node_0.exists())Files.createDirectories(Paths.get(file_node_0.getPath()));

            nodeWriter = new BufferedWriter(new FileWriter(dir+"/Node_0/Part_0.txt"));
            int parts = node.saveTree(dir+"/Node_0/Part_",2,0);
            nodeWriter.close();
            mainFile.write(Integer.toString(NODE_COMPTER)+" "+Integer.toString(parts/chunkSize+parts%chunkSize==0?0:1));
            mainFile.newLine();
            List<Node> beginNodes = new ArrayList<>();
            for (int i = 0; i < node.nextNodes.size(); i++) {
                if(node.nextNodes.get(i).nextNodes!=null)beginNodes.addAll(node.nextNodes.get(i).nextNodes);
            }
            mainFile.write(Integer.toString(beginNodes.size()));
            mainFile.newLine();
            for (int i = 0; i < beginNodes.size(); i++) {
                //crée dossier
                File file_node = new File(dir+"/Node_"+beginNodes.get(i).getNodeId()+"/");
                if(!file_node.exists())Files.createDirectories(Paths.get(file_node.getPath()));


                nodeWriter = new BufferedWriter(new FileWriter(dir+"/Node_"+Integer.toString(beginNodes.get(i).getNodeId())+"/Part_0.txt"));
                int partsSSnode = beginNodes.get(i).saveTree(dir+"/Node_"+beginNodes.get(i).getNodeId()+"/Part_",999,0);
                nodeWriter.close();
                mainFile.write(Integer.toString(beginNodes.get(i).getNodeId())+" "+Integer.toString(beginNodes.get(i).previousNode.getNodeId())+" "+Integer.toString(partsSSnode/chunkSize+partsSSnode%chunkSize==0?0:1));
                mainFile.newLine();

            }


            mainFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Node readMCTS(String dir, MCTSPlayer player){
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

    public static Node createTreeFromFile(String path,MCTSPlayer player, int nbFile){
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
    }

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

    public List<Node> getNextNodes() {
        return nextNodes;
    }

    public int getTurn() {
        return turn;
    }
}
