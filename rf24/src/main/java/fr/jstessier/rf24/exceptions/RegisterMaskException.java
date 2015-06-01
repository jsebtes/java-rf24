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
public class RegisterMaskException extends RF24RuntimeException {

	private static final long serialVersionUID = -692171224533558703L;

	/** The name of the register. */
	private final String registerName;

	private final byte registerMask;

	private final byte registerValue;
	
	public RegisterMaskException(String registerName, byte registerValue, byte registerMask) {
		super();
		this.registerName = registerName;
		this.registerValue = registerValue;
		this.registerMask = registerMask;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocalizedMessage() {
		final StringBuilder message = new StringBuilder().append("Register ")
				.append(registerName).append(" - The value is not compatible with register mask (value=")
				.append(registerValue).append(", mask=").append(registerMask).append(")");
		return message.toString();
	}

}
