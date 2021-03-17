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
	private static final int SEARCH_DEPTH = 5;
	private static final int BAD_MOVE_CUTOFF = -OPENED_FOUR + 100;

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
		var bestMove = alphabetaBestMove(board, SEARCH_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		displayMoveFoundMessage(bestMove);
		return bestMove;
	}

	protected MoveEval findNextMove(Board board, int searchDepth) {
		moveNumber++;
		var bestMove = alphabetaBestMove(board, searchDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		displayMoveFoundMessage(bestMove);
		return bestMove;
	}

	private void displayMoveFoundMessage(MoveEval move) {
		gameEventListener.printInfo(String.format("Best move [%s] eval [%s]", move.sqNum, move.eval));
	}


	/**
	 * This does actually search for the best available move
	 */
	private MoveEval alphabetaBestMove(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {

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
					int moveValue = alphabetaEvaluate(board, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
					System.out.println("pre-evaluated move " + moveSq + " with value " + moveValue);
					board.undoLastMove();

					if (moveValue == VICTORY) {
						// this is a victory move, play immediately
						return new MoveEval(moveSq, VICTORY);
					}
					if (moveValue <= BAD_MOVE_CUTOFF) {
						// this is a bad move, don't play it
						continue;
					}
					filteredMoves.add(new MoveEval(moveSq, moveValue));
				}
				// no moves to play - resign
				if (filteredMoves.isEmpty()) {
					return new MoveEval(MOVE_RESIGN, DEFEAT);
				}
				// only one playable move
				if (filteredMoves.size() == 1) {
					return new MoveEval(filteredMoves.get(0).sqNum,
							filteredMoves.get(0).eval);
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
				//TODO based on numnber of moves which are worth calculating, set search depth +1
				if (moves.length < 5) {
					depth++;
				}
			}

			int bestValue = Integer.MIN_VALUE;
			int bestSquare = MOVE_RESIGN;
			int counter = 0;
			for (int moveSquare : moves) {
				if (depth >= SEARCH_DEPTH) {
					gameEventListener.printInfo("Evaluating move " + (++counter) + "/" + moves.length + ", depth " + depth);
				}
				board.makeMove(moveSquare, COMPUTER);
				MoveEval node = alphabetaBestMove(board, depth - 1, alpha, beta, false);
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
				MoveEval node = alphabetaBestMove(board, depth - 1, alpha, beta, true);
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


	/*
	This is used to pre-evaluate individual moves.
	 */
	private int alphabetaEvaluate(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {

		if (depth == 0 || positionEvaluator.isFinalPosition(board)) {
			return positionEvaluator.evaluatePositionOrGetCached(board);
		}

		if (maximizingPlayer) {
			int value = Integer.MIN_VALUE;
			int[] moves = moveGenerator.generateMoves(board);
			for (int moveSquare : moves) {
				board.makeMove(moveSquare, COMPUTER);
				value = Math.max(value, alphabetaEvaluate(board, depth - 1, alpha, beta, false));
				alpha = Math.max(alpha, value);
				board.undoLastMove();
				if (alpha >= beta) {
					break;
				}
			}
			return value;
		} else {
			int value = Integer.MAX_VALUE;
			int[] moves = moveGenerator.generateMoves(board);
			for (int moveSquare : moves) {
				board.makeMove(moveSquare, HUMAN);
				value = Math.min(value, alphabetaEvaluate(board, depth - 1, alpha, beta, true));
				beta = Math.min(beta, value);
				board.undoLastMove();
				if (beta <= alpha) {
					break;
				}
			}
			return value;
		}
	}

	@AllArgsConstructor
	@Getter
	public static class MoveEval {

		public int sqNum;
		public int eval;
	}

}
