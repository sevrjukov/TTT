package cz.sevrjukov.ttt.gui;

import cz.sevrjukov.ttt.game.Game;
import cz.sevrjukov.ttt.game.GameEventListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class GameController implements ActionListener, GameEventListener {

	private MainGameWindow window;
	private Game game = new Game();
	private BoardModel boardModel = new BoardModel();

	public GameController(MainGameWindow window) {
		this.window = window;
		game.setGameEventsListener(this);
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
		game.newGame();
		refreshBoard();
	}

	@Override
	public void refreshBoard() {
		refreshBoardModel();
		window.refreshBoard();
	}

	@Override
	public void resign() {
		window.appendTextMessage("Computer resigns");
		JOptionPane.showMessageDialog(window, "Computer resigns");
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

}
