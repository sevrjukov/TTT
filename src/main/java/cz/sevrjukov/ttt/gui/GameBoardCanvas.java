package cz.sevrjukov.ttt.gui;

import java.awt.BasicStroke;
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
	private static final int WINNING_LINE_OFFSET = (FIELD_SIZE / 2) + 1;


	private static final Color BACKGROUND_COLOR = new Color(199, 217, 211);
	private static final Color LINES_COLOR = new Color(237, 247, 244);
	private static final Color BLUE_CIRCLE_COLOR = new Color(95, 141, 218);
	private static final Color ORANGE_CIRCLE_COLOR = new Color(161, 86, 28);
	private static final Color LAST_MOVE_COLOR = new Color(218, 240, 233);

	private BoardModel boardModel;

	public GameBoardCanvas(BoardModel boardModel) {
		this.boardModel = boardModel;
	}

	public void init() {
		createBufferStrategy(2);
	}

	@Override
	public void paint(Graphics g) {

		var bufferStrategy = getBufferStrategy();
		var graphics = bufferStrategy.getDrawGraphics();
		try {
			final Graphics2D g2 = setRenderingHints((Graphics2D) graphics);

			paintBackground(g2);
			paintMoves(g2);
			paintWinningSequence(g2);
		} finally {
			graphics.dispose();
		}
		bufferStrategy.show();

	}

	private void paintBackground(Graphics2D g) {
		var parentColor = getParent().getBackground();
		g.setColor(parentColor);
		g.fillRect(0, 0, DIMENSION, DIMENSION);
		g.setColor(BACKGROUND_COLOR);
		g.fillRoundRect(0, 0, DIMENSION, DIMENSION, 20, 20);
		paintLines(g);
	}

	private void paintMoves(Graphics2D g) {

		var movesList = boardModel.getMovesList();

		for (int moveNum = 0; moveNum < movesList.size(); moveNum++) {
			var move = movesList.get(moveNum);
			if (moveNum == movesList.size() - 1 && move.side == COMPUTER) {
				paintLastMove(g, move.squareNum, move.side);
			} else {
				paintMove(g, move.squareNum, move.side);
			}
		}
	}

	private void paintWinningSequence(Graphics2D g) {
		if (boardModel.isWinningPosition()) {
			int[] line = boardModel.getWinningSequence();
			paintWinningLine(g, line[0], line[1]);
		}
	}

	private void paintWinningLine(Graphics2D g, int startSqNum, int endSqNum) {

		g.setColor(ORANGE_CIRCLE_COLOR);

		int startSqX = startSqNum % NUM_CELLS;
		int startSqY = startSqNum / NUM_CELLS;
		int x1 = startSqX * FIELD_SIZE + WINNING_LINE_OFFSET;
		int y1 = startSqY * FIELD_SIZE + WINNING_LINE_OFFSET;

		int endSqX = endSqNum % NUM_CELLS;
		int endSqY = endSqNum / NUM_CELLS;

		int x2 = endSqX * FIELD_SIZE + WINNING_LINE_OFFSET;
		int y2 = endSqY * FIELD_SIZE + WINNING_LINE_OFFSET;

		g.setStroke(new BasicStroke(3));
		g.drawLine(x1, y1, x2, y2);
	}


	@Override
	public Dimension getPreferredSize() {
		return new Dimension(DIMENSION, DIMENSION);
	}


	public void paintMove(Graphics2D g, int squareNum, int side) {
		// square coordinates
		int sqX = squareNum % NUM_CELLS;
		int sqY = squareNum / NUM_CELLS;

		int coordX = sqX * FIELD_SIZE + CIRCLE_OFFSET;
		int coordY = sqY * FIELD_SIZE + CIRCLE_OFFSET;

		Color circleColor = (side == COMPUTER) ? ORANGE_CIRCLE_COLOR : BLUE_CIRCLE_COLOR;
		paintCircle(g, coordX, coordY, circleColor);
	}

	// highlighted one
	public void paintLastMove(Graphics2D g, int squareNum, int size) {

		// draw a small highlighted square
		int sqX = squareNum % NUM_CELLS;
		int sqY = squareNum / NUM_CELLS;

		int coordX = sqX * FIELD_SIZE;
		int coordY = sqY * FIELD_SIZE;

		g.setColor(LAST_MOVE_COLOR);
		g.fillRect(coordX, coordY, FIELD_SIZE, FIELD_SIZE);

		paintMove(g, squareNum, size);
	}

	private void paintCircle(Graphics2D g, int x, int y, Color color) {
		g.setColor(color);
		g.fillOval(x, y, CIRCLE_SIZE, CIRCLE_SIZE);
	}


	private void paintLines(Graphics g) {
		g.setColor(LINES_COLOR);
		// verticals
		var offset = 0;
		for (int i = 0; i < NUM_CELLS - 1; i++) {
			offset += FIELD_SIZE;
			g.drawLine(offset, LINES_OFFSET, offset, DIMENSION - LINES_OFFSET);
			g.drawLine(LINES_OFFSET, offset, DIMENSION - LINES_OFFSET, offset);
		}
	}

	private Graphics2D setRenderingHints(Graphics2D g2) {
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		rh.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
		rh.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE));
		rh.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));
		g2.setRenderingHints(rh);
		return g2;
	}
}
