package MCTSMinimax;

import MCTS.GameP4;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class P4Frame extends Frame {
    Button[][] buttons=new Button[7][6];
    public int selected = -1;
    public boolean choice =false;
    public P4Frame() {
        super();
        setTitle("Connect-4");
        setSize(300, 150);
        setLayout(new GridLayout(7, 7));
        for (int i = 0; i < 7; i++) {
            Button button = new Button(""+i);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selected = Integer.parseInt(e.getActionCommand());//action cmd renvoie nom bouton
                    choice =true;
                }
            });
            add(button);
        }
        for (int j = 0; j < 6; j++) {
            for (int i = 0; i < 7; i++) {
                Button button = new Button(" ");
                button.setBackground(Color.WHITE);
                buttons[i][5-j] = button;
                add(button);
            }
        }

        pack();
        show(); // affiche la fenetre
    }
    public void setColor(GameP4 game){
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                int v = game.map[i][j];
                if(v==0)
                    buttons[i][j].setBackground(Color.white);
                else if(v==1)
                    buttons[i][j].setBackground(Color.RED);
                else if(v==-1)
                    buttons[i][j].setBackground(Color.YELLOW);
            }
        }
    }
    public void setColor(Connect4.GameP4 game){
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                int v = game.map[i][j];
                if(v==0)
                    buttons[i][j].setBackground(Color.white);
                else if(v==1)
                    buttons[i][j].setBackground(Color.RED);
                else if(v==-1)
                    buttons[i][j].setBackground(Color.YELLOW);
            }
        }
    }
}
