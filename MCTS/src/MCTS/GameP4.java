package MCTS;

import java.util.Arrays;
import java.util.Scanner;

public class GameP4 {

    public int[][] map = new int[7][6];
    public int[] numbers = new int[7];
    public int turn =1;

    public void party(){
        Scanner s = new Scanner(System.in);

        while (checkWin()==0){
            display();
            int id = s.nextInt();
            play(id);

            int[][] states = getAllState();

            for (int i = 0; i <states.length ; i++) {
                for (int j = 0; j < 7; j++) {
                    System.out.println(states[i][j]);
                }
                System.out.println();
            }

        }

        System.out.println("WIN:"+checkWin());
    }

    public int randomPlay(int depth) {
        if (depth >= 42)
            return -1;

        for (int i = 0; i < 7; i++) {
            int wId = -1;
            for (int j = 0; j < 6; j++) {
                if (map[i][j] == 0) {
                    map[i][j] = turn;
                    wId = j;
                    break;
                }
            }
            if (checkWin() != 0) {
                return 1;
            }
            if(wId!=-1)map[i][wId] = 0;

        }

        //move aleatoire
        int i = Node.RDM.nextInt(7);
        int c =0;
        while (numbers[i]>=6 && c<20 ){
            i = Node.RDM.nextInt(7);
            c++;
        }
        if(c==20)return -1;

        map[i][numbers[i]] = turn;
        numbers[i]++;

        turn*=-1;

        int result = randomPlay(depth + 1);
        if(result==-1)return -1;
        return 1- result;
    }

    public int[] hashMap(){
        int[] hash = new int[7];
        for (int i = 0; i < 7; i++) {
            int nb = 0;
            int exp =1;
            for (int j = 0; j < 6; j++) {
                nb += (1+map[i][j])*exp;
                exp*=10;
            }
            hash[i] = nb;
        }
        return hash;
    }

    public static GameP4 createMap(int[] mapKey){
        int[][] map = new int[7][6];

        for (int i = 0; i < 7; i++) {
            int column= mapKey[i];

            for (int j = 0; j < 6; j++) {
                map[i][j] = column%10 - 1;
                column/=10;
            }
        }
        GameP4 g = new GameP4();
        g.setMap(map);
        int[] number =new int[7];
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                if(map[i][j]!=0)number[i]++;
            }
        }
        g.setNumbers(number);
        return g;
    }

    public void setNumbers(int[] numbers) {
        this.numbers = numbers;
    }

    public int[][] getAllState(){
        int nbState = 0;
        for (int i = 0; i < 7; i++) {
            if(numbers[i] <6){
                nbState++;
            }
        }
        int[][] states = new int[nbState][7];
        int[] currentState = hashMap();

        for (int i = 0; i < 7; i++) {
            if(numbers[i] <6){
                nbState--;
                states[nbState] = Arrays.copyOf(currentState,7);
                states[nbState][i] = currentState[i] + (turn)* (int)Math.pow(10,numbers[i]);
                //System.out.println(Arrays.toString(currentState)+" "+i+"   "+Arrays.toString(states[nbState])+" "+(turn+1)/2* (int)Math.pow(10,numbers[i])+" "+numbers[i]);
            }
        }
        return states;
    }

    public void play(int id){
        for (int i = 0; i < 6; i++) {
            if(map[id][i]==0){
                map[id][i]=turn;
                turn*=-1;
                numbers[id]++;
                return;
            }
        }
        turn*=-1;
    }

    public void nextPlay(int[] id){
        int[] state = hashMap();
        for (int i = 0; i < 7; i++) {
            if(id[i] != state[i]){
                play(i);
                return;
            }
        }
    }

    public void display(){
        System.out.println("0 1 2 3 4 5 6");
        for (int i = 0; i < 6; i++) {
            String line ="";
            for (int j = 0; j < 7; j++) {
                int id = map[j][5-i];
                line += (id==0? ' ' : id==1?'X':'O')+" ";
            }
            System.out.println(line);
        }
    }

    public int checkWin(){
        int[]mx = {0,1,1,1};
        int[]my = {1,0,1,-1};
        for (int i = 0; i < 4; i++) {

            for (int j = 0; j <= Math.min(6, 7-mx[i]*4); j++) {
                for (int k = Math.max(0, -my[i]*3); k <= Math.min(5, 6- Math.max(0,my[i]*4)); k++) {
                    int id = map[j][k];
                    if(id!=0) {
                        boolean win = true;
                        for (int l = 1; l <= 3; l++) {
                            if (map[j + mx[i] * l][k + my[i] * l] != id) {
                                win = false;
                                break;
                            }
                        }
                        if (win) return id;
                    }
                }
            }
        }
        return 0;
    }

    public int getTurn() {
        return turn;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }

    public int[] getNumbers() {
        return numbers;
    }
}
