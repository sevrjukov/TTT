package cz.sevrjukov.ttt.game;

import cz.sevrjukov.ttt.board.Board;
import cz.sevrjukov.ttt.engine.MoveSearch;
import cz.sevrjukov.ttt.engine.MoveSearch.MoveEval;
import cz.sevrjukov.ttt.engine.PositionEvaluator;
import cz.sevrjukov.ttt.game.history.GameHistoryController;

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
	private GameHistoryController history = new GameHistoryController();

	private GameEventListener gameEventListener;
	boolean isFirstMove = true;

	public void newGame() {
		board.reset();
		moveSearch.reset();
		isFirstMove = true;
		gameFinished = false;
		gameEventListener.printInfo("New game");
		history.newGame();
	}

	public Board getBoard() {
		return board;
	}

	public boolean isCalculating() {
		return calculating;
	}

	public boolean isGameFinished() {
		return gameFinished;
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
			history.recordMove(squareNum, HUMAN);
		} catch (Exception ex) {
			//TODO this is ugly
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
			history.recordMove(computerMove.sqNum, COMPUTER, computerMove.eval);

			// this evaluation is only in order to detect the winning position for the computer
			int eval = positionEvaluator.evaluatePositionOrGetCached(board);

			gameEventListener.refreshBoard();
			if (eval == VICTORY) {
				gameEventListener.announceVictory();
				gameFinished = true;
				history.recordVictory(COMPUTER);
			}
		} else {
			gameEventListener.resign(COMPUTER);
			history.recordResign(COMPUTER);
			gameFinished = true;
		}
	}

	public void makeFirstMove() {
		if (isFirstMove) {
			var squareNum = moveSearch.getMoveGenerator().generateFirstMove();
			board.makeMove(squareNum, COMPUTER);
			history.recordMove(squareNum, COMPUTER);
			gameEventListener.refreshBoard();
			isFirstMove = false;
		}
	}

	public String getStats() {
		return moveSearch.getStats();
	}

	public void humanResigns() {
		gameEventListener.resign(HUMAN);
		history.recordResign(HUMAN);
		gameFinished = true;
	}

	public GameHistoryController getHistory() {
		return history;
	}
}
