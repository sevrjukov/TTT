package cz.sevrjukov.ttt.gui;

import cz.sevrjukov.ttt.board.Board;
import cz.sevrjukov.ttt.game.Game;
import cz.sevrjukov.ttt.game.GameEventListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class GameController implements ActionListener, GameEventListener {

	private MainGameWindow window;
	private Game game = new Game();
	private BoardModel boardModel = new BoardModel();

	public GameController(MainGameWindow window) {
		this.window = window;
		game.setGameEventsListener(this);
	}

	public void init() {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				printStats();
			}
		}, 1000, 1000);
		game.newGame();
	}

	public BoardModel getBoardModel() {
		return boardModel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		JButton btn = (JButton) e.getSource();

		if (btn == window.btnNewGame) {
			newGame();
		}

		if (btn == window.btnMakeMove) {
			game.makeFirstMove();
		}

		if (btn == window.btnSaveGames) {
			promptToSaveHistory();
		}
	}

	public void boardClicked(MouseEvent e) {
		int xCoord = e.getX();
		int yCoord = e.getY();
		int sqX = xCoord / GameBoardCanvas.FIELD_SIZE;
		int sqy = yCoord / GameBoardCanvas.FIELD_SIZE;
		int sqNum = sqy * GameBoardCanvas.NUM_CELLS + sqX;

		if (game.inputHumanMove(sqNum)) {
			refreshBoard();
			game.findComputerMove();
		}
	}

	private void refreshBoardModel() {
		var board = game.getBoard();
		var movesHistory = board.getMovesHistory();
		boardModel.getMovesList().clear();
		movesHistory.forEach(move -> boardModel.getMovesList().add(move));

		boardModel.setWinningPosition(board.isWinningPosition());
		boardModel.setWinningSequence(board.getWinningSequence());
	}

	private void newGame() {
		if (!game.isGameFinished()) {

			String[] options = {"Continue playing", "Resign"};
			int answer = JOptionPane.showOptionDialog(window, "What do you want to do?", "Game is not finished",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]);

			if (answer == 1) {
				game.humanResigns();
				game.newGame();
				refreshBoard();
			}
		} else {
			game.newGame();
			refreshBoard();
		}
	}

	@Override
	public void refreshBoard() {
		refreshBoardModel();
		window.refreshBoard();
	}

	@Override
	public void resign(int player) {
		if (player == Board.COMPUTER) {
			window.appendTextMessage("Computer resigns");
			JOptionPane.showMessageDialog(window, "Computer resigns");
		} else {
			window.appendTextMessage("You resigned");
		}
	}

	@Override
	public void announceVictory() {
		window.appendTextMessage("Computer wins");
		JOptionPane.showMessageDialog(window, "Computer wins");
	}

	@Override
	public void printInfo(String info) {
		System.out.println(info);
		window.appendTextMessage(info);
	}


	public void printStats() {
		var stats = game.getStats();
		window.statsTextPane.setText(stats);
	}

	private void promptToSaveHistory() {
		try {

			var fc = createSaveHistoryFileChooser();

			var returnVal = fc.showSaveDialog(window);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				var file = fc.getSelectedFile();
				System.out.println("Saving games to file " + file.getAbsolutePath());
				saveHistory(file);
				printInfo("Saved games to file " + file.getName());
			}

		} catch (IOException ex) {
			ex.printStackTrace();
			printInfo("Error saving file history");
		}
	}

	private JFileChooser createSaveHistoryFileChooser() {
		var fileName = "games_" + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".json";
		final JFileChooser fc = new JFileChooser() {
			@Override
			public void approveSelection() {
				File f = getSelectedFile();
				if (f.exists() && getDialogType() == SAVE_DIALOG) {
					int result = JOptionPane.showConfirmDialog(this,
							"The file exists, overwrite?", "Existing file",
							JOptionPane.YES_NO_CANCEL_OPTION);
					switch (result) {
						case JOptionPane.YES_OPTION:
							super.approveSelection();
							return;
						case JOptionPane.CANCEL_OPTION:
							cancelSelection();
							return;
						default:
							return;
					}
				}
				super.approveSelection();
			}
		};
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setSelectedFile(new File(fileName));
		return fc;
	}

	private void saveHistory(File f) throws IOException {
		var historyJson = game.getHistory().exportToJson();
		Files.writeString(f.toPath(), historyJson, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
	}


}
