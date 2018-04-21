
public class Move {
	Tile from;
	Tile to;
	boolean jumpMove;
	
	public Move (Tile from, Tile to, boolean b) {
		this.from = from;
		this.to= to;
		jumpMove = b;
	}
	
	public Tile getTileFrom() {
		return from;
	}
	
	public Tile getTileTo() {
		return to;
	}
	
	public boolean isJumpMove() {
		return jumpMove;
	}
	

}
