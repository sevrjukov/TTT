package cz.sevrjukov.ttt.game;

import cz.sevrjukov.ttt.board.Board;
import cz.sevrjukov.ttt.engine.MoveSearch;
import cz.sevrjukov.ttt.engine.MoveSearch.MoveEval;
import cz.sevrjukov.ttt.engine.PositionEvaluator;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.board.Board.HUMAN;
import static cz.sevrjukov.ttt.engine.MoveSearch.MOVE_RESIGN;
import static cz.sevrjukov.ttt.engine.PositionEvaluator.VICTORY;

public class Game {

	boolean calculating = false;
	boolean gameFinished = false;
	private Board board = new Board();
	private MoveSearch moveSearch = new MoveSearch();
	private PositionEvaluator positionEvaluator = new PositionEvaluator();

	private GameEventListener gameEventListener;
	boolean isFirstMove = true;

	public void newGame() {
		board.reset();
		moveSearch.reset();
		isFirstMove = true;
		gameFinished = false;
	}

	public Board getBoard() {
		return board;
	}

	public boolean isCalculating() {
		return calculating;
	}

	public void setGameEventsListener(GameEventListener gameEventListener) {
		this.gameEventListener = gameEventListener;
		moveSearch.setGameEventListener(gameEventListener);
	}

	public void inputHumanMove(int squareNum) {
		isFirstMove = false;
		if (calculating || gameFinished) {
			return;
		}
		try {
			board.makeMove(squareNum, HUMAN);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void findComputerMove() {
		if (gameFinished || calculating) {
			return;
		}
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
			// this evaluation is only in order to detect the winning position for the computer
			int eval = positionEvaluator.evaluatePositionOrGetCached(board);
			gameEventListener.refreshBoard();
			if (eval == VICTORY) {
				gameEventListener.announceVictory();
				gameFinished = true;
			}
		} else {
			gameEventListener.resign();
			gameFinished = true;
		}
	}

	public void makeFirstMove() {
		if (isFirstMove) {
			Random r = new Random();
			var move = r.nextInt(Board.SIZE) + 1;
			board.makeMove(move, COMPUTER);
			gameEventListener.refreshBoard();
			isFirstMove = false;
		}
	}
}
