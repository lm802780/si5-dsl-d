package io.github.mosser.arduinoml.kernel.behavioral.condition;

public class SleepCondition extends Condition {
    private Long timeInMillis;

    public Long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(Long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

}
