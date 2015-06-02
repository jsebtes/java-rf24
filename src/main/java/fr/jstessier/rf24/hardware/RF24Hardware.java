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

import fr.jstessier.rf24.exceptions.RF24Exception;

/**
 * Hardware interface to RF24 module.
 * 
 * @author J.S. TESSIER
 */
public interface RF24Hardware {

	/** 
	 * SPI mode is 0. 
	 * See http://en.wikipedia.org/wiki/Serial_Peripheral_Interface_Bus#Mode_numbers.
	 */
	public static final int SPI_MODE_0 = 0;

	public void setPinChipEnableHigh();

	public void setPinChipEnableLow();

	public byte[] spiWrite(byte... data) throws RF24Exception;

}
