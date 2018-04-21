import java.awt.Color;

public class Pawn extends Piece {
	
	private Color pieceColor;
	private boolean option;
	
	public Pawn(Color c) {
		pieceColor = c;
		setOption(true);
	}
	
	public Color getPieceColor() {
		return pieceColor;
	}

	public boolean isOption() {
		return option;
	}

	public void setOption(boolean option) {
		this.option = option;
	}
}
