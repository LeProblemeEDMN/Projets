package Chess;

import Connect4.GameP4;
import MCTSGame.GameState;
import MCTSGame.MCTSNode;
import MCTSGame.MCTSPlayer;
import MCTSMinimax.MCTSMinimaxConstant;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class ChessPlayer extends MCTSPlayer {
    public ChessPlayer() {
        super(new Plateau().hashMap());

    }
    public GameState createMap(int[] key){
        Plateau plateau = new Plateau();
        plateau.reset();

        for (int i = 0; i < 8; i++) {
            int column= key[i];

            for (int j = 0; j < 8; j++) {
                int v = column%13;
                column/=13;
                if(v<12){
                    boolean c = v<6;//couleur
                    v=v%6;
                    if(v==0)plateau.place(Pieces.PION,c,i,j);
                    else if(v==1)plateau.place(Pieces.FOU,c,i,j);
                    else if(v==2)plateau.place(Pieces.CAVALIER,c,i,j);
                    else if(v==3)plateau.place(Pieces.TOUR,c,i,j);
                    else if(v==4)plateau.place(Pieces.REINE,c,i,j);
                    else if(v==5)plateau.place(Pieces.ROI,c,i,j);
                }
            }
        }
        return plateau;
    }
    private static int chunkSize = 200000;
    private static BufferedWriter nodeWriter;
    public ChessPlayer(String dir) {
        super(new Plateau().hashMap());
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(dir+"/ChessFile.txt"));

            fileReader.readLine();
            int numberFile = Integer.parseInt(fileReader.readLine());
            String line_key =fileReader.readLine();
            System.out.println(line_key);
            int[] firstKey = stringToKey(line_key);
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

                    //fill la node
                    MCTSNode node = new MCTSNode(nodeId, win ,visit, canWin,key,this);
                    //node.setNextNodesKey(childsKey);

                    node.setTurn(turn);

                    addNode(key, node);

                    line = nodeReader.readLine();
                }
            }
            System.out.println(Arrays.toString(new Plateau().hashMap()));
            firstNode = getNode(firstKey);
            if(firstNode==null){
                System.out.println("ADD A NEW FIRST NODE");
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
        if(parts.length!=8)return null;
        int[] i = new int[8];
        for (int j = 0; j < 8; j++) {
            int n=1;
            for (int k = 0; k < 8; k++) {
                i[j] +=((int)parts[j].charAt(k)-65)*n;
                n*=13;
            }
        }
        return i;
    }


    public void saveMCTS(String dir){
        try {
            BufferedWriter mainFile = new BufferedWriter(new FileWriter(dir+"/ChessFile.txt"));
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
                String keyToString=""+transformToString(node.getKey()[0]);
                for (int i = 1; i < node.getKey().length; i++){
                    keyToString+="/"+transformToString(node.getKey()[i]);

                }
                nodeWriter.write(keyToString+" ");
                nodeWriter.newLine();
                nodeRegister++;
            }
            nodeWriter.close();
            System.out.println("Register nodes:"+nodeRegister);
            System.out.println(firstNode);

            mainFile.newLine();
            mainFile.write(Integer.toString(nodeRegister%chunkSize==0?nodeRegister/chunkSize : nodeRegister/chunkSize+1));
            mainFile.newLine();
            String keyToString=""+transformToString(firstNode.getKey()[0]);
            for (int i = 1; i < 8; i++) keyToString+="/"+transformToString(firstNode.getKey()[i]);
            mainFile.write(keyToString);
            mainFile.newLine();
            mainFile.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String transformToString(int n){
        String keyToString="";
        for (int j = 0; j < 8; j++) {
            keyToString+=(char)(65+n%13);
            n/=13;
        }
        return keyToString;
    }
}
