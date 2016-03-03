# HomeAutomation_IFTTT

Create a class for the sensor code. The java code used pi4j library. It’s a java library for I2C and GPIO created for pi. 
Instructions to install library are in the comments.
Run the code as
pi4j MCP23017
Instructions to run the code are in comments of the code.

The temperature sensors keeps monitoring the temperature of the room and when the temperature falls below 25ᵒC, 
pi switches ON the relay channel 0 and the fan is turned ON. Similarly the light sensor keeps a watch on the ambient
light intensity inside the room , and when intensity falls below 200 lux , the pi again gives command to MCP23017 and 
turns the channel2 relay ON which turns the bulb ON.
