sensor "buttonOne" onPin 9
sensor "buttonTwo" pin 8
actuator "led" pin 12
state "on" means "led" becomes high
state "off" means "led" becomes low
initial "off"
from "on" to "off" when "buttonOne" becomes low
from "on" to "off" when "buttonTwo" becomes low
from "off" to "on" when "buttonTwo" becomes high and "buttonOne" becomes high
export "dualCheckAlarm"