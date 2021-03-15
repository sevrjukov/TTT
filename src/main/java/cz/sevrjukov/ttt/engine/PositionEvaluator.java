package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import cz.sevrjukov.ttt.engine.BoardSequences.Line;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.board.Board.H;
import static cz.sevrjukov.ttt.board.Board.HUMAN;
import static cz.sevrjukov.ttt.board.Board.SIZE;
import static cz.sevrjukov.ttt.board.Board.W;

public class PositionEvaluator {

	public static final int VICTORY = Integer.MAX_VALUE;
	public static final int DEFEAT = Integer.MIN_VALUE;

	final Map<Long, Integer> cache = new ConcurrentHashMap<>(100_000);
	private long cacheHits = 0;
	private long durationPrep = 0;
	private long durationCalc = 0;

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
				board.makeMove(moveSquare, COMPUTER);
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
				board.makeMove(moveSquare, HUMAN);
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

	protected int evaluatePosition(Board board) {
		// only evaluate lines that contain something
		int [] position = board.getPosition();

		long start = System.currentTimeMillis();

		final Set<Line> evaluatedLines = new HashSet<>();


		//TODO possible remedy - board keeps info itself and can return "list of activated lines"
		board.getMovesHistory().forEach(
				move -> {
					var lines = BoardSequences.ASSOCIATIVE_INDEXES.get(move.squareNum);
					evaluatedLines.addAll(lines);
				});

		durationPrep += System.currentTimeMillis() - start;
		start = System.currentTimeMillis();

		int computerEvaluation = 0;
		int humanEvaluation = 0;
		var it = evaluatedLines.stream().iterator();
		try {
			while (it.hasNext()) {
				var line = it.next();
				evaluatorForComputer.newSequence();
				evaluatorForHuman.newSequence();
				for (int sqNum : line.getSquares()) {
					if (sqNum < board.getMinBound()) {
						continue;
					}
					if (sqNum > board.getMaxBound()) {
						break;
					}
					evaluatorForComputer.feedNextSquare(position[sqNum]);
					evaluatorForHuman.feedNextSquare(position[sqNum]);
				}
				computerEvaluation += evaluatorForComputer.getEvaluation();
				humanEvaluation += evaluatorForHuman.getEvaluation();
			}
			durationCalc += System.currentTimeMillis() - start;
			return computerEvaluation - humanEvaluation;
		} catch (VictoryFound e) {
			if (e.getPlayer() == COMPUTER) {
				return VICTORY;
			} else {
				return DEFEAT;
			}
		}
	}

	/**
	 * Actual evaluation
	 */
	protected int evaluatePositionOld(Board board) {

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
					index += W;
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
				return VICTORY;
			} else {
				return DEFEAT;
			}
		}
	}


	private boolean isFinalPosition(Board board) {
		// no more free squares to make next move (all squares occupied)
		return (board.getPosition().length == board.getMovesHistory().size());
	}


	@Override
	public String toString() {
		return "duration prep [ms] " + durationPrep + " duration calc [ms] " + durationCalc;
	}


}
