package io.github.mosser.arduinoml.kernel.behavioral.condition;

import io.github.mosser.arduinoml.kernel.structural.CONNECTOR;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

public class Condition {
	private CONNECTOR connector;
	private Sensor sensor;

	public CONNECTOR getConnector() {
		return connector;
	}

	public void setConnector(CONNECTOR connector) {
		this.connector = connector;
	}

	public Sensor getSensor() {
		return sensor;
	}

	public void setSensor(Sensor sensor) {
		this.sensor = sensor;
	}

}
