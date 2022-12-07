package io.github.mosser.arduinoml.kernel.behavioral.condition;

import io.github.mosser.arduinoml.kernel.structural.CONNECTOR;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

public abstract class Condition {

	private Sensor sensor;

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

}
