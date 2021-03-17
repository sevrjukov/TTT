package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import org.junit.Assert;
import org.junit.Test;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.engine.MoveSearch.MOVE_RESIGN;

public class MoveSearchTest {


	@Test
	public void moveSearchTest() {

		var board = new Board();
		board.parseBoard("-----------------"
				+ "----------xoox-ooxx------o---xxo-x-----");
		board.printBoard();
		MoveSearch moveSearch = new MoveSearch();
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
		Assert.fail("implement me");
	}

	@Test
	public void testLosingPosition() {
		Assert.fail("implement me");
	}


}
