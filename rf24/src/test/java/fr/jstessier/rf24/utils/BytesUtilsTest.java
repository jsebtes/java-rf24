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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

/**
 * Tests for BytesUtils.
 * 
 * @author J.S. TESSIER
 */
public class BytesUtilsTest {

	@Test
	public void removeFirstByte_with_null() {
		assertNull(BytesUtils.removeFirstByte(null));
	}

	@Test
	public void removeFirstByte_with_empty() {
		assertEquals(0, BytesUtils.removeFirstByte(new byte[0]).length);
	}

	@Test
	public void removeFirstByte_with_1_byte() {
		assertEquals(0, BytesUtils.removeFirstByte(new byte[1]).length);
	}

	@Test
	public void removeFirstByte_with_4_bytes() {
		byte[] result = BytesUtils.removeFirstByte(new byte[] { (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04}); 
		assertEquals(3, result.length);
		assertEquals((byte) 0x02, result[0]);
		assertEquals((byte) 0x03, result[1]);
		assertEquals((byte) 0x04, result[2]);
	}

	@Test
	public void reverseBytes_with_null() {
		assertNull(BytesUtils.reverseBytes(null));
	}

	@Test
	public void reverseBytes_with_empty() {
		assertEquals(0, BytesUtils.reverseBytes(new byte[0]).length);
	}

	@Test
	public void reverseBytes_with_1_byte() {
		byte[] result = BytesUtils.reverseBytes(new byte[] { (byte) 0x04 });
		assertEquals(1, result.length);
		assertEquals((byte) 0x04, result[0]);
	}

	@Test
	public void reverseBytes_with_4_bytes() {
		byte[] result = BytesUtils.reverseBytes(new byte[] { (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04}); 
		assertEquals(4, result.length);
		assertEquals((byte) 0x04, result[0]);
		assertEquals((byte) 0x03, result[1]);
		assertEquals((byte) 0x02, result[2]);
		assertEquals((byte) 0x01, result[3]);
	}

	@Test
	public void bytesToHex_with_null() {
		assertNull(BytesUtils.bytesToHex(null));
	}

	@Test
	public void bytesToHex_with_empty() {
		assertEquals("", BytesUtils.bytesToHex());
		assertEquals("", BytesUtils.bytesToHex(new byte[0]));
	}

	@Test
	public void bytesToHex() {
		assertEquals("00030F4F8FFFF0", 
				BytesUtils.bytesToHex(new byte[] { 
						(byte) 0x00, (byte) 0x03, (byte) 0x0F, (byte) 0x4F, (byte) 0x8F, (byte) 0xFF, (byte) 0xF0 }));
	}

	@Test
	public void bytesToString_nocharset_with_null() {
		assertNull(BytesUtils.bytesToString(null));
	}

	@Test
	public void bytesToString_nocharset_with_empty() {
		assertEquals("", BytesUtils.bytesToString());
		assertEquals("", BytesUtils.bytesToString(new byte[0]));
	}

	@Test
	public void bytesToString_nocharset_with_bytes() {
		assertEquals("O^4", BytesUtils.bytesToString((byte) 0x4F, (byte) 0x5E, (byte) 0x34));
		assertEquals("O^4", BytesUtils.bytesToString(new byte[] { (byte) 0x4F, (byte) 0x5E, (byte) 0x34 }));
	}

	@Test(expected = UnsupportedEncodingException.class)
	public void bytesToString_charset_unsupported() throws UnsupportedEncodingException {
		assertNull(BytesUtils.bytesToString("TOTO", new byte[] { (byte) 0xFF }));
	}

	@Test
	public void bytesToString_charset_with_null() throws UnsupportedEncodingException {
		assertNull(BytesUtils.bytesToString("UTF-16", null));
	}

	@Test
	public void bytesToString_charset_with_empty() throws UnsupportedEncodingException {
		assertEquals("", BytesUtils.bytesToString("UTF-16"));
		assertEquals("", BytesUtils.bytesToString("UTF-16", new byte[0]));
	}

	@Test
	public void bytesToString_chartset_with_bytes() throws UnsupportedEncodingException {
		assertEquals("TEST", BytesUtils.bytesToString("UTF-16", 
				new byte[] { (byte) 0xFE, (byte) 0xFF, (byte) 0x00, (byte) 0x54, (byte) 0x00, (byte) 0x45, 
						(byte) 0x00, (byte) 0x53, (byte) 0x00, (byte) 0x54 }));
	}

}
