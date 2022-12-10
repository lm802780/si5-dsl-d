package groovuinoml.dsl

import io.github.mosser.arduinoml.kernel.behavioral.Action
import io.github.mosser.arduinoml.kernel.behavioral.State
import io.github.mosser.arduinoml.kernel.behavioral.transition.Transition
import io.github.mosser.arduinoml.kernel.structural.Actuator
import io.github.mosser.arduinoml.kernel.structural.INFSUP
import io.github.mosser.arduinoml.kernel.structural.SIGNAL
import io.github.mosser.arduinoml.kernel.structural.Sensor
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection

abstract class GroovuinoMLBasescript extends Script {
    // sensor "name" pin n
    def sensor(String name) {
        [pin  : { Integer n -> ((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createSensor(name, String.valueOf(n)) },
         onPin: { Integer n -> ((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createSensor(name, String.valueOf(n)) }]
    }

    def analog_sensor(String name) {
        [pin: { String pin -> ((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createSensor(name, pin) }]
    }
    // actuator "name" pin n
    def actuator(String name) {
        [pin: { Integer n -> ((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createActuator(name, n) }]
    }

    // state "name" means actuator becomes signal [and actuator becomes signal]*n
    def state(String name) {
        List<Action> actions = new ArrayList<>()
        ((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createState(name, actions)
        // recursive closure to allow multiple and statements
        def closure
        closure = { actuator ->
            [becomes: { signal ->
                Action action = new Action()
                action.setActuator(actuator instanceof String ? (Actuator) ((GroovuinoMLBinding) this.getBinding()).getVariable(actuator) : (Actuator) actuator)
                action.setValue(signal instanceof String ? (SIGNAL) ((GroovuinoMLBinding) this.getBinding()).getVariable(signal) : (SIGNAL) signal)
                actions.add(action)
                [and: closure]
            }]
        }
        [means: closure]
    }

    // initial state
    def initial(state) {
        ((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().setInitialState(state instanceof String ? (State) ((GroovuinoMLBinding) this.getBinding()).getVariable(state) : (State) state)
    }

    // from state1 to state2 when sensor becomes signal
    def from(state1) {
        State s1 = state1 instanceof String ? (State) ((GroovuinoMLBinding) this.getBinding()).getVariable(state1) : (State) state1
        [to: { state2 ->
            State s2 = state2 instanceof String ? (State) ((GroovuinoMLBinding) this.getBinding()).getVariable(state2) : (State) state2
            Transition transition = ((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().createTransitionWithoutCondition(s1, s2)
            def whenClosure
            whenClosure = { sensor ->
                Sensor s = sensor instanceof String ? (Sensor) ((GroovuinoMLBinding) this.getBinding()).getVariable(sensor) : (Sensor) sensor
                def becomesClosure = { signal ->
                    SIGNAL sig = signal instanceof String ? (SIGNAL) ((GroovuinoMLBinding) this.getBinding()).getVariable(signal) : (SIGNAL) signal
                    ((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().addDigitalConditionToTransition(transition, s, sig)
                    [and: whenClosure]
                }
                def aboveClosure = { double n ->
                    ((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().addAnalogConditionToTransition(transition, s, INFSUP.ABOVE, n)
                    [and: whenClosure]
                }
                def belowClosure = { double n ->
                    ((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().addAnalogConditionToTransition(transition, s, INFSUP.BELOW, n)
                    [and: whenClosure]
                }
                [becomes: becomesClosure, above: aboveClosure, below: belowClosure]
            }
            def afterClosure
            afterClosure = { long time ->
                [ms:
                         ((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().addSleepConditionToTransition(transition, time)
                ]

            }
            [when: whenClosure, after: afterClosure]
        }]
    }

    // export name
    def export(String name) {
        String resultCode = ((GroovuinoMLBinding) this.getBinding()).getGroovuinoMLModel().generateCode(name).toString()
        println(resultCode)

        // Copy the code in the clipboard.
        StringSelection selection = new StringSelection(resultCode)
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard()
        clipboard.setContents(selection, selection)
    }

    // disable run method while running
    int count = 0

    abstract void scriptBody()

    def run() {
        if (count == 0) {
            count++
            scriptBody()
        } else {
            println "Run method is disabled"
        }
    }
}
