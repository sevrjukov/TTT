package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import cz.sevrjukov.ttt.game.GameEventListener;
import org.junit.Assert;
import org.junit.Test;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.engine.MoveSearch.MOVE_RESIGN;
import static cz.sevrjukov.ttt.engine.PositionEvaluator.VICTORY;

public class MoveSearchTest {


	@Test
	public void moveSearchTest() {

		var board = new Board();
		board.parseBoard("-----------------"
				+ "----------xoox-----");
		board.printBoard();
		MoveSearch moveSearch = new MoveSearch();
		moveSearch.setGameEventListener(dummyGameListener());
		var nextMove = moveSearch.findNextMove(board);

		board.makeMove(nextMove.sqNum, COMPUTER);
		board.printBoard();
	}

	@Test
	public void moveSearchTestMidsizePosition() {

		var board = new Board();
		board.parseBoard(
				"-----------------" +
						"-----------------"
						+ "----------xxx------"
						+ "---------ooxo------"
						+ "---------oxxxo-----"
						+ "---------oxo-o-----"
						+ "--------oxxx-------"
						+ "----------o--------"
	);
		board.printBoard();
		MoveSearch moveSearch = new MoveSearch();
		moveSearch.setGameEventListener(dummyGameListener());
		var nextMove = moveSearch.findNextMove(board);

		board.makeMove(nextMove.sqNum, COMPUTER);
		board.printBoard();
	}

	@Test
	public void simpleCase() {
		var board = new Board();
		board.parseBoard(
				"-------------------"
						+ "-o-----------------"
						+ "-o-----------------"
						+ "-o-----------------"
						+ "-------------------"
		);
		MoveSearch moveSearch = new MoveSearch();
		moveSearch.setGameEventListener(dummyGameListener());
		var nextMove = moveSearch.findNextMove(board);
		if (nextMove.eval == MOVE_RESIGN) {
			System.out.println("computer resigned");
		} else {
			board.makeMove(nextMove.sqNum, COMPUTER);
			board.printBoard();
		}
	}

	@Test
	public void testWinningPosition() {
		var board = new Board();
		board.parseBoard(
				"-------------------"
						+ "-xo----------------"
						+ "-x--o--------------"
						+ "-x-----o-----------"
						+ "-x--oo-------------"
						+ "-------------------"
		);

		MoveSearch moveSearch = new MoveSearch();
		moveSearch.setGameEventListener(dummyGameListener());
		var nextMove = moveSearch.findNextMove(board);

		Assert.assertEquals("Found move must be a winnning move", nextMove.eval, VICTORY);
	}

	@Test
	public void testLosingPosition() {
		var board = new Board();
		board.parseBoard(
				"-------------------"
						+ "-ox----------------"
						+ "-o--x--------------"
						+ "-o-----o-----------"
						+ "-o--xo-------------"
						+ "-------------------"
		);

		MoveSearch moveSearch = new MoveSearch();
		moveSearch.setGameEventListener(dummyGameListener());
		var nextMove = moveSearch.findNextMove(board);

		Assert.assertEquals("Found move must be a resign move", nextMove.sqNum, MOVE_RESIGN);
	}



	private GameEventListener dummyGameListener() {
		return new GameEventListener() {
			@Override
			public void refreshBoard() {

			}

			@Override
			public void resign(int player) {

			}

			@Override
			public void announceVictory() {

			}

			@Override
			public void printInfo(String info) {

			}
		};
	}


}
