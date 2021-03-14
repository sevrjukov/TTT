package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

public class PositionEvaluatorTest {


	@Test
	public void testEvaluation() {

		var board = new Board();
		board.setDebug(true);

		board.parseBoard("----------------"
				+ "-------------------"
				+ "---ox---------");

//		board.printBoard();
		var moveGenerator = new MoveGenerator();
		var positionEvaluator = new PositionEvaluator(moveGenerator);
		var evaluation = positionEvaluator.alphabeta(board, 7, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

		System.out.println("hotovo");

	}


	@Test
	public void testFourInARow() {
		var board = new Board();
		board.setDebug(true);

		board.parseBoard("----------------"
				+ "--------oooo-----------"
				+ "---x---------");
		board.printBoard();

//		board.printBoard();
		var moveGenerator = new MoveGenerator();
		var positionEvaluator = new PositionEvaluator(moveGenerator);
		var evaluation = positionEvaluator.evaluatePosition(board);
		assertTrue(evaluation < 10000);
		System.out.println(evaluation);
	}



	@Test
	public void testVerticalFour() {
		var board = new Board();
		board.parseBoard(
				"-----------"
						+	"-o---------"
						+	"-o---------"
						+	"-o---------"
						+	"-o---------"
		);
		board.printBoard();

		var moveGenerator = new MoveGenerator();
		var positionEvaluator = new PositionEvaluator(moveGenerator);
		var evaluation = positionEvaluator.evaluatePosition(board);
		System.out.println(evaluation);
		assertTrue(evaluation < 10000);

	}

	@Test
	public void testDiagonals() {
		fail("impleement me");
	}

}
