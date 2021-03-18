package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class PositionEvaluatorTest {


	@Test
	public void testFourInARow() {
		var board = new Board();
		board.setDebug(true);

		board.parseBoard("----------------"
				+ "--------oooo-----------"
				+ "------------");
		board.printBoard();

//		board.printBoard();
		var moveGenerator = new MoveGenerator();
		var positionEvaluator = new PositionEvaluator();
		var evaluation = positionEvaluator.evaluatePosition(board);
		assertTrue(evaluation < -10000);
		System.out.println(evaluation);
	}


	@Test
	public void testVerticalFour() {
		var board = new Board();
		board.parseBoard(
				"-------------------"
						+ "-o-----------------"
						+ "-o-----------------"
						+ "-o-----------------"
						+ "-o-----------------"
		);
		board.printBoard();

		var moveGenerator = new MoveGenerator();
		var positionEvaluator = new PositionEvaluator();
		var evaluation = positionEvaluator.evaluatePosition(board);
		System.out.println(evaluation);
		assertTrue(evaluation < -10000);
	}


	@Test
	public void testDiagonalFour1() {
		var board = new Board();
		board.parseBoard(
				"-------------------"
						+ "-o-----------------"
						+ "--o----------------"
						+ "---o---------------"
						+ "----o--------------"
		);
		board.printBoard();

		var positionEvaluator = new PositionEvaluator();
		var evaluation = positionEvaluator.evaluatePosition(board);
		System.out.println(evaluation);
		assertTrue(evaluation < -10000);
	}

	@Test
	public void testDiagonalFour2() {
		var board = new Board();
		board.parseBoard(
				"-------------------"
						+ "----o--------------"
						+ "---o---------------"
						+ "--o----------------"
						+ "-o-----------------"
		);
		board.printBoard();

		var positionEvaluator = new PositionEvaluator();
		var evaluation = positionEvaluator.evaluatePosition(board);
		System.out.println(evaluation);
		assertTrue(evaluation < -10000);
	}

	@Test
	public void testStrangeThing() {
		var board = new Board();
		board.parseBoard(
				"x------------------"
						+ "o------------------"
						+ "o------------------"
						+ "o------------------"
						+ "-o------------------"
		);
		board.printBoard();

		var positionEvaluator = new PositionEvaluator();
		var evaluation = positionEvaluator.evaluatePosition(board);
		System.out.println(evaluation);
	}


	@Test
	public void measurementTest() {
		var board = new Board();
		board.parseBoard(
				"-------------------"
						+ "----o--------------"
						+ "---o-x-------------"
						+ "--oxx--x------------"
						+ "-o-----------------"
						+ "---x----------------"
		);
		board.printBoard();

		var positionEvaluator = new PositionEvaluator();
		int numRepetitions = 1_000_000;

		for (int i = 0; i < numRepetitions; i++) {
			positionEvaluator.evaluatePosition(board);
		}

		System.out.println(positionEvaluator);
	}

	@Test
	public void testFinalPositionEval() {
		var board = new Board();
		var positionEvaluator = new PositionEvaluator();

		board.parseBoard(
				"-------------------"
						+ "-o--x--------------"
						+ "-ox----------------"
						+ "-o---x-------------"
						+ "-o-----------------"
						+ "-o-----------------"
		);
		Assert.assertTrue("must be win", positionEvaluator.isFinalPosition(board));

		board.reset();
		board.parseBoard(
				"-------------------"
						+ "-o--x--------------"
						+ "-ox----------------"
						+ "-o---x-------------"
						+ "-o-----------------"
						+ "-xxxxx-------------"
		);
		Assert.assertTrue("must be win", positionEvaluator.isFinalPosition(board));

		board.reset();
		board.parseBoard(
				"-------------------"
						+ "-o--xx-------------"
						+ "-ox-x--------------"
						+ "-o-x-x-------------"
						+ "-ox----------------"
						+ "-x-----------------"
		);
		Assert.assertTrue("must be win", positionEvaluator.isFinalPosition(board));


		board.reset();
		board.parseBoard(
				"-------------------"
						+ "-o--xx-------------"
						+ "-ox-o--------------"
						+ "-o-x-x-------------"
						+ "-ox-x--------------"
						+ "-x---x-------------"
						+ "-o----x------------"
		);
		Assert.assertTrue("must be win", positionEvaluator.isFinalPosition(board));

	}


}
