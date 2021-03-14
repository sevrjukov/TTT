package cz.sevrjukov.ttt.engine;

import cz.sevrjukov.ttt.board.Board;

public class Evaluator {


	public int alphabeta(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {

		if (depth == 0 || isFinalPosition(board.getPosition())) {
			return evaluatePosition(board.getPosition());
		}

		if (maximizingPlayer) {
			int val = Integer.MIN_VALUE;

		}

		return -1;
	}

	private int evaluatePosition(int [] position) {
		return 0;
	}

	private boolean isFinalPosition(int [] position) {
		return false;
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

}
