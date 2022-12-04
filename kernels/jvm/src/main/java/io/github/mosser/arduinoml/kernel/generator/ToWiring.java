package io.github.mosser.arduinoml.kernel.generator;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.*;
import io.github.mosser.arduinoml.kernel.behavioral.condition.AnalogCondition;
import io.github.mosser.arduinoml.kernel.behavioral.condition.DigitalCondition;
import io.github.mosser.arduinoml.kernel.behavioral.transition.AnalogTransition;
import io.github.mosser.arduinoml.kernel.behavioral.transition.DigitalTransition;
import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.Brick;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

/**
 * Quick and dirty visitor to support the generation of Wiring code
 */
public class ToWiring extends Visitor<StringBuffer> {
    public ToWiring() {
        this.result = new StringBuffer();
    }

    private void w(String s) {
        result.append(String.format("%s", s));
    }

    @Override
    public void visit(App app) {
        //first pass, create global vars
        context.put("pass", PASS.ONE);
        w("// Wiring code generated from an ArduinoML model\n");
        w(String.format("// Application name: %s%n%n", app.getName()));

        w("long debounce = 200;\n");
        w("\nenum STATE {");
        String sep = "";
        for (State state : app.getStates()) {
            w(sep);
            state.accept(this);
            sep = ", ";
        }
        w("};\n");
        if (app.getInitial() != null) {
            w("STATE currentState = " + app.getInitial().getName() + ";\n");
        }

        for (Brick brick : app.getBricks()) {
            brick.accept(this);
        }

        //second pass, setup and loop
        context.put("pass", PASS.TWO);
        w("\nvoid setup(){\n");
        for (Brick brick : app.getBricks()) {
            brick.accept(this);
        }
        w("}\n");

        w("\nvoid loop() {\n" +
                "\tswitch(currentState){\n");
        for (State state : app.getStates()) {
            state.accept(this);
        }
        w("\t}\n" +
                "}");
    }

    @Override
    public void visit(Actuator actuator) {
        if (context.get("pass") == PASS.ONE) {
            return;
        }
        if (context.get("pass") == PASS.TWO) {
            w(String.format("  pinMode(%s, OUTPUT); // %s [Actuator]%n", actuator.getPin(), actuator.getName()));
        }
    }

    @Override
    public void visit(Sensor sensor) {
        if (context.get("pass") == PASS.ONE) {
            w(String.format("%nboolean %sBounceGuard = false;%n", sensor.getName()));
            w(String.format("long %sLastDebounceTime = 0;%n", sensor.getName()));
            return;
        }
        if (context.get("pass") == PASS.TWO) {
            w(String.format("  pinMode(%s, INPUT);  // %s [Sensor]%n", sensor.getPin(), sensor.getName()));
        }
    }

    @Override
    public void visit(State state) {
        if (context.get("pass") == PASS.ONE) {
            w(state.getName());
            return;
        }
        if (context.get("pass") == PASS.TWO) {
            w("\t\tcase " + state.getName() + ":\n");
            for (Action action : state.getActions()) {
                action.accept(this);
            }
            if (state.getTransition() != null) {
                state.getTransition().accept(this);
                w("\t\tbreak;\n");
            }
        }

    }


    @Override
    public void visit(AnalogTransition analogTransition) {
        if (context.get("pass") == PASS.ONE) {
            return;
        }
        if (context.get("pass") == PASS.TWO) {
            StringBuilder conditionBuilder = new StringBuilder();
            conditionBuilder.append("\t\t\tif(");
            for(AnalogCondition condition : analogTransition.getConditions()) {
                conditionBuilder.append(String.format("(analogRead(%s) == %s)",condition.getSensor().getPin(), condition.getValue()));
                if(condition.getConnector() != null) {
                    conditionBuilder.append(String.format("%s ", condition.getConnector().getCondition()));
                }
            }
            conditionBuilder.append(") {\n");
            w(conditionBuilder.toString());
            w("\t\t\t\tcurrentState = " + analogTransition.getNext().getName() + ";\n");
            w("\t\t\t}\n");
        }
    }

    @Override
    public void visit(DigitalTransition digitalTransition) {
        if (context.get("pass") == PASS.ONE) {
            return;
        }
        if (context.get("pass") == PASS.TWO) {
            StringBuilder conditionBuilder = new StringBuilder();
            conditionBuilder.append("\t\t\tif(");
             for(DigitalCondition condition : digitalTransition.getConditions()) {
                w(String.format("\t\t\t%sBounceGuard = millis() - %sLastDebounceTime > debounce;%n", condition.getSensor().getName(), condition.getSensor().getName()));
                conditionBuilder.append(String.format("(digitalRead(%s) == %s && %sBounceGuard)",condition.getSensor().getPin(), condition.getValue(), condition.getSensor().getName()));
                if(condition.getConnector() != null) {
                    conditionBuilder.append(String.format(" %s ", condition.getConnector().getCondition()));
                }
             }
                 conditionBuilder.append(") {\n");
                 w(conditionBuilder.toString());
            for(DigitalCondition condition : digitalTransition.getConditions()) {
                 w(String.format("\t\t\t\t%sLastDebounceTime = millis();%n", condition.getSensor().getName()));
}
            w("\t\t\t\tcurrentState = " + digitalTransition.getNext().getName() + ";\n");
            w("\t\t\t}\n");
        }
    }

    @Override
    public void visit(Action action) {
        if (context.get("pass") == PASS.ONE) {
            return;
        }
        if (context.get("pass") == PASS.TWO) {
            w(String.format("\t\t\tdigitalWrite(%s,%s);%n", action.getActuator().getPin(), action.getValue()));
        }
    }

    enum PASS {ONE, TWO}
}
