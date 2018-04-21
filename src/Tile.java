import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Tile extends JPanel implements MouseListener, Comparable<Tile> {
	
	// internal tile state
	private Color tileColor;
	private int rowNum;
	private int colNum;
	private Piece tilePiece;
	private boolean highlighted;
	private Board board;
	
	public Tile(Color c, int row, int col, Piece p, Board b) {
		tileColor = c;
		rowNum = row;
		colNum = col;
		tilePiece = p;
		board = b;
		setHighlighted(false);
		
		Dimension size = getPreferredSize();
		size.height = 50;
		size.width = 50;
		setPreferredSize(size);
		
		this.addMouseListener(this);
	}
	
	/**
	 * TILE METHODS
	 */
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Paint the actual tile!
		g.setColor(tileColor);
		g.fillRect(0, 0, 50, 50);
		
		// paint a highlight if the tile is highlighted
		if (highlighted) {
			g.setColor(Color.red);
			g.drawRect(0, 0, 49, 49);
		}
		
		// Paint the piece on the tile -- if there is one!
		if (hasPiece()) {
			if (tilePiece.isKing()) {
				g.setColor(Color.black);
				g.fillOval(13, 13, 25, 25);
				g.setColor(tilePiece.getPieceColor());
				g.fillOval(15, 15, 22, 22);
			}
			g.setColor(Color.black);
			g.fillOval(10, 10, 25, 25);
			g.setColor(tilePiece.getPieceColor());
			g.fillOval(12, 12, 22, 22);
			g.setColor(Color.black);
			g.fillOval(15, 15, 15, 15);
			g.setColor(tilePiece.getPieceColor());
			g.fillOval(17, 17, 12, 12);
		}
	}
	
	/*
	 * Boolean method which returns whether the tile has a Piece - NOTE:
	 * Empty piece (aka new Piece()) is counted as FALSE!
	 */
	public boolean hasPiece() {
		return tilePiece.isOption();
	}

	public boolean hasEnemyPiece(Color questioningColor) {
		if (!tilePiece.isOption()) {
			return false;
		}
		Color thisColor = tilePiece.getPieceColor();
			if (!thisColor.equals(questioningColor)) {
				return true;
			} else {
				return false;
			}
		}
	
	public boolean equals(Tile t) {
		if ((t.getRowNum() == rowNum)&&(t.getColNum() == colNum)) {
			return true;
		}
		return false;
	}
	
	/**
	 * EVENT HANDLING
	 */

	@Override
	public void mouseClicked(MouseEvent e) {
		board.handleClick(this);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * GETTERS AND SETTERS
	 */

	public int getRowNum() {
		return rowNum;
	}

	public int getColNum() {
		return colNum;
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}
	
	public Piece getPiece() {
		return tilePiece;
	}
	
	public void setPiece(Piece p) {
		tilePiece = p;
	}
	
	/**
	 * This method isn't really used - just so Tiles can be ordered in collections.
	 */
	@Override
	public int compareTo(Tile arg0) {
		if (this.rowNum == arg0.getRowNum()) {
			if (this.colNum == arg0.getColNum()) {
				return 0;
			} else if (this.colNum < arg0.getColNum()) {
				return -1;
			} else {
				return 1;
			}
		} else if (this.rowNum < arg0.getRowNum()) {
			return -1;
		} else {
			return 1;
		}
	}

}
