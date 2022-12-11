analog_sensor "temp" pin "A1"
actuator "buzzer" pin 11

state "on" means "buzzer" becomes high
state "off" means "buzzer" becomes low

initial "off"

from "on" to "off" when "temp" above 19
from "off" to "on" when "temp" below 10

export "handlingAnalogicalBricks"
