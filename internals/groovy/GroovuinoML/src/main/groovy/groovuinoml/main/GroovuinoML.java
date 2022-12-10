package groovuinoml.main;

import groovuinoml.dsl.GroovuinoMLDSL;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * This main takes one argument: the path to the Groovy script file to execute.
 * This Groovy script file must follow GroovuinoML DSL's rules.
 * <p>
 * "We've Got A Groovy Thing Goin'"!
 *
 * @author Thomas Moreau
 */
public class GroovuinoML {
    public static void main(String[] args) throws FileNotFoundException {
        GroovuinoMLDSL dsl = new GroovuinoMLDSL();
        if (args.length > 0) {

            File output = new File(args[0].substring(0, args[0].lastIndexOf('.')) + ".txt");
            PrintStream stream = new PrintStream(output);
            System.setOut(stream);
            dsl.eval(new File(args[0]));
        } else {
            System.out.println("/!\\ Missing arg: Please specify the path to a Groovy script file to execute");
        }
    }
}
