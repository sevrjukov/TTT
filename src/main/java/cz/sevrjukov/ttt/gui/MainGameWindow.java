package cz.sevrjukov.ttt.gui;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static cz.sevrjukov.ttt.util.Versions.PROGRAM_VERSION;

public class MainGameWindow extends JFrame {

    protected JButton btnNewGame;
    protected JButton btnMakeMove;
    protected JButton btnSaveGames;
    protected JTextPane infoTextPane;
    protected JTextPane statsTextPane;
    private GameBoardCanvas boardCanvas;
    private GameController controller;

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        MainGameWindow o = new MainGameWindow();
        o.init();
    }

    public void init() {
        controller = new GameController(this);
        javax.swing.SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
            controller.init();
        });

    }

    private void createAndShowGUI() {
        this.setTitle("Gomoku " + PROGRAM_VERSION);
        this.setBounds(100, 100, 1000, 640);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // icon
        try (var stream = new ByteArrayInputStream(GUIResources.ICON_IMAGE)) {
            setIconImage(ImageIO.read(stream));
        } catch (IOException e) {
            e.printStackTrace();
        }


        BorderLayout borderLayout = new BorderLayout();
        this.getContentPane().setLayout(borderLayout);

        var leftSidePannel = new JPanel();
        leftSidePannel.setPreferredSize(new Dimension(310, 400));

        this.getContentPane().add(leftSidePannel, BorderLayout.WEST);

        btnNewGame = new JButton("New game");
        btnNewGame.addActionListener(controller);
        leftSidePannel.add(btnNewGame);

        btnMakeMove = new JButton("Make first move");
        btnMakeMove.addActionListener(controller);
        leftSidePannel.add(btnMakeMove);

        btnSaveGames = new JButton("Save games");
        btnSaveGames.addActionListener(controller);
        leftSidePannel.add(btnSaveGames);

        infoTextPane = new JTextPane();
        infoTextPane.setPreferredSize(new Dimension(280, 200));

        infoTextPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        infoTextPane.setEditable(false);
        leftSidePannel.add(infoTextPane);

        statsTextPane = new JTextPane();
        statsTextPane.setPreferredSize(new Dimension(280, 110));
        statsTextPane.setText("Engine statistics");
        statsTextPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        Font smallFont = new Font("Monospaced", Font.PLAIN, 10);
        statsTextPane.setFont(smallFont);
        statsTextPane.setForeground(Color.DARK_GRAY);
        statsTextPane.setEditable(false);
        leftSidePannel.add(statsTextPane);

        var boardPanel = new JPanel();
        this.getContentPane().add(boardPanel, BorderLayout.CENTER);

        setVisible(true);

        initGameBoard(boardPanel);
    }

    private void initGameBoard(JPanel boardPanel) {
        boardCanvas = new GameBoardCanvas(controller.getBoardModel());
        boardCanvas.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        controller.boardClicked(e);
                    }
                });
        boardPanel.add(boardCanvas);
        boardCanvas.init();
    }


    public void refreshBoard() {
        boardCanvas.repaint();
    }


    public void appendTextMessage(String text) {
        var currentText = infoTextPane.getText();
        var lines = currentText.split(System.lineSeparator());
        if (lines.length < 11) {
            if (currentText.isBlank()) {
                infoTextPane.setText(text);
            } else {
                infoTextPane.setText(currentText + System.lineSeparator() + text);
            }
        } else {
            var builder = new StringBuilder();
            for (int i = 1; i < lines.length; i++) {
                builder.append(lines[i].trim());
                builder.append(System.lineSeparator());
            }
            builder.append(text);
            infoTextPane.setText(builder.toString());
        }
    }
}
