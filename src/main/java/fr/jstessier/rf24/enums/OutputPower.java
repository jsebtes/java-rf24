package fr.jstessier.rf24.enums;

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

/**
 * Output Power.
 * 
 * @author J.S. TESSIER
 */
public enum OutputPower {

	/** Power -18 dBm. */
	RF_M_18_DBM((byte) 0b00),
	/** Power -12 dBm. */
	RF_M_12_DBM((byte) 0b01),
	/** Power -6 dBm. */
	RF_M_6_DBM((byte) 0b10),
	/** Power 0 dBm. */
	RF_0_DBM((byte) 0b11);

	/** The value of the output power in the register RF_SETUP. */
	private final byte value;

	/**
	 * Private constructor.
	 * 
	 * @param value	The value of the output power in the register RF_SETUP.
	 */
	private OutputPower(byte value) {
		this.value = value;
	}

	/**
	 * Return the value of the output power in the register RF_SETUP.
	 * @return	The value of the output power in the register RF_SETUP.
	 */
	public byte getValue() {
		return value;
	}

	/**
	 * Return an OutputPower from a value read in register RF_SETUP.
	 * 
	 * @param value	The value of the output power in the register RF_SETUP.
	 * @return	Return the OutputPower from the value.
	 * @throws IllegalArgumentException If the value not exists in the enum.
	 */
	public static OutputPower getFromValue(byte value) {
		for (OutputPower outputPower : values()) {
			if (value == outputPower.getValue()) {
				return outputPower;
			}
		}
		throw new IllegalArgumentException("There is no OutputPower for the value = " + value);
	}

}
