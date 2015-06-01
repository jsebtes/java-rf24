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
public class SpiCommandRequestLengthException extends RF24RuntimeException {

	private static final long serialVersionUID = 808324971263013213L;

	/** The name of the spi command. */
	private final String spiCommand;

	/** The request data length. */
	private final int requestDataLength;

	/** The spi command min request data length. */
	private final byte minRequestLength;

	/** The spi command max request data length. */
	private final byte maxRequestLength;


	/**
	 * Constructor.
	 * 
	 * @param spiCommand		The name of the spi command.
	 * @param requestDataLength	The request data length.
	 * @param minRequestLength	The spi command min request data length.
	 * @param maxRequestLength	The spi command max request data length.
	 */
	public SpiCommandRequestLengthException(String spiCommand, int requestDataLength, byte minRequestLength, byte maxRequestLength) {
		super();
		this.spiCommand = spiCommand;
		this.requestDataLength = requestDataLength;
		this.minRequestLength = minRequestLength;
		this.maxRequestLength = maxRequestLength;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLocalizedMessage() {
		final StringBuilder message = new StringBuilder().append("Spi command ").append(spiCommand).append(" request error ")
				.append(" - The request data length is not compatible with the spi command (requestDataLength=")
				.append(requestDataLength).append(", minRequestLength=").append(minRequestLength)
				.append(", maxRequestLength=").append(maxRequestLength).append(")");
		return message.toString();
	}

}
