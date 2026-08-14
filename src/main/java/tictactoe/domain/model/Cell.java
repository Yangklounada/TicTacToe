package tictactoe.domain.model;

public enum Cell {
    EMPTY, X, O;

    public static Cell fromValue(int value) {
        return switch (value) {
            case 0 -> EMPTY;
            case 1 -> X;
            case 2 -> O;
            default -> throw new IllegalArgumentException("Invalid cell value: " + value);
        };
    }

    public int toValue() {
        return switch (this) {
            case EMPTY -> 0;
            case X -> 1;
            case O -> 2;
        };
    }
}