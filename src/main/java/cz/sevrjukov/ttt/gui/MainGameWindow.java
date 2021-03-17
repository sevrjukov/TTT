package cz.sevrjukov.ttt.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class MainGameWindow extends JFrame {

	protected JButton btnNewGame;
	protected JButton btnMakeMove;
	protected JButton btnSaveGames;
	protected JTextPane infoTextPane;
	protected JTextPane statsTextPane;
	private GameBoardCanvas boardCanvas;
	private GameController controller;

	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		MainGameWindow o = new MainGameWindow();
		o.init();
	}

	public void init() {
		controller = new GameController(this);

		javax.swing.SwingUtilities.invokeLater(() -> {
			try {
				createAndShowGUI();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}

	private void createAndShowGUI() throws IOException {
		this.setTitle("TicTacToe");
		this.setBounds(100, 100, 1000, 680);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		BorderLayout borderLayout = new BorderLayout();
		this.getContentPane().setLayout(borderLayout);

		var leftSidePannel = new JPanel();
		leftSidePannel.setPreferredSize(new Dimension(350, 400));

		this.getContentPane().add(leftSidePannel, BorderLayout.WEST);

		btnNewGame = new JButton("New game");
		btnNewGame.addActionListener(controller);
		leftSidePannel.add(btnNewGame);

		btnMakeMove = new JButton("Make first move");
		btnMakeMove.addActionListener(controller);
		leftSidePannel.add(btnMakeMove);

		btnSaveGames = new JButton("Save games");
		btnMakeMove.addActionListener(controller);
		leftSidePannel.add(btnSaveGames);

		infoTextPane = new JTextPane();
		infoTextPane.setPreferredSize(new Dimension(300, 200));
		infoTextPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		infoTextPane.setEditable(false);
		leftSidePannel.add(infoTextPane);

		statsTextPane = new JTextPane();
		statsTextPane.setPreferredSize(new Dimension(300, 200));
		statsTextPane.setText("Flabadop blip blup blip");
		statsTextPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		statsTextPane.setEditable(false);
		leftSidePannel.add(statsTextPane);

		var boardPanel = new JPanel();
		this.getContentPane().add(boardPanel, BorderLayout.CENTER);

		initGameBoard(boardPanel);

		setVisible(true);
	}

	private void initGameBoard(JPanel boardPanel) {
		boardCanvas = new GameBoardCanvas(controller.getBoardModel());
		boardCanvas.addMouseListener(
				new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						controller.boardClicked(e);
					}
				});
		boardPanel.add(boardCanvas);
	}


	public void refreshBoard() {
		boardCanvas.repaint();
	}


	public void appendTextMessage(String text) {
		infoTextPane.setText(text);
	}
}
