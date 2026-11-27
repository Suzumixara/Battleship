package server;

import common.Message;
import java.io.*;
import java.net.Socket;

public class PlayerHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private GameRoom room;
    private int id;
    private boolean[][] ships = new boolean[10][10];
    private boolean[][] hits = new boolean[10][10];

    public PlayerHandler(Socket socket, ObjectInputStream in, ObjectOutputStream out, GameRoom room, int id) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.room = room;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message msg = (Message) in.readObject();

                if (msg.type == Message.Type.PLACE_SHIPS) {
                    this.ships = msg.ships;
                    System.out.println("Player " + id + " placed ships");
                } else if (msg.type == Message.Type.SHOOT) {
                    System.out.println("Player " + id + " shoots at " + msg.x + "," + msg.y);

                    PlayerHandler opponent = room.getOpponent(this);
                    boolean hit = opponent.ships[msg.x][msg.y];
                    opponent.hits[msg.x][msg.y] = hit;

                    boolean sunk = checkSunk(opponent, msg.x, msg.y);

                    // Результат для стрелка — на поле противника
                    Message res = new Message(Message.Type.RESULT);
                    res.x = msg.x;
                    res.y = msg.y;
                    res.hit = hit;
                    res.sunk = sunk;
                    res.targetIsEnemy = true; // обновляем enemyBoard стрелка
                    sendMessage(res);

                    // Результат для противника — на его поле
                    Message oppRes = new Message(Message.Type.RESULT);
                    oppRes.x = msg.x;
                    oppRes.y = msg.y;
                    oppRes.hit = hit;
                    oppRes.sunk = sunk;
                    oppRes.targetIsEnemy = false; // обновляем ownBoard противника
                    opponent.sendMessage(oppRes);

                    if (checkGameOver(opponent)) {
                        Message gameOver = new Message(Message.Type.GAME_OVER);
                        gameOver.hit = true; // победил стрелявший
                        sendMessage(gameOver);
                        gameOver.hit = false; // проиграл противник
                        opponent.sendMessage(gameOver);
                    } else {
                        room.nextTurn();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkSunk(PlayerHandler opponent, int x, int y) {
        return false;
    }

    private boolean checkGameOver(PlayerHandler player) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (player.ships[i][j] && !player.hits[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public void sendMessage(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}