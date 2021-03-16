package cz.sevrjukov.ttt.gui;

import cz.sevrjukov.ttt.board.Board;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;

public class GameBoardCanvas extends Canvas {

	public static final int NUM_CELLS = 19;
	public static final int FIELD_SIZE = 26;

	private static final int DIMENSION = NUM_CELLS * FIELD_SIZE;

	private static final int CIRCLE_SIZE = FIELD_SIZE - 12;
	private static final int CIRCLE_OFFSET = (FIELD_SIZE - CIRCLE_SIZE) / 2;
	private static final int LINES_OFFSET = 6;



	private static final Color BACKGROUND_COLOR = new Color(235, 239, 245);
	private static final Color LINES_COLOR = new Color(199, 205, 214);
	private static final Color BLUE_CIRCLE_COLOR = new Color(95, 141, 218);
	private static final Color ORANGE_CIRCLE_COLOR = new Color(161, 86, 28);

	private Board board;

	public GameBoardCanvas(Board board) {
		this.board = board;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		this.setBackground(BACKGROUND_COLOR);
		paintLines();

		var movesList = board.getMovesHistory();

		for (var move : movesList) {
			drawMove(move.squareNum, move.side);
		}

	}


	@Override
	public Dimension getPreferredSize() {
		return new Dimension(DIMENSION, DIMENSION);
	}

	public void drawMove(int squareNum, int side) {
		// square coordinates
		int sqX = squareNum % NUM_CELLS;
		int sqY = squareNum / NUM_CELLS + 1;

		int rectTopLeftX = sqX * FIELD_SIZE + CIRCLE_OFFSET;
		int rectTopLeftY = sqY * FIELD_SIZE + CIRCLE_OFFSET;

		Color circleColor = (side == COMPUTER) ? BLUE_CIRCLE_COLOR : ORANGE_CIRCLE_COLOR;
		drawCircle(rectTopLeftX, rectTopLeftY, circleColor);
	}

	private void drawCircle(int x, int y, Color color) {
		Graphics g = getGraphics();
		g.setColor(color);
		g.fillOval(x, y, CIRCLE_SIZE, CIRCLE_SIZE);
	}


	private void paintLines() {
		Graphics g = getGraphics();
		g.setColor(LINES_COLOR);
		// verticals
		var offset = 0;
		for (int i = 0; i < NUM_CELLS - 1; i++) {
			offset += FIELD_SIZE;
			g.drawLine(offset, LINES_OFFSET, offset, DIMENSION - LINES_OFFSET);
			g.drawLine(LINES_OFFSET, offset, DIMENSION - LINES_OFFSET, offset);
		}
	}


}
