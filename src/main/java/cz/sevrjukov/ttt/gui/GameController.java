package cz.sevrjukov.ttt.gui;

import cz.sevrjukov.ttt.game.Game;
import cz.sevrjukov.ttt.game.MovesListener;

import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static cz.sevrjukov.ttt.board.Board.HUMAN;

public class GameController implements ActionListener, MovesListener {

	private MainGameWindow window;
	private Game game = new Game();

	public GameController(MainGameWindow window) {
		this.window = window;
		game.setMovesListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		JButton btn = (JButton) e.getSource();

		String actionCommand = btn.getActionCommand();

		// buttons on game board
		if (actionCommand.startsWith("square_")) {
			squareClicked(btn);
			return;
		}

		// other buttons:
		if (btn == window.btnNewGame) {
			newGame();
		}


//		if (btn == window.btnMakeMove) {
//		//	game.makeNextMove();
//		}
	}

	private void newGame() {
		game.newGame();
		window.resetBoard();
	}

	private void squareClicked(JButton btn) {
		String actionCommand = btn.getActionCommand();

		String[] tokens = actionCommand.split("_");
		int sqNum = Integer.parseInt(tokens[1]);
		try {
			game.inputHumanMove(sqNum);
			displayMove(sqNum, HUMAN);
			game.findComputerMove();
		} catch (IllegalArgumentException i) {
			i.printStackTrace();
		}
	}


	@Override
	public void displayMove(int sq, int side) {
		window.displayMoveOnBoard(sq, side);
	}

	@Override
	public void resign() {
		window.appendTextMessage("Computer resigns");
	}

	@Override
	public void announceVictory() {
		window.appendTextMessage("Computer wins");
	}
}
