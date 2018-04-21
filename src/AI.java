import java.util.LinkedList;

public class AI {
	public boolean isInitialized(){
		return false;
	}

	public Move strategy(LinkedList<Move> AImoves) {
		if (AImoves.size() > 1) {
			int moveNum = (int) Math.round(Math.random() * (AImoves.size()-1));
			Move donaldMove = AImoves.get(moveNum);
			System.out.println("Multiple moves available");
			System.out.println("Playing from tile: " + donaldMove.getTileFrom().getRowNum() + ", " + donaldMove.getTileTo().getColNum());
			return donaldMove;
		} else if (AImoves.size() <= 0) {
			System.out.println("NO VALID MOVES!");
			return null;
		} else {
			Move donaldMove = AImoves.getFirst();
			System.out.println("Only one move");
			System.out.println("Playing from tile: " + donaldMove.getTileFrom().getRowNum() + ", " + donaldMove.getTileTo().getColNum());
			System.out.println("Playing to tile: " + donaldMove.getTileTo().getRowNum() + ", " + donaldMove.getTileTo().getColNum());
			return donaldMove;
		}
	}
	
}
