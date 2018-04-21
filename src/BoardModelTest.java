import static org.junit.Assert.*;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.junit.Test;

public class BoardModelTest {
	
// selecting a white piece on a white turn
	@Test
	public void testSingleClickGoesThrough() {
		JLabel jlabel_test = new JLabel("");
		JTextField jtext_test = new JTextField("");
		Board b = new Board(jlabel_test, jtext_test);
		Tile[][] tiles = b.getTiles();
		
		assertTrue(tiles[7][0].hasPiece());
		
		b.handleClick(tiles[7][0]); // white tile with piece on white turn
		assertTrue(b.getMode().isHighlightedOption());
		assertTrue(tiles[7][0].isHighlighted());
		assertEquals(b.getMode().getHighlighted(), tiles[7][0]);
	}

// selecting a red piece on a white turn
	@Test
	public void testSingleClickWrongTurn() {
		JLabel jlabel_test = new JLabel("");
		JTextField jtext_test = new JTextField("");
		Board b = new Board(jlabel_test, jtext_test);
		Tile[][] tiles = b.getTiles();
		
		assertTrue(tiles[1][0].hasPiece());
		
		b.handleClick(tiles[1][0]); // white tile with piece on white turn
		assertFalse(b.getMode().isHighlightedOption());
		assertFalse(tiles[7][0].isHighlighted());
	}

// selecting a white tile, then unselecting
@Test
public void testSelectAndUnselect() {
	JLabel jlabel_test = new JLabel("");
	JTextField jtext_test = new JTextField("");
	Board b = new Board(jlabel_test, jtext_test);
	Tile[][] tiles = b.getTiles();
	
	assertTrue(tiles[7][0].hasPiece());
	
	b.handleClick(tiles[7][0]); // white tile with piece on white turn
	assertTrue(b.getMode().isHighlightedOption());
	assertEquals(b.getMode().getHighlighted(), tiles[7][0]);
	
	b.handleClick(tiles[7][0]); // unselect tile
	assertFalse(b.getMode().isHighlightedOption());
	assertFalse(tiles[7][0].isHighlighted());
}

// selecting a tile, then selecting a non-legal move
@Test
public void testNonLegalMove() {
	JLabel jlabel_test = new JLabel("");
	JTextField jtext_test = new JTextField("");
	Board b = new Board(jlabel_test, jtext_test);
	Tile[][] tiles = b.getTiles();
	
	assertTrue(tiles[7][0].hasPiece());
	
	b.handleClick(tiles[7][0]); // white tile with piece on white turn
	assertTrue(b.getMode().isHighlightedOption());
	assertEquals(b.getMode().getHighlighted(), tiles[7][0]);
	
	b.handleClick(tiles[0][0]); // non-legal move: game state should be unchanged
	assertTrue(b.getMode().isHighlightedOption());
	assertEquals(b.getMode().getHighlighted(), tiles[7][0]);	
	assertFalse(tiles[0][0].hasPiece());
	assertEquals(b.getTurn(), Color.white);
}

// select a tile then selecting a legal move - should perform the move!
@Test
public void testLegalMove() {
	JLabel jlabel_test = new JLabel("");
	JTextField jtext_test = new JTextField("");
	Board b = new Board(jlabel_test, jtext_test);
	Tile[][] tiles = b.getTiles();
	
	assertTrue(tiles[5][4].hasPiece());
	
	b.handleClick(tiles[5][4]); // white tile with piece on white turn
	assertTrue(b.getMode().isHighlightedOption());
	assertEquals(b.getMode().getHighlighted(), tiles[5][4]);
	
	b.handleClick(tiles[4][3]); // legal move: game state should be changed
	assertFalse(b.getMode().isHighlightedOption());
	assertFalse(tiles[5][4].isHighlighted());
	assertTrue(tiles[4][3].hasPiece());
	assertEquals(tiles[4][3].getPiece().getPieceColor(), Color.white);

	assertEquals(b.getTurn(), Color.red);
}

// test a jump
@Test
public void testLegalJump() {
	JLabel jlabel_test = new JLabel("");
	JTextField jtext_test = new JTextField("");
	Board b = new Board(jlabel_test, jtext_test);
	Tile[][] tiles = b.getTiles();
	
	b.handleClick(tiles[5][4]); 
	b.handleClick(tiles[4][3]); // white piece moves
	b.handleClick(tiles[2][3]);
	b.handleClick(tiles[3][2]); // red piece moves
	b.handleClick(tiles[5][6]);
	b.handleClick(tiles[4][5]); // white piece moves
	b.handleClick(tiles[3][2]);
	b.handleClick(tiles[5][4]); // red piece on [3][2] jumps white piece on [4][3]
	
	assertFalse(b.getMode().isHighlightedOption());
	assertTrue(tiles[5][4].hasPiece());
	assertEquals(tiles[5][4].getPiece().getPieceColor(), Color.red);
	assertEquals(b.getTurn(), Color.white);
	assertFalse(tiles[4][3].hasPiece());
}

// test the piece-must-capture rule
@Test
public void testMustCaptureRule() {
	JLabel jlabel_test = new JLabel("");
	JTextField jtext_test = new JTextField("");
	Board b = new Board(jlabel_test, jtext_test);
	Tile[][] tiles = b.getTiles();
	
	b.handleClick(tiles[5][4]); 
	b.handleClick(tiles[4][3]); // white piece moves
	b.handleClick(tiles[2][3]);
	b.handleClick(tiles[3][2]); // red piece moves
	b.handleClick(tiles[5][6]);
	b.handleClick(tiles[4][5]); // white piece moves
	b.handleClick(tiles[3][2]);
	b.handleClick(tiles[4][1]); // red piece cannot make this move due to must-capture rule
	
	assertTrue(b.getMode().isHighlightedOption());
	assertFalse(tiles[4][1].hasPiece());
	assertEquals(tiles[3][2].getPiece().getPieceColor(), Color.red);
	assertEquals(b.getTurn(), Color.red);
}

//test a double jump!
@Test
public void testDoubleJump() {
	JLabel jlabel_test = new JLabel("");
	JTextField jtext_test = new JTextField("");
	Board b = new Board(jlabel_test, jtext_test);
	Tile[][] tiles = b.getTiles();
	
	// set-up for the double jump
	b.handleClick(tiles[5][4]); 
	b.handleClick(tiles[4][3]); // white piece moves
	b.handleClick(tiles[2][3]); 
	b.handleClick(tiles[3][2]); // red piece moves
	b.handleClick(tiles[5][6]);
	b.handleClick(tiles[4][5]); // white piece moves
	b.handleClick(tiles[3][2]);
	b.handleClick(tiles[5][4]); // red piece moves
	b.handleClick(tiles[4][5]);
	b.handleClick(tiles[3][6]); // white piece moves
	b.handleClick(tiles[2][1]);
	b.handleClick(tiles[3][2]); // red piece moves
	b.handleClick(tiles[6][5]);
	b.handleClick(tiles[4][3]); // white piece jumps first red piece - mid double-jump!
	
	assertTrue(b.isDoubleJumpEnabled());
	
	b.handleClick(tiles[2][1]); // white piece completes the double jump
	
	assertFalse(b.isDoubleJumpEnabled());
	
	assertFalse(b.getMode().isHighlightedOption());
	assertTrue(tiles[2][1].hasPiece());
	assertEquals(tiles[2][1].getPiece().getPieceColor(), Color.white);
	assertFalse(tiles[3][2].hasPiece());
	assertEquals(b.getTurn(), Color.red);
}

// ensure that one cannot simply de-select during a double jump
@Test
public void testNoBrakesOnThisDoubleJump() {
	JLabel jlabel_test = new JLabel("");
	JTextField jtext_test = new JTextField("");
	Board b = new Board(jlabel_test, jtext_test);
	Tile[][] tiles = b.getTiles();
	
	// set-up for the double jump
	b.handleClick(tiles[5][4]); 
	b.handleClick(tiles[4][3]); // white piece moves
	b.handleClick(tiles[2][3]);
	b.handleClick(tiles[3][2]); // red piece moves
	b.handleClick(tiles[5][6]);
	b.handleClick(tiles[4][5]); // white piece moves
	b.handleClick(tiles[3][2]);
	b.handleClick(tiles[5][4]); // red piece moves
	b.handleClick(tiles[4][5]);
	b.handleClick(tiles[3][6]); // white piece moves
	b.handleClick(tiles[2][1]);
	b.handleClick(tiles[3][2]); // red piece moves
	b.handleClick(tiles[6][5]);
	b.handleClick(tiles[4][3]); // white piece jumps first red piece - mid double-jump!
	
	assertTrue(b.isDoubleJumpEnabled());
	
	b.handleClick(tiles[4][3]); // white piece completes the double jump
	
	assertTrue(b.isDoubleJumpEnabled());
	assertTrue(b.getMode().isHighlightedOption());
	assertEquals(b.getMode().getHighlighted(), tiles[4][3]);
}

//test the functionality of reset
@Test
public void testReset() {
	JLabel jlabel_test = new JLabel("");
	JTextField jtext_test = new JTextField("");
	Board b = new Board(jlabel_test, jtext_test);
	Tile[][] tiles = b.getTiles();
	
	b.handleClick(tiles[5][4]); // white tile with piece on white turn
	b.handleClick(tiles[4][3]); // legal move: game state should be changed

	assertEquals(b.getTurn(), Color.red); // red turn before reset
	
	b.reset();
	
	assertTrue(tiles[5][4].hasPiece()); // piece back to [5][4]
	assertFalse(tiles[4][3].hasPiece());
	
	assertEquals(b.getTurn(), Color.white);
}

// EDGE CASE - make sure the reset works properly - even if reset during a double jump!
@Test
public void testResetMidDoubleJump() {
	JLabel jlabel_test = new JLabel("");
	JTextField jtext_test = new JTextField("");
	Board b = new Board(jlabel_test, jtext_test);
	Tile[][] tiles = b.getTiles();
	
	// set-up for the double jump
	b.handleClick(tiles[5][4]); 
	b.handleClick(tiles[4][3]); // white piece moves
	b.handleClick(tiles[2][3]); 
	b.handleClick(tiles[3][2]); // red piece moves
	b.handleClick(tiles[5][6]);
	b.handleClick(tiles[4][5]); // white piece moves
	b.handleClick(tiles[3][2]);
	b.handleClick(tiles[5][4]); // red piece moves
	b.handleClick(tiles[4][5]);
	b.handleClick(tiles[3][6]); // white piece moves
	b.handleClick(tiles[2][1]);
	b.handleClick(tiles[3][2]); // red piece moves
	b.handleClick(tiles[6][5]);
	b.handleClick(tiles[4][3]); // white piece jumps first red piece - mid double-jump!
	
	assertTrue(b.isDoubleJumpEnabled());
	
	b.reset();
	
	assertFalse(b.isDoubleJumpEnabled());
	assertFalse(b.getMode().isHighlightedOption());
	
	for (int i = 0; i < 8; i++) {
		for (int j = 0; j < 8; j++) {
			Piece p = tiles[i][j].getPiece();
			int test;
			if (!p.isOption()) {
				test = 0;
			} else if (p.getPieceColor().equals(Color.white)) {
				test = 1;
			} else {
				test = 2;
			}
			assertEquals(test, Board.startingPieces[j][i]);
		}
	}
}

@Test
public void testPromotion() {
	JLabel jlabel_test = new JLabel("");
	JTextField jtext_test = new JTextField("");
	Board b = new Board(jlabel_test, jtext_test);
	Tile[][] tiles = b.getTiles();
	
	b.modifyTile(tiles[0][1], new Piece(), "junit_test");
	b.modifyTile(tiles[1][2], new Pawn(Color.white), "junit_test");
	
	b.handleClick(tiles[1][2]);
	b.handleClick(tiles[0][1]); // white pawn promotes to King
	
	assertTrue(tiles[0][1].getPiece().isKing());
	assertEquals(tiles[0][1].getPiece().getPieceColor(), Color.white);
}

@Test
public void testKingMovement() {
	JLabel jlabel_test = new JLabel("");
	JTextField jtext_test = new JTextField("");
	Board b = new Board(jlabel_test, jtext_test);
	Tile[][] tiles = b.getTiles();
	
	b.modifyTile(tiles[0][1], new Piece(), "junit_test");
	b.modifyTile(tiles[1][2], new Pawn(Color.white), "junit_test");
	
	b.handleClick(tiles[1][2]);
	b.handleClick(tiles[0][1]); // white pawn promotes to King
	b.handleClick(tiles[2][1]);
	b.handleClick(tiles[3][0]); // red piece moves
	b.handleClick(tiles[0][1]);
	b.handleClick(tiles[1][2]); // white king moves backwards
	
	assertTrue(tiles[1][2].hasPiece());
	
	b.handleClick(tiles[3][0]);
	b.handleClick(tiles[4][1]); // red piece moves
	b.handleClick(tiles[1][2]);
	b.handleClick(tiles[3][4]); // white king jumps backwards over red pawn on [2][3]
	
	assertFalse(tiles[2][3].hasPiece());
	assertTrue(tiles[3][4].hasPiece());
}



}