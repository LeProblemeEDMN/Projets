package Chess;

import Chess.EchecBouton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.Soundbank;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;




public class FenetreEchec {
	public int bx=-1;
	public int by=-1;
	public FenetreEchec() {

		JFrame frame = new JFrame();
		frame.setTitle("Le jeu de la Dame");
		frame.setSize(new Dimension(1000,720));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(8,8));
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j <8; j++) {

				bouton[i][j]= new EchecBouton(i,j);
				bouton[i][j].addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						EchecBouton bouton = (EchecBouton)e.getSource();
						bx=bouton.getX();
						by=bouton.getY();
					}
				});
				frame.add(bouton[i][j]);
			}
		}frame.setVisible(true);
		
	}
	public JButton[][] bouton = new JButton[8][8];
	
	public void affiche (Plateau a) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j <8; j++) {
				if (a.place[i][j]==null) {
					bouton[i][j].setText("");
					bouton[i][j].setBackground(Color.GRAY);
				}else {
					bouton[i][j].setText(""+a.place[i][j]);
					if (a.estBlanc[i][j]==true) {
						bouton[i][j].setBackground(Color.WHITE);
						
					}else bouton[i][j].setBackground(Color.BLACK);
					
				}
				
				
			}
		}
	}
	
}
