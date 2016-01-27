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

import org.junit.Assert;
import org.junit.Test;

import fr.jstessier.rf24.Registers;

/**
 * Tests for RegistersUtils.
 * 
 * @author J.S. TESSIER
 */
public class RegistersUtilsTest {

	@Test
	public void updateRegisterBits_with_bits() {

		byte updatedValue = RegistersUtils.updateRegisterBits((byte) 0b00010101, Registers.SETUP_RETR.ARD, (byte) 0b0001);
		Assert.assertEquals((byte) 0b00010101, updatedValue);

		updatedValue = RegistersUtils.updateRegisterBits((byte) 0b01110101, Registers.SETUP_RETR.ARD, (byte) 0b0001);	
		Assert.assertEquals((byte) 0b00010101, updatedValue);

		updatedValue = RegistersUtils.updateRegisterBits((byte) 0b00000101, Registers.SETUP_RETR.ARD, (byte) 0b1111);
		Assert.assertEquals((byte) 0b11110101, updatedValue);

		updatedValue = RegistersUtils.updateRegisterBits((byte) 0b00000101, Registers.SETUP_RETR.ARC, (byte) 0b1111);
		Assert.assertEquals((byte) 0b00001111, updatedValue);
	}

	@Test
	public void updateRegisterBits_with_bit() {

		byte updatedValue = RegistersUtils.updateRegisterBits((byte) 0b01000000, Registers.CONFIG.MASK_RX_DR, (byte) 0b1);
		Assert.assertEquals((byte) 0b01000000, updatedValue);

		updatedValue = RegistersUtils.updateRegisterBits((byte) 0b01001000, Registers.CONFIG.MASK_RX_DR, (byte) 0b0);
		Assert.assertEquals((byte) 0b00001000, updatedValue);
	}

}
