package Connect4;

import MCTSGame.GameState;
import MCTSGame.MCTSNode;
import MCTSGame.MCTSPlayer;
import MCTSMinimax.MCTSMinimaxConstant;
import MCTSMinimax.Node;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;

public class Connect4Player extends MCTSPlayer {

    public Connect4Player() {
        super(new Connect4.GameP4().hashMap());

    }
    public GameState createMap(int[] key){
        return GameP4.createMap(key);
    }
    private static int chunkSize = 200000;
    private static BufferedWriter nodeWriter;
    public Connect4Player(String dir) {
        super(new Connect4.GameP4().hashMap());
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(dir+"/MCTSFile.txt"));
            fileReader.readLine();//compteur de node
            int numberFile = Integer.parseInt(fileReader.readLine());
            int[] firstKey = stringToKey(fileReader.readLine());
            String prefix=dir+"/Nodes/Part_";
            for (int i = 0; i < numberFile; i++) {
                System.out.println("read file "+i+" /"+numberFile);
                BufferedReader nodeReader= new BufferedReader(new FileReader(prefix+i+".txt"));
                String line = nodeReader.readLine();
                while (line!=null){
                    String[] parts = line.split(" ");

                    int nodeId = Integer.parseInt(parts[0]);
                    int win = Integer.parseInt(parts[1]);
                    int visit = Integer.parseInt(parts[2]);
                    boolean canWin = Boolean.parseBoolean(parts[3]);
                    int turn = Integer.parseInt(parts[4]);
                    int[] key = stringToKey(parts[5]);
                    if(key==null){
                        line = nodeReader.readLine();
                        continue;//si key null erreur de sauvegarde skip cette node
                    }
                   /* int numberChild = Integer.parseInt(parts[6]);
                    List<int[]> childsKey = new ArrayList<>();
                    for (int j = 0; j < numberChild; j++) {
                        childsKey.add(stringToKey(parts[7+j]));
                    }*/

                    //fill la node
                    MCTSNode node = new MCTSNode(nodeId, win ,visit, canWin,key,this);
                    //node.setNextNodesKey(childsKey);

                    node.setTurn(turn);

                    addNode(key, node);

                    line = nodeReader.readLine();
                }
            }
            firstNode = getNode(firstKey);
            if(firstNode==null){
                firstNode = new MCTSNode(firstKey,this,1);
                addNode(firstKey,firstNode);
                firstNode.setVisited(300000);
                firstNode.setWin(150000);
            }
            System.out.println(Arrays.toString(firstKey));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //converti une cl? chaine de caract?re en cl? tableau
    public int[] stringToKey(String part){
        String[] parts = part.split("/");
        if(parts.length!=7)return null;
        int[] i = new int[7];
        for (int j = 0; j < 7; j++) {
            i[j] = Integer.parseInt(parts[j]);
        }
        return i;
    }


    public void saveMCTS(String dir){
        try {
            BufferedWriter mainFile = new BufferedWriter(new FileWriter(dir+"/MCTSFile.txt"));
            File file_node_0 = new File(dir+"/Nodes/");
            if(!file_node_0.exists()) Files.createDirectories(Paths.get(file_node_0.getPath()));
            String prefix=dir+"/Nodes/Part_";
            nodeWriter = new BufferedWriter(new FileWriter(prefix+"0.txt"));

            int nodeRegister=0;
            for (String key: map.keySet()) {
                //divise en plusieurs fichier pr faciliter al sauvegarde et le chargement des donn?es
                if(nodeRegister%chunkSize==0){
                    int partId= nodeRegister/chunkSize;
                    nodeWriter.close();
                    nodeWriter = new BufferedWriter(new FileWriter(prefix+partId+".txt"));
                }

                MCTSNode node = map.get(key);
                if(node.getVisited()< MCTSMinimaxConstant.MIN_REMOVE_LIMIT)continue;

                nodeWriter.write(0+" "+node.getWin()+" "+node.getVisited()+" "+node.canWin()+" "+node.getTurn()+" ");
                String keyToString=""+node.getKey()[0];
                for (int i = 1; i < 7; i++) keyToString+="/"+node.getKey()[i];
                nodeWriter.write(keyToString+" ");
                /*if(node.nextNodesKey==null) nodeWriter.write("0");
                else {
                    nodeWriter.write(node.nextNodesKey.size()+" ");
                    for (int i = 0; i < node.nextNodesKey.size(); i++) {
                        int[] kChild = node.nextNodesKey.get(i);
                        keyToString=""+kChild[0];
                        for (int j = 1; j < 7; j++) keyToString+="/"+kChild[j];
                        nodeWriter.write(keyToString+" ");
                    }
                }*/
                nodeWriter.newLine();
                nodeRegister++;
            }
            nodeWriter.close();
            System.out.println("Register nodes:"+nodeRegister);

            mainFile.newLine();
            mainFile.write(Integer.toString(nodeRegister%chunkSize==0?nodeRegister/chunkSize : nodeRegister/chunkSize+1));
            mainFile.newLine();
            String keyToString=""+firstNode.getKey()[0];
            for (int i = 1; i < 7; i++) keyToString+="/"+firstNode.getKey()[i];
            mainFile.write(keyToString);
            mainFile.newLine();
            mainFile.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
