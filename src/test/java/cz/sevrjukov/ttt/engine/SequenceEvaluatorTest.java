package cz.sevrjukov.ttt.engine;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;
import static cz.sevrjukov.ttt.board.Board.EMPTY;
import static cz.sevrjukov.ttt.board.Board.HUMAN;

public class SequenceEvaluatorTest {


	@Test
	public void testSimple() {

		SequenceEvaluator evaluator = new SequenceEvaluator(COMPUTER);
		evaluator.newSequence();

//		var sequence = parseLine("--xx-oxx-");
		var sequence = parseLine("--xxxx-oo---");
		for (int sq: sequence) {
			evaluator.feedNextSquare(sq);
		}
		var result = evaluator.getEvaluation();
		System.out.println(result);
	}

	private List<Integer> parseLine(String line) {
		List<Integer> result = new ArrayList<>();
		for (char c: line.toUpperCase(Locale.ROOT).toCharArray()) {
			if (c == 'X') {
				result.add(COMPUTER);
			} else
			if (c == 'O') {
				result.add(HUMAN);
			} else
			if (c == '-') {
				result.add(EMPTY);
			} else {
				throw new IllegalArgumentException("invalid string");
			}
		}
		return result;
	}
}
