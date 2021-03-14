package cz.sevrjukov.ttt.gui;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimpleVisualBoard {


	private JTextArea textArea;

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

		// Sets JTextArea font and color.
		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 15);
		textArea.setFont(font);

		JButton btnDraw = new JButton("refresh");
		btnDraw.addActionListener(event -> drawPosition());

		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		frame.getContentPane().add(textArea);
		frame.getContentPane().add(btnDraw);
		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public void start() {
		// Here, we can safely update the GUI
		// because we'll be called from the
		// event dispatch thread
		SwingUtilities.invokeLater(this::createAndShowGUI);

		var scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(this::drawPosition, 0, 2, TimeUnit.SECONDS);


	}

	public void drawPosition() {
		try {
			var lines = Files.readAllLines(Paths.get("/tmp", "board.txt"));
			var content = String.join("\n", lines);
			updateTextarea(content);
		} catch (IOException e) {
			e.printStackTrace();
			updateTextarea("error");
		}

	}

	private void updateTextarea(String string) {
		SwingUtilities.invokeLater(() -> {
			// Here, we can safely update the GUI
			// because we'll be called from the
			// event dispatch thread
			textArea.setText(string);
		});
	}
}
