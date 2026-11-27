package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameModelTest {

    @Test
    void turnSwitchesOnMiss() {
        GameModel game = new GameModel();
        boolean hit = game.shoot(5, 5);
        assertFalse(hit);
    }

    @Test
    void hitDoesNotSwitchTurn() {
        GameModel game = new GameModel();
        game.getCurrentBoard().placeShip(0, 0, 1, true);
        assertTrue(game.shoot(0, 0));
    }
}
