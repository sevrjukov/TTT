package cz.sevrjukov.ttt.engine;

import org.junit.Assert;
import org.junit.Test;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.board.Board.EMPTY;
import static cz.sevrjukov.ttt.board.Board.HUMAN;

public class WinPositionEvaluatorTest {


	@Test
	public void testWin1() {
		int[] line = {COMPUTER, COMPUTER, COMPUTER, COMPUTER, COMPUTER};
		Assert.assertTrue("incorrect result", testWithLine(line));
	}



	@Test
	public void testWin2() {
		int[] line = {
				COMPUTER, COMPUTER, COMPUTER, COMPUTER, COMPUTER,
				EMPTY, COMPUTER, COMPUTER, HUMAN, EMPTY,
				HUMAN};
		Assert.assertTrue("incorrect result", testWithLine(line));
	}

	@Test
	public void testWin3() {
		int[] line = {
				HUMAN,
				COMPUTER, COMPUTER, COMPUTER, COMPUTER, COMPUTER,
				EMPTY, COMPUTER, COMPUTER, HUMAN, EMPTY,
				HUMAN};
		Assert.assertTrue("incorrect result", testWithLine(line));
	}

	@Test
	public void testWin4() {
		int[] line = {
				HUMAN,
				EMPTY, COMPUTER, COMPUTER, HUMAN, EMPTY,
				COMPUTER, COMPUTER, COMPUTER, COMPUTER, COMPUTER};
		Assert.assertTrue("incorrect result", testWithLine(line));
	}

	@Test
	public void testWin5() {
		int[] line = {
				HUMAN,
				EMPTY, COMPUTER, COMPUTER, HUMAN, EMPTY,
				COMPUTER, COMPUTER, COMPUTER, COMPUTER, COMPUTER, EMPTY};
		Assert.assertTrue("incorrect result", testWithLine(line));
	}




	@Test
	public void testNotWin1() {
		int[] line = {COMPUTER, COMPUTER, COMPUTER, COMPUTER};
		Assert.assertFalse("incorrect result", testWithLine(line));
	}


	@Test
	public void testNotWin2() {
		int[] line = {
				COMPUTER, COMPUTER, EMPTY, COMPUTER, COMPUTER, COMPUTER,
				EMPTY, COMPUTER, COMPUTER, HUMAN, EMPTY,
				HUMAN};
		Assert.assertFalse("incorrect result", testWithLine(line));
	}

	@Test
	public void testNotWin3() {
		int[] line = {
				HUMAN, COMPUTER, COMPUTER, COMPUTER, COMPUTER,
				EMPTY, COMPUTER, COMPUTER, HUMAN, EMPTY,
				HUMAN};
		Assert.assertFalse("incorrect result", testWithLine(line));
	}



	private boolean testWithLine(int[] line) {
		var evaluator = new WinPositionEvaluator();
		boolean win = false;
		for (int sq : line) {
			if (evaluator.feedNextSquare(sq)) {
				win = true;
				break;
			}
		}
		return win;
	}

}
