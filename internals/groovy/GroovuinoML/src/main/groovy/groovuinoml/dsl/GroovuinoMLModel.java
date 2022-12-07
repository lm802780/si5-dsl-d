package groovuinoml.dsl;

import groovy.lang.Binding;
import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.behavioral.Action;
import io.github.mosser.arduinoml.kernel.behavioral.State;
import io.github.mosser.arduinoml.kernel.behavioral.condition.AnalogCondition;
import io.github.mosser.arduinoml.kernel.behavioral.condition.Condition;
import io.github.mosser.arduinoml.kernel.behavioral.condition.DigitalCondition;
import io.github.mosser.arduinoml.kernel.behavioral.condition.SleepCondition;
import io.github.mosser.arduinoml.kernel.behavioral.transition.Transition;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.*;

import java.util.ArrayList;
import java.util.List;

public class GroovuinoMLModel {
    private final List<Brick> bricks;
    private final List<State> states;
    private State initialState;

    private final Binding binding;

    public GroovuinoMLModel(Binding binding) {
        this.bricks = new ArrayList<>();
        this.states = new ArrayList<>();
        this.binding = binding;
    }

    public void createSensor(String name, String pinNumber) {
        Sensor sensor = new Sensor();
        sensor.setName(name);
        sensor.setPin(pinNumber);
        this.bricks.add(sensor);
        this.binding.setVariable(name, sensor);
//		System.out.println("> sensor " + name + " on pin " + pinNumber);
    }

    public void createActuator(String name, Integer pinNumber) {
        Actuator actuator = new Actuator();
        actuator.setName(name);
        actuator.setPin(String.valueOf(pinNumber));
        this.bricks.add(actuator);
        this.binding.setVariable(name, actuator);
    }

    public void createState(String name, List<Action> actions) {
        State state = new State();
        state.setName(name);
        state.setActions(actions);
        this.states.add(state);
        this.binding.setVariable(name, state);
    }

    public Transition createTransitionWithoutCondition(State from, State to) {
        Transition transition = new Transition();
        if(transition.getConditions() == null) {
            transition.setConditions(new ArrayList<>());
        }
        transition.setNext(to);
        if(from.getTransitions() == null) {
            from.setTransitions(new ArrayList<>());
        }
        from.addTransition(transition);
        return transition;
    }

    public void addDigitalConditionToTransition(Transition transition, Sensor sensor, SIGNAL value) {
        List<Condition> conditions = transition.getConditions();
        DigitalCondition condition = new DigitalCondition();
        condition.setSensor(sensor);
        condition.setValue(value);
        conditions.add(condition);
    }
    public void addAnalogConditionToTransition(Transition transition,Sensor sensor, INFSUP infsup,double value) {
        List<Condition> conditions = transition.getConditions();
        AnalogCondition analogCondition = new AnalogCondition();
        analogCondition.setSensor(sensor);
        analogCondition.setInfsup(infsup);
        analogCondition.setValue(value);
        conditions.add(analogCondition);
    }


    public void addSleepConditionToTransition(Transition transition, Long time){
        List<Condition> conditions = transition.getConditions();
        SleepCondition condition = new SleepCondition();
        condition.setTimeInMillis(time);
        conditions.add(condition);
    }

    public void setInitialState(State state) {
        this.initialState = state;
    }

    public Object generateCode(String appName) {
        App app = new App();
        app.setName(appName);
        app.setBricks(this.bricks);
        app.setStates(this.states);
        app.setInitial(this.initialState);
        Visitor<StringBuffer> codeGenerator = new ToWiring();
        app.accept(codeGenerator);

        return codeGenerator.getResult();
    }
}
