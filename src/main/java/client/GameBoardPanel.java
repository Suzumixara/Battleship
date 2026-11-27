package client;

import common.Constants;
import common.Message;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameBoardPanel extends JPanel {
    private final boolean own;
    private final Client client;
    private boolean[][] ships;
    private boolean[][] hits;   // попадания на этом поле
    private boolean[][] misses; // промахи на этом поле

    public GameBoardPanel(boolean own, Client client) {
        this.own = own;
        this.client = client;

        if (own) {
            this.ships = client.getPlayerShips();
            this.hits = client.getEnemyHits();   // противник попал по мне
            this.misses = client.getEnemyMisses(); // противник промахнулся по мне
        } else {
            this.ships = new boolean[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
            this.hits = client.getMyShots();     // мои попадания по противнику
            this.misses = client.getMyMisses();  // мои промахи по противнику
        }

        setPreferredSize(new Dimension(400, 400));
        setEnabled(!own);

        if (!own) {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isEnabled()) {
                        int cellSizeX = getWidth() / Constants.BOARD_SIZE;
                        int cellSizeY = getHeight() / Constants.BOARD_SIZE;
                        int cellX = e.getX() / cellSizeX;
                        int cellY = e.getY() / cellSizeY;
                        cellX = Math.min(cellX, Constants.BOARD_SIZE - 1);
                        cellY = Math.min(cellY, Constants.BOARD_SIZE - 1);

                        if (hits[cellX][cellY] || misses[cellX][cellY]) {
                            JOptionPane.showMessageDialog(GameBoardPanel.this,
                                    "Вы уже стреляли в эту клетку!");
                            return;
                        }

                        Message msg = new Message(Message.Type.SHOOT);
                        msg.x = cellX;
                        msg.y = cellY;
                        client.sendMessage(msg);
                        setEnabled(false);
                    }
                }
            });
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int cellSize = Math.min(getWidth(), getHeight()) / Constants.BOARD_SIZE;

        for (int x = 0; x < Constants.BOARD_SIZE; x++) {
            for (int y = 0; y < Constants.BOARD_SIZE; y++) {
                int xPos = x * cellSize;
                int yPos = y * cellSize;

                // Фон клетки
                g.setColor(Color.CYAN);
                g.fillRect(xPos, yPos, cellSize, cellSize);

                // Отображение кораблей (только на своем поле)
                if (own && ships[x][y]) {
                    g.setColor(Color.GRAY);
                    g.fillRect(xPos + 2, yPos + 2, cellSize - 4, cellSize - 4);
                }

                // Отображение попаданий
                if (hits[x][y]) {
                    g.setColor(Color.RED);
                    g.fillOval(xPos + 5, yPos + 5, cellSize - 10, cellSize - 10);
                }

                // Отображение промахов
                if (misses[x][y]) {
                    g.setColor(Color.BLUE);
                    g.drawLine(xPos + 5, yPos + 5, xPos + cellSize - 5, yPos + cellSize - 5);
                    g.drawLine(xPos + cellSize - 5, yPos + 5, xPos + 5, yPos + cellSize - 5);
                }

                // Сетка
                g.setColor(Color.BLACK);
                g.drawRect(xPos, yPos, cellSize, cellSize);
            }
        }
    }

    public void updateBoard() {
        if (own) {
            this.hits = client.getEnemyHits();
            this.misses = client.getEnemyMisses();
        } else {
            this.hits = client.getMyShots();
            this.misses = client.getMyMisses();
        }
        repaint();
    }
}