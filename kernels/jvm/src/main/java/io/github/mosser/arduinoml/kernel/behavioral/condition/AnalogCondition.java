package io.github.mosser.arduinoml.kernel.behavioral.condition;

import io.github.mosser.arduinoml.kernel.structural.INFSUP;

public class AnalogCondition extends Condition {

	private Double value;

	private INFSUP infsup;

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public INFSUP getInfsup() {
		return infsup;
	}

	public void setInfsup(INFSUP infsup) {
		this.infsup = infsup;
	}
}
