package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import org.junit.Test;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;

public class MoveSearchTest {


	@Test
	public void moveSearchTest() {

		var board = new Board();
		board.parseBoard("-----------------"
				+ "-------------xx---xoox-ooxx------o---xxo-x-----");
		board.printBoard();
		MoveSearch moveSearch = new MoveSearch();
		int nextMove = moveSearch.findNextMove(board, 5);

		board.makeMove(nextMove, COMPUTER);
		board.printBoard();

	}

	@Test
	public void simpleCase() {
		var board = new Board();
		board.parseBoard(
				"-------------------"
			+	"-o-----------------"
			+	"-o-----------------"
			+	"-o-----------------"
			+	"-------------------"
			);
		MoveSearch moveSearch = new MoveSearch();
		int nextMove = moveSearch.findNextMove(board, 4);
		board.makeMove(nextMove, COMPUTER);
		board.printBoard();
	}

}
