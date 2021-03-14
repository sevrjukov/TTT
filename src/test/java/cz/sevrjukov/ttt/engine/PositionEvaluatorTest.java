package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import org.junit.Test;

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
		var evaluation = positionEvaluator.alphabeta(board, 7, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

		System.out.println("hotovo");

	}

}
