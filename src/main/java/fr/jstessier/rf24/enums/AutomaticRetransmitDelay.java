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
 * Delay for auto retry.
 * 
 * @author J.S. TESSIER
 */
public enum AutomaticRetransmitDelay {

	/** 250 µS. */
	ARD_0250_US((byte) 0b0000), 
	/** 500 µS. */
	ARD_0500_US((byte) 0b0001),
	/** 750 µS. */
	ARD_0750_US((byte) 0b0010),
	/** 1000 µS. */
	ARD_1000_US((byte) 0b0011),
	/** 1250 µS. */
	ARD_1250_US((byte) 0b0100),
	/** 1500 µS. */
	ARD_1500_US((byte) 0b0101),
	/** 1750 µS. */
	ARD_1750_US((byte) 0b0110),
	/** 2000 µS. */
	ARD_2000_US((byte) 0b0111),
	/** 2250 µS. */
	ARD_2250_US((byte) 0b1000),
	/** 2500 µS. */
	ARD_2500_US((byte) 0b1001),
	/** 2750 µS. */
	ARD_2750_US((byte) 0b1010),
	/** 3000 µS. */
	ARD_3000_US((byte) 0b1011),
	/** 3250 µS. */
	ARD_3250_US((byte) 0b1100),
	/** 3500 µS. */
	ARD_3500_US((byte) 0b1101),
	/** 3750 µS. */
	ARD_3750_US((byte) 0b1110),
	/** 4000 µS. */
	ARD_4000_US((byte) 0b1111);

	/** The value of the delay in the register SETUP_RETR. */
	private final byte value;

	/**
	 * Private constructor.
	 * 
	 * @param value	The value of the delay in the register SETUP_RETR.
	 */
	private AutomaticRetransmitDelay(byte value) {
		this.value = value;
	}

	/**
	 * Return the value of the delay in the register SETUP_RETR.
	 * @return	The value of the delay in the register SETUP_RETR.
	 */
	public byte getValue() {
		return value;
	}

	/**
	 * Return an AutomaticRetransmitDelay from a value read in register SETUP_RETR.
	 * 
	 * @param value	Return an AutomaticRetransmitDelay from a value read in register SETUP_RETR.
	 * @return	Return the AutomaticRetransmitDelay from the value.
	 * @throws IllegalArgumentException If the value not exists in the enum.
	 */
	public static AutomaticRetransmitDelay getFromValue(byte value) {
		for (AutomaticRetransmitDelay automaticRetransmitDelay : values()) {
			if (value == automaticRetransmitDelay.getValue()) {
				return automaticRetransmitDelay;
			}
		}
		throw new IllegalArgumentException("There is no AutomaticRetransmitDelay for the value = " + value);
	}

}
