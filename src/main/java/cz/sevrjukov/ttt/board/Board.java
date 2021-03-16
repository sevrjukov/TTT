package cz.sevrjukov.ttt.board;

import cz.sevrjukov.ttt.engine.BoardSequences;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Stack;

public class Board {

	public static int W = 19;
	public static int H = 19;
	public static int SIZE = W * H;

	public static int EMPTY = 0;
	public static int COMPUTER = 1;
	public static int HUMAN = 2;
	public static int UNDEFINED = -1;

	private int[] position;
	private int minBound = Integer.MAX_VALUE;
	private int maxBound = Integer.MIN_VALUE;

	private Stack<Move> movesHistory = new Stack<>();
	// for faster access, no need to access the stack
	private int lastMoveSquare = -1;

	private int[] activatedLines = new int[BoardSequences.LINES.length];

	private boolean debug = false;

	private long timeTotalMakeMove = 0;
	private long timeTotalUndoMove = 0;


	public Board() {
		reset();
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void reset() {
		position = new int[SIZE];
		for (int i = 0; i < SIZE; i++) {
			position[i] = EMPTY;
		}
		movesHistory.clear();
		lastMoveSquare = -1;
		minBound = Integer.MAX_VALUE;
		maxBound = Integer.MIN_VALUE;
		Arrays.fill(activatedLines, 0);
		timeTotalMakeMove = 0;
		timeTotalUndoMove = 0;
	}

	public int[] getPosition() {
		return position;
	}

	public long getPositionHash() {
		long result = 1;
		for (int element : position) {
			result = 31 * result + element;
		}
		return result;
	}

	public long getPositionHashShallow() {
		long result = 1;
		for (int element : position) {
			result = 31 * result + (element > 0 ? 1 : 0);
		}
		return result;
	}

	public int getMaxBound() {
		return maxBound;
	}

	public int getMinBound() {
		return minBound;
	}

	public int getLastMove() {
		return lastMoveSquare;
	}

	public void makeMove(int squareNum, int side) {
		var start = System.currentTimeMillis();
		if (position[squareNum] != EMPTY) {
			throw new IllegalArgumentException("Cannot execute move " + squareNum + " " + side);
		}

		movesHistory.push(new Move(squareNum, side, maxBound, minBound));
		lastMoveSquare = squareNum;
		position[squareNum] = side;
		// calculate new bounds
		maxBound = Math.max(maxBound, squareNum + W + 1);
		minBound = Math.min(minBound, squareNum - W - 1);

		// activate lines
		int[] newlyActivatedLines = BoardSequences.LINES_USAGE[squareNum];
		for (int lineNumber : newlyActivatedLines) {
			activatedLines[lineNumber] = activatedLines[lineNumber] + 1;
		}
		timeTotalMakeMove += System.currentTimeMillis() - start;
	}

	public void undoLastMove() {
		var start = System.currentTimeMillis();
		var lastMove = movesHistory.pop();
		position[lastMove.squareNum] = EMPTY;
		lastMoveSquare = movesHistory.peek().squareNum;

		// restore bounds
		maxBound = lastMove.maxBound;
		minBound = lastMove.minBound;

		// deactivate lines
		int[] deactivatedLines = BoardSequences.LINES_USAGE[lastMove.squareNum];
		for (int lineNumber : deactivatedLines) {
			activatedLines[lineNumber] = activatedLines[lineNumber] - 1;
		}
		timeTotalUndoMove += System.currentTimeMillis() - start;
	}

	public Stack<Move> getMovesHistory() {
		return movesHistory;
	}

	public void parseBoard(String boardNotation) {
		boardNotation = boardNotation.toLowerCase().trim().replaceAll("\\s+", "");
		if (boardNotation.length() > SIZE) {
			throw new IllegalArgumentException("board notation invalid");
		}
		int i = 0;
		for (char c : boardNotation.toCharArray()) {
			if (c == 'x') {
				makeMove(i, COMPUTER);
			}
			if (c == 'o') {
				makeMove(i, HUMAN);
			}
			i++;
		}
	}

	public void printBoard() {
		System.out.println();
		System.out.println(toString());
	}

	public void saveToFile() {
		if (!debug) {
			throw new IllegalArgumentException("Board will not save to file, not in debug mode!");
		}
		try {
			Files.writeString(Paths.get("/tmp", "board.txt"), toString(), StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		var builder = new StringBuilder();

		int i = 0;
		for (int square : position) {
			i++;
			if (square == EMPTY) {
				builder.append(" . ");
			}
			if (square == COMPUTER) {
				builder.append(" X ");
			}
			if (square == HUMAN) {
				builder.append(" O ");
			}
			if (i % W == 0) {
				builder.append("\n");
			}
		}
		builder.append(" total time make moves [ms] " + timeTotalMakeMove);
		builder.append(" total time undo moves [ms] " + timeTotalUndoMove);
		return builder.toString();
	}

	public int[] getActivatedLines() {
		return activatedLines;
	}
}
