package cz.sevrjukov.ttt.board;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Move {

	public int squareNum;
	public int side;
	public int maxBound;
	public int minBound;

}
