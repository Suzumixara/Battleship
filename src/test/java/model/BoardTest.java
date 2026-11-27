package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void shipPlacedCorrectly() {
        Board board = new Board();
        assertTrue(board.placeShip(0, 0, 4, true));
    }

    @Test
    void cannotPlaceShipOutOfBounds() {
        Board board = new Board();
        assertFalse(board.placeShip(8, 0, 4, true));
    }

    @Test
    void cannotPlaceShipsTouching() {
        Board board = new Board();
        board.placeShip(0, 0, 3, true);
        assertFalse(board.placeShip(0, 1, 2, true));
    }

    @Test
    void hitAndMissWorkCorrectly() {
        Board board = new Board();
        board.placeShip(0, 0, 1, true);

        assertTrue(board.shoot(0, 0));
        assertFalse(board.shoot(5, 5));
    }

    @Test
    void gameOverDetected() {
        Board board = new Board();
        board.placeShip(0, 0, 1, true);
        board.shoot(0, 0);
        assertTrue(board.isGameOver());
    }
}
