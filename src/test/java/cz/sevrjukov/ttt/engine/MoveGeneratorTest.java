package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class MoveGeneratorTest {


	@Test
	public void testOptimizationAlreadFound() {
		System.out.println("Optimization - already found");
		testWithSquare(21, 22);
	}

	@Test
	public void testBasicGeneration() {
		System.out.println("Basic");
		testWithSquare(121);
	}

	@Test
	public void testCorners() {
		System.out.println("Corners");
		testWithSquare(0, 18, 342, 360);
	}

	@Test
	public void testSides() {
		System.out.println("Sides");
		testWithSquare(8, 171, 189, 350);
	}

	@Test
	public void testSubCorners() {
		System.out.println("Subcorners");
		testWithSquare(20, 36, 324, 340);
	}

	@Test
	public void testComplexPosition() {
		System.out.println("Complex position");
		var board = new Board();
		board.parseBoard("x-----------x-xx----"
				+ "---xx-xxx-xx-xx-xxx--xxxxx-xxx");
		board.makeMove(293, Board.COMPUTER);

		var moveGenerator = new MoveGenerator();
		var moves = moveGenerator.generateMoves(board);

		for (int squareNum : moves) {
			board.makeMove(squareNum, Board.HUMAN);
		}
		board.printBoard();
	}

	@Test
	public void testRealPosition() {
		System.out.println("Real position");
		var board = new Board();
		board.parseBoard("----------------"
				+ "---xoxx----"
				+ "---oxxoxxxoxx-ooo-oxxx--ooo-xxx");

		board.printBoard();
		var moveGenerator = new MoveGenerator();
		var moves = moveGenerator.generateMoves(board);

		for (int squareNum : moves) {
			board.makeMove(squareNum, Board.HUMAN);
		}
		board.printBoard();
	}

	private void testWithSquare(int... testSquares) {
		var board = new Board();
		for (int testSquare : testSquares) {
			board.makeMove(testSquare, Board.COMPUTER);
		}

		var moveGenerator = new MoveGenerator();
		var moves = moveGenerator.generateMoves(board);

		for (int squareNum : moves) {
			board.makeMove(squareNum, Board.HUMAN);
		}
		board.printBoard();
	}

	@Test
	public void testGenerationSpeed() throws ExecutionException, InterruptedException {

		System.out.println("Cache test");
		var board = new Board();
		var moveGenerator = new MoveGenerator();

		board.parseBoard("----------------"
				+ "---xoxx----"
				+ "---oxxoxxxoxx-ooo-oxxx--ooo-xxx");

		var start = System.currentTimeMillis();

		for (int i = 0; i < 1_000_000; i++) {
			moveGenerator.generateMoves(board);
		}

		var end = System.currentTimeMillis();
		var duration = end - start;

		System.out.println("it took " + duration + " ms");
	}


}
