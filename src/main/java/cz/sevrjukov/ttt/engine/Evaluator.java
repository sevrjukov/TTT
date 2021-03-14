package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;

import java.util.Random;

import static cz.sevrjukov.ttt.board.Board.COMP;
import static cz.sevrjukov.ttt.board.Board.HUMAN;

public class Evaluator {

	private MoveGenerator moveGenerator;

	public Evaluator(MoveGenerator moveGenerator) {
		this.moveGenerator = moveGenerator;

	}

	public int alphabeta(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {

		if (depth == 0 || isFinalPosition(board)) {
			return evaluatePosition(board);
		}

		if (maximizingPlayer) {
			int value = Integer.MIN_VALUE;
			int [] moves = moveGenerator.generateMoves(board);
			for (int moveSquare: moves) {
				board.makeMove(moveSquare, HUMAN);

				value = Math.max(value, alphabeta(board, depth - 1, alpha, beta, false));
				alpha = Math.max(alpha, value);
				if (alpha >= beta) {
					board.undoLastMove();
					break;
				}
				board.undoLastMove();
			}
			return value;
		} else {
			int value = Integer.MAX_VALUE;
			int [] moves = moveGenerator.generateMoves(board);
			for (int moveSquare: moves) {
				board.makeMove(moveSquare, COMP);

				value = Math.min(value, alphabeta(board, depth - 1, alpha, beta, true));
				beta = Math.min(beta, value);
				if (beta <= alpha) {
					board.undoLastMove();
					break;
				}
				board.undoLastMove();
			}
			return value;
		}
	}



	/*
	function alphabeta(node, depth, α, β, maximizingPlayer) is
    if depth = 0 or node is a terminal node then
        return the heuristic value of node
    if maximizingPlayer then
        value := −∞
        for each child of node do
            value := max(value, alphabeta(child, depth − 1, α, β, FALSE))
            α := max(α, value)
            if α ≥ β then
                break (* β cutoff *)
        return value
    else
        value := +∞
        for each child of node do
            value := min(value, alphabeta(child, depth − 1, α, β, TRUE))
            β := min(β, value)
            if β ≤ α then
                break (* α cutoff *)
        return value
	 */

	private int evaluatePosition(Board board) {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		var r = new Random();
		return r.nextInt(500);
	}

	private boolean isFinalPosition(Board board) {
		return false;
	}

}
