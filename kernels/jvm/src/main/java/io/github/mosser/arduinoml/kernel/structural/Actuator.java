package io.github.mosser.arduinoml.kernel.structural;

import io.github.mosser.arduinoml.kernel.generator.Visitor;

public class Actuator extends Brick {
	@Override
	public void accept(Visitor<StringBuffer> visitor) {
		visitor.visit(this);
	}
}
