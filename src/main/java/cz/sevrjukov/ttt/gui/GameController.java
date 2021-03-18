package cz.sevrjukov.ttt.gui;

import cz.sevrjukov.ttt.board.Board;
import cz.sevrjukov.ttt.game.Game;
import cz.sevrjukov.ttt.game.GameEventListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

public class GameController implements ActionListener, GameEventListener {

	private MainGameWindow window;
	private Game game = new Game();
	private BoardModel boardModel = new BoardModel();
	private TimerTask refreshStatsTask;

	public GameController(MainGameWindow window) {
		this.window = window;
		game.setGameEventsListener(this);

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				printStats();
			}
		}, 1000, 1000);
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
	}

	public void boardClicked(MouseEvent e) {
		int xCoord = e.getX();
		int yCoord = e.getY();
		int sqX = xCoord / GameBoardCanvas.FIELD_SIZE;
		int sqy = yCoord / GameBoardCanvas.FIELD_SIZE;
		int sqNum = sqy * GameBoardCanvas.NUM_CELLS + sqX;
		System.out.println("sqx " + sqX + " sqy " + sqy);

		if (!game.isCalculating()) {
			game.inputHumanMove(sqNum);
			refreshBoard();
			// refresh moves model for GUI
			game.findComputerMove();
		}
	}

	private void refreshBoardModel() {
		var movesHistory = game.getBoard().getMovesHistory();
		boardModel.getMovesList().clear();
		movesHistory.forEach(move -> boardModel.getMovesList().add(move));
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

}
