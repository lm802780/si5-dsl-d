sensor "button" pin 9
actuator "led" pin 12
actuator "buzzer" pin 11

state "firstStep" means "buzzer" becomes high
state "secondStep"
state "thirdStep" means "led" becomes high and "buzzer" becomes low
state "fourthStep"
state "fifthStep" means "led" becomes low
state "off" means "led" becomes low and "buzzer" becomes low

initial "off"

from "firstStep" to "secondStep" when "button" becomes low
from "secondStep" to "thirdStep" when "button" becomes high
from "thirdStep" to "fourthStep" when "button" becomes low
from "fourthStep" to "fifthStep" when "button" becomes high
from "fifthStep" to "off" when "button" becomes low
from "off" to "firstStep" when "button" becomes high

export "multiStateAlarm"
