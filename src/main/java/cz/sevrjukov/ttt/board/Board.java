package cz.sevrjukov.ttt.board;

import java.util.Stack;

public class Board {

	public static int W = 11;
	public static int H = 13;
	public static int SIZE = W * H;

	public static int EMPTY = 0;
	public static int COMP = 1;
	public static int HUMAN = 2;

	private int[] position;

	private Stack<Move> movesHistory = new Stack<>();

	public Board() {
		clearBoard();
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

	public void parseBoard(String boardNotation) {
		boardNotation = boardNotation.toLowerCase().trim().replaceAll("\\s+", "");
		if (boardNotation.length() > SIZE) {
			throw new IllegalArgumentException("board notation invalid");
		}
		int i = 0;
		for (char c : boardNotation.toCharArray()) {
			if (c == 'x') {
				position[i] = COMP;
			}
			if (c == 'o') {
				position[i] = HUMAN;
			}
			i++;
		}
	}


	public void printBoard() {
		System.out.println();
		int i = 0;
		for (int square : position) {
			i++;
			if (square == EMPTY) {
				System.out.print(" . ");
			}
			if (square == COMP) {
				System.out.print(" X ");
			}
			if (square == HUMAN) {
				System.out.print(" O ");
			}
			if (i % W == 0) {
				System.out.println();
			}
		}
	}

}
