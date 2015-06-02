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
 * The address field width for pipes. 
 * 
 * @author J.S. TESSIER
 */
public enum AddressFieldWidth {

	/** Address on 3 bytes. */
	WIDTH_3_BYTES((byte) 0b01, (byte) 3),
	/** Address on 4 bytes. */
	WIDTH_4_BYTES((byte) 0b10, (byte) 4),
	/** Address on 5 bytes. */
	WIDTH_5_BYTES((byte) 0b11, (byte) 5);

	/** The value of the address field width in the register SETUP_AW. */
	private final byte value;

	/** The width of the address field width in bytes. */
	private final byte width;

	/**
	 * Private constructor.
	 * 
	 * @param value	The value of the address field width in the register SETUP_AW.
	 * @param width	The width of the address field width in bytes.
	 */
	private AddressFieldWidth(byte value, byte width) {
		this.value = value;
		this.width = width;
	}

	/**
	 * Return the value of the address field width in the register SETUP_AW.
	 * @return	The value of the address field width in the register SETUP_AW.
	 */
	public byte getValue() {
		return value;
	}

	/**
	 * Return the width of the address field width in bytes.
	 * @return	The width of the address field width in bytes.
	 */
	public byte getWidth() {
		return width;
	}

	/**
	 * Return an AddressFieldWidth from a value read in register SETUP_AW.
	 * 
	 * @param value	The value of the address in the register SETUP_AW.
	 * @return	Return the AddressFieldWidth from the value.
	 * @throws IllegalArgumentException If the value not exists in the enum.
	 */
	public static AddressFieldWidth getFromValue(byte value) {
		for (AddressFieldWidth addressFieldWidth : values()) {
			if (value == addressFieldWidth.getValue()) {
				return addressFieldWidth;
			}
		}
		throw new IllegalArgumentException("There is no AddressFieldWidth for the value = " + value);
	}

	/**
	 * Return an AddressFieldWidth from a width in bytes.
	 * 
	 * @param width	The width of the address field width in bytes.
	 * @return The AddressFieldWidth from the width in bytes.
	 * @throws IllegalArgumentException If the value not exists in the enum.
	 */
	public static AddressFieldWidth getFromWidth(byte width) {
		for (AddressFieldWidth addressFieldWidth : values()) {
			if (width == addressFieldWidth.getWidth()) {
				return addressFieldWidth;
			}
		}
		throw new IllegalArgumentException("There is no AddressFieldWidth for the width = " + width);
	}

}
