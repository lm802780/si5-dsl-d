package io.github.mosser.arduinoml.kernel.structural;

public enum INFSUP {
    ABOVE(">"),
    BELOW("<");

    private final String symbol;

    INFSUP(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
