package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;
import cz.sevrjukov.ttt.game.GameEventListener;
import cz.sevrjukov.ttt.util.TextUtils;
import cz.sevrjukov.ttt.util.Versions;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
	private static final int PREEVAL_SEARCH_DEPTH = 3;
	private static final int BAD_MOVE_CUTOFF = -OPENED_FOUR + 100;

	// stats
	long positionsEvaluated = 0;
	long searchStartTime = 0;
	boolean running = false;

	public void reset() {
		moveNumber = 0;
		positionsEvaluated = 0;
		moveGenerator.resetCache();
		positionEvaluator.resetCache();
	}

	public void setGameEventListener(GameEventListener gameEventListener) {
		this.gameEventListener = gameEventListener;
	}

	public MoveEval findNextMove(Board board) {
		moveNumber++;
		positionsEvaluated = 0;
		moveGenerator.resetStats();
		moveGenerator.clearCacheIfNeeded();
		positionEvaluator.resetStats();
		positionEvaluator.clearCacheIfNeeded();
		searchStartTime = System.currentTimeMillis();
		running = true;
		var bestMove = alphabetaBestMove(board, SEARCH_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		running = false;
		displayMoveFoundMessage(bestMove);
		return bestMove;
	}

	private void displayMoveFoundMessage(MoveEval move) {
		gameEventListener.printInfo(String.format("Best move [%s] eval [%s]", move.sqNum, move.eval));
	}

	public MoveGenerator getMoveGenerator() {
		return moveGenerator;
	}

	/**
	 * This does actually search for the best available move
	 */
	private MoveEval alphabetaBestMove(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {

		if (depth == 0 || positionEvaluator.isFinalPosition(board)) {
			positionsEvaluated++;
			int value = positionEvaluator.evaluatePositionOrGetCached(board);
			int sqNum = board.getLastMove();
			return new MoveEval(sqNum, value);
		}

		if (maximizingPlayer) {
			// evaluating computer turn

			int[] moves = moveGenerator.generateMoves(board);

			// pre-evaluation and iterative deepening
			if (depth == SEARCH_DEPTH) {
				List<MoveEval> filteredMoves = new ArrayList<>();
				gameEventListener.printInfo("Pre-evaluating moves...");
				for (int moveSq : moves) {

					board.makeMove(moveSq, COMPUTER);
					var preEvaluatedMove = alphabetaEvaluate(board, PREEVAL_SEARCH_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
					System.out.printf("pre-evaluated move %s with value %s and remaining depth %s %n", moveSq, preEvaluatedMove.eval, preEvaluatedMove.depth);
					board.undoLastMove();

					if (preEvaluatedMove.eval <= BAD_MOVE_CUTOFF) {
						// this is a bad move, don't play it
						continue;
					}
					filteredMoves.add(new MoveEval(moveSq, preEvaluatedMove.eval, preEvaluatedMove.depth));
				}
				// no moves to play - resign
				if (filteredMoves.isEmpty()) {
					return new MoveEval(MOVE_RESIGN, DEFEAT);
				}
				// only one playable move - no need to evaluate it
				if (filteredMoves.size() == 1) {
					return new MoveEval(filteredMoves.get(0).sqNum,
							filteredMoves.get(0).eval);
				}
				// sort them from best to worst. Shuffle will introduce some randomness
				// in case that we have multiple moves with the same pre-evaluation.
				Collections.shuffle(filteredMoves);
				var sortedMovesList = filteredMoves.stream()
						.sorted(Comparator.comparing(MoveEval::getEval).reversed())
						.sorted(Comparator.comparing(MoveEval::getDepth).reversed())
						.collect(Collectors.toList());

				if (sortedMovesList.get(0).eval == VICTORY) {
					// this is a victory move with the least depth, play immediately
					return sortedMovesList.get(0);
				}

				moves = new int[sortedMovesList.size()];
				Arrays.fill(moves, -1);
				for (int i = 0; i < moves.length; i++) {
					moves[i] = sortedMovesList.get(i).sqNum;
				}
				// if we have just a few reasonable moves, calculate them properly
				if (moves.length < 5) {
					depth++;
				}
			}

			// regular evaluation (computer turn)
			int bestValue = Integer.MIN_VALUE;
			/*
			 * We pick the first move here to be the best "by default".
			 * Even if this is a hopeless situation for the computer, and it cannot win, it's not going
			 * to resign too early, as the human opponent may make a mistake. Otherwise it assumes
			 * perfect play from the human and therefore "loses hope".
			 */
			int bestSquare = moves[0];
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
	This is used to pre-evaluate individual moves in early phase of search.
	 */
	private MovePreEval alphabetaEvaluate(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {

		if (depth == 0 || positionEvaluator.isFinalPosition(board)) {
			positionsEvaluated++;
			return new MovePreEval(positionEvaluator.evaluatePositionOrGetCached(board), depth);
		}

		if (maximizingPlayer) {
			int value = Integer.MIN_VALUE;
			int reachedDepth = 0;
			int[] moves = moveGenerator.generateMoves(board);
			for (int moveSquare : moves) {

				board.makeMove(moveSquare, COMPUTER);
				var preEvalMove = alphabetaEvaluate(board, depth - 1, alpha, beta, false);
				board.undoLastMove();

				value = Math.max(value, preEvalMove.getEval());
				reachedDepth = preEvalMove.getDepth();
				alpha = Math.max(alpha, value);

				if (alpha >= beta) {
					break;
				}
			}
			return new MovePreEval(value, reachedDepth);
		} else {
			int value = Integer.MAX_VALUE;
			int reachedDepth = 0;
			int[] moves = moveGenerator.generateMoves(board);
			for (int moveSquare : moves) {

				board.makeMove(moveSquare, HUMAN);
				var preEvalMove = alphabetaEvaluate(board, depth - 1, alpha, beta, true);
				board.undoLastMove();

				value = Math.min(value, preEvalMove.getEval());
				reachedDepth = preEvalMove.getDepth();
				beta = Math.min(beta, value);

				if (beta <= alpha) {
					break;
				}
			}
			return new MovePreEval(value, reachedDepth);
		}
	}

	@Getter
	public static class MoveEval {

		public int sqNum;
		public int eval;
		public int depth;

		public MoveEval(int sqNum, int eval) {
			this.sqNum = sqNum;
			this.eval = eval;
		}

		public MoveEval(int sqNum, int eval, int depth) {
			this.sqNum = sqNum;
			this.eval = eval;
			this.depth = depth;
		}
	}

	@Getter
	public static class MovePreEval {

		private int eval;
		private int depth;

		public MovePreEval(int eval, int depth) {
			this.eval = eval;
			this.depth = depth;
		}
	}

	public String getStats() {
		var EL = System.lineSeparator();
		var builder = new StringBuilder();

		builder.append("Engine version: ");
		builder.append(Versions.ENGINE_VERSION);
		builder.append(EL);

		builder.append("Positions evaluated: ");
		builder.append(TextUtils.numToHumanStr(positionsEvaluated));
		builder.append(EL);

		long timeElapsed = System.currentTimeMillis() - searchStartTime;
		builder.append("performance: ");
		long positionsPerSeconds = (timeElapsed > 0 && running) ? positionsEvaluated * 1000 / timeElapsed : 0;
		builder.append(TextUtils.numToHumanStr(positionsPerSeconds));
		builder.append(" pps");
		builder.append(EL);

		// movegen cache
		builder.append("Movegen cache");
		builder.append(EL);
		builder.append("size: ");
		builder.append(TextUtils.numToHumanStr(moveGenerator.getCacheSize()));

		builder.append(" hits: ");
		builder.append(TextUtils.numToHumanStr(moveGenerator.cacheHits()));
		builder.append(" (");

		var moveGenHitsPerc = (moveGenerator.getGenRequests() > 0) ? moveGenerator.cacheHits() * 100 / moveGenerator.getGenRequests() : 0;
		builder.append(moveGenHitsPerc);
		builder.append("%)");
		builder.append(EL);

		// position evaluator cache
		builder.append("Position evaluator cache");
		builder.append(EL);
		builder.append("size: ");
		builder.append(TextUtils.numToHumanStr(positionEvaluator.getCacheSize()));

		builder.append(" hits: ");
		builder.append(TextUtils.numToHumanStr(positionEvaluator.cacheHits()));
		builder.append(" (");

		var posEvHitsPerc = (positionEvaluator.getEvalRequests() > 0) ? positionEvaluator.cacheHits() * 100 / positionEvaluator.getEvalRequests() : 0;
		builder.append(posEvHitsPerc);
		builder.append("%)");
		builder.append(EL);

		return builder.toString();
	}


}
