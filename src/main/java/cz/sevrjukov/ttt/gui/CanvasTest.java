package cz.sevrjukov.ttt.gui;

import cz.sevrjukov.ttt.board.Board;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CanvasTest extends JFrame {

	public CanvasTest() {
		initGui();
	}

	public static void main(String[] args) {

		CanvasTest o = new CanvasTest();

	}

	private void initGui() {
		this.setTitle("Canvas test");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		var board = new Board();
		board.parseBoard("----------------"
				+ "---xoxx----"
				+ "---oxxoxxxoxx-ooo-oxxx--ooo-xxx");

		GameBoardCanvas canvas = new GameBoardCanvas(board);

		JButton paintStuffBtn = new JButton("paint stuff");

		paintStuffBtn.addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {

						canvas.repaint();
					}
				}
		);

		canvas.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int xCoord= e.getX();
				int yCoord = e.getY();

				int sqX = xCoord / GameBoardCanvas.FIELD_SIZE;
				int sqy = yCoord / GameBoardCanvas.FIELD_SIZE;
				int sqNum = sqy * GameBoardCanvas.NUM_CELLS + sqX;
				System.out.println(sqX + " " + sqy + " sq num " + sqNum);
			}
		});

		this.getContentPane().setLayout(new FlowLayout());
		this.getContentPane().add(canvas);
		this.getContentPane().add(paintStuffBtn);
		this.pack();
		this.setVisible(true);
	}


}
