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
import fr.jstessier.rf24.SpiCommands.SpiCommandReadWrite;
import fr.jstessier.rf24.annotations.Command;
import fr.jstessier.rf24.annotations.LengthRange;
import fr.jstessier.rf24.annotations.Mask;
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
	public static byte getSpiCommand(final Class<? extends SpiCommand> spiCommand) {
		return spiCommand.getAnnotation(Command.class).value();
	}

	/**
	 * Return the command value of a spi command for read / write register.
	 * 
	 * @param spiCommand	The SpiCommand.
	 * @param register		The register.
	 * @return	The command value.
	 */
	public static byte getSpiCommand(final Class<? extends SpiCommandReadWrite> spiCommand, 
			final Class<? extends Register> register) {
		return getSpiCommand(spiCommand, RegistersUtils.getRegisterAddress(register));
	}

	/**
	 * Return the command value of a spi command.
	 * 
	 * @param spiCommand		The SpiCommand.
	 * @param registerAddress	The register address.
	 * @return	The command value.
	 */
	public static byte getSpiCommand(final Class<? extends SpiCommandReadWrite> spiCommand, final byte registerAddress) {
		final byte mask = spiCommand.getAnnotation(Mask.class).value();
		final byte command = spiCommand.getAnnotation(Command.class).value();
		return (byte) (command | (byte) (mask & registerAddress));
	}

	public static byte getDataMinLength(final Class<? extends SpiCommand> spiCommand) {
		return spiCommand.getAnnotation(LengthRange.class).min();
	}

	public static byte getDataMaxLength(final Class<? extends SpiCommand> spiCommand) {
		return spiCommand.getAnnotation(LengthRange.class).max();
	}

	public static void checkDataLength(final Class<? extends SpiCommand> spiCommand, final byte... data) {
		int requestLength = (data == null) ? 0 : data.length;
		byte minRequestLength = getDataMinLength(spiCommand);
		byte maxRequestLength = getDataMaxLength(spiCommand);
		if (requestLength < minRequestLength || requestLength > maxRequestLength) {
			throw new SpiCommandRequestLengthException(spiCommand.getSimpleName(), requestLength, minRequestLength, maxRequestLength);
		}
	}

}
