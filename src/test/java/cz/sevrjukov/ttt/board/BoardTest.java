package cz.sevrjukov.ttt.board;

import org.junit.Test;

public class BoardTest {


	@Test
	public void testParse() {

		var board = new Board();

		board.parseBoard("---xoxx----"
				+ "---oxxoxxxoxx-ooo-oxxx--ooo-xxx");

		board.printBoard();
	}

	@Test
	public void testMove() {

		var board = new Board();

		board.move(2, 1);
		board.move(3, 2);

		board.printBoard();

		board.undoLastMove();

		board.printBoard();

		board.clearBoard();

		board.printBoard();
	}
}
