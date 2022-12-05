package io.github.mosser.arduinoml.kernel.behavioral.transition;

import io.github.mosser.arduinoml.kernel.behavioral.condition.DigitalCondition;
import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

import java.util.ArrayList;
import java.util.List;

public class DigitalTransition extends Transition implements Visitable {
    private List<DigitalCondition> conditions = new ArrayList<>();

    public List<DigitalCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<DigitalCondition> conditions) {
        this.conditions.addAll(conditions);
    }

    @Override
    public void accept(Visitor<StringBuffer> visitor) {
        visitor.visit(this);
    }
}
