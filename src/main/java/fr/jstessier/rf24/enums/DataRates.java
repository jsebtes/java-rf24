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
 * Data rates.
 * 
 * @author J.S. TESSIER
 */
public enum DataRates {

	/** 250 kBps. */
	DR_250_KBPS(true, false), 
	/** 1 MBps. */
	DR_1_MBPS(false, false), 
	/** 2 MBps. */
	DR_2_MBPS(false, true);

	/** The value RF_DR_LOW bit value in register RF_SETUP. */
	private final boolean rfDrLow;

	/** The value of RF_DR_HIGH bit value in register RF_SETUP. */
	private final boolean rfDrHigh;

	/**
	 * Private constructor.
	 * 
	 * @param rfDrLow	RF_DR_LOW bit value in register RF_SETUP.
	 * @param rfDrHigh	RF_DR_HIGH bit value in register RF_SETUP.
	 */
	private DataRates(boolean rfDrLow, boolean rfDrHigh) {
		this.rfDrLow = rfDrLow;
		this.rfDrHigh = rfDrHigh;
	}

	/**
	 * Return the value RF_DR_LOW bit value in register RF_SETUP.
	 * @return	The value RF_DR_LOW bit value in register RF_SETUP.
	 */
	public boolean getRfDrLow() {
		return rfDrLow;
	}

	/**
	 * Return the value of RF_DR_HIGH bit value in register RF_SETUP.
	 * @return	The value of RF_DR_HIGH bit value in register RF_SETUP.
	 */
	public boolean getRfDrHigh() {
		return rfDrHigh;
	}

	/**
	 * Return an DataRates from RF_DR_LOW and RF_DR_HIGH values read in register RF_SETUP.
	 * 
	 * @param rfDrLow	The value RF_DR_LOW bit value read in register RF_SETUP.
	 * @param rfDrHigh	The value of RF_DR_HIGH bit value read in register RF_SETUP.
	 * @return	Return the DataRates from RF_DR_LOW and RF_DR_HIGH values read in register RF_SETUP.
	 * @throws IllegalArgumentException If the value not exists in the enum.
	 */
	public static DataRates getFromValue(boolean rfDrLow, boolean rfDrHigh) {
		for (DataRates dataRates : values()) {
			if (rfDrLow == dataRates.getRfDrLow() && rfDrHigh == dataRates.getRfDrHigh()) {
				return dataRates;
			}
		}
		throw new IllegalArgumentException("There is no DataRates for the rfDrLow = " + rfDrLow + " and rfDrHigh = " + rfDrHigh);
	}

}
