/*
Platform : Raspberry pi

Project uses pi4j library.
Please follow a detailed tutorial to install pi4j here.

http://pi4j.com/install.html

Compile the java program with command pi4j Filename.java
Run it with pi4j Filename
*/

import java.io.IOException;
import com.pi4j.gpio.extension.mcp.MCP23017GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP23017Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

public class MCP23017
{
	public static void main(String args[]) throws Exception
	{
		System.out.println("MCP23017 GPIO Example...started.");
		// Create gpio controller
		final GpioController gpio = GpioFactory.getInstance();
		// Create custom MCP23017 GPIO provider
		final MCP23017GpioProvider gpioProvider = new MCP23017GpioProvider(I2CBus.BUS_1, 0x20);
		
		// Create I2C bus
		I2CBus Bus = I2CFactory.getInstance(I2CBus.BUS_1);
		// Get I2C device, MCP9808 I2C address is 0x18(24)
		I2CDevice MCP9808 = Bus.getDevice(0x18);
		// Get I2C device, TCS34903 I2C address is 0x39(55)
		I2CDevice TCS34903 = Bus.getDevice(0x39);
		
		
		// Set Wait Time register = 0xff (255) , wait time = 2.78 ms
		TCS34903.write(0x83,(byte)0xFF);
		// Enable Access to IR channel
	    TCS34903.write(0xC0,(byte)0x80);
		// Set Atime register to 0x00 (0) , maximum counts = 65535
		TCS34903.write(0x81,(byte)0x00); 
		// Power ON , ADC enabled , Wait enabled
		TCS34903.write(0x80,(byte)0x0B);
		
		Thread.sleep(250);
		
		// Read 8 Bytes of Data with clear/ir data LSB first
		byte[] data1 = new byte[8];
        // Read Temperature Data
        byte[] data = new byte[2];
		
		// Provision gpio output pins and make sure they are all LOW at startup
		GpioPinDigitalOutput myOutputs[] = 
		{
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_A0, "MyOutput-A0", PinState.LOW),
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_A1, "MyOutput-A1", PinState.LOW),
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_A2, "MyOutput-A2", PinState.LOW),
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_A3, "MyOutput-A3", PinState.LOW),
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_A4, "MyOutput-A4", PinState.LOW),
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_A5, "MyOutput-A5", PinState.LOW),
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_A6, "MyOutput-A6", PinState.LOW),
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_A7, "MyOutput-A7", PinState.LOW),
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_B0, "MyOutput-B0", PinState.LOW),
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_B1, "MyOutput-B1", PinState.LOW),
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_B2, "MyOutput-B2", PinState.LOW),
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_B3, "MyOutput-B3", PinState.LOW),
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_B4, "MyOutput-B4", PinState.LOW),
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_B5, "MyOutput-B5", PinState.LOW),
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_B6, "MyOutput-B6", PinState.LOW),
			gpio.provisionDigitalOutputPin(gpioProvider, MCP23017Pin.GPIO_B7, "MyOutput-B7", PinState.LOW)
		};
		
		// Read 2 bytes of data from address 0x00(0)
		// temp msb, temp lsb
		
		while(true){
			
		MCP9808.read(0x05, data, 0, 2);
		
		// Convert data
		int temp = ((data[0] & 0x1F) * 256 + (data[1] & 0xFF));
		if(temp > 4096)
		{
			temp -= 8192;
		}
		double cTemp = temp * 0.0625;
		
            System.out.printf("Temperature in celsisus is : %.2f C %n", cTemp);
		
		if (cTemp > 25)
		{
			gpio.setState(true, myOutputs[0]);
		}
		else
		{
			gpio.setState(false, myOutputs[0]);
		}
		
		TCS34903.read(0x94,data1,0,8);
            
		double ir    = ((data1[1] & 0xFF) * 256) + (data1[0] & 0xFF) * 1.00;
		double red   = ((data1[3] & 0xFF) * 256) + (data1[2] & 0xFF) * 1.00;
		double green = ((data1[5] & 0xFF) * 256) + (data1[4] & 0xFF) * 1.00; 
		double blue  = ((data1[7] & 0xFF) * 256) + (data1[6] & 0xFF) * 1.00;
		
		// Calculate illuminance 
		double illuminance = (-0.32466) * (red) + (1.57837) * (green) + (-0.73191) * (blue);
		System.out.printf("Illuminance is :  %.2f  lux%n ", illuminance);
		
		if (illuminance < 200)
		{
			gpio.setState(true, myOutputs[1]);
		}
		else
		{
			gpio.setState(false, myOutputs[1]);
		}
		
		Thread.sleep(300);
		}
	}	
}