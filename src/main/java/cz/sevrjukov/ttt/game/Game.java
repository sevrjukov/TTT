package cz.sevrjukov.ttt.game;

import cz.sevrjukov.ttt.board.Board;
import cz.sevrjukov.ttt.engine.MoveSearch;
import cz.sevrjukov.ttt.engine.MoveSearch.MoveEval;

import java.util.concurrent.CompletableFuture;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.board.Board.HUMAN;
import static cz.sevrjukov.ttt.engine.MoveSearch.MOVE_RESIGN;
import static cz.sevrjukov.ttt.engine.PositionEvaluator.VICTORY;

public class Game {

	boolean calculating = false;
	private Board board = new Board();
	private MoveSearch moveSearch = new MoveSearch();
	private MovesListener movesListener;
	boolean isFirstMove = true;

	public void newGame() {
		board.reset();
		moveSearch.reset();
		isFirstMove = true;
	}


	public void setMovesListener(MovesListener movesListener) {
		this.movesListener = movesListener;
	}

	public void inputHumanMove(int squareNum) {
		isFirstMove = false;
		if (calculating) {
			return;
		}
		try {
			board.makeMove(squareNum, HUMAN);
			movesListener.displayMove(squareNum, HUMAN);
			findComputerMove();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void findComputerMove() {
		calculating = true;
		CompletableFuture
				.supplyAsync(() -> moveSearch.findNextMove(board))
				.thenAccept(this::processFoundMove);
		isFirstMove = false;
	}

	private void processFoundMove(MoveEval computerMove) {
		calculating = false;
		if (computerMove.sqNum != MOVE_RESIGN) {
			board.makeMove(computerMove.sqNum, COMPUTER);
			movesListener.displayMove(computerMove.sqNum, COMPUTER);
		} else {
			movesListener.resign();
		}

		if (computerMove.eval == VICTORY) {
			movesListener.announceVictory();
		}
	}

	public void makeNextMove() {
		if (isFirstMove) {
			board.makeMove(179, COMPUTER);
			movesListener.displayMove(179, COMPUTER);
			isFirstMove = false;
		}
	}
}
