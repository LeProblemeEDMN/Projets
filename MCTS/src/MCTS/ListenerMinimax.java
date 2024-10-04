package MCTS;

import java.util.Scanner;

public class ListenerMinimax {
    public static boolean PLAYGAME=false;
    private static MinimaxPlayer p1 = new MinimaxPlayer();

    public static void train(){


        //play vs human
        while (true) {
            Scanner s = new Scanner(System.in);
            GameP4 game = new GameP4();
            p1.setupGame(game);

            Node.nbSim = 200;
            while (game.checkWin() == 0) {
                if (game.turn == 1){
                    int[] map = p1.turn(true);
                    game = GameP4.createMap(map);
                    game.turn = -1;
                }
                else if (game.turn == -1) {
                    game.display();
                    int id = s.nextInt();
                    game.play(id);
                    p1.setNextStep(game.hashMap());

                }
            }
            game.display();
            System.out.println("WIN:" + game.checkWin());
        }
    }
}
