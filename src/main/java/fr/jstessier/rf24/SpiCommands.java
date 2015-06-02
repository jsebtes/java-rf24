package fr.jstessier.rf24;

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

import fr.jstessier.rf24.annotations.Command;
import fr.jstessier.rf24.annotations.LengthRange;
import fr.jstessier.rf24.annotations.Mask;

/**
 * SPI Commands to communicate with the nRF24L01+ module.
 * 
 * @author J.S. TESSIER
 */
public class SpiCommands {

	/**
	 * Marker interface for spi command.
	 * To get command value.
	 */
	public interface SpiCommand {}

	/**
	 * Marker interface for read / write spi command.
	 * To get command and mask value.
	 */
	public interface SpiCommandReadWrite {}

	/**
	 * Read command and status registers.
	 * Command word (binary) : 000A AAAA
	 * AAAAA = 5 bit Register Map Address
	 * 
	 * Data bytes : 1 to 5 LSByte first.
	 */
	@Command(0b00000000)
	@Mask(0b00011111)
	@LengthRange(min = 1, max = 5)
	public class R_REGISTER implements SpiCommandReadWrite {}

	/**
	 * Write command and status registers.
	 * Command word (binary) : 001A AAAA
	 * AAAAA = 5 bit Register Map Address
	 * Executable in power down or standby modes only.
	 * 
	 * Data bytes : 1 to 5 LSByte first.
	 */
	@Command(0b00100000)
	@Mask(0b00011111)
	@LengthRange(min = 1, max = 5)
	public class W_REGISTER implements SpiCommandReadWrite {}

	/**
	 * Read RX-payload: 1 – 32 bytes. 
	 * A read operation always starts at byte 0.
	 * Payload is deleted from FIFO after it is read. Used in RX mode.
	 * 
	 * Data bytes : 1 to 32 LSByte first.
	 */
	@Command(0b01100001)
	@LengthRange(min = 1, max = 32)
	public class R_RX_PAYLOAD implements SpiCommand {}

	/**
	 * Write TX-payload: 1 – 32 bytes. 
	 * A write operation always starts at byte 0 used in TX payload.
	 * 
	 * Data bytes : 1 to 32 LSByte first.
	 */
	@Command((byte) 0b10100000)
	@LengthRange(min = 1, max = 32)
	public class W_TX_PAYLOAD implements SpiCommand {}

	/**
	 * Flush TX FIFO, used in TX mode.
	 * 
	 * Data bytes : 0.
	 */
	@Command((byte) 0b11100001)
	@LengthRange(min = 0, max = 0)
	public class FLUSH_TX implements SpiCommand {}

	/**
	 * Flush RX FIFO, used in RX mode.
	 * Should not be executed during transmission of acknowledge, that is, acknowledge package will not be completed.
	 * 
	 * Data bytes : 0.
	 */
	@Command((byte) 0b11100010)
	@LengthRange(min = 0, max = 0)
	public class FLUSH_RX implements SpiCommand {}

	/**
	 * Used for a PTX device.
	 * Reuse last transmitted payload.
	 * TX payload reuse is active until W_TX_PAYLOAD or FLUSH TX is executed. 
	 * TX payload reuse must not be activated or deactivated during package transmission.
	 * 
	 * Data bytes : 0.
	 */
	@Command((byte) 0b11100011)
	@LengthRange(min = 0, max = 0)
	public class REUSE_TX_PL implements SpiCommand {}

	/**
	 * Read RX payload width for the top R_RX_PAYLOAD in the RX FIFO.
	 * Note: Flush RX FIFO if the read value is larger than 32 bytes.
	 * 
	 * Data bytes : 1.
	 * 
	 * Note: The bits in the FEATURE register have to be set.
	 */
	@Command(0b01100000)
	@LengthRange(min = 1, max = 1)
	public class R_RX_PL_WID implements SpiCommand {};

	/**
	 * Used in RX mode.
	 * Command word (binary) : 1010 1PPP.
	 * Write Payload to be transmitted together with ACK packet on PIPE PPP. 
	 * (PPP valid in the range from 000 to 101). Maximum three ACK packet payloads 
	 * can be pending. Payloads with same PPP are handled using first in - first out 
	 * principle. Write payload: 1– 32 bytes. A write operation always starts at byte 0.
	 * 
	 * Data bytes : 1 to 32 LSByte first.
	 * 
	 * Note: The bits in the FEATURE register have to be set.
	 */
	@Command((byte) 0b10101000)
	@LengthRange(min = 1, max = 32)
	public class W_ACK_PAYLOAD implements SpiCommand {};

	/**
	 * Used in TX mode. Disables AUTOACK on this specific packet.
	 * 
	 * Data bytes : 1 to 32 LSByte first.
	 * 
	 * Note: The bits in the FEATURE register have to be set.
	 */
	@Command((byte) 0b10110000)
	@LengthRange(min = 1, max = 32)
	public class W_TX_PAYLOAD_NOACK implements SpiCommand {}

	/**
	 * No Operation. Might be used to read the STATUS register.
	 * 
	 * Data bytes : 0.
	 */
	@Command((byte) 0b11111111)
	@LengthRange(min = 0, max = 0)
	public class NOP implements SpiCommand {}

}
