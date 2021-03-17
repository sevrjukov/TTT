package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import cz.sevrjukov.ttt.game.GameEventListener;
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
	private PositionEvaluator positionEvaluator = new PositionEvaluator();
	private GameEventListener gameEventListener;

	public int moveNumber = 0;
	private static final int INITIAL_SEARCH_DEPTH = 2;
	private static final int SEARCH_DEPTH = 5;
	private int currentSearchDepth = INITIAL_SEARCH_DEPTH;

	public void reset() {
		moveNumber = 0;
		moveGenerator.resetCache();
		positionEvaluator.resetCache();
	}

	public void setGameEventListener(GameEventListener gameEventListener) {
		this.gameEventListener = gameEventListener;
	}

	public MoveEval findNextMove(Board board) {
		moveNumber++;
		var depth = moveNumber < 4 ? INITIAL_SEARCH_DEPTH : SEARCH_DEPTH;
		currentSearchDepth = depth;
		var bestMove = alphabeta(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		displayMoveFoundMessage(bestMove);
		return bestMove;
	}

	protected MoveEval findNextMove(Board board, int searchDepth) {
		moveNumber++;
		currentSearchDepth = searchDepth;
		var bestMove = alphabeta(board, searchDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		displayMoveFoundMessage(bestMove);
		return bestMove;
	}

	private void displayMoveFoundMessage(MoveEval move) {
		gameEventListener.printInfo(String.format("Best move [%s] eval [%s]", move.sqNum, move.eval));
	}


	private MoveEval alphabeta(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {

		if (depth == 0 || positionEvaluator.isFinalPosition(board)) {
			int value = positionEvaluator.evaluatePositionOrGetCached(board);
			int sqNum = board.getLastMove();
			return new MoveEval(sqNum, value);
		}

		if (maximizingPlayer) {
			// evaluating computer turn

			int[] moves = moveGenerator.generateMoves(board);

			if (depth == SEARCH_DEPTH) {
				List<MoveEval> filteredMoves = new ArrayList<>();
				for (int moveSq : moves) {
					board.makeMove(moveSq, COMPUTER);
					int moveValue = positionEvaluator.evaluatePosition(board);
					board.undoLastMove();
					if (moveValue == VICTORY) {
						// this is a victory move, play immediately
						return new MoveEval(moveSq, VICTORY);
					}
					if (moveValue <= -OPENED_FOUR) {
						// this is a bad move, don't play it
						continue;
					}
					filteredMoves.add(new MoveEval(moveSq, moveValue));
				}
				// no moves to play - resign
				if (filteredMoves.isEmpty()) {
					return new MoveEval(MOVE_RESIGN, DEFEAT);
				}
				// sort them from best to worst
				var sortedMovesList = filteredMoves.stream()
						.sorted(Comparator.comparing(MoveEval::getEval).reversed())
						.collect(Collectors.toList());
				moves = new int[sortedMovesList.size()];
				Arrays.fill(moves, -1);
				for (int i = 0; i < moves.length; i++) {
					moves[i] = sortedMovesList.get(i).sqNum;
				}
			}

			int bestValue = Integer.MIN_VALUE;
			int bestSquare = MOVE_RESIGN;
			int counter = 0;
			for (int moveSquare : moves) {
				if (depth == currentSearchDepth) {
					gameEventListener.printInfo("Evaluating move " + (++counter) + "/" + moves.length + ", depth " + depth);
				}
				board.makeMove(moveSquare, COMPUTER);
				MoveEval node = alphabeta(board, depth - 1, alpha, beta, false);
				board.undoLastMove();

				if (node.eval > bestValue) {
					bestValue = node.eval;
					bestSquare = moveSquare;
				}
				alpha = Math.max(alpha, bestValue);

				if (alpha >= beta) {
					break;
				}
			}
			return new MoveEval(bestSquare, bestValue);
		} else {

			// evaluating opponent turn

			int worstValue = Integer.MAX_VALUE;
			int worstSquare = -1;

			int[] moves = moveGenerator.generateMoves(board);
			// TODO heuristika i tady?

			for (int moveSquare : moves) {
				board.makeMove(moveSquare, HUMAN);
				MoveEval node = alphabeta(board, depth - 1, alpha, beta, true);
				board.undoLastMove();

				if (node.eval < worstValue) {
					worstValue = node.eval;
					worstSquare = moveSquare;
				}

				beta = Math.min(beta, worstValue);
				if (beta <= alpha) {
					break;
				}
			}
			return new MoveEval(worstSquare, worstValue);
		}
	}

	@AllArgsConstructor
	@Getter
	public static class MoveEval {

		public int sqNum;
		public int eval;
	}

}
