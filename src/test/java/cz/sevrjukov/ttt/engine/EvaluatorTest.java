package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import cz.sevrjukov.ttt.gui.SimpleVisualBoard;
import org.junit.Test;

import static cz.sevrjukov.ttt.board.Board.COMP;

public class EvaluatorTest {


	@Test
	public void testSpeed() {

		var board = new Board();

		board.parseBoard("----------------"
				+ "-------------------"
				+ "---o---------");

//		board.printBoard();

		var simpleDisplay = new SimpleVisualBoard();
		board.setDisplay(simpleDisplay);
		simpleDisplay.start();
		MoveGenerator moveGenerator = new MoveGenerator();
		Evaluator ev = new Evaluator(moveGenerator);
		var moves = moveGenerator.generateMoves(board);

		for (int moveSquare: moves) {
			board.makeMove(moveSquare, COMP);

			// evaluating this position:
			board.drawBoard();
			var evaluation = ev.alphabeta(board, 2, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
			System.out.println("value " + evaluation);
			board.undoLastMove();
		}



		//alphabeta(origin, depth, −∞, +∞, TRUE)


	}

}
