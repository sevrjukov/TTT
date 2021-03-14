package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.board.Board.H;
import static cz.sevrjukov.ttt.board.Board.HUMAN;
import static cz.sevrjukov.ttt.board.Board.SIZE;
import static cz.sevrjukov.ttt.board.Board.W;

public class PositionEvaluator {

	final Map<Long, Integer> cache = new ConcurrentHashMap<>(100_000);
	private long cacheHits = 0;

	private MoveGenerator moveGenerator;
	private SequenceEvaluator evaluatorForComputer = new SequenceEvaluator(COMPUTER);
	private SequenceEvaluator evaluatorForHuman = new SequenceEvaluator(HUMAN);

	public PositionEvaluator(MoveGenerator moveGenerator) {
		this.moveGenerator = moveGenerator;
	}

	public int alphabeta(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {

		//	board.saveToFile();
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
			cacheHits++;
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

		int[] position = board.getPosition();
		// evaluation points for Cross and Zero players:
		int computerEvaluation = 0;
		int humanEvaluation = 0;

		try {

			/* feed verticals */
			for (int colNum = 0; colNum < W; colNum++) {

				evaluatorForComputer.newSequence();
				evaluatorForHuman.newSequence();

				int index = colNum;
				while (index < SIZE) {
					// feed it
					evaluatorForComputer.feedNextSquare(position[index]);
					evaluatorForHuman.feedNextSquare(position[index]);
					index += H;
				}
				computerEvaluation += evaluatorForComputer.getEvaluation();
				humanEvaluation += evaluatorForHuman.getEvaluation();
			}


			/* feed horizontals */
			evaluatorForComputer.newSequence();
			evaluatorForHuman.newSequence();

			for (int index = 0; index < SIZE; index++) {
				if (index % W == 0 && index > 0) {
					computerEvaluation += evaluatorForComputer.getEvaluation();
					humanEvaluation += evaluatorForHuman.getEvaluation();

					evaluatorForComputer.newSequence();
					evaluatorForHuman.newSequence();
				}
				evaluatorForComputer.feedNextSquare(position[index]);
				evaluatorForHuman.feedNextSquare(position[index]);
			}

			/* feed right-top diagonal */
			for (int p = 4; p < Board.W * 2 - 3; p++) {
				evaluatorForComputer.newSequence();
				evaluatorForHuman.newSequence();

				// through one diagonal:
				for (int q = Board.H; q >= 0; q--) {
					int x = p - q;
					if (x < 0 || x > W - 1 || q > H - 1) {
						continue;
					}
					int index = q * W + x;

					evaluatorForComputer.feedNextSquare(position[index]);
					evaluatorForHuman.feedNextSquare(position[index]);
				}
				computerEvaluation += evaluatorForComputer.getEvaluation();
				humanEvaluation += evaluatorForHuman.getEvaluation();
			}

			/* feed left-top diagonal */
			for (int p = 0; p < Board.W * 2 + 1; p++) {
				evaluatorForComputer.newSequence();
				evaluatorForHuman.newSequence();
				// through one diagonal:
				for (int q = Board.H; q >= 0; q--) {
					int x = p - q;
					int y = Board.H - q - 1;
					if (x < 0 || y < 0 || x > W - 1 || y > H - 1) {
						continue;
					}
					int index = y * W + x;
					evaluatorForComputer.feedNextSquare(position[index]);
					evaluatorForHuman.feedNextSquare(position[index]);
				}
				computerEvaluation += evaluatorForComputer.getEvaluation();
				humanEvaluation += evaluatorForHuman.getEvaluation();
			}

			return computerEvaluation - humanEvaluation;
		} catch (VictoryFound e) {
			if (e.getPlayer() == COMPUTER) {
				return Integer.MAX_VALUE;
			} else {
				return Integer.MIN_VALUE;
			}
		}
	}


	private boolean isFinalPosition(Board board) {
		// no more free squares to make next move (all squares occupied)
		return (board.getPosition().length == board.getMovesHistory().size());
	}

}
