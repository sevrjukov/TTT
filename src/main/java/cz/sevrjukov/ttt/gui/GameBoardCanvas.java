package cz.sevrjukov.ttt.gui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

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

	private BoardModel boardModel;

	public GameBoardCanvas(BoardModel boardModel) {
		this.boardModel = boardModel;
	}

	@Override
	public void paint(Graphics g) {

		g.setColor(BACKGROUND_COLOR);

		g.fillRoundRect(0, 0, DIMENSION, DIMENSION, 20, 20);
		paintLines();

		var movesList = boardModel.getMovesList();

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

		int coordX = sqX * FIELD_SIZE + CIRCLE_OFFSET + 1;
		int coordY = sqY * FIELD_SIZE + CIRCLE_OFFSET + 1;

		Color circleColor = (side == COMPUTER) ? ORANGE_CIRCLE_COLOR : BLUE_CIRCLE_COLOR;
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
		Graphics2D g2 = (Graphics2D) g;

		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		rh.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		rh.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE));
		rh.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));

		g2.setRenderingHints(rh);
		g2.setColor(color);

		g2.fillOval(x, y, CIRCLE_SIZE, CIRCLE_SIZE);
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
