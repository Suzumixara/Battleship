package model;

public class GameModel {

    private final Board player1 = new Board();
    private final Board player2 = new Board();
    private int currentPlayer = 1;

    public Board getCurrentBoard() {
        return currentPlayer == 1 ? player2 : player1;
    }

    public void nextTurn() {
        currentPlayer = 3 - currentPlayer;
    }

    public boolean shoot(int x, int y) {
        boolean hit = getCurrentBoard().shoot(x, y);
        if (!hit) nextTurn();
        return hit;
    }

    public boolean isGameOver() {
        return player1.isGameOver() || player2.isGameOver();
    }
}
