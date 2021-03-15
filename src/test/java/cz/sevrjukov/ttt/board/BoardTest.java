package cz.sevrjukov.ttt.board;

import org.junit.Assert;
import org.junit.Test;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.board.Board.HUMAN;
import static junit.framework.TestCase.assertEquals;

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

		board.makeMove(2, 1);
		board.makeMove(3, 2);

		board.printBoard();

		board.undoLastMove();

		board.printBoard();

		board.clearBoard();

		board.printBoard();
	}


	@Test
	public void testHashCalculation() {
		var board = new Board();

		board.parseBoard("---xoxx----");
		var hash1 = board.getPositionHash();

		board.parseBoard("---xxxo----");
		var hash2 = board.getPositionHash();

		Assert.assertNotEquals(hash1, hash2);
	}

	@Test
	public void testShallowHashCalculation() {
		var board = new Board();

		board.parseBoard("x---xoxx----xo");
		var hash1 = board.getPositionHashShallow();

		board.parseBoard("x---oooo----oo");
		var hash2 = board.getPositionHashShallow();

		Assert.assertEquals(hash1, hash2);
	}


	@Test
	public void testBounds() {
		var board = new Board();

		board.makeMove(50, COMPUTER);

		assertEquals("bad min bounds", 30, board.getMinBound());
		assertEquals("bad max bounds", 70, board.getMaxBound());

		board.makeMove(100, HUMAN);

		assertEquals("bad min bounds", 30, board.getMinBound());
		assertEquals("bad max bounds", 120, board.getMaxBound());

		board.makeMove(68, HUMAN);
		assertEquals("bad min bounds", 30, board.getMinBound());
		assertEquals("bad max bounds", 120, board.getMaxBound());

		board.undoLastMove();
		assertEquals("bad min bounds", 30, board.getMinBound());
		assertEquals("bad max bounds", 120, board.getMaxBound());

		board.undoLastMove();
		assertEquals("bad min bounds", 30, board.getMinBound());
		assertEquals("bad max bounds", 70, board.getMaxBound());

	}
}
