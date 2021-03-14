package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;

import java.util.HashMap;
import java.util.Map;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.engine.PositionEvaluator.VICTORY;

public class MoveSearch {

	private MoveGenerator moveGenerator = new MoveGenerator();
	private PositionEvaluator positionEvaluator = new PositionEvaluator(moveGenerator);

	public int findNextMove(Board board, int searchDepth) {

		// generates move candidates, pre-checks them
		// sorts, etc..
		// and runs MoveEvaluator and selects the best one

		var moves = moveGenerator.generateMoves(board);

		Map<Integer, Integer> evaluatedMoves = new HashMap<>();

		for (int moveSquare : moves) {
			board.makeMove(moveSquare, COMPUTER);
			// evaluating this position:
			var evaluation = positionEvaluator.alphabeta(board, searchDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
			evaluatedMoves.put(evaluation, moveSquare);
			System.out.println("Move " + moveSquare + " was evaluated " + evaluation);
			board.undoLastMove();
			if (evaluation == VICTORY) {
				break;
			}
		}

		// sort and take the best found move
		var bestMoveKey = evaluatedMoves.keySet().stream().max(Integer::compare).get();
		var bestMove = evaluatedMoves.get(bestMoveKey);
		System.out.println("best move is " + bestMove);
		return bestMove;
	}

}
