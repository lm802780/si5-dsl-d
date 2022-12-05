package io.github.mosser.arduinoml.kernel.behavioral.condition;

public class AnalogCondition extends Condition {

	private Double value;

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
