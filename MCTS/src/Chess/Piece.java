package Chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Piece {
	
// deplacement chaque piece
	public static List<Plateau> deplacementPION(int x, int y, Plateau a){
		List<Plateau> mouvementPossible = new ArrayList<>();
		boolean b = a.estBlanc[x][y];
		int sens = 1;
		if (b) {
			sens =-1;
		}
		
		if(y+sens>7 || y+sens<0) return mouvementPossible;
		
		if(a.place[x][y+sens]==null) {
			Plateau copyPlateau = new Plateau(a);
			copyPlateau.place(Pieces.PION,b,x,y+sens);
			copyPlateau.place(null,b,x,y);
			mouvementPossible.add(copyPlateau);
		}
		for (int i = -1; i < 2; i=i+2) {
			
			if (x+i<8 && x+i>-1 && a.place[x+i][y+sens]!=null && b!=a.estBlanc[x+i][y+sens]) {
				Plateau copyPlateau = new Plateau(a);
				copyPlateau.place(Pieces.PION,b,x+i,y+sens);
				copyPlateau.place(null,b,x,y);
				mouvementPossible.add(copyPlateau);
			}
		}return mouvementPossible;
		
		
	}
	public static List<Plateau> deplacementTOUR(int x, int y, Plateau a){
		int[][]tour_dir = {{0,1},{0,-1,},{1,0},{-1,0}};
		List<Plateau> mouvementPossible = new ArrayList<>();
		boolean b = a.estBlanc[x][y];
		
		for (int i = 0; i < tour_dir.length; i++) {
			int copie_x=x+tour_dir[i][0];
			int copie_y=y+tour_dir[i][1];
			while (copie_x>-1 && copie_x<8 && copie_y>-1 && copie_y<8) {
				if (a.place[copie_x][copie_y]!=null && b==a.estBlanc[copie_x][copie_y]) {
					break;
				}
				Plateau copyPlateau = new Plateau(a);
				copyPlateau.place(Pieces.TOUR,b,copie_x,copie_y);
				copyPlateau.place(null,b,x,y);
				mouvementPossible.add(copyPlateau);
				if (a.place[copie_x][copie_y]!=null && b!=a.estBlanc[copie_x][copie_y]) {
					
					break;
				}
				copie_x+=tour_dir[i][0];
				copie_y+=tour_dir[i][1];
			}
		}return mouvementPossible;
		
		
	}
	public static List<Plateau> deplacementROI(int x, int y, Plateau a){
		List<Plateau> mouvementPossible = new ArrayList<>();
		boolean b = a.estBlanc[x][y];
		
		//Random random = new Random();
		//int sens;
		//sens = random.nextInt(2);
		//sens--;
		
		//double bouclass
		for (int i = -1; i < 2; i++) {
		for (int sens = -1; sens < 2; sens++) {
			
			if(y+sens>7 || y+sens<0 || x+i>7 || x+i<0)continue;
			if(sens ==0 && i==0)continue; //cas ou le roi ne bouge pas
			
			if(a.place[x+i][y+sens]==null || b!=a.estBlanc[x+i][y+sens]) {
				Plateau copyPlateau = new Plateau(a);
				copyPlateau.place(Pieces.ROI,b,x+i,y+sens);
				copyPlateau.place(null,b,x,y);
				mouvementPossible.add(copyPlateau);
			}
		}
		}return mouvementPossible;
	}
	
	public static List<Plateau> deplacementFOU(int x, int y, Plateau a){
		int[][]fou_dir = {{1,1},{1,-1},{-1,1},{-1,-1}};
		List<Plateau> mouvementPossible = new ArrayList<>();
		boolean b = a.estBlanc[x][y];
		
		for (int i = 0; i < fou_dir.length; i++) {
			int copie_x=x+fou_dir[i][0];
			int copie_y=y+fou_dir[i][1];
			while (copie_x>-1 && copie_x<8 && copie_y>-1 && copie_y<8) {
				if (a.place[copie_x][copie_y]!=null && b==a.estBlanc[copie_x][copie_y]) {
					break;
				}
				Plateau copyPlateau = new Plateau(a);
				copyPlateau.place(Pieces.FOU,b,copie_x,copie_y);
				copyPlateau.place(null,b,x,y);
				mouvementPossible.add(copyPlateau);
				if (a.place[copie_x][copie_y]!=null && b!=a.estBlanc[copie_x][copie_y]) {
					
					break;
				}
				copie_x+=fou_dir[i][0];
				copie_y+=fou_dir[i][1];
			}
		}return mouvementPossible;
		
		
	}
	public static List<Plateau> deplacementREINE(int x, int y, Plateau a){
		int[][]reine_dir = {{1,1},{1,-1,},{-1,1},{-1,-1},{0,1},{0,-1},{1,0},{-1,0}};
		List<Plateau> mouvementPossible = new ArrayList<>();
		boolean b = a.estBlanc[x][y];
		
		for (int i = 0; i < reine_dir.length; i++) {
			int copie_x=x+reine_dir[i][0];
			int copie_y=y+reine_dir[i][1];
			while (copie_x>-1 && copie_x<8 && copie_y>-1 && copie_y<8) {
				if (a.place[copie_x][copie_y]!=null && b==a.estBlanc[copie_x][copie_y]) {
					break;
				}
				Plateau copyPlateau = new Plateau(a);
				copyPlateau.place(Pieces.REINE,b,copie_x,copie_y);
				copyPlateau.place(null,b,x,y);
				mouvementPossible.add(copyPlateau);
				if (a.place[copie_x][copie_y]!=null && b!=a.estBlanc[copie_x][copie_y]) {
					
					break;
				}
				copie_x+=reine_dir[i][0];
				copie_y+=reine_dir[i][1];
			}
		}return mouvementPossible;
		
		
	}
	public static List<Plateau> deplacementCAV(int x, int y, Plateau a){
		int[][]cav_dir = {{2,1},{2,-1,},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};
		List<Plateau> mouvementPossible = new ArrayList<>();
		boolean b = a.estBlanc[x][y];
		
		for (int i = 0; i < cav_dir.length; i++) {
			int copie_x=x+cav_dir[i][0];
			int copie_y=y+cav_dir[i][1];
			if (copie_x>-1 && copie_x<8 && copie_y>-1 && copie_y<8) {
				
			if (a.place[copie_x][copie_y]!=null && b==a.estBlanc[copie_x][copie_y]) {
				continue;
			}
			Plateau copyPlateau = new Plateau(a);
			copyPlateau.place(Pieces.CAVALIER,b,copie_x,copie_y);
			copyPlateau.place(null,b,x,y);
			mouvementPossible.add(copyPlateau);
			}
		}return mouvementPossible;
		
		
	}
	
// position
// a qui
}
