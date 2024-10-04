package MCTSGame;

public abstract class GameState {
    public int turn=1;


    public abstract int[][]getAllState();

    public abstract int checkWin();

    public abstract int[]hashMap();
}
