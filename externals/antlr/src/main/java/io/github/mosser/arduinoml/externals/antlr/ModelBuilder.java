package io.github.mosser.arduinoml.externals.antlr;

import io.github.mosser.arduinoml.externals.antlr.grammar.ArduinomlBaseListener;
import io.github.mosser.arduinoml.externals.antlr.grammar.ArduinomlParser;
import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.Action;
import io.github.mosser.arduinoml.kernel.behavioral.Condition;
import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.behavioral.TransitionCondition;
import io.github.mosser.arduinoml.kernel.behavioral.TransitionSleep;
import io.github.mosser.arduinoml.kernel.structural.Actuator;
import io.github.mosser.arduinoml.kernel.structural.CONNECTOR;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final Map<String, Binding> bindings = new HashMap<>();

    /**
     * Used to support state resolution for transitions.
     */
    private static class Binding {
        /**
         * Name of the next state, as its instance might not have been compiled yet.
         */
        protected String to;
        protected Sensor trigger;
        protected List<Condition> conditions = new ArrayList<>();
        protected SIGNAL value;
        protected Long timeInMillis;
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
        bindings.forEach((key, binding) -> {
            if (binding.timeInMillis != null) {
                TransitionSleep t = new TransitionSleep();
                t.setTimeInMillis(binding.timeInMillis);
                t.setNext(states.get(binding.to));
                states.get(key).setTransition(t);
            } else {
                TransitionCondition t = new TransitionCondition();
                t.setSensor(binding.trigger);
                t.setConditions(binding.conditions);
                t.setValue(binding.value);
                t.setNext(states.get(binding.to));
                states.get(key).setTransition(t);
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
        sensor.setPin(Integer.parseInt(ctx.location().port.getText()));
        this.theApp.getBricks().add(sensor);
        sensors.put(sensor.getName(), sensor);
    }

    @Override
    public void enterActuator(ArduinomlParser.ActuatorContext ctx) {
        Actuator actuator = new Actuator();
        actuator.setName(ctx.location().id.getText());
        actuator.setPin(Integer.parseInt(ctx.location().port.getText()));
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
    public void enterTransition(ArduinomlParser.TransitionContext ctx) {
        // Creating a placeholder as the next state might not have been compiled yet.
        Binding toBeResolvedLater = new Binding();
        toBeResolvedLater.to = ctx.next.getText();
        toBeResolvedLater.trigger = sensors.get(ctx.trigger.getText());
        toBeResolvedLater.value = SIGNAL.valueOf(ctx.value.getText());
        bindings.put(currentState.getName(), toBeResolvedLater);
    }

    @Override
    public void enterTransitionCondition(ArduinomlParser.TransitionConditionContext ctx) {
        // Creating a placeholder as the next state might not have been compiled yet.
        Binding toBeResolvedLater = new Binding();
        toBeResolvedLater.to = ctx.next.getText();
        ctx.condition().forEach(conditionContext -> {
            Condition condition = new Condition();
            condition.setConnector(CONNECTOR.valueOf(conditionContext.connector.getText()));
            condition.setSensor(sensors.get(conditionContext.trigger.getText()));
            condition.setValue(SIGNAL.valueOf(conditionContext.value.getText()));
            toBeResolvedLater.conditions.add(condition);
        });
        toBeResolvedLater.to = ctx.next.getText();
        toBeResolvedLater.trigger = sensors.get(ctx.trigger1.getText());
        toBeResolvedLater.value = SIGNAL.valueOf(ctx.value.getText());
        bindings.put(currentState.getName(), toBeResolvedLater);
    }

    @Override
    public void enterTransitionSleep(ArduinomlParser.TransitionSleepContext ctx) {
        // Creating a placeholder as the next state might not have been compiled yet.
        Binding toBeResolvedLater = new Binding();
        toBeResolvedLater.to = ctx.next.getText();
        toBeResolvedLater.timeInMillis = Long.valueOf(ctx.timeInMillis.getText());
        bindings.put(currentState.getName(), toBeResolvedLater);
    }

    @Override
    public void enterInitial(ArduinomlParser.InitialContext ctx) {
        this.theApp.setInitial(this.currentState);
    }

}

