package model;

import common.Constants;

public class Board {

    private final boolean[][] ships = new boolean[Constants.BOARD_SIZE][Constants.BOARD_SIZE];
    private final boolean[][] hits = new boolean[Constants.BOARD_SIZE][Constants.BOARD_SIZE];

    public boolean placeShip(int x, int y, int size, boolean horizontal) {
        if (!canPlaceShip(x, y, size, horizontal)) return false;

        for (int i = 0; i < size; i++) {
            if (horizontal) ships[x + i][y] = true;
            else ships[x][y + i] = true;
        }
        return true;
    }

    public boolean shoot(int x, int y) {
        hits[x][y] = true;
        return ships[x][y];
    }

    public boolean isGameOver() {
        for (int x = 0; x < Constants.BOARD_SIZE; x++) {
            for (int y = 0; y < Constants.BOARD_SIZE; y++) {
                if (ships[x][y] && !hits[x][y]) return false;
            }
        }
        return true;
    }

    boolean canPlaceShip(int x, int y, int size, boolean horizontal) {
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
}
