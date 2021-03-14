package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.board.Board.HUMAN;

public class PositionEvaluator {

	final Map<Long, Integer> cache = new ConcurrentHashMap<>(100_000);

	private MoveGenerator moveGenerator;

	public PositionEvaluator(MoveGenerator moveGenerator) {
		this.moveGenerator = moveGenerator;
	}

	public int alphabeta(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {

		board.saveToFile();
		if (depth == 0 || isFinalPosition(board)) {
			return evaluatePositionOrGetCached(board);
		}

		if (maximizingPlayer) {
			int value = Integer.MIN_VALUE;
			int[] moves = moveGenerator.generateMoves(board);
			for (int moveSquare : moves) {
				board.makeMove(moveSquare, HUMAN);
				value = Math.max(value, alphabeta(board, depth - 1, alpha, beta, false));
				alpha = Math.max(alpha, value);
				board.undoLastMove();
				if (alpha >= beta) {
					break;
				}
			}
			return value;
		} else {
			int value = Integer.MAX_VALUE;
			int[] moves = moveGenerator.generateMoves(board);
			for (int moveSquare : moves) {
				board.makeMove(moveSquare, COMPUTER);
				value = Math.min(value, alphabeta(board, depth - 1, alpha, beta, true));
				beta = Math.min(beta, value);
				board.undoLastMove();
				if (beta <= alpha) {
					break;
				}
			}
			return value;
		}
	}

	/**
	 * Evaluation is always performed from the computer point of view.
	 */
	private int evaluatePositionOrGetCached(Board board) {
		var cachedValue = cache.get(board.getPositionHash());
		if (cachedValue != null) {
			return cachedValue;
		}
		// do actual search
		int result = evaluatePosition(board);
		cache.put(board.getPositionHash(), result);
		return result;
	}

	/**
	 * Actual evaluation
	 */
	private int evaluatePosition(Board board) {
		return 0;
	}


	private boolean isFinalPosition(Board board) {
		// no more free squares to make next move (all squares occupied)
		return (board.getPosition().length == board.getMovesHistory().size());
	}

}
