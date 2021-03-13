package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import org.junit.Test;

public class MoveGeneratorTest {

	@Test
	public void testBasicGeneration() {

		testWithSquare(35);
	}

	@Test
	public void testCorners() {
		testWithSquare(0, 10, 132, 142);
	}

	@Test
	public void testSides() {
		testWithSquare(8, 22, 65, 138);
	}

	@Test
	public void testComplexPosition() {
		var board = new Board();
		board.parseBoard("x-----------x-xx----"
				+ "---xx-xxx-xx-xx-xxx--xxxxx-xxx");

		var moveGenerator = new MoveGenerator();
		var moves = moveGenerator.generateMoves(board.getPosition());

		for (int squareNum : moves) {
			board.makeMove(squareNum, Board.O);
		}
		board.printBoard();
	}

	@Test
	public void testRealPosition() {
		var board = new Board();
		board.parseBoard("----------------"
				+ "---xoxx----"
				+ "---oxxoxxxoxx-ooo-oxxx--ooo-xxx");

		board.printBoard();
		var moveGenerator = new MoveGenerator();
		var moves = moveGenerator.generateMoves(board.getPosition());

		for (int squareNum : moves) {
			board.makeMove(squareNum, Board.O);
		}
		board.printBoard();
	}

	private void testWithSquare(int... testSquares) {
		var board = new Board();
		for (int testSquare : testSquares) {
			board.makeMove(testSquare, Board.X);
		}

		var moveGenerator = new MoveGenerator();
		var moves = moveGenerator.generateMoves(board.getPosition());

		for (int squareNum : moves) {
			board.makeMove(squareNum, Board.O);
		}
		board.printBoard();
	}


}
