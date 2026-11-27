package server;

import common.Message;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final List<GameRoom> rooms = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Сервер запущен на порту 5000");

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> handleClient(socket)).start();
        }
    }

    private static void handleClient(Socket socket) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            Message firstMsg = (Message) in.readObject();

            GameRoom room = null;

            synchronized (rooms) {
                if (firstMsg.type == Message.Type.CREATE_GAME) {
                    room = new GameRoom();
                    rooms.add(room);
                }
                else if (firstMsg.type == Message.Type.JOIN_GAME) {
                    for (GameRoom r : rooms) {
                        if (!r.isFull()) {
                            room = r;
                            break;
                        }
                    }
                }

                if (room == null) {
                    room = new GameRoom();
                    rooms.add(room);
                }
            }

            room.addPlayer(socket, in, out);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
