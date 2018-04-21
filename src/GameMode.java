/**
 * A special class made for tracking highlighted tiles.
 * May handle other information in future updates.
 * @author David
 *
 */
public class GameMode {
	private boolean highlightedOption;
	private Tile highlighted;
	
	public GameMode(boolean hOption) {
		highlightedOption = hOption;
	}
	
	/**
	 * GETTERS AND SETTERS
	 */
	public boolean isHighlightedOption() {
		return highlightedOption;
	}
	public void setHighlightedOption(boolean highlightedOption) {
		this.highlightedOption = highlightedOption;
	}
	public Tile getHighlighted() {
		return highlighted;
	}
	public void setHighlighted(Tile highlighted) {
		this.highlighted = highlighted;
	}

}
