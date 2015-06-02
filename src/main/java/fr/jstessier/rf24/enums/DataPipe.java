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
 * Data pipe.
 * 
 * @author J.S. TESSIER
 */
public enum DataPipe {

	/** Data pipe 0. */
	P0((byte) 0b000001, (byte) 0),
	/** Data pipe 1. */
	P1((byte) 0b000010, (byte) 1),
	/** Data pipe 2. */
	P2((byte) 0b000100, (byte) 2),
	/** Data pipe 3. */
	P3((byte) 0b001000, (byte) 3),
	/** Data pipe 4. */
	P4((byte) 0b010000, (byte) 4),
	/** Data pipe 5. */
	P5((byte) 0b100000, (byte) 5);

	/** The mask to use with R_REGISTER/W_REGISTER SPI commands. */
	private final byte mask;

	/** The index of the pipe. */
	private final byte index;

	/**
	 * Private constructor.
	 * 
	 * @param mask	The mask to use with R_REGISTER/W_REGISTER SPI commands.
	 * @param index	The index of the pipe.
	 */
	private DataPipe(byte mask, byte index) {
		this.mask = mask;
		this.index = index;
	}

	/**
	 * Return the mask to use with R_REGISTER/W_REGISTER SPI commands.
	 * @return	The mask to use with R_REGISTER/W_REGISTER SPI commands.
	 */
	public byte getMask() {
		return mask;
	}

	/**
	 * Return the index of the pipe.
	 * @return	The index of the pipe.
	 */
	public byte getIndex() {
		return index;
	}

	/**
	 * Return an DataPipe from an index.
	 * 
	 * @param index	The index of the pipe.
	 * @return	Return the DataPipe from the index.
	 * @throws IllegalArgumentException If the value not exists in the enum.
	 */
	public static DataPipe getFromIndex(byte index) {
		for (DataPipe dataPipe : values()) {
			if ((1 << index) == dataPipe.getIndex()) {
				return dataPipe;
			}
		}
		throw new IllegalArgumentException("There is no DataPipe for index = " + index);
	}

}
