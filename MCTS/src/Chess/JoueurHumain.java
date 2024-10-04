package Chess;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.LayoutStyle.ComponentPlacement;

import com.sun.source.util.TreePathScanner;



public class JoueurHumain {

	public String nom;
	public FenetreEchec f;
	public boolean couleur;

	public JoueurHumain(String nom, FenetreEchec f,boolean b) {
		this.couleur=b;
		this.nom=nom;
		this.f=f;
	}
	
	public Plateau decision(List<Plateau> a, Plateau p) {
		this.f.bx = -1;
		this.f.by = -1;
		
		while (true) {
			if(this.f.bx!=-1&& p.estBlanc[this.f.bx][this.f.by]==couleur ) {
				Pieces o = p.place[this.f.bx][this.f.by];
				List<Plateau> f= new ArrayList<>();
				for (int i = 0; i < a.size(); i++) {
					if (a.get(i).place[this.f.bx][this.f.by]==null) {
						f.add(a.get(i));
					}
				}
				coloration(f, p);
				Plateau j= action(f, p, this.f.bx, this.f.by);
				
				if(j!=null) {
					j.turn=couleur?-1:1;
					return j;
				}
				this.f.affiche(p);
			}
			this.f.bx = -1;
			this.f.by = -1;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(this.f.bx==62)break;
		}	
		return null;
	}
	
	public Plateau action(List<Plateau> a, Plateau p,int px,int py) {
		this.f.bx = -1;
		this.f.by = -1;
		while (true) {
			if (this.f.bx==px&&this.f.by==py) {
				return null;
			}
			if (this.f.bx > -1) {
				
				if (this.f.bouton[this.f.bx][this.f.by].getBackground()==Color.red) {
					for (int i = 0; i < a.size(); i++) {
						if(  (a.get(i).place[this.f.bx][this.f.by]!=p.place[this.f.bx][this.f.by] || (a.get(i).place[this.f.bx][this.f.by]==p.place[this.f.bx][this.f.by] && a.get(i).estBlanc[this.f.bx][this.f.by]!=p.estBlanc[this.f.bx][this.f.by]))) {
							return a.get(i);
						}
					}
					
				}
			}
			this.f.bx = -1;
			this.f.by = -1;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
	}
	
	public void coloration(List<Plateau> a,Plateau p) {
		for (int i = 0; i < a.size(); i++) {
			for (int j = 0; j < 8; j++) {
				for (int j2 = 0; j2 < 8; j2++) {
					if(a.get(i).place[j][j2]!=p.place[j][j2] ||(a.get(i).place[j][j2]==p.place[j][j2] && a.get(i).estBlanc[j][j2]!=p.estBlanc[j][j2])) {
						this.f.bouton[j][j2].setBackground(Color.red);
					}
				}
			}

		}
	}
}
