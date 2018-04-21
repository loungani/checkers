import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.junit.Test;

public class SimpleFunctionTest {

	@Test
	public void testTileHasPiece() {
		JLabel jlabel_test = new JLabel("");
		JTextField jtext_test = new JTextField("");
		Tile t1 = new Tile(Color.white, 0, 0, new Piece(), new Board(jlabel_test, jtext_test));
		Tile t2 = new Tile(Color.white, 0, 0, new Pawn(Color.white), new Board(jlabel_test, jtext_test));
		Tile t3 = new Tile(Color.white, 0, 0, new King(Color.white), new Board(jlabel_test, jtext_test));
		
		assertFalse(t1.hasPiece());
		assertTrue(t2.hasPiece());
		assertTrue(t3.hasPiece());
	}
	
	@Test
	public void testTileEquality() {
		JLabel jlabel_test = new JLabel("");
		JTextField jtext_test = new JTextField("");
		Tile t1 = new Tile(Color.white, 0, 0, new Piece(), new Board(jlabel_test, jtext_test));
		Tile t2 = new Tile(Color.white, 0, 0, new Pawn(Color.white), new Board(jlabel_test, jtext_test));

		assertTrue(t1.equals(t2));
	}
	
	@Test
	public void testTileHasEnemyPiece() {
		JLabel jlabel_test = new JLabel("");
		JTextField jtext_test = new JTextField("");
		Tile t1 = new Tile(Color.white, 0, 0, new Pawn(Color.white), new Board(jlabel_test, jtext_test));
		Tile t2 = new Tile(Color.white, 0, 0, new Pawn(Color.red), new Board(jlabel_test, jtext_test));

		assertTrue(t1.hasEnemyPiece(Color.red));
		assertTrue(t2.hasEnemyPiece(Color.white));
	}

}
