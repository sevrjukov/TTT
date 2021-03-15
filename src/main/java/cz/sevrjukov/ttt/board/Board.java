package cz.sevrjukov.ttt.board;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

	private Stack<Move> movesHistory = new Stack<>();

	private boolean debug = false;

	public Board() {
		clearBoard();
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void clearBoard() {
		position = new int[SIZE];
		for (int i = 0; i < SIZE; i++) {
			position[i] = EMPTY;
		}
		movesHistory.clear();
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

	public void makeMove(int squareNum, int side) {
		if (position[squareNum] != EMPTY) {
			throw new IllegalArgumentException("Cannot execute move " + squareNum + " " + side);
		}
		movesHistory.push(new Move(squareNum, side));
		position[squareNum] = side;
	}

	public void undoLastMove() {
		var lastMove = movesHistory.pop();
		position[lastMove.squareNum] = EMPTY;
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
			throw  new IllegalArgumentException("Board will not save to file, not in debug mode!");
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
		return builder.toString();
	}

}
