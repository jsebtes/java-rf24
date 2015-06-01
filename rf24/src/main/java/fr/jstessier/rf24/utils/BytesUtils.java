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

import java.io.UnsupportedEncodingException;

/**
 * Utilities for bytes.
 * 
 * @author J.S. TESSIER
 */
public class BytesUtils {

	/**
	 * Private constructor.
	 */
	private BytesUtils() {
		// NOP
	}

	/**
	 * Remove the first byte (index = 0) of a array of byte.
	 * 
	 * @param bytes	The array of bytes source.
	 * @return	The array of bytes without the first byte.
	 */
	public static byte[] removeFirstByte(byte[] bytes) {
		if (bytes == null) {
			return null;
		} else if (bytes.length < 2) {
			return new byte[0];
		} else {
			byte[] result = new byte[bytes.length - 1];
			System.arraycopy(bytes, 1, result, 0, result.length);
			return result;
		}
	}

	/**
	 * Reverse the order of bytes in a array of byte. 
	 * [ByteA, ByteB, ByteC, ByteD] is tranform in [ByteD, ByteC, ByteB, ByteA].
	 * 
	 * @param bytes	The array of bytes source.
	 * @return	The inverted array of byte.
	 */
	public static byte[] reverseBytes(byte[] bytes) {
		if (bytes == null) {
			return null;
		} else if (bytes.length < 2) {
			return bytes;
		} else {
			byte[] value = new byte[bytes.length];
			for (int i = 0; i < bytes.length; i++) {
				value[i] = bytes[bytes.length - 1 - i];
			}
			return value;
		}
	}

	/**
	 * Decode bytes to a string with default charset ("null safe").
	 * 
	 * @param bytes Bytes to decode.
	 * @return	The result string.
	 */
	public static String bytesToString(byte... bytes) {
		if (bytes == null) {
			return null;
		}
		return new String(bytes);
	}

	/**
	 * Decode bytes to a string ("null safe").
	 * 
	 * @param charset	The charset to decode bytes.
	 * @param bytes		Bytes to decode.
	 * @return	The result string.
	 * @throws UnsupportedEncodingException	If the named charset is not supported.
	 */
	public static String bytesToString(String charset, byte... bytes) throws UnsupportedEncodingException {
		if (bytes == null) {
			return null;
		}
		return new String(bytes, charset);
	}

	/**
	 * Convert bytes to HEX values in a string ("null safe").
	 * 
	 * @param bytes	Bytes to convert.
	 * @return	The result string.
	 */
	public static String bytesToHex(byte... bytes) {
		if (bytes == null) {
			return null;
		}
		final char[] hexArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] hexChars = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			hexChars[i * 2] = hexArray[(Byte.toUnsignedInt(bytes[i]) >>> 4)];
			hexChars[i * 2 + 1] = hexArray[bytes[i] & 0x0F];
		}
		return new String(hexChars);
	}

}
