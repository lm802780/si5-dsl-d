package io.github.mosser.arduinoml.kernel.behavioral.transition;

import io.github.mosser.arduinoml.kernel.generator.Visitor;

public class SleepTransition extends Transition {
    private Long timeInMillis;

    public Long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(Long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    @Override
    public void accept(Visitor<StringBuffer> visitor) {
        visitor.visit(this);
    }
}
