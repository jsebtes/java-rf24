package fr.jstessier.rf24.hardware;

/*
 * Copyright (C) 2015 J.S. TESSIER
 * 
 * This file is part of java-rf24.
 * 
 * java-rf24 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * java-rf24 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with java-rf24. If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

import java.io.IOException;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;
import com.pi4j.io.spi.SpiMode;

import fr.jstessier.rf24.exceptions.RF24Exception;

/**
 * Hardware interface to RF24 module for Raspberry Pi (based on pi4J).
 * 
 * @author J.S. TESSIER
 */
public class RF24HardwarePi4j implements RF24Hardware {

	/** SPI default channel is 0. */
	public static final byte DEFAULT_SPI_CHANNEL = 0;

	/** SPI default speed is 4MHz (like Arduino default speed). */
	public static final int DEFAULT_SPI_SPEED = 4000000;


	// SPI Configuration

	/** SPI Channel to use. */
	private final byte spiChannel;

	/** SPI Speed (in Hertz) for channel. Range is 500kHz - 32MHz. */
	private final int spiSpeed;

	/** Driver of the SPI Device. */
	private final SpiDevice spiDevice;


	// GPIO Configuration

	/** "Chip Enable" pin, activates the RX or TX role (GPIO). */
	private final byte gpioPinChipEnable;

	/** Driver of the GPIO pin "Chip Enable". */
	private final GpioPinDigitalOutput pinChipEnable;


	/* ============
	 * Constructors
	 * ============ */

	/**
	 * Constructor.
	 * Use the default SPI speed value (4 MHz).
	 * 
	 * @param spiChannel		The number of the SPI Channel to use.
	 * @param gpioPinChipEnable	The GPIO pin number for "Chip Enable" selector. 
	 * 								NOTE : Thisimplementation use WiringPi pin numerotation. 
	 * 								See :
	 *           						http://pi4j.com/pins/model-a-plus.html
	 *            						http://pi4j.com/pins/model-b-plus.html
	 *            						http://pi4j.com/pins/model-2b-rev1.html
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24HardwarePi4j(byte spiChannel, byte gpioPinChipEnable) throws RF24Exception {
		this(spiChannel, gpioPinChipEnable, DEFAULT_SPI_SPEED);
	}

	/**
	 * Constructor.
	 * 
	 * @param spiChannel		The number of the SPI Channel to use.
	 * @param gpioPinChipEnable	The GPIO pin number for "Chip Enable" selector. 
	 * 								NOTE : Thisimplementation use WiringPi pin numerotation. 
	 * 								See :
	 *           						http://pi4j.com/pins/model-a-plus.html
	 *            						http://pi4j.com/pins/model-b-plus.html
	 *            						http://pi4j.com/pins/model-2b-rev1.html
	 * @param spiSpeed			The SPI speed in Hertz for channel to communicate at [500kHz - 32MHz].
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24HardwarePi4j(byte spiChannel, byte gpioPinChipEnable, int spiSpeed) throws RF24Exception {
		// // Check Spi channel value
		if (spiChannel < 0) {
			throw new IllegalArgumentException("spiChannel is negative");
		}
		// Check pinChipEnable value
		if (gpioPinChipEnable < 0) {
			throw new IllegalArgumentException("pinChipEnable is negative");
		}
		// Check Spi speed value
		if (spiSpeed < 500000 || spiSpeed > 32000000) {
			throw new IllegalArgumentException("spiSpeed is out of range [500kHz - 32MHz].");
		}

		this.gpioPinChipEnable = gpioPinChipEnable;
		this.spiChannel = spiChannel;
		this.spiSpeed = spiSpeed;

		// Initialize GPIO Pin
		pinChipEnable = GpioFactory.getInstance().provisionDigitalOutputPin(RaspiPin.getPinByName("GPIO " + gpioPinChipEnable));
		pinChipEnable.low();

		// Initialize SPI Device
		try {
			spiDevice = SpiFactory.getInstance(SpiChannel.getByNumber(spiChannel), spiSpeed, SpiMode.getByNumber(RF24Hardware.SPI_MODE_0));
		} catch (IOException e) {
			throw new RF24Exception("Failed to initialize communication with RF module through SPI channel " + spiChannel);
		}
	}


	/* =======
	 * Methods
	 * ======= */

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPinChipEnableHigh() {
		pinChipEnable.high();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPinChipEnableLow() {
		pinChipEnable.low();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte[] spiWrite(byte... data) throws RF24Exception {
		try {
			return spiDevice.write(data);
		} catch (IOException e) {
			throw new RF24Exception("Failed to write on SPI channel " + spiChannel, e);
		}
	}


	/* =======
	 * Getters
	 * ======= */

	public byte getGpioPinChipEnable() {
		return gpioPinChipEnable;
	}

	public byte getSpiChannel() {
		return spiChannel;
	}

	public int getSpiSpeed() {
		return spiSpeed;
	}

}
