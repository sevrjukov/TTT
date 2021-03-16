package cz.sevrjukov.ttt.gui;

import cz.sevrjukov.ttt.board.Board;
import cz.sevrjukov.ttt.board.Move;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Stack;

import static cz.sevrjukov.ttt.board.Board.COMPUTER;

public class GameBoardCanvas extends Canvas {

	public static final int NUM_CELLS = 19;
	public static final int FIELD_SIZE = 30;

	private static final int DIMENSION = NUM_CELLS * FIELD_SIZE;

	private static final int CIRCLE_SIZE = FIELD_SIZE - 12;
	private static final int CIRCLE_OFFSET = (FIELD_SIZE - CIRCLE_SIZE) / 2;
	private static final int LINES_OFFSET = 6;


	private static final Color BACKGROUND_COLOR = new Color(199, 217, 211);
	private static final Color LINES_COLOR = new Color(237, 247, 244);
	private static final Color BLUE_CIRCLE_COLOR = new Color(95, 141, 218);
	private static final Color ORANGE_CIRCLE_COLOR = new Color(161, 86, 28);
	private static final Color LAST_MOVE_COLOR = new Color(218, 240, 233);

	private Board board;

	public GameBoardCanvas(Board board) {
		this.board = board;
	}

	@Override
	public void paint(Graphics g) {

		g.setColor(BACKGROUND_COLOR);

		g.fillRoundRect(0, 0, DIMENSION, DIMENSION, 20, 20);
		paintLines();

		var movesList = (Stack<Move>) board.getMovesHistory().clone();

		for (int moveNum = 0; moveNum < movesList.size(); moveNum++) {
			var move = movesList.get(moveNum);
			if (moveNum == movesList.size() - 1 && move.side == COMPUTER) {
				drawLastMove(move.squareNum, move.side);
			} else {
				drawMove(move.squareNum, move.side);
			}
		}

	}


	@Override
	public Dimension getPreferredSize() {
		return new Dimension(DIMENSION, DIMENSION);
	}


	public void drawMove(int squareNum, int side) {
		// square coordinates
		int sqX = squareNum % NUM_CELLS;
		int sqY = squareNum / NUM_CELLS;

		int coordX = sqX * FIELD_SIZE + CIRCLE_OFFSET;
		int coordY = sqY * FIELD_SIZE + CIRCLE_OFFSET;

		Color circleColor = (side == COMPUTER) ? BLUE_CIRCLE_COLOR : ORANGE_CIRCLE_COLOR;
		drawCircle(coordX, coordY, circleColor);
	}

	// highlighted one
	public void drawLastMove(int squareNum, int size) {

		// draw a small highlighted square
		int sqX = squareNum % NUM_CELLS;
		int sqY = squareNum / NUM_CELLS;

		int coordX = sqX * FIELD_SIZE;
		int coordY = sqY * FIELD_SIZE;

		Graphics g = getGraphics();
		g.setColor(LAST_MOVE_COLOR);

		g.fillRect(coordX, coordY, FIELD_SIZE, FIELD_SIZE);

		drawMove(squareNum, size);
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
