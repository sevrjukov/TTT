package cz.sevrjukov.ttt.board;

import java.util.Stack;

public class Board {

	public static int W = 11;
	public static int H = 13;
	public static int SIZE = W * H;

	public static int EMPTY = 0;
	public static int X = 1;
	public static int O = 2;

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

	public int [] getPosition() {
		return position;
	}

	public void move(int squareNum, int side) {
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
				position[i] = X;
			}
			if (c == 'o') {
				position[i] = O;
			}
			i++;
		}
	}


	public void printBoard() {
		System.out.println("");
		int i = 0;
		for (int square : position) {
			i++;
			if (square == EMPTY) {
				System.out.print(" . ");
			}
			if (square == X) {
				System.out.print(" X ");
			}
			if (square == O) {
				System.out.print(" O ");
			}
			if (i % W == 0) {
				System.out.println("");
			}
		}
	}

}
