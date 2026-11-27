package client;

import common.Message;

import javax.swing.*;
import java.awt.*;

public class StartMenu extends JFrame {

    private final Client client;

    public StartMenu(Client client) {
        this.client = client;

        setTitle("Морской бой");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1, 10, 10));

        JButton createBtn = new JButton("Создать игру");
        JButton joinBtn = new JButton("Присоединиться");

        add(createBtn);
        add(joinBtn);

        createBtn.addActionListener(e -> {
            client.sendMessage(new Message(Message.Type.CREATE_GAME));
            openPlacement();
        });

        joinBtn.addActionListener(e -> {
            client.sendMessage(new Message(Message.Type.JOIN_GAME));
            openPlacement();
        });
    }

    private void openPlacement() {
        JFrame frame = new JFrame("Расстановка кораблей");
        frame.setSize(500, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new ShipPlacement(client));
        client.setFrame(frame);
    }
}
