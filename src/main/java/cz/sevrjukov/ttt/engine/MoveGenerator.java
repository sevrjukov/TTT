package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cz.sevrjukov.ttt.board.Board.EMPTY;
import static cz.sevrjukov.ttt.board.Board.H;
import static cz.sevrjukov.ttt.board.Board.SIZE;
import static cz.sevrjukov.ttt.board.Board.W;

public class MoveGenerator {

	private static final int MAX_CACHE_SIZE = 500_000;
	final Map<Long, int[]> cache = new ConcurrentHashMap<>(100_000);
	private long cacheHits = 0;
	private long genRequests = 0;

	public int[] generateMoves(Board board) {

		genRequests++;
		var cacheKey = board.getPositionHashShallow();
		var cachedResult = cache.get(cacheKey);
		if (cachedResult != null) {
			cacheHits++;
			return cachedResult;
		}

		var position = board.getPosition();

		// index-based array. If true, than move is possible on that position
		boolean[] tmp = new boolean[SIZE];
		for (int i = 0; i < SIZE; i++) {
			tmp[i] = false;
		}
		for (int sqNum = 0; sqNum < SIZE; sqNum++) {
			if (position[sqNum] != EMPTY) {
				// s - w - 1
				if (validate(sqNum, sqNum - W - 1, position, tmp)) {
					tmp[sqNum - W - 1] = true;
				}
				// s - w
				if (validate(sqNum, sqNum - W, position, tmp)) {
					tmp[sqNum - W] = true;
				}
				// s - w + 1
				if (validate(sqNum, sqNum - W + 1, position, tmp)) {
					tmp[sqNum - W + 1] = true;
				}
				// s - 1
				if (validate(sqNum, sqNum - 1, position, tmp)) {
					tmp[sqNum - 1] = true;
				}
				// s + 1
				if (validate(sqNum, sqNum + 1, position, tmp)) {
					tmp[sqNum + 1] = true;
				}
				// s + w - 1
				if (validate(sqNum, sqNum + W - 1, position, tmp)) {
					tmp[sqNum + W - 1] = true;
				}
				// s + w
				if (validate(sqNum, sqNum + W, position, tmp)) {
					tmp[sqNum + W] = true;
				}
				// s + w + 1
				if (validate(sqNum, sqNum + W + 1, position, tmp)) {
					tmp[sqNum + W + 1] = true;
				}
			}
		}

		// count "true" values
		int resultArrSize = 0;
		for (boolean val : tmp) {
			if (val) {
				resultArrSize++;
			}
		}

		int[] result = new int[resultArrSize];
		int index = 0;
		for (int i = 0; i < SIZE; i++) {
			if (tmp[i]) {
				result[index] = i;
				index++;
			}
		}

		cache.put(cacheKey, result);
		return result;
	}

	private boolean validate(int squareNum, int moveSquare, int[] position, boolean[] tmp) {
		if (moveSquare < 0) {
			return false;
		}
		if (moveSquare > SIZE - 1) {
			return false;
		}
		if (position[moveSquare] != EMPTY) {
			return false;
		}
		// already marked as a valid move
		if (tmp[moveSquare]) {
			return true;
		}
		// square coordinates
		int sqX = squareNum % W;
		int sqY = squareNum / H + 1;

		// move coordinates
		int moveX = moveSquare % W;
		int moveY = moveSquare / H + 1;

		return (Math.abs(sqX - moveX) < 2 && Math.abs(sqY - moveY) < 2);
	}

	public void resetCache() {
		cache.clear();
		resetStats();
	}

	public void resetStats() {
		cacheHits = 0;
		genRequests = 0;
	}

	public int getCacheSize() {
		return cache.size();
	}

	public long cacheHits() {
		return cacheHits;
	}

	public long getGenRequests() {
		return genRequests;
	}

	public void clearCacheIfNeeded() {
		if (cache.size() > MAX_CACHE_SIZE) {
			cache.clear();
		}
	}
}
