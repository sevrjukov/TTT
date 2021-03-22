package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import cz.sevrjukov.ttt.util.DummyGameEventListener;
import org.junit.Test;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.board.Board.HUMAN;

public class EngineDebugTest {


	@Test
	public void classicalBlunderTest() {
		// the game itself, human starts

		int[] game = {
				178, 179, 160, 196, 181, 197, 161, 215, 199,
				234
//				235,
//				 163
		};

		var board = new Board();
		var moveSearch = new MoveSearch();
		var positionEvaluator = new PositionEvaluator();
		moveSearch.setGameEventListener(new DummyGameEventListener());

		for (int moveNum = 0; moveNum < game.length; moveNum++) {
			int sqNum = game[moveNum];
			int player = (moveNum % 2 == 0) ? HUMAN : COMPUTER;
			board.makeMove(sqNum, player);
		}

		board.printBoard();
		var eval = positionEvaluator.evaluatePosition(board);
		System.out.println("current position eval " + eval);

		var nextMove = moveSearch.findNextMove(board);
		System.out.printf("Next move [%s] [%s]%n", nextMove.sqNum, nextMove.eval);
	}


}
