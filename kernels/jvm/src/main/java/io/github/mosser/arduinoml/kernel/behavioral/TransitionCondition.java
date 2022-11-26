package io.github.mosser.arduinoml.kernel.behavioral;

import io.github.mosser.arduinoml.kernel.generator.Visitable;
import io.github.mosser.arduinoml.kernel.generator.Visitor;
import io.github.mosser.arduinoml.kernel.structural.CONNECTOR;
import io.github.mosser.arduinoml.kernel.structural.SIGNAL;
import io.github.mosser.arduinoml.kernel.structural.Sensor;

public class TransitionCondition extends Transition implements Visitable {


	private Sensor sensor2;
	private CONNECTOR connector;

	public Sensor getSensor2() {
		return sensor2;
	}

	public void setSensor2(Sensor sensor2) {
		this.sensor2 = sensor2;
	}

	public CONNECTOR getConnector() {
		return connector;
	}

	public void setConnector(CONNECTOR connector) {
		this.connector = connector;
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
