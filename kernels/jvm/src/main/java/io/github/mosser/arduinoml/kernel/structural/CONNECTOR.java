package io.github.mosser.arduinoml.kernel.structural;

public enum CONNECTOR {
    OR("||"),
    AND("&&");

    private final String condition;

    CONNECTOR(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }
}