package fr.jstessier.rf24.utils;

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

import fr.jstessier.rf24.Registers.Register;
import fr.jstessier.rf24.SpiCommands.SpiCommand;
import fr.jstessier.rf24.SpiCommands.SpiCommandReadWriteRegister;
import fr.jstessier.rf24.exceptions.SpiCommandRequestLengthException;

/**
 * Utilities for SPI commands.
 * 
 * @author J.S. TESSIER
 */
public class SpiCommandsUtils {

	/**
	 * Private constructor.
	 */
	private SpiCommandsUtils() {
		// NOP
	}

	/**
	 * Return the command value of a spi command.
	 * 
	 * @param spiCommand	The SpiCommand.
	 * @return	The command value.
	 */
	public static byte getSpiCommand(final SpiCommand spiCommand) {
		return spiCommand.getCommand();
	}

	/**
	 * Return the command value of a spi command for read / write register.
	 * 
	 * @param spiCommand	The SpiCommand.
	 * @param register		The register.
	 * @return	The command value.
	 */
	public static byte getSpiCommand(final SpiCommandReadWriteRegister spiCommand, final Register register) {
		return getSpiCommand(spiCommand, register.getAddress());
	}

	/**
	 * Return the command value of a spi command.
	 * 
	 * @param spiCommand		The SpiCommand.
	 * @param registerAddress	The register address.
	 * @return	The command value.
	 */
	public static byte getSpiCommand(final SpiCommandReadWriteRegister spiCommand, final byte registerAddress) {
		return (byte) (spiCommand.getBaseCommand() | (byte) (spiCommand.getCommandRegisterMask() & registerAddress));
	}

	public static void checkDataLength(final SpiCommand spiCommand, final byte... data) {
		int requestLength = (data == null) ? 0 : data.length;
		if (requestLength < spiCommand.getDataMinLength() || requestLength > spiCommand.getDataMaxLength()) {
			throw new SpiCommandRequestLengthException(spiCommand.getName(), requestLength, 
					spiCommand.getDataMinLength(), spiCommand.getDataMaxLength());
		}
	}

}
