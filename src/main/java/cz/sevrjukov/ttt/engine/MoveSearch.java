package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.board.Board.HUMAN;
import static cz.sevrjukov.ttt.engine.PositionEvaluator.DEFEAT;
import static cz.sevrjukov.ttt.engine.PositionEvaluator.VICTORY;
import static cz.sevrjukov.ttt.engine.SequenceEvaluator.OPENED_FOUR;

public class MoveSearch {

	public static final int MOVE_RESIGN = -2;

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
				//TODO heuristika - seradit tahy
				// pre-evaluate moves and sort them
				List<MoveValue> filteredMoves = new ArrayList<>();
				for (int moveSq : moves) {
					board.makeMove(moveSq, COMPUTER);
					int moveValue = positionEvaluator.evaluatePosition(board);
					board.undoLastMove();
					if (moveValue == VICTORY) {
						// this is a victory move, play immediately
						return new MoveValue(moveSq, VICTORY);
					}
					if (moveValue <= -OPENED_FOUR) {
						// this is a bad move, don't play it
						continue;
					}
					filteredMoves.add(new MoveValue(moveSq, moveValue));

				}
				// no moves to play - resign
				if (filteredMoves.isEmpty()) {
					return new MoveValue(MOVE_RESIGN, DEFEAT);
				}
				// sort them from best to worst
				var sortedMovesList = filteredMoves.stream()
						.sorted(Comparator.comparing(MoveValue::getValue).reversed())
						.collect(Collectors.toList());
				moves = new int[sortedMovesList.size()];
				Arrays.fill(moves, -1);
				for (int i = 0; i < moves.length; i++) {
					moves[i] = sortedMovesList.get(i).sqNum;
				}
			}

			int bestValue = Integer.MIN_VALUE;
			int bestSquare = MOVE_RESIGN;
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

			// evaluating opponent turn

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
	@Getter
	private class MoveValue {

		public int sqNum;
		public int value;
	}

}
