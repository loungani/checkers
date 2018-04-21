import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Game implements Runnable {
	
	private static final String message =
	"The instructions are simple. Goal is to kill all of the opponent's checker pieces. \nPieces move"
	+ " diagonally forward, and can move diagonally backward if promoted to king at end of board. \nPieces"
	+ " capture by jumping one space over to an empty tile. Pieces can double jump, triple jump, etc. if"
	+ " allowed. \n\nIf a piece has the ability to capture, then it cannot play a non-capturing move (it must"
	+ " take). \nThis adds a bit of strategy. \nSimilarly, pieces cannot stop in the middle of a double jump.\n\n"
	+ "Click to highlight a cell and click again to perform a move. The move will play if legal. \n"
	+ "If you change your mind, click the cell again to de-select. \n"
	+ "At the end of the game, whether the game is won or fought to a stalemate, hit Reset to start again.\n"
	+ "\nIf you aren't sure what move to play, feel free to let Donald or Hillary play for you.";

	/**
	 * Private AI field which can be modified by radio buttons.
	 */
	private AI ai = new AI();
	
	@Override
	public void run() {
				// Top-level frame in which game components live
				final JFrame frame = new JFrame("Checkers v10");
				Dimension d = frame.getPreferredSize();
				d.setSize(600, 600);
				frame.setPreferredSize(d);
				frame.setLocation(300, 100);
				
				// Status panel
				final JPanel status_panel = new JPanel();
				frame.add(status_panel, BorderLayout.SOUTH);
				final JLabel status = new JLabel("Running...");
				status_panel.add(status);
				
				// Game updates
				JTextField game_text = new JTextField("                   ");
				game_text.setEnabled(false);

				// Main playing area
				final Board board = new Board(status, game_text);
				frame.add(board, BorderLayout.CENTER);

				// Reset button
				final JPanel control_panel = new JPanel();
				frame.add(control_panel, BorderLayout.NORTH);
				

				// Note here that when we add an action listener to the reset
				// button, we define it as an anonymous inner class that is
				// an instance of ActionListener with its actionPerformed()
				// method overridden. When the button is pressed,
				// actionPerformed() will be called.
				final JButton reset = new JButton("Reset");
				reset.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						board.reset();
					}
				});
				
				final JButton instructions = new JButton("Instructions");
				instructions.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JOptionPane.showMessageDialog(frame, message);
					}
				});
				
				// Create the radio buttons for AI
				JRadioButton donaldButton = new JRadioButton("Donald");
				JRadioButton hillaryButton = new JRadioButton("Hillary");
				
				donaldButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						ai = new Donald();
					}
				});
				hillaryButton.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						ai = new Hillary();
					}
				});
				
				ButtonGroup AI_group = new ButtonGroup();
				AI_group.add(donaldButton);
				AI_group.add(hillaryButton);
				
				JPanel buttonPanel = new JPanel();
				buttonPanel.setLayout(new BorderLayout());
				buttonPanel.add(donaldButton, BorderLayout.NORTH);
				buttonPanel.add(hillaryButton, BorderLayout.SOUTH);
				
				// Create the button to play AI Moves!
				JButton playAI = new JButton("Have Computer Play Turn");
				playAI.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
							if (!ai.isInitialized()) {
								JOptionPane.showMessageDialog(frame, "Must select AI option");
							} else {
								board.handlePlay(ai);
							}
					}
				});
				
				control_panel.add(buttonPanel);
				control_panel.add(playAI);
				control_panel.add(instructions);
				control_panel.add(reset);
				control_panel.add(game_text);

				// Put the frame on the screen
				frame.pack();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);

				// Start game
				board.reset();
			}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Game());
	}

}
