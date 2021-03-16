package cz.sevrjukov.ttt.gui;

import cz.sevrjukov.ttt.game.Game;
import cz.sevrjukov.ttt.game.MovesListener;

import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameController implements ActionListener, MovesListener {

	private MainGameWindow window;
	private Game game = new Game();

	public GameController(MainGameWindow window) {
		this.window = window;
		game.setMovesListener(this);
	}

	public Game getGame() {
		return game;
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

	private void newGame() {
		game.newGame();
		refreshBoard();
	}

	@Override
	public void refreshBoard() {
		window.refreshBoard();
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
