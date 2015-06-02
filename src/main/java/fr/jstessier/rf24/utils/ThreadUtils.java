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

/**
 * Utilities for thread.
 * 
 * @author J.S. TESSIER
 */
public class ThreadUtils {

	private ThreadUtils() {
		// NOP
	}

	/**
	 * Delay in milliseconds.
	 * 
	 * @param milliseconds	The length of time to sleep in milliseconds.
	 */
	public static void delay(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			// NOP
		}
	}

	/**
	 * Delay in microseconds.
	 * 
	 * @param microseconds	The length of time to sleep in microseconds.
	 */
	public static void delayMicroseconds(int microseconds) {
		try {
			Thread.sleep(0, microseconds * 1000);
		} catch (InterruptedException e) {
			// NOP
		}
	}

}
