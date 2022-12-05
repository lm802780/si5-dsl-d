sensor "buttonOne" onPin 9
sensor "buttonTwo" pin 8
actuator "led" pin 12

state "on" means led becomes high
state "off" means led becomes low

initial "off"

from "on" to "off" when "buttonOne" becomes "low" or "buttonTwo" becomes "low"
from "off" to "on" when "buttonOne" becomes "high" and "buttonTwo" becomes "high"

export "dualCheckAlarm"
