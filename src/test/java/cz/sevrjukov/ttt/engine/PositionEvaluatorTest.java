package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import org.junit.Test;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;

public class PositionEvaluatorTest {


	@Test
	public void testEvaluation() {

		var board = new Board();
		board.setDebug(true);

		board.parseBoard("----------------"
				+ "-------------------"
				+ "---o---------");

//		board.printBoard();


		var moveGenerator = new MoveGenerator();

		var positionEvaluator = new PositionEvaluator(moveGenerator);
		var moves = moveGenerator.generateMoves(board);

		for (int moveSquare: moves) {
			board.makeMove(moveSquare, COMPUTER);
			// evaluating this position:
			var evaluation = positionEvaluator.alphabeta(board, 8, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
			System.out.println("value " + evaluation);
			board.undoLastMove();

		}

		System.out.println("hotovo");

	}

}
