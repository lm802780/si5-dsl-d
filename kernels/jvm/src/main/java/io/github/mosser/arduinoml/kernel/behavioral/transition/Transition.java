package io.github.mosser.arduinoml.kernel.behavioral.transition;

import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.behavioral.condition.AnalogCondition;
import io.github.mosser.arduinoml.kernel.behavioral.condition.Condition;
import io.github.mosser.arduinoml.kernel.behavioral.condition.DigitalCondition;
import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

import java.util.ArrayList;
import java.util.List;

public class Transition implements Visitable {
    private State next;

    private List<Condition> conditions = new ArrayList<>();
    public State getNext() {
        return next;
    }
    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<DigitalCondition> conditions) {
        this.conditions.addAll(conditions);
    }
    public void setNext(State next) {
        this.next = next;
    }

    @Override
    public void accept(Visitor<StringBuffer> visitor) {
        visitor.visit(this);
    }
}
