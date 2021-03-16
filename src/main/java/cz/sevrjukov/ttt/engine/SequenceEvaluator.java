package cz.sevrjukov.ttt.engine;

import static cz.sevrjukov.ttt.board.Board.EMPTY;
import static cz.sevrjukov.ttt.board.Board.UNDEFINED;

public class SequenceEvaluator {

	/**
	 * Points given for opened positions
	 */
	public static final int OPENED_FOUR = 10000;
	public static final int OPENED_THREE = 20;
	public static final int OPENED_TWO = 1;
	// necessary length of the line to win:
	private static final int LENGTH_TO_WIN = 5;
	private int finalEvaluation = 0;
	private int previousSquare = UNDEFINED;
	private int playerToEvaluate;

	/**
	 * Sequence is a row of my squares or empty squares. When an opponent square appears, sequence is restarted.
	 */
	private int sequenceLength;
	private int numMySquares; // number of my squares in the sequence
	private int numHoles; // number of "holes" in the sequence
	private boolean endsWithHole;
	private boolean startsWithHole;
	private int adjacentSquares; // for quick detecting win

	public SequenceEvaluator(int playerToEvaluate) {
		this.playerToEvaluate = playerToEvaluate;
	}

	private void reset() {
		sequenceLength = 0;
		numMySquares = 0;
		numHoles = 0;
		adjacentSquares = 0;
		endsWithHole = false;
		startsWithHole = false;
	}

	public void newSequence() {
		reset();
		finalEvaluation = 0;
	}

	/**
	 * Algorithm performs analysis on the input row of squares, so it doesn't care if it's vertical, diagonal or horizontal.<br>
	 * <br>
	 * Sequence is a row of my squares or empty squares. If opponent's square or end-of-line square is reached, the sequence restarts.<br>
	 * <br>
	 * The input sequence is divided into "rounds" - subsequences of my squares, ended with an empty square. addVal() function is triggered at the end of each round and then at the end of the whole
	 * sequence.<br>
	 * <br>
	 * The algorithm counts number of my squares in the sequence and rounds, number of empty squares (called "holes"), and identifies, if the sequence stars and/or ends with empty squares.<br>
	 * <br>
	 * The function returns true if an immediate win was just found.
	 */
	public boolean feedNextSquare(int sq) {
		// opponent's square - reset the machine
		if (sq != EMPTY && sq != playerToEvaluate) {
			if (previousSquare == EMPTY) {
				endsWithHole = true;
			}
			addEval();
			reset();
		}
		// on my square - increase count of my squares and check for the winning
		// position
		if (sq == playerToEvaluate) {
			numMySquares++;
			if (previousSquare == playerToEvaluate) {
				adjacentSquares++;
				if (adjacentSquares == LENGTH_TO_WIN - 1) {
					return true;
				}
			}
		}
		// detect starting with an empty squares
		if (sequenceLength == 0 && sq == EMPTY) {
			startsWithHole = true;
		}
		// increase the sequence length - total length of the line with empty
		// squares or my squares
		if (sq == EMPTY || sq == playerToEvaluate) {
			sequenceLength++;
		}
		// on empty square perform the subsequence evaluation, but don't reset
		// the sequence.
		// By a "hole" we understand an empty square between two my squares.
		if (sq == EMPTY && previousSquare == playerToEvaluate) {
			numHoles++;
			endsWithHole = true;
			addEval();
			endsWithHole = false;
		}
		// reset the number of adjacent squares - for detecting winning position
		if (sq == EMPTY) {
			adjacentSquares = 0;
		}
		previousSquare = sq;
		return false;
	}


	/**
	 * Evaluate the part of the sequence and add the evaluation to final result
	 */
	private void addEval() {
		/*
		 * Do nothing if total length of the sequence is lower than 5 because
		 * otherwise there's no sense to do anything - you never can generate a
		 * winning line from this sequence, therefore it gets no points.
		 */
		if (sequenceLength >= LENGTH_TO_WIN && numMySquares > 0) {
			int evalRound = 0; // evaluation for this particular round
			// (subsequence) in the sequence
			// remove the last hole, if there's any (because it's not really a hole)
			// if (numHoles > 0) numHoles--;
			if (endsWithHole && numHoles > 0) {
				numHoles--;
			}
			/*
			 * This is the evaluation itself - very simple. The less
			 */
			evalRound += (numMySquares - numHoles);
			if (startsWithHole && endsWithHole) {
				// special case - opened four, leads to win in the next move
				// (sort of checkmate :-))
				// so we give it a very high evaluation
				if (adjacentSquares == (LENGTH_TO_WIN - 2)) {
					evalRound = OPENED_FOUR;
				} else if (adjacentSquares == (LENGTH_TO_WIN - 3)) {
					// opened three is also a desired position - it's a "check"
					evalRound += OPENED_THREE;
				} else {
					// otherwise, just add a small bonus for opened position:
					evalRound += OPENED_TWO;
				}
			}
			finalEvaluation += evalRound;
		}
	}

	public int getEvaluation() {
		// feed artificial square to trigger the evaluation
		feedNextSquare(UNDEFINED);
		return finalEvaluation;
	}
}
