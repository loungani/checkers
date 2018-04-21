import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Board extends JPanel {
	
	/**
	 *  the state of the game logic
	 */
	public boolean playing = false; // whether the game is running
	private JLabel status; // Current status text (i.e. Running...)
	private Tile[][] tiles = new Tile[8][8]; // the board's own internal reference to the tiles
	private GameMode mode; // Keeps track of which square is highlighted
	private LinkedList<Tile> moves; // keeps track of single moves
	private Map<Tile, Tile> jumps; // first tile is one we are jumping to, second is one jumped over
	private Color turn; // color of the player's turn
	private JTextField outerTextRef;
	private boolean doubleJump;
	
	/**
	 *  Game constants
	 */
	// The below is the starting matrix which serves as reference for the program set-up
	// 0 represents no piece, 1 represents a white piece, 2 represents a red piece
	public static final int[] ref1 = {0, 2, 0, 0, 0, 1, 0, 1};
	public static final int[] ref2 = {2, 0, 2, 0, 0, 0, 1, 0};
	public static final int[][] startingPieces = {ref1, ref2, ref1, ref2, ref1, ref2, ref1, ref2};
			
	/**
	 * Constructor for a new Board.	
	 * @param status
	 * @param game_text
	 */
	public Board (JLabel status, JTextField game_text) {
		// creates border around the court area, JComponent method
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		this.status = status;
		this.outerTextRef = game_text;
		outerTextRef.setText("White to move.         ");
		mode = new GameMode(false); // keeps track of highlighted squares
		moves = new LinkedList<Tile>(); 
		jumps = new TreeMap<Tile, Tile>(); 
		turn = Color.white; // white moves first
		doubleJump = false;
		
		Dimension size = getPreferredSize();
		size.setSize(500, 500);
		setPreferredSize(size);
		
		Color tileColor;
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (((i % 2 == 0)&&(!(j % 2 == 0)))||
						((!(i % 2 == 0))&&(j % 2 == 0))) {
						tileColor = new Color(100,149,237); // Corn-flower blue
					} else {
						tileColor = new Color(255,250,250); // Snow white
					}
				
				Piece p;
				if (startingPieces[i][j] == 1) {
					p = new Pawn(Color.white);
				} else if (startingPieces[i][j] == 2) {
					p = new Pawn(Color.red);
				} else {
					p = new Piece();
				}
				
				Tile t = new Tile(tileColor, j, i, p, this); // tranpose (initial bug)
				tiles[j][i] = t; // transpose
			}
		}
	}
		
	/**
	 * (Re-)set the game to its initial state.
	 */
	public void reset() {
		playing = true;
		doubleJump = false;
		status.setText("Running...");
		turn = Color.white;
		if (mode.isHighlightedOption()) {
			mode.getHighlighted().setHighlighted(false);
			mode.getHighlighted().repaint();
			mode.setHighlightedOption(false);
		}
		/**
		 * return all pieces to the starting position
		 */
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				Piece p;
				if (startingPieces[i][j] == 1) {
					p = new Pawn(Color.white);
				} else if (startingPieces[i][j] == 2) {
					p = new Pawn(Color.red);
				} else {
					p = new Piece();
				}
				tiles[j][i].setPiece(p); // transpose
				tiles[j][i].repaint();
			}
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		// We use a chess-board Grid
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				gc.gridx = 50*i;
				gc.gridy = 50*j;
				add(tiles[j][i], gc);
			}
		}
	}
	
	/**
	 * The Board's method for handling clicks.
	 * @param tile which is clicked
	 */
	public void handleClick(Tile tile) {
		// Handle tile selection/highlighting
		if (!mode.isHighlightedOption()) {
			// if there is no highlighted square yet
			if ((tile.hasPiece())&&(tile.getPiece().getPieceColor().equals(turn))) {
				select(tile);
			}
		} else {
			// there is already a highlighted square, so...
			// note: you CANNOT unselect the highlighted square if you are in DOUBLE JUMP!
			if ((mode.getHighlighted() == tile)&&(!doubleJump)) {
				// the highlighted square is being unselected
				tile.setHighlighted(false);
				tile.repaint();
				mode.setHighlightedOption(false);
				mode.setHighlighted(null); // TODO: Should this be done?
			} else {
				// This must be the move piece case
				// first see if the move is contained as a normal move
				// TODO: abstract the following code so it's not copied!
				for (Tile t : moves) {
					if (tile.equals(t)) {
						jumpToNew(tile);
						checkIfPromotedOn(tile);
						nextTurn();
					}
				}
				// then see if it's included as a jump
				for (Tile t : jumps.keySet()) {
					if (tile.equals(t)) {
						jumpToNew(tile);
						
						Tile jumped = jumps.get(t);
						jumped.setPiece(new Piece());
						tiles[jumped.getRowNum()][jumped.getColNum()].setPiece(new Piece()); // TODO: <-- why needed?
						jumped.repaint();
						tiles[jumped.getRowNum()][jumped.getColNum()].repaint();
						
						checkIfPromotedOn(tile);
						checkIfDoubleJumpOn(tile); // NEW FEATURE: Can double jump -- same turn!
					}
				}
				checkIfGameOver();
			}
		}
	}
	
	/**
	 * The board handles a play by the computer.
	 * @param ai
	 */
	public void handlePlay(AI ai) {
		moves.clear(); // flush any preexisting moves
		jumps.clear();
		LinkedList<Move> AImoves = new LinkedList<Move>();
		AImoves = getAllLegalMoves(AImoves);
		/**
		 * AI STRATEGIES
		 */
		if (ai.getClass().equals(Donald.class)) {
			// The Donald strategy -- random moves
			if (AImoves.size() <= 0) {
				// do nothing
			} else {
				Move donaldMove = ai.strategy(AImoves);
				handleClick(donaldMove.getTileFrom());
				handleClick(donaldMove.getTileTo());
			}
		} else if (ai.getClass().equals(Hillary.class)) {
			
			boolean anyJumps = false;
			LinkedList<Move> jumpMoves = new LinkedList<Move>();
			
			for (Move m : AImoves) {
				if (m.isJumpMove()) {
					jumpMoves.add(m);
					anyJumps = true;
				}
			}
			
			if (anyJumps) {
				Move m = ai.strategy(jumpMoves);
				handleClick(m.getTileFrom());
				handleClick(m.getTileTo());
			} else {
				if (AImoves.size() <= 0) {
					// do nothing
				} else {
					Move m = ai.strategy(AImoves);
					handleClick(m.getTileFrom());
					handleClick(m.getTileTo());
				}
			}
			
		} else {
			throw new IllegalArgumentException("unexpected input in handlePlay");
		}
	}


	private LinkedList<Move> getAllLegalMoves(LinkedList<Move> AImoves) {
		if (!doubleJump) {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					if (tiles[i][j].hasPiece()&&tiles[i][j].getPiece().getPieceColor().equals(turn)) {
						calculateLegalMoves(tiles[i][j]);
						AImoves = addAnyLegalMoves(tiles[i][j], AImoves);
						moves.clear(); // flush list after each tile calculation
						jumps.clear();
					}
				}
			}
		} else {
			// if there is a double jump
			Tile t = mode.getHighlighted();
			calculateLegalMoves(t);
			AImoves = addAnyLegalMoves(t, AImoves);
		}
		return AImoves;
	}

	private LinkedList<Move> addAnyLegalMoves(Tile tileFrom, LinkedList<Move> AImoves) {
		for (Tile t : moves) {
			AImoves.add(new Move(tileFrom, t, hasAJump(tileFrom)));
		}
		for (Tile t : jumps.keySet()) {
			AImoves.add(new Move(tileFrom, t, hasAJump(tileFrom)));
		}
		return AImoves;
	}

	private void select(Tile tile) {
		// if there is a piece to move, highlight that square AND it's the matching turn
		tile.setHighlighted(true);
		mode.setHighlightedOption(true);
		mode.setHighlighted(tile);
		tile.repaint();
		// ensure that the piece is now ready to move
		calculateLegalMoves(tile);
	}

	private void checkIfDoubleJumpOn(Tile tile) {
		if (hasAJump(tile)) {
			outerTextRef.setText("DOUBLE JUMP!");
			select(tile);
			doubleJump = true;
		} else {
			doubleJump = false;
			nextTurn();
		}
	}


	/**
	 * This game considers it game over when all the pieces from one side have been eliminated.
	 */
	private void checkIfGameOver() {
		boolean anyWhitePieces = false;
		boolean anyRedPieces = false;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {	
				if (tiles[i][j].hasPiece()) {
					
					if (tiles[i][j].getPiece().getPieceColor().equals(Color.white)) {
						anyWhitePieces = true;
					}
					if (tiles[i][j].getPiece().getPieceColor().equals(Color.red)) {
						anyRedPieces = true;
					}
					
				}
			}
		}
		if (!anyWhitePieces||!anyRedPieces) {
			playing = false;
			status.setText("Game over. Hit Reset for new game.");
			// Declare the game as over!
			if (!anyWhitePieces) {
				outerTextRef.setText("Game over - red wins.");
				turn = Color.black;
			} else {
				outerTextRef.setText("Game over - white wins.");
				turn = Color.black;
			}
		}
	}

	private void checkIfPromotedOn(Tile tile) {
		Color c = tile.getPiece().getPieceColor();
		if (c.equals(Color.white)&&tile.getRowNum() == 0) {
			Piece p = new King(Color.white);
			tile.setPiece(p);
			tiles[tile.getRowNum()][tile.getColNum()].setPiece(p);
			tile.repaint();
		}
		if (c.equals(Color.red)&&tile.getRowNum() == 7) {
			Piece p = new King(Color.red);
			tile.setPiece(p);
			tiles[tile.getRowNum()][tile.getColNum()].setPiece(p);
			tile.repaint();
		}
	}

	private void jumpToNew(Tile tile) {
		Piece p  = mode.getHighlighted().getPiece();
		tile.setPiece(p);
		tiles[tile.getRowNum()][tile.getColNum()].setPiece(p); // TODO: <-- why needed?
		tile.repaint();
		mode.getHighlighted().setPiece(new Piece());
		mode.setHighlightedOption(false);
		mode.getHighlighted().setHighlighted(false);
		mode.getHighlighted().repaint();
		mode.setHighlighted(null); // TODO: Should this be done?
	}

	private void nextTurn() {
		if (turn.equals(Color.white)) {
			turn = Color.red;
			outerTextRef.setText("Red to move.");
		} else {
			turn = Color.white;
			outerTextRef.setText("White to move.");
		}
	}

	/**
	 * Workhorse method which takes a tile with a pawn piece and updates
	 * the linkedlist of moves with every tile that pawn can legally move to
	 * @param tile
	 */
	private void calculateLegalMoves(Tile tile) {
		
		moves.clear(); // flush the prexisting collections
		jumps.clear();
		
		int rowDelta;
		Color thisColor = tile.getPiece().getPieceColor();
		if (thisColor.equals(Color.white)) {
			rowDelta = -1;
		} else {
			rowDelta = 1;
		}
		
		int rowNum = tile.getRowNum();
		int colNum = tile.getColNum();
		int newRow = rowNum + rowDelta;
		int newCol1 = colNum - 1;
		int newCol2 = colNum + 1;
		int kingRowDelta = rowDelta * -1;
		int newKingRow = rowNum + kingRowDelta;
		
		/** because a piece MUST move if it can capture, we first check to see if
		 *  the piece can capture. If it can, then only capture moves will be legal 
		 */		
		// if and only if the piece cannot jump, then check if there are any open squares
		if (!hasAJump(tile)) {
			if (checkBounds(newRow, newCol1)&&(!tiles[newRow][newCol1].hasPiece())) {
				moves.add(tiles[newRow][newCol1]);
			}
			if (checkBounds(newRow, newCol2)&&(!tiles[newRow][newCol2].hasPiece())) {
				moves.add(tiles[newRow][newCol2]);
			}
			// (and for kings)
			if (tile.getPiece().isKing()) {
				if (checkBounds(newKingRow, newCol1)&&(!tiles[newKingRow][newCol1].hasPiece())) {
					moves.add(tiles[newKingRow][newCol1]);
				}
				if (checkBounds(newKingRow, newCol2)&&(!tiles[newKingRow][newCol2].hasPiece())) {
					moves.add(tiles[newKingRow][newCol2]);
				}
			}
		}
	}
	
	private boolean hasAJump(Tile tile) {
		int rowDelta;
		Color thisColor = tile.getPiece().getPieceColor();
		if (thisColor.equals(Color.white)) {
			rowDelta = -1;
		} else {
			rowDelta = 1;
		}
		
		int rowNum = tile.getRowNum();
		int colNum = tile.getColNum();
		int newRow = rowNum + rowDelta;
		int newCol1 = colNum - 1;
		int newCol2 = colNum + 1;
		
		/** because a piece MUST move if it can capture, we first check to see if
		 *  the piece can capture. If it can, then only capture moves will be legal 
		 */
		boolean canCapture = false;
		
		// first check the square to the diagonal-left
		if (checkDiagonalLeft(newRow, newCol1, rowDelta, thisColor)) {
			canCapture = true;
		}
		
		// then, check the square to the diagonal-right
		if (checkDiagonalRight(newRow, newCol2, rowDelta, thisColor)) {
			canCapture = true;
		}
		
		// Now, if the piece is a king - check the possible jumps but in the opposite direction
		int kingRowDelta = rowDelta * -1;
		int newKingRow = rowNum + kingRowDelta;
		if (tile.getPiece().isKing()) {
			if (checkDiagonalLeft(newKingRow, newCol1, kingRowDelta, thisColor)) {
				canCapture = true;
			}
			if (checkDiagonalRight(newKingRow, newCol2, kingRowDelta, thisColor)) {
				canCapture = true;
			}
		}
		
		return canCapture;
	}

	
	private boolean checkDiagonalRight(int newRow, int newCol2, int rowDelta, Color thisColor) {
		if (checkBounds(newRow, newCol2)&&tiles[newRow][newCol2].hasEnemyPiece(thisColor)) {
			// square to the diagonal-right with piece
			int nextNewRow = newRow + rowDelta;
			int nextNewCol = newCol2 + 1;
			if (checkBounds(nextNewRow, nextNewCol)&&(!tiles[nextNewRow][nextNewCol].hasPiece())) {
				jumps.put(tiles[nextNewRow][nextNewCol], tiles[newRow][newCol2]);
				return true;
			}
		}
		return false;
	}

	private boolean checkDiagonalLeft(int newRow, int newCol1, int rowDelta, Color thisColor) {
		if (checkBounds(newRow, newCol1)&&tiles[newRow][newCol1].hasEnemyPiece(thisColor)) {
			// square to the diagonal-left with piece
			int nextNewRow = newRow + rowDelta;
			int nextNewCol = newCol1 - 1;
			if (checkBounds(nextNewRow, nextNewCol)&&(!tiles[nextNewRow][nextNewCol].hasPiece())) {
				jumps.put(tiles[nextNewRow][nextNewCol], tiles[newRow][newCol1]);
				return true;
			}
		}
		return false;
	}

	/**
	 * Helper method which determines whether the bounds passed in are contained
	 * within the normal 8x8 grid.
	 * @param rowNum
	 * @param colNum
	 * @return
	 */
	private boolean checkBounds (int rowNum, int colNum) {
		if ((rowNum < 0)||(rowNum > 7)) {
			return false;
		} else if ((colNum < 0)||(colNum > 7)) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * GETTERS - to be used for testing!
	 */
	public Tile[][] getTiles() {
		return tiles; // NOTE - this BREAKS encapsulation so must be careful!
	}
	
	public Color getTurn() {
		return turn;
	}
	
	public GameMode getMode() {
		return mode;
	}
	
	public boolean isDoubleJumpEnabled() {
		return doubleJump;
	}
	
	// Below methods allow you to circumvent methods and modify tiles and game state. 
	// TO BE USED FOR TESTING ONLY! Security password included to prevent accidental use!
	public void modifyTile(Tile t, Piece p, String password) {
		if (!password.equals("junit_test")) {
			throw new IllegalArgumentException("DO NOT USE MODIFY RECKLESSLY!");
		}
		t.setPiece(p);
		tiles[t.getRowNum()][t.getColNum()].setPiece(p);
		t.repaint();
		tiles[t.getRowNum()][t.getColNum()].repaint();
	}
}