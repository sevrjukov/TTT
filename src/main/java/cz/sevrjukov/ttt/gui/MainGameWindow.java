package cz.sevrjukov.ttt.gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class MainGameWindow extends JFrame {

	protected JButton btnNewGame;
	protected JButton btnMakeMove;
	protected JTextPane textPane;
	private JPanel boardPanel;
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
		this.setBounds(100, 100, 890, 680);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBag = new GridBagLayout();
		gridBag.columnWidths = new int[]{286, 672, 0};
		gridBag.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gridBag.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBag.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
				Double.MIN_VALUE};
		this.getContentPane().setLayout(gridBag);

		btnNewGame = new JButton("New game");
		btnNewGame.addActionListener(controller);
		GridBagConstraints gbc_btnResetGame = new GridBagConstraints();
		gbc_btnResetGame.anchor = GridBagConstraints.EAST;
		gbc_btnResetGame.insets = new Insets(0, 0, 5, 5);
		gbc_btnResetGame.gridx = 0;
		gbc_btnResetGame.gridy = 1;
		this.getContentPane().add(btnNewGame, gbc_btnResetGame);

		btnMakeMove = new JButton("Make first move");
		btnMakeMove.addActionListener(controller);

		GridBagConstraints gbc_rdbtnYouVs = new GridBagConstraints();
		gbc_rdbtnYouVs.anchor = GridBagConstraints.WEST;
		gbc_rdbtnYouVs.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnYouVs.gridx = 1;
		gbc_rdbtnYouVs.gridy = 1;

		GridBagConstraints gbc_rdbtnTwoPlayers = new GridBagConstraints();
		gbc_rdbtnTwoPlayers.anchor = GridBagConstraints.WEST;
		gbc_rdbtnTwoPlayers.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnTwoPlayers.gridx = 1;
		gbc_rdbtnTwoPlayers.gridy = 2;
		GridBagConstraints gbc_btnEvaluate = new GridBagConstraints();
		gbc_btnEvaluate.anchor = GridBagConstraints.EAST;
		gbc_btnEvaluate.insets = new Insets(0, 0, 5, 5);
		gbc_btnEvaluate.gridx = 0;
		gbc_btnEvaluate.gridy = 3;
		this.getContentPane().add(btnMakeMove, gbc_btnEvaluate);

		textPane = new JTextPane();

		GridBagConstraints gbc_textPane = new GridBagConstraints();
		gbc_textPane.insets = new Insets(0, 0, 5, 5);
		gbc_textPane.fill = GridBagConstraints.BOTH;
		gbc_textPane.gridx = 0;
		gbc_textPane.gridy = 4;
		getContentPane().add(textPane, gbc_textPane);

		boardPanel = new JPanel();
		GridBagConstraints gbc_boardPanel = new GridBagConstraints();
		gbc_boardPanel.insets = new Insets(0, 0, 5, 0);
		gbc_boardPanel.fill = GridBagConstraints.BOTH;
		gbc_boardPanel.gridx = 1;
		gbc_boardPanel.gridy = 4;
		this.getContentPane().add(boardPanel, gbc_boardPanel);

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
		textPane.setText(text);
	}
}
