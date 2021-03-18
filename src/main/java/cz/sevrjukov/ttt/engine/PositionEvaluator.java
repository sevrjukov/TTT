package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;

import java.util.HashMap;
import java.util.Map;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.board.Board.HUMAN;

public class PositionEvaluator {

	public static final int VICTORY = Integer.MAX_VALUE;
	public static final int DEFEAT = Integer.MIN_VALUE;
	private static final int MAX_CACHE_SIZE = 5_000_000;

	private final Map<Long, Integer> cache = new HashMap<>(1_000_000);
	private long cacheHits = 0;
	private long evalRequests = 0;

	private SequenceEvaluator evaluatorForComputer = new SequenceEvaluator(COMPUTER);
	private SequenceEvaluator evaluatorForHuman = new SequenceEvaluator(HUMAN);
	private WinPositionEvaluator winPositionEvaluator = new WinPositionEvaluator();

	/**
	 * Evaluation is always performed from the computer point of view.
	 */
	public int evaluatePositionOrGetCached(Board board) {
		evalRequests++;
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


	public void resetCache() {
		cacheHits = 0;
		cache.clear();
	}

	public void resetStats() {
		cacheHits = 0;
		evalRequests = 0;
	}

	public int getCacheSize() {
		return cache.size();
	}

	public long cacheHits() {
		return cacheHits;
	}

	public long getEvalRequests() {
		return evalRequests;
	}


	public void clearCacheIfNeeded() {
		if (cache.size() > MAX_CACHE_SIZE) {
			cache.clear();
		}
	}
}
