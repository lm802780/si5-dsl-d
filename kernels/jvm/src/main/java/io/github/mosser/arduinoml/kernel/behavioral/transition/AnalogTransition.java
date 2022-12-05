package io.github.mosser.arduinoml.kernel.behavioral.transition;

import io.github.mosser.arduinoml.kernel.behavioral.condition.AnalogCondition;
import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

import java.util.ArrayList;
import java.util.List;

public class AnalogTransition extends Transition implements Visitable {
    private List<AnalogCondition> conditions = new ArrayList<>();

    public List<AnalogCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<AnalogCondition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public void accept(Visitor<StringBuffer> visitor) {
        visitor.visit(this);
    }
}
