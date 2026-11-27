package client;

import common.Constants;
import common.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ShipPlacement extends JPanel {

    private final Client client;

    private final boolean[][] ships =
            new boolean[Constants.BOARD_SIZE][Constants.BOARD_SIZE];

    private final JPanel boardPanel;

    private final int[] shipsToPlace = Constants.SHIP_SIZES;
    private int currentShipIndex = 0;

    private boolean horizontal = true;

    public ShipPlacement(Client client) {
        this.client = client;
        setLayout(new BorderLayout());

        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };

        boardPanel.setPreferredSize(new Dimension(400, 400));
        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleBoardClick(e);
            }
        });

        add(boardPanel, BorderLayout.CENTER);

        JPanel buttons = new JPanel();

        JButton rotateBtn = new JButton("Повернуть");
        JButton autoBtn = new JButton("Авто");
        JButton clearBtn = new JButton("Очистить");
        JButton confirmBtn = new JButton("Подтвердить");

        buttons.add(rotateBtn);
        buttons.add(autoBtn);
        buttons.add(clearBtn);
        buttons.add(confirmBtn);

        add(buttons, BorderLayout.SOUTH);

        rotateBtn.addActionListener(e -> horizontal = !horizontal);
        autoBtn.addActionListener(e -> autoPlaceShips());
        clearBtn.addActionListener(e -> clearBoard());
        confirmBtn.addActionListener(e -> confirmPlacement());
    }

    private void drawBoard(Graphics g) {
        int cellSize = Math.min(boardPanel.getWidth(), boardPanel.getHeight())
                / Constants.BOARD_SIZE;

        for (int x = 0; x < Constants.BOARD_SIZE; x++) {
            for (int y = 0; y < Constants.BOARD_SIZE; y++) {

                int px = x * cellSize;
                int py = y * cellSize;

                g.setColor(Color.CYAN);
                g.fillRect(px, py, cellSize, cellSize);

                if (ships[x][y]) {
                    g.setColor(Color.GRAY);
                    g.fillRect(px + 2, py + 2, cellSize - 4, cellSize - 4);
                }

                g.setColor(Color.BLACK);
                g.drawRect(px, py, cellSize, cellSize);
            }
        }

        g.setColor(Color.BLACK);
        g.drawString(
                currentShipIndex < shipsToPlace.length
                        ? "Корабль: " + shipsToPlace[currentShipIndex]
                        + " | " + (horizontal ? "Горизонталь" : "Вертикаль")
                        : "Все корабли расставлены",
                10, 15
        );
    }

    private void handleBoardClick(MouseEvent e) {

        if (SwingUtilities.isRightMouseButton(e)) {
            horizontal = !horizontal;
            boardPanel.repaint();
            return;
        }

        if (!SwingUtilities.isLeftMouseButton(e)) return;

        if (currentShipIndex >= shipsToPlace.length) {
            JOptionPane.showMessageDialog(this,
                    "Все корабли уже расставлены");
            return;
        }

        int cellSizeX = boardPanel.getWidth() / Constants.BOARD_SIZE;
        int cellSizeY = boardPanel.getHeight() / Constants.BOARD_SIZE;

        int x = e.getX() / cellSizeX;
        int y = e.getY() / cellSizeY;

        int size = shipsToPlace[currentShipIndex];

        if (!canPlaceShip(x, y, size, horizontal)) {
            JOptionPane.showMessageDialog(this,
                    "Нельзя поставить корабль здесь");
            return;
        }

        placeShip(x, y, size, horizontal);
        currentShipIndex++;
        boardPanel.repaint();
    }

    private void placeShip(int x, int y, int size, boolean horizontal) {
        for (int i = 0; i < size; i++) {
            if (horizontal) ships[x + i][y] = true;
            else ships[x][y + i] = true;
        }
    }

    private boolean canPlaceShip(int x, int y, int size, boolean horizontal) {
        if (horizontal && x + size > Constants.BOARD_SIZE) return false;
        if (!horizontal && y + size > Constants.BOARD_SIZE) return false;

        for (int i = -1; i <= size; i++) {
            for (int j = -1; j <= 1; j++) {

                int nx = horizontal ? x + i : x + j;
                int ny = horizontal ? y + j : y + i;

                if (nx >= 0 && nx < Constants.BOARD_SIZE &&
                        ny >= 0 && ny < Constants.BOARD_SIZE) {

                    if (ships[nx][ny]) return false;
                }
            }
        }
        return true;
    }

    private void clearBoard() {
        for (int x = 0; x < Constants.BOARD_SIZE; x++) {
            for (int y = 0; y < Constants.BOARD_SIZE; y++) {
                ships[x][y] = false;
            }
        }
        currentShipIndex = 0;
        boardPanel.repaint();
    }

    private void autoPlaceShips() {
        clearBoard();

        for (int size : shipsToPlace) {
            boolean placed = false;

            while (!placed) {
                int x = (int) (Math.random() * Constants.BOARD_SIZE);
                int y = (int) (Math.random() * Constants.BOARD_SIZE);
                boolean hor = Math.random() < 0.5;

                if (canPlaceShip(x, y, size, hor)) {
                    placeShip(x, y, size, hor);
                    placed = true;
                }
            }
            currentShipIndex++;
        }

        boardPanel.repaint();
    }

    private void confirmPlacement() {
        if (currentShipIndex < shipsToPlace.length) {
            JOptionPane.showMessageDialog(this,
                    "Расставьте все корабли!");
            return;
        }

        client.setPlayerShips(ships);
        Message msg = new Message(Message.Type.PLACE_SHIPS);
        msg.ships = ships;
        client.sendMessage(msg);

        JOptionPane.showMessageDialog(this,
                "Корабли расставлены. Ожидание начала игры...");
    }
}
