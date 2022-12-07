package io.github.mosser.arduinoml.kernel.generator;

import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.Action;
import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.behavioral.condition.AnalogCondition;
import io.github.mosser.arduinoml.kernel.behavioral.condition.Condition;
import io.github.mosser.arduinoml.kernel.behavioral.condition.DigitalCondition;
import io.github.mosser.arduinoml.kernel.behavioral.condition.SleepCondition;
import io.github.mosser.arduinoml.kernel.behavioral.transition.Transition;
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

        // Now clock
        w("long now = 0;\n");

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

            // If the state contains any sleepTransition, initialize the now clock if it is not.
            if (state.getTransitions().stream().anyMatch(t -> t.getConditions().stream().anyMatch(c -> c instanceof SleepCondition))) {
                w("\t\t\tif (now == 0) {\n");
                w("\t\t\t\tnow = millis();\n");
                w("\t\t\t}\n");
            }

            for (Action action : state.getActions()) {
                action.accept(this);
            }

            if (state.getTransitions() != null) {
                for (Transition transition : state.getTransitions()) {
                    transition.accept(this);
                }
                w("\t\tbreak;\n");
            }
        }

    }



//    @Override
//    public void visit(SleepTransition sleepTransition) {
//        w(String.format("\t\t\tif (millis()-now > %d) {%n", sleepTransition.getTimeInMillis()));
//        w(String.format("\t\t\t\tcurrentState = %s;%n", sleepTransition.getNext().getName()));
//        w("\t\t\t\tnow = 0;\n");
//        w("\t\t\t\tbreak;\n");
//        w("\t\t\t}\n");
//    }

    @Override
    public void visit(Transition transition) {
        if (context.get("pass") == PASS.ONE) {
            return;
        }
        if (context.get("pass") == PASS.TWO) {
            StringBuilder conditionBuilder = new StringBuilder();
            conditionBuilder.append("\t\t\tif(");
            for (Condition condition : transition.getConditions()) {
                if(condition instanceof DigitalCondition) {
                    w(String.format("\t\t\t%sBounceGuard = millis() - %sLastDebounceTime > debounce;%n", condition.getSensor().getName(), condition.getSensor().getName()));
                    conditionBuilder.append(String.format("(digitalRead(%s) == %s && %sBounceGuard)", condition.getSensor().getPin(), ((DigitalCondition)condition).getValue(), condition.getSensor().getName()));

                } else if (condition instanceof AnalogCondition) {
                    conditionBuilder.append(String.format("(analogRead(%s) %s %s)",condition.getSensor().getPin(), ((AnalogCondition)condition).getInfsup().getSymbol(),((AnalogCondition)condition).getValue()));

                }else if (condition instanceof SleepCondition){
                    w(String.format("\t\t\tif (millis()-now > %d) {%n", ((SleepCondition) condition).getTimeInMillis()));
                }
                conditionBuilder.append(String.format(" %s ", "&&"));
            }
            conditionBuilder.delete(conditionBuilder.length()-3, conditionBuilder.length());
            conditionBuilder.append(") {\n");
            w(conditionBuilder.toString());
            for (Condition condition: transition.getConditions()) {
                if(condition instanceof DigitalCondition) {
                    w(String.format("\t\t\t\t%sLastDebounceTime = millis();%n", condition.getSensor().getName()));
                }

            }
            w("\t\t\t\tcurrentState = " + transition.getNext().getName() + ";\n");
            if(transition.getConditions().contains(SleepCondition.class)){
                w("\t\t\t\tnow = 0;\n");
            }
            w("\t\t\t\tbreak;\n");
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
