import java.util.LinkedList;

public class Donald extends AI {
	
	@Override
	public boolean isInitialized() {
		return true;
	}
	
	@Override
	public Move strategy(LinkedList<Move> AImoves) {
		if (AImoves.size() > 1) {
			int moveNum = (int) Math.round(Math.random() * (AImoves.size()-1));
			Move donaldMove = AImoves.get(moveNum);
			return donaldMove;
		} else if (AImoves.size() <= 0) {
			// do nothing
			return null;
		} else {
			Move donaldMove = AImoves.getFirst();
			return donaldMove;
		}
	}

}
