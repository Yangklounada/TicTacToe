package tictactoe.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CellTest {

    @Test
    void fromValueMapsIntsToCells() {
        assertEquals(Cell.EMPTY, Cell.fromValue(0));
        assertEquals(Cell.X, Cell.fromValue(1));
        assertEquals(Cell.O, Cell.fromValue(2));
    }

    @Test
    void toValueMapsCellsToInts() {
        assertEquals(0, Cell.EMPTY.toValue());
        assertEquals(1, Cell.X.toValue());
        assertEquals(2, Cell.O.toValue());
    }

    @Test
    void fromValueRejectsUnknownValues() {
        assertThrows(IllegalArgumentException.class, () -> Cell.fromValue(-1));
        assertThrows(IllegalArgumentException.class, () -> Cell.fromValue(3));
    }

    @Test
    void toValueIsReverseOfFromValue() {
        for (Cell cell : Cell.values()) {
            assertEquals(cell, Cell.fromValue(cell.toValue()));
        }
    }
}