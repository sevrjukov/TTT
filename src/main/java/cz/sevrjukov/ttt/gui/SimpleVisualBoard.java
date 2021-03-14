package cz.sevrjukov.ttt.gui;

import cz.sevrjukov.ttt.board.Board;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import java.awt.Font;

public class SimpleVisualBoard implements BoardDisplay {


	JTextArea textArea;

	public static void main(String[] args) {
		SimpleVisualBoard o = new SimpleVisualBoard();
		o.start();

	}

	private void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("HelloWorldSwing");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setSize(500,300);

		//Add the ubiquitous "Hello World" label.
		textArea = new JTextArea(30, 60);
		textArea.setLineWrap(true);
		textArea.setText("The quick brown fox jumps over the lazy dog.");

		// Sets JTextArea font and color.
		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 15);
		textArea.setFont(font);
		frame.getContentPane().add(textArea);

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public void start() {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.

				createAndShowGUI();
	}

	@Override
	public void drawPosition(Board board) {
		textArea.setText(board.toString());
	}
}
