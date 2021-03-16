package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import lombok.AllArgsConstructor;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.board.Board.HUMAN;

public class MoveSearch {

	private MoveGenerator moveGenerator = new MoveGenerator();
	private PositionEvaluator positionEvaluator = new PositionEvaluator(moveGenerator);

	private int searchDepth = 5;

	public int findNextMove(Board board) {
		return findNextMove(board, searchDepth);
	}

	public int findNextMove(Board board, int searchDepth) {
		this.searchDepth = searchDepth;

		var bestMove = alphabeta(board, searchDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		System.out.println("best move was " + bestMove.sqNum + " with evaluation " + bestMove.value);
		return bestMove.sqNum;
	}


	public MoveValue alphabeta(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {

		if (depth == 0 || positionEvaluator.isFinalPosition(board)) {
			int value = positionEvaluator.evaluatePositionOrGetCached(board);
			int sqNum = board.getLastMove();
			return new MoveValue(sqNum, value);
		}

		if (maximizingPlayer) {
			// evaluating computer turn

			int[] moves = moveGenerator.generateMoves(board);

			if (depth == searchDepth) {
				// TODO odsekat takove tahy, ktere vedou k prohre, a vyhodnotit jen ty
				//TODO heuristika - seradit tahy
			}

			int bestValue = Integer.MIN_VALUE;
			int bestSquare = -1;
			for (int moveSquare : moves) {

				board.makeMove(moveSquare, COMPUTER);
				MoveValue node = alphabeta(board, depth - 1, alpha, beta, false);
				board.undoLastMove();

				if (node.value > bestValue) {
					bestValue = node.value;
					bestSquare = moveSquare;
				}
				alpha = Math.max(alpha, bestValue);

				if (alpha >= beta) {
					break;
				}
			}
			return new MoveValue(bestSquare, bestValue);
		} else {

			// evaluating human turn

			int worstValue = Integer.MAX_VALUE;
			int worstSquare = -1;

			int[] moves = moveGenerator.generateMoves(board);
			// TODO heuristika i tady?

			for (int moveSquare : moves) {
				board.makeMove(moveSquare, HUMAN);
				MoveValue node = alphabeta(board, depth - 1, alpha, beta, true);
				board.undoLastMove();

				if (node.value < worstValue) {
					worstValue = node.value;
					worstSquare = moveSquare;
				}

				beta = Math.min(beta, worstValue);
				if (beta <= alpha) {
					break;
				}
			}
			return new MoveValue(worstSquare, worstValue);
		}
	}

	@AllArgsConstructor
	private class MoveValue {

		public int sqNum;
		public int value;
	}

}
