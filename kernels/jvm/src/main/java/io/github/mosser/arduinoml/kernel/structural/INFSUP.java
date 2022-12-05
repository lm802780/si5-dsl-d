package io.github.mosser.arduinoml.kernel.structural;

public enum INFSUP {
    ABOVE(">"),
    BELOW("<");

    private final String symbol;

    INFSUP(String condition) {
        this.symbol = condition;
    }

    public String getSymbol() {
        return symbol;
    }
}
