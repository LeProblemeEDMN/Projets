package Chess;

import MCTSGame.GameState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Plateau extends GameState{
	
	// quadrillage espace
	public Pieces[][] place = new Pieces[8][8];
	public boolean[][]estBlanc = new boolean[8][8];
	
	
	public Plateau (Plateau a) {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				this.place(a.place[i][j],a.estBlanc[i][j],i,j);
			}
		}
	}


	public Plateau () {
		for (int i = 0; i < 8; i++) {
			this.place(Pieces.PION,true,i,6);
			this.place(Pieces.PION,false,i,1);
		}
		this.place(Pieces.TOUR,true,0,7);
		this.place(Pieces.TOUR,true,7,7);
		this.place(Pieces.CAVALIER,true,1,7);
		this.place(Pieces.CAVALIER,true,6,7);
		this.place(Pieces.FOU,true,2,7);
		this.place(Pieces.FOU,true,5,7);
		this.place(Pieces.REINE,true,4,7);
		this.place(Pieces.ROI,true,3,7);
		
		this.place(Pieces.TOUR,false,0,0);
		this.place(Pieces.TOUR,false,7,0);
		this.place(Pieces.CAVALIER,false,1,0);
		this.place(Pieces.CAVALIER,false,6,0);
		this.place(Pieces.FOU,false,2,0);
		this.place(Pieces.FOU,false,5,0);
		this.place(Pieces.REINE,false,3,0);
		this.place(Pieces.ROI,false,4,0);
	}
	public void reset() {
		place = new Pieces[8][8];
		estBlanc = new boolean[8][8];
	}
	public void place (Pieces p, boolean b, int x,int y) {
		place[x][y]=p;
		estBlanc[x][y]=b;
	}
	
	// deplacement possibles
	
	public List<Plateau> deplacementTotal(boolean b){
		List<Plateau> mouvementTotal = new ArrayList<>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if(this.place[i][j]!=null && b==this.estBlanc[i][j]) {
					if (this.place[i][j]==Pieces.PION) {
						mouvementTotal.addAll(Piece.deplacementPION(i, j, this));
					}
					else if (this.place[i][j]==Pieces.ROI) {
						mouvementTotal.addAll(Piece.deplacementROI(i, j, this));
					}
					else if (this.place[i][j]==Pieces.TOUR) {
						mouvementTotal.addAll(Piece.deplacementTOUR(i, j, this));
					}
					else if (this.place[i][j]==Pieces.FOU) {
						mouvementTotal.addAll(Piece.deplacementFOU(i, j, this));
					}
					else if (this.place[i][j]==Pieces.REINE) {
						mouvementTotal.addAll(Piece.deplacementREINE(i, j, this));
					}
					else if (this.place[i][j]==Pieces.CAVALIER) {
						mouvementTotal.addAll(Piece.deplacementCAV(i, j, this));
					}
				}
			}
		}return mouvementTotal;
	}

	// nb piece/joueur
	// victoire
	// echec
	public boolean echec2 (boolean b) {
		int x =-1;
		int y=-1;
		//cherche le roi
		for (int i= 0;  i< estBlanc.length; i++) {
			for (int j = 0; j < estBlanc.length; j++) {
				if(this.estBlanc[i][j]!=b && this.place[i][j]==Pieces.ROI) {
					x=i;
					y=j;
					break;
				}
				if(x!=-1 && y!=-1)break;
			}
		}
		//tour
		int[][]tour_dir = {{0,1},{0,-1,},{1,0},{-1,0}};
		for (int i = 0; i < tour_dir.length; i++) {
			int copie_x=x+tour_dir[i][0];
			int copie_y=y+tour_dir[i][1];
			while (copie_x>-1 && copie_x<8 && copie_y>-1 && copie_y<8) {
				if (this.place[copie_x][copie_y]!=null) {
					if(this.estBlanc[copie_x][copie_y]==b && (this.place[copie_x][copie_y]==Pieces.TOUR || this.place[copie_x][copie_y]==Pieces.REINE))
						return true;
					break;
				}
				copie_x+=tour_dir[i][0];
				copie_y+=tour_dir[i][1];
			}
		}
		//fou
		int[][]fou_dir = {{1,1},{1,-1,},{-1,1},{-1,-1}};
		for (int i = 0; i < fou_dir.length; i++) {
			int copie_x=x+fou_dir[i][0];
			int copie_y=y+fou_dir[i][1];
			while (copie_x>-1 && copie_x<8 && copie_y>-1 && copie_y<8) {
				if (this.place[copie_x][copie_y]!=null) {
					if(this.estBlanc[copie_x][copie_y]==b && (this.place[copie_x][copie_y]==Pieces.FOU || this.place[copie_x][copie_y]==Pieces.REINE))
						return true;
					break;
				}
				copie_x+=fou_dir[i][0];
				copie_y+=fou_dir[i][1];
			}
		}
		//cavalier
		int[][]cav_dir = {{2,1},{2,-1,},{-2,1},{-2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};
		for (int i = 0; i < cav_dir.length; i++) {
			int copie_x=x+cav_dir[i][0];
			int copie_y=y+cav_dir[i][1];
			if (copie_x>-1 && copie_x<8 && copie_y>-1 && copie_y<8) {
				if (this.place[copie_x][copie_y] ==Pieces.CAVALIER && b == this.estBlanc[copie_x][copie_y]) {
					return true;
				}
			}
		}
		//ROI
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <=1 ; j++) {
				if(x+i>=0 && x+i<8 && y+j>-1 && y+j<8)
				if (this.place[x+i][y+j] ==Pieces.ROI && b == this.estBlanc[x+i][y+j]) {
					return true;
				}
			}
		}

		int sens = 1;
		if (b) {
			sens =-1;
		}
		if(y-sens<8 && y-sens>-1){
			if (x+1<8 &&this.place[x+1][y-sens] ==Pieces.PION && b == this.estBlanc[x+1][y-sens]) {
				return true;
			}
			if (x-1>=0 && this.place[x-1][y-sens] ==Pieces.PION && b == this.estBlanc[x-1][y-sens]) {
				return true;
			}
		}

		return false;
	}
	public boolean echec (boolean b) {
		int x =-1;
		int y=-1;

		for (int i= 0;  i< estBlanc.length; i++) {
			for (int j = 0; j < estBlanc.length; j++) {
				if(this.estBlanc[i][j]!=b && this.place[i][j]==Pieces.ROI) {
					x=i;
					y=j;
					
					break;
				}
				if(x!=-1 && y!=-1)break;
			}
		}

		List<Plateau> echecList = this.deplacementTotal(b);
		for (int i = 0; i < echecList.size(); i++) {
			if(echecList.get(i).place[x][y]!=Pieces.ROI && echecList.get(i).estBlanc[x][y]==b) {
				return true;
			}
		}return false;
	}
	public List<Plateau> deplacementPossible(boolean b){
		List<Plateau> echecList = this.deplacementTotal(b);
		List<Plateau> t = new ArrayList<>();
		for (int i = 0; i < echecList.size(); i++) {
			if (echecList.get(i).echec2(!b)) {
				t.add(echecList.get(i));
			} 
		}
		echecList.removeAll(t);
		return echecList;
	}


	@Override
	public int[][] getAllState() {
		List<Plateau> list=deplacementPossible(turn==1);
		int[][] moves=new int[list.size()][8];
		for (int i = 0; i < moves.length; i++) {
			moves[i]= list.get(i).hashMap();
		}
		return moves;
	}

	@Override
	public int checkWin() {
		if(deplacementPossible(turn==1).size()==0)return -turn;
		return 0;
	}

	@Override
	public int[] hashMap() {
		int[]hash=new int[8];
		for (int i = 0; i < 8; i++) {
			int t = 1;
			int v=0;
			for (int j = 0; j < 8; j++) {
				int c = this.estBlanc[i][j]?0:6;
				if(this.place[i][j]==null)v+=12*t;
				else if(this.place[i][j]==Pieces.PION)v+=t*(c);
				else if(this.place[i][j]==Pieces.FOU)v+=t*(c+1);
				else if(this.place[i][j]==Pieces.CAVALIER)v+=t*(c+2);
				else if(this.place[i][j]==Pieces.TOUR)v+=t*(c+3);
				else if(this.place[i][j]==Pieces.REINE)v+=t*(c+4);
				else if(this.place[i][j]==Pieces.ROI)v+=t*(c+5);
				t*=13;
			}
			hash[i]=v;
		}
		return hash;
	}

	@Override
	public String toString() {
		String l="";
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if(place[i][j]==null)l+="null ";
				else if(estBlanc[i][j])l+=place[i][j].toString().toUpperCase()+" ";
				else{
					l+=place[i][j].toString().toLowerCase()+" ";
				}
			}
			l+=" \n";
		}

		return l;
	}
}
