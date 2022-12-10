package io.github.mosser.arduinoml.externals.antlr;

import io.github.mosser.arduinoml.externals.antlr.grammar.ArduinomlBaseListener;
import io.github.mosser.arduinoml.externals.antlr.grammar.ArduinomlParser;
import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.Action;
import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.behavioral.condition.AnalogCondition;
import io.github.mosser.arduinoml.kernel.behavioral.condition.Condition;
import io.github.mosser.arduinoml.kernel.behavioral.condition.DigitalCondition;
import io.github.mosser.arduinoml.kernel.behavioral.condition.SleepCondition;
import io.github.mosser.arduinoml.kernel.behavioral.transition.Transition;
import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.INFSUP;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModelBuilder extends ArduinomlBaseListener {

    /********************
     ** Business Logic **
     ********************/

    private App theApp;
    private boolean built = false;

    public App retrieve() {
        if (built) {
            return theApp;
        }
        throw new RuntimeException("Cannot retrieve a model that was not created!");
    }

    /*******************
     ** Symbol tables **
     *******************/

    private final Map<String, Sensor> sensors = new HashMap<>();
    private final Map<String, Actuator> actuators = new HashMap<>();
    private final Map<String, State> states = new HashMap<>();
    private final Map<String, Set<Binding>> bindings = new HashMap<>();

    private void addBiding(String key, Binding binding) {
        Set<Binding> bindingsSource = (bindings.get(key) == null ? new HashSet<>() : new HashSet<>(bindings.get(key)));
        bindingsSource.add(binding);
        bindings.put(key, bindingsSource);
    }

    /**
     * Used to support state resolution for transitions.
     */
    private static class Binding {
        /**
         * Name of the next state, as its instance might not have been compiled yet.
         */
        protected String to;
        protected Long timeInMillis;
        protected List<Condition> digitalConditions = new ArrayList<>();
        protected List<Condition> analogConditions = new ArrayList<>();
    }

    private State currentState;

    /**************************
     ** Listening mechanisms **
     **************************/

    @Override
    public void enterRoot(ArduinomlParser.RootContext ctx) {
        built = false;
        theApp = new App();
    }

    @Override
    public void exitRoot(ArduinomlParser.RootContext ctx) {
        // Resolving states in transitions
        bindings.forEach((key, bindingList) -> {
            for (Binding binding : bindingList) {
                Transition transition = new Transition();

                if (binding.timeInMillis != null) {
                    // Sleep transition
                    SleepCondition sleepCondition = new SleepCondition();
                    sleepCondition.setTimeInMillis(binding.timeInMillis);
                    transition.setConditions(List.of(sleepCondition));
                } else if (!binding.digitalConditions.isEmpty()) {
                    // Digital transition
                    transition.getConditions().addAll(binding.digitalConditions);
                } else {
                    // Analog transition
                    transition.getConditions().addAll(binding.analogConditions);
                }

                // Add transition
                transition.setNext(states.get(binding.to));
                if (transition.getNext() != null) {
                    System.out.println("transition = " + transition);
                    states.get(key).getTransitions().add(transition);
                }
            }
        });
        this.built = true;
    }

    @Override
    public void enterDeclaration(ArduinomlParser.DeclarationContext ctx) {
        theApp.setName(ctx.name.getText());
    }

    @Override
    public void enterSensor(ArduinomlParser.SensorContext ctx) {
        Sensor sensor = new Sensor();
        sensor.setName(ctx.location().id.getText());
        sensor.setPin(ctx.location().port.getText());
        this.theApp.getBricks().add(sensor);
        sensors.put(sensor.getName(), sensor);
    }

    @Override
    public void enterAnalogSensor(ArduinomlParser.AnalogSensorContext ctx) {
        Sensor sensor = new Sensor();
        sensor.setName(ctx.location().id.getText());
        sensor.setPin(ctx.location().port.getText());
        this.theApp.getBricks().add(sensor);
        sensors.put(sensor.getName(), sensor);
    }

    @Override
    public void enterActuator(ArduinomlParser.ActuatorContext ctx) {
        Actuator actuator = new Actuator();
        actuator.setName(ctx.location().id.getText());
        actuator.setPin(ctx.location().port.getText());
        this.theApp.getBricks().add(actuator);
        actuators.put(actuator.getName(), actuator);
    }

    @Override
    public void enterState(ArduinomlParser.StateContext ctx) {
        State local = new State();
        local.setName(ctx.name.getText());
        this.currentState = local;
        this.states.put(local.getName(), local);
    }

    @Override
    public void exitState(ArduinomlParser.StateContext ctx) {
        this.theApp.getStates().add(this.currentState);
        this.currentState = null;
    }

    @Override
    public void enterAction(ArduinomlParser.ActionContext ctx) {
        ctx.actionable().forEach(actionableContext -> {
            Action action = new Action();
            action.setActuator(actuators.get(actionableContext.getText()));
            action.setValue(SIGNAL.valueOf(ctx.value.getText()));
            currentState.getActions().add(action);
        });
    }

    @Override
    public void enterDigitalTransition(ArduinomlParser.DigitalTransitionContext ctx) {
        // Creating a placeholder as the next state might not have been compiled yet.
        Binding toBeResolvedLater = new Binding();
        toBeResolvedLater.to = ctx.next.getText();
        ctx.condition().forEach(conditionContext -> {
            DigitalCondition condition = new DigitalCondition();
            condition.setSensor(sensors.get(conditionContext.trigger.getText()));
            condition.setValue(SIGNAL.valueOf(conditionContext.value.getText()));
            toBeResolvedLater.digitalConditions.add(condition);

            toBeResolvedLater.to = ctx.next.getText();
            addBiding(currentState.getName(), toBeResolvedLater);
        });
    }

    @Override
    public void enterAnalogTransition(ArduinomlParser.AnalogTransitionContext ctx) {
        // Creating a placeholder as the next state might not have been compiled yet.
        Binding toBeResolvedLater = new Binding();
        toBeResolvedLater.to = ctx.next.getText();
        ctx.conditionA().forEach(conditionContext -> {
            AnalogCondition condition = new AnalogCondition();
            condition.setSensor(sensors.get(conditionContext.trigger.getText()));
            condition.setValue(Double.valueOf(conditionContext.value.getText()));
            condition.setInfsup(INFSUP.valueOf(conditionContext.infsup.getText()));
            toBeResolvedLater.analogConditions.add(condition);

            toBeResolvedLater.to = ctx.next.getText();
            addBiding(currentState.getName(), toBeResolvedLater);
        });
    }

    @Override
    public void enterSleepTransition(ArduinomlParser.SleepTransitionContext ctx) {
        // Creating a placeholder as the next state might not have been compiled yet.
        Binding toBeResolvedLater = new Binding();
        toBeResolvedLater.to = ctx.next.getText();
        toBeResolvedLater.timeInMillis = Long.valueOf(ctx.timeInMillis.getText());
        addBiding(currentState.getName(), toBeResolvedLater);
    }

    @Override
    public void enterInitial(ArduinomlParser.InitialContext ctx) {
        this.theApp.setInitial(this.currentState);
    }

}

