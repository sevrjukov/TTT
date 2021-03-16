package cz.sevrjukov.ttt.game;

import cz.sevrjukov.ttt.board.Board;
import cz.sevrjukov.ttt.engine.MoveSearch;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.board.Board.HUMAN;
import static cz.sevrjukov.ttt.engine.MoveSearch.MOVE_RESIGN;
import static cz.sevrjukov.ttt.engine.PositionEvaluator.VICTORY;

public class Game {

	boolean calculating = false;
	private Board board = new Board();
	private MoveSearch moveSearch = new MoveSearch();
	private MovesListener movesListener;

	public void newGame() {
		board.reset();
		moveSearch.reset();
	}

	public void setMovesListener(MovesListener movesListener) {
		this.movesListener = movesListener;
	}

	public void inputHumanMove(int squareNum) {
		if (calculating) {
			return;
		}
		try {
			board.makeMove(squareNum, HUMAN);
			movesListener.displayMove(squareNum, HUMAN);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void findComputerMove() {
		var calculationThread = new Thread(() -> {
			calculating = true;
			var computerMove = moveSearch.findNextMove(board);
			if (computerMove.sqNum != MOVE_RESIGN) {
				board.makeMove(computerMove.sqNum, COMPUTER);
				movesListener.displayMove(computerMove.sqNum, COMPUTER);
			} else {
				movesListener.resign();
			}

			if (computerMove.eval == VICTORY) {
				movesListener.announceVictory();
			}
			calculating = false;
		});
		calculationThread.start();
	}

}
