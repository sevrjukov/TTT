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

	public static final int VICTORY = Integer.MAX_VALUE;
	public static final int DEFEAT = Integer.MIN_VALUE;

	final Map<Long, Integer> cache = new ConcurrentHashMap<>(100_000);
	private long cacheHits = 0;
	private long durationPrep = 0;
	private long durationCalc = 0;

	private MoveGenerator moveGenerator;
	private SequenceEvaluator evaluatorForComputer = new SequenceEvaluator(COMPUTER);
	private SequenceEvaluator evaluatorForHuman = new SequenceEvaluator(HUMAN);
	private WinPositionEvaluator winPositionEvaluator = new WinPositionEvaluator();

	public PositionEvaluator(MoveGenerator moveGenerator) {
		this.moveGenerator = moveGenerator;
	}



	/**
	 * Evaluation is always performed from the computer point of view.
	 */
	public int evaluatePositionOrGetCached(Board board) {
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

		int[] position = board.getPosition();

		// only evaluate lines that contain something
		int[] activatedLines = board.getActivatedLines();
		int minBound = board.getMinBound();
		int maxBound = board.getMaxBound();

		int computerEvaluation = 0;
		int humanEvaluation = 0;
		for (int lineNumber = 0; lineNumber < BoardSequences.LINES.length; lineNumber++) {
			int[] line;
			if (activatedLines[lineNumber] > 0) {
				line = BoardSequences.LINES[lineNumber];
			} else {
				continue;
			}
			evaluatorForComputer.newSequence();
			evaluatorForHuman.newSequence();
			for (int sqNum : line) {
				if (sqNum < minBound) {
					continue;
				}
				if (sqNum > maxBound) {
					break;
				}
				// this serves double purpose - feeds state machine, but also checks for immediate win/lose
				if (evaluatorForComputer.feedNextSquare(position[sqNum])) {
					return VICTORY;
				}
				if (evaluatorForHuman.feedNextSquare(position[sqNum])) {
					return DEFEAT;
				}
			}
			computerEvaluation += evaluatorForComputer.getEvaluation();
			humanEvaluation += evaluatorForHuman.getEvaluation();
		}
		return computerEvaluation - humanEvaluation;
	}

	/**
	 * Actual evaluation
	 */
	//TODO remove this
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


	protected boolean isFinalPosition(Board board) {
		// try to find a win (doesn't matter if a computer or human win)
		int[] checkedLineIds = BoardSequences.LINES_USAGE[board.getLastMove()];
		int[] position = board.getPosition();

		int minBound = board.getMinBound();
		int maxBound = board.getMaxBound();

		for (int lineId : checkedLineIds) {
			winPositionEvaluator.reset();
			int[] line = BoardSequences.LINES[lineId];
			for (int sqNum : line) {
				if (sqNum < minBound) {
					continue;
				}
				if (sqNum > maxBound) {
					break;
				}
				if (winPositionEvaluator.feedNextSquare(position[sqNum])) {
					return true;
				}
			}
		}
		// no more free squares to make next move (all squares occupied)
		return (board.getPosition().length == board.getMovesHistory().size());
	}


	@Override
	public String toString() {
		return "duration prep [ms] " + durationPrep + " duration calc [ms] " + durationCalc +
				"\n" +
				" cache hits " + cacheHits + " total cache size " + cache.size();
	}


}
