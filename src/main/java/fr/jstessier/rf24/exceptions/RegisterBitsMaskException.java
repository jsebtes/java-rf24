package fr.jstessier.rf24.exceptions;

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
 * 
 * @author J.S. TESSIER
 */
public class RegisterBitsMaskException extends RF24RuntimeException {

	private static final long serialVersionUID = -692171224533558703L;

	/** The name of the bit(s). */
	private final String bitsName;

	/** The bit(s) mask in the register. */
	private final byte bitsMask;

	/** The bit(s) value. */
	private final byte bitsValue;

	/**
	 * Constructor.
	 * 
	 * @param bitsName		The name of the bit(s) in the register.
	 * @param bitsValue		The bit(s) mask in the register.
	 * @param bitsMask		The bit(s) value.
	 */
	public RegisterBitsMaskException(String bitsName, byte bitsValue, byte bitsMask) {
		super();
		this.bitsName = bitsName;
		this.bitsValue = bitsValue;
		this.bitsMask = bitsMask;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocalizedMessage() {
		final StringBuilder message = new StringBuilder().append("Bits ").append(bitsName)
				.append(" - The bits value is not compatible with bits mask (value=")
				.append(bitsValue).append(", mask=").append(bitsMask).append(")");
		return message.toString();
	}

}
