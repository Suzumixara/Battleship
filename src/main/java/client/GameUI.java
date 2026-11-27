package client;

import common.Message;
import javax.swing.*;
import java.awt.*;

public class GameUI extends JPanel {
    private final GameBoardPanel ownBoard;
    private final GameBoardPanel enemyBoard;
    private final Client client;

    public GameUI(Client client) {
        this.client = client;
        setLayout(new GridLayout(1, 2));

        ownBoard = new GameBoardPanel(true, client);
        enemyBoard = new GameBoardPanel(false, client);

        add(wrap("Ваше поле", ownBoard));
        add(wrap("Поле противника", enemyBoard));
    }

    private JPanel wrap(String title, JPanel panel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new JLabel(title, SwingConstants.CENTER), BorderLayout.NORTH);
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    public void handleMessage(Message msg) {
        SwingUtilities.invokeLater(() -> {
            switch (msg.type) {
                case RESULT -> {
                    // Обновляем оба поля
                    ownBoard.updateBoard();
                    enemyBoard.updateBoard();

                    // Показываем сообщение о результате выстрела
                    String message = msg.targetIsEnemy
                            ? (msg.hit ? "Вы попали!" : "Вы промахнулись!")
                            : (msg.hit ? "Противник попал в ваш корабль!" : "Противник промахнулся!");
                    JOptionPane.showMessageDialog(this, message);
                }
                case WAIT -> {
                    enemyBoard.setEnabled(false);
                    JOptionPane.showMessageDialog(this, "Ожидайте хода противника...");
                }
                case START -> {
                    enemyBoard.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "Ваш ход!");
                }
                case GAME_OVER -> {
                    String result = msg.hit ? "Вы победили!" : "Вы проиграли!";
                    JOptionPane.showMessageDialog(this, "Игра окончена! " + result);
                    enemyBoard.setEnabled(false);
                    ownBoard.setEnabled(false);
                }
            }
        });
    }
}