sensor "button" pin 9
actuator "ledOne" pin 12

state "off" means ledOne becomes low
state "on" means ledOne becomes high

initial "off"

from "off" to "on" when "button" becomes "high"
from "on" to "off" after 800 ms

export "temporalTransitions"
