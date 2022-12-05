import io.github.mosser.arduinoml.kernel.App;
import io.github.mosser.arduinoml.kernel.generator.ToWiring;
import io.github.mosser.arduinoml.kernel.generator.Visitor;

import static io.github.mosser.arduinoml.embedded.java.dsl.AppBuilder.*;

public class Main {


    public static void main (String[] args) {
        Visitor codeGenerator = new ToWiring();
        Main.verySimpleAlarm().accept(codeGenerator);
        System.out.println(codeGenerator.getResult());
    }

    public static App exampleRedButton(){
        return application("red_button")
                        .uses(sensor("button", 9))
                        .uses(actuator("led", 12))
                        .hasForState("on")
                        .setting("led").toHigh()
                        .endState()
                        .hasForState("off").initial()
                        .setting("led").toLow()
                        .endState()
                        .beginTransitionTable()
                        .from("on").when("button").isHigh().goTo("off")
                        .from("off").when("button").isHigh().goTo("on")
                        .endTransitionTable()
                        .build();
    }

    // Scenario 1: very simple alarm
    public static App verySimpleAlarm(){
        return application("very_simple_alarm")
                .uses(sensor("button", 9))
                .uses(actuator("led", 12))
                .uses(actuator("buzzer", 11))
                    .hasForState("on")
                        .setting("led").toHigh()
                        .setting("buzzer").toHigh()
                    .endState()
                    .hasForState("off").initial()
                        .setting("led").toLow()
                        .setting("buzzer").toLow()
                    .endState()
                .beginTransitionTable()
                        .from("on").when("button").isLow().goTo("off")
                        .from("off").when("button").isHigh().goTo("on")
                .endTransitionTable()
                .build();
    }

    // Scenario 2: alarm with a delay
    public static App dualCheckAlarm() {
        return application("dual_check_alarm")
                .uses(sensor("buttonOne", 9))
                .uses(actuator("buttonTwo", 8))
                .uses(actuator("led", 12))
                .hasForState("on")
                    .setting("led").toHigh()
                .endState()
                .hasForState("off").initial()
                    .setting("led").toLow()
                .endState()
                .beginTransitionTable()
                    .from("on").when("buttonOne").isLow().or().when("buttonTwo").isLow().goTo("off")
                    .from("off").when("buttonOne").isHigh().goTo("on")
                .endTransitionTable().build();
    }

    // Scenario 3: state based alarm
    public static App stateBasedAlarm() {
        return application("state_based_alarm")
                .uses(sensor("button", 9))
                .uses(actuator("led", 12))
                .hasForState("on")
                    .setting("led").toHigh()
                .endState()
                .hasForState("off").initial()
                    .setting("led").toLow()
                .endState()
                .beginTransitionTable()
                    .from("on").when("button").isHigh().goTo("off")
                    .from("off").when("button").isHigh().goTo("on")
                .endTransitionTable().build();

    }
}
