package server;

import common.Message;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameRoom {
    private List<PlayerHandler> players = new ArrayList<>();
    private int currentPlayer = 0;

    public synchronized void addPlayer(Socket socket, ObjectInputStream in, ObjectOutputStream out) throws IOException {
        PlayerHandler player = new PlayerHandler(socket, in, out, this, players.size());
        players.add(player);
        new Thread(player).start();

        if (players.size() == 2) {
            players.get(0).sendMessage(new Message(Message.Type.START));
            players.get(1).sendMessage(new Message(Message.Type.WAIT));
        }
    }

    public boolean isFull() {
        return players.size() == 2;
    }

    public synchronized void nextTurn() {
        currentPlayer = 1 - currentPlayer;
        players.get(currentPlayer).sendMessage(new Message(Message.Type.START));
        players.get(1 - currentPlayer).sendMessage(new Message(Message.Type.WAIT));
    }

    public PlayerHandler getOpponent(PlayerHandler player) {
        return players.get(1 - player.getId());
    }
}
