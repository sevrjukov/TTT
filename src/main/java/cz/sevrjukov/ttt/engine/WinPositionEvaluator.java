package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;

public class WinPositionEvaluator {

	private static final int WIN_COUNT = 5;
	private int lastSquare = Board.UNDEFINED;
	private int consecutiveSquares = 1;

	public void reset() {
		lastSquare = Board.UNDEFINED;
		consecutiveSquares = 1;
	}


	public boolean feedNextSquare(int square) {
		if (square > 0 && square == lastSquare) {
			consecutiveSquares++;
		} else {
			consecutiveSquares = 1;
		}
		lastSquare = square;
		return (consecutiveSquares >= WIN_COUNT);
	}

}
