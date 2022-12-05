package io.github.mosser.arduinoml.kernel.behavioral.condition;

import io.github.mosser.arduinoml.kernel.structural.SIGNAL;

public class DigitalCondition extends Condition {

	private SIGNAL value;

	public SIGNAL getValue() {
		return value;
	}

	public void setValue(SIGNAL value) {
		this.value = value;
	}
}
