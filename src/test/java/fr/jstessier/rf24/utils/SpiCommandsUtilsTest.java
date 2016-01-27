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

import org.junit.Test;

import fr.jstessier.rf24.Registers;
import fr.jstessier.rf24.SpiCommands;
import fr.jstessier.rf24.exceptions.SpiCommandRequestLengthException;

/**
 * Tests for SpiCommandsUtils.
 * 
 * @author J.S. TESSIER
 */
public class SpiCommandsUtilsTest {

	@Test
	public void getSpiCommand_for_standard_command() {
		assertEquals((byte) 0b01100001, SpiCommandsUtils.getSpiCommand(SpiCommands.R_RX_PAYLOAD));
	}

	@Test
	public void getSpiCommand_for_registers_by_class() {
		assertEquals((byte) 0b00100001, SpiCommandsUtils.getSpiCommand(SpiCommands.W_REGISTER, Registers.EN_AA));
	}

	@Test
	public void getSpiCommand_for_registers_by_address() {
		assertEquals((byte) 0b00100010, SpiCommandsUtils.getSpiCommand(SpiCommands.W_REGISTER, (byte) 0x02));
	}

	@Test
	public void checkDataLength_min_include_OK() {
		SpiCommandsUtils.checkDataLength(SpiCommands.R_RX_PAYLOAD, new byte[1]);
	}
	
	@Test
	public void checkDataLength_max_include_OK() {
		SpiCommandsUtils.checkDataLength(SpiCommands.R_RX_PAYLOAD, new byte[32]);
	}

	@Test(expected = SpiCommandRequestLengthException.class)
	public void checkDataLength_min_KO() {
		SpiCommandsUtils.checkDataLength(SpiCommands.R_RX_PAYLOAD, new byte[0]);
	}
	
	@Test(expected = SpiCommandRequestLengthException.class)
	public void checkDataLength_max_KO() {
		SpiCommandsUtils.checkDataLength(SpiCommands.R_RX_PAYLOAD, new byte[33]);
	}

}
