import java.awt.Color;

public class Piece {
	
	private Color pieceColor;
	private boolean option;
	
	public Color getPieceColor() {
		return pieceColor;
	}
	
	public Piece() {
		setOption(false);
	}

	public boolean isOption() {
		return option;
	}

	public void setOption(boolean option) {
		this.option = option;
	}
	
	public boolean isKing() {
		return false;
	}

}
