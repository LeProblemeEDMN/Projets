package Chess;

import javax.swing.JButton;

public class EchecBouton extends JButton{
	private int x;
	private int y;
	
	public EchecBouton(int x, int y) {
		super();
		this.x = x;
		this.y = y;
		this.setVisible(true);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	
}
