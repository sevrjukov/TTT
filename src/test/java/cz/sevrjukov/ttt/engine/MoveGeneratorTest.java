package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

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
		var moves = moveGenerator.generateMoves(board);

		for (int squareNum : moves) {
			board.makeMove(squareNum, Board.HUMAN);
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
		var moves = moveGenerator.generateMoves(board);

		for (int squareNum : moves) {
			board.makeMove(squareNum, Board.HUMAN);
		}
		board.printBoard();
	}

	private void testWithSquare(int... testSquares) {
		var board = new Board();
		for (int testSquare : testSquares) {
			board.makeMove(testSquare, Board.COMP);
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

		var board = new Board();
		var moveGenerator = new MoveGenerator();

		board.parseBoard("----------------"
				+ "---xoxx----"
				+ "---oxxoxxxoxx-ooo-oxxx--ooo-xxx");

		var start = System.currentTimeMillis();

		AtomicInteger howMany = new AtomicInteger(1_000_000);

		ExecutorService e = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		Future<Boolean> f = e.submit(
				() -> {
					while (howMany.decrementAndGet() > 0) {
						// this is cached, so it's fast
						moveGenerator.generateMoves(board);
					}
					return true;
				});

		f.get();

		var end = System.currentTimeMillis();
		var duration = end - start;

		System.out.println("it took " + duration + " ms");
	}


}
