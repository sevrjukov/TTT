package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
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
		var positionEvaluator = new PositionEvaluator(moveGenerator);
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
		var positionEvaluator = new PositionEvaluator(moveGenerator);
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

		var moveGenerator = new MoveGenerator();
		var positionEvaluator = new PositionEvaluator(moveGenerator);
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

		var moveGenerator = new MoveGenerator();
		var positionEvaluator = new PositionEvaluator(moveGenerator);
		var evaluation = positionEvaluator.evaluatePosition(board);
		System.out.println(evaluation);
		assertTrue(evaluation < -10000);
	}



}
