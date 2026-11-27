package common;

import java.io.Serializable;

public class Message implements Serializable {

    public enum Type {
        CREATE_GAME,
        JOIN_GAME,
        PLACE_SHIPS,
        SHOOT,
        RESULT,
        START,
        WAIT,
        GAME_OVER
    }

    public Type type;
    public int x, y;
    public boolean hit;
    public boolean sunk;
    public boolean[][] ships;
    public String info;
    public boolean targetIsEnemy;

    public Message(Type type) {
        this.type = type;
    }

    public Message() { }
}
