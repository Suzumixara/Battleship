package client;

import common.Message;
import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class Client {
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private JFrame currentFrame;
    private boolean[][] playerShips = new boolean[10][10];

    private boolean[][] myShots = new boolean[10][10];     // мои выстрелы по противнику
    private boolean[][] myMisses = new boolean[10][10];   // мои промахи по противнику
    private boolean[][] enemyHits = new boolean[10][10];  // попадания противника по мне
    private boolean[][] enemyMisses = new boolean[10][10];// промахи противника по мне

    public Client(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        SwingUtilities.invokeLater(() -> {
            StartMenu menu = new StartMenu(this);
            setFrame(menu);
        });

        new Thread(this::listen).start();
    }

    public void setFrame(JFrame frame) {
        if (currentFrame != null) currentFrame.dispose();
        currentFrame = frame;
        currentFrame.setVisible(true);
    }

    public void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Геттеры
    public boolean[][] getPlayerShips() { return playerShips; }
    public boolean[][] getMyShots() { return myShots; }
    public boolean[][] getMyMisses() { return myMisses; }
    public boolean[][] getEnemyHits() { return enemyHits; }
    public boolean[][] getEnemyMisses() { return enemyMisses; }

    // Сеттеры
    public void setPlayerShips(boolean[][] ships) {
        this.playerShips = ships;
    }

    public void setMyShots(int x, int y, boolean hit) {
        if (hit) {
            myShots[x][y] = true;
        } else {
            myMisses[x][y] = true;
        }
    }

    public void setEnemyShots(int x, int y, boolean hit) {
        if (hit) {
            enemyHits[x][y] = true;
        } else {
            enemyMisses[x][y] = true;
        }
    }

    private void listen() {
        try {
            while (true) {
                Message msg = (Message) in.readObject();
                System.out.println("Client received: " + msg.type + " targetIsEnemy=" + msg.targetIsEnemy);

                if (msg.type == Message.Type.START) {
                    SwingUtilities.invokeLater(() -> {
                        GameUI gameUI = new GameUI(this);
                        JFrame frame = new JFrame("Морской бой");
                        frame.setSize(900, 500);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        frame.setContentPane(gameUI);
                        setFrame(frame);
                    });
                }

                // Обработка RESULT
                if (msg.type == Message.Type.RESULT) {
                    SwingUtilities.invokeLater(() -> {
                        if (msg.targetIsEnemy) {
                            // Это результат моего выстрела по врагу
                            setMyShots(msg.x, msg.y, msg.hit);
                            System.out.println("My shot at " + msg.x + "," + msg.y + " hit=" + msg.hit);
                        } else {
                            // Это результат выстрела противника по мне
                            setEnemyShots(msg.x, msg.y, msg.hit);
                            System.out.println("Enemy shot at " + msg.x + "," + msg.y + " hit=" + msg.hit);
                        }

                        if (currentFrame.getContentPane() instanceof GameUI gameUI) {
                            gameUI.handleMessage(msg);
                        }
                    });
                }

                // Обработка WAIT и GAME_OVER
                if (msg.type == Message.Type.WAIT || msg.type == Message.Type.GAME_OVER) {
                    SwingUtilities.invokeLater(() -> {
                        if (currentFrame.getContentPane() instanceof GameUI gameUI) {
                            gameUI.handleMessage(msg);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Client("localhost", 5000);
    }
}