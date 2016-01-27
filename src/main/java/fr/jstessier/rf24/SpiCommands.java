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

/**
 * SPI Commands to communicate with the nRF24L01+ module.
 * 
 * @author J.S. TESSIER
 */
public class SpiCommands {

	/**
	 * Interface for spi command.
	 * To get command value and min/max data length.
	 */
	public interface SpiCommandCommon {

		/**
		 * Return the data min length in Byte.
		 * @return	The data min length.
		 */
		byte getDataMinLength();

		/**
		 * Return the data mas length in Byte.
		 * @return	The data max length.
		 */
		byte getDataMaxLength();

		/**
		 * Return the name of the SPI command.
		 * @return
		 */
		String getName();
	}
	
	/**
	 * Interface for spi command.
	 * To get command value and min/max data length.
	 */
	public interface SpiCommand extends SpiCommandCommon {

		/**
		 * Return the SPI command value.
		 * @return	The SPI command value.
		 */
		byte getCommand();

	}

	/**
	 * Interface for read / write register spi command.
	 * To get command register mask value.
	 */
	public interface SpiCommandReadWriteRegister extends SpiCommandCommon {

		/**
		 * Return the SPI command value.
		 * @return	The SPI command value.
		 */
		byte getBaseCommand();

		/**
		 * Return the mask to calculate SPI from register address. 
		 * @return	The command register mask.
		 */
		byte getCommandRegisterMask();

	}

	/**
	 * Read command and status registers.
	 * Command word (binary) : 000A AAAA
	 * AAAAA = 5 bit Register Map Address
	 * 
	 * Data bytes : 1 to 5 LSByte first.
	 */
	public static final RRegisterCommand R_REGISTER = new RRegisterCommand(); 

	public static class RRegisterCommand implements SpiCommandReadWriteRegister {

		/** {@inheritDoc} */
		@Override
		public byte getBaseCommand() { return 0b00000000; }

		/** {@inheritDoc} */
		@Override
		public byte getCommandRegisterMask() { return 0b00011111; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMinLength() { return 1; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMaxLength() { return 5; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "R_REGISTER"; }

	}

	/**
	 * Write command and status registers.
	 * Command word (binary) : 001A AAAA
	 * AAAAA = 5 bit Register Map Address
	 * Executable in power down or standby modes only.
	 * 
	 * Data bytes : 1 to 5 LSByte first.
	 */
	public static final WRegisterCommand W_REGISTER = new WRegisterCommand(); 

	public static class WRegisterCommand implements SpiCommandReadWriteRegister {

		/** {@inheritDoc} */
		@Override
		public byte getBaseCommand() { return 0b00100000; }

		/** {@inheritDoc} */
		@Override
		public byte getCommandRegisterMask() { return 0b00011111; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMinLength() { return 1; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMaxLength() { return 5; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "W_REGISTER"; }

	}

	/**
	 * Read RX-payload: 1 – 32 bytes. 
	 * A read operation always starts at byte 0.
	 * Payload is deleted from FIFO after it is read. Used in RX mode.
	 * 
	 * Data bytes : 1 to 32 LSByte first.
	 */
	public static final RRxPayloadCommand R_RX_PAYLOAD = new RRxPayloadCommand(); 

	public static class RRxPayloadCommand implements SpiCommand {

		/** {@inheritDoc} */
		@Override
		public byte getCommand() { return 0b01100001; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMinLength() { return 1; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMaxLength() { return 32; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "R_RX_PAYLOAD"; }

	}

	/**
	 * Write TX-payload: 1 – 32 bytes. 
	 * A write operation always starts at byte 0 used in TX payload.
	 * 
	 * Data bytes : 1 to 32 LSByte first.
	 */
	public static final WTxPayloadCommand W_TX_PAYLOAD = new WTxPayloadCommand(); 

	public static class WTxPayloadCommand implements SpiCommand {

		/** {@inheritDoc} */
		@Override
		public byte getCommand() { return (byte) 0b10100000; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMinLength() { return 1; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMaxLength() { return 32; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "W_TX_PAYLOAD"; }

	}

	/**
	 * Flush TX FIFO, used in TX mode.
	 * 
	 * Data bytes : 0.
	 */
	public static final FlushTxCommand FLUSH_TX = new FlushTxCommand(); 

	public static class FlushTxCommand implements SpiCommand {

		/** {@inheritDoc} */
		@Override
		public byte getCommand() { return (byte) 0b11100001; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMinLength() { return 0; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMaxLength() { return 0; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "FLUSH_TX"; }

	}

	/**
	 * Flush RX FIFO, used in RX mode.
	 * Should not be executed during transmission of acknowledge, that is, acknowledge package will not be completed.
	 * 
	 * Data bytes : 0.
	 */
	public static final FlushRxCommand FLUSH_RX = new FlushRxCommand(); 

	public static class FlushRxCommand implements SpiCommand {

		/** {@inheritDoc} */
		@Override
		public byte getCommand() { return (byte) 0b11100010; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMinLength() { return 0; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMaxLength() { return 0; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "FLUSH_RX"; }

	}

	/**
	 * Used for a PTX device.
	 * Reuse last transmitted payload.
	 * TX payload reuse is active until W_TX_PAYLOAD or FLUSH TX is executed. 
	 * TX payload reuse must not be activated or deactivated during package transmission.
	 * 
	 * Data bytes : 0.
	 */
	public static final ReuseTxPlCommand REUSE_TX_PL = new ReuseTxPlCommand(); 

	public static class ReuseTxPlCommand implements SpiCommand {

		/** {@inheritDoc} */
		@Override
		public byte getCommand() { return (byte) 0b11100011; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMinLength() { return 0; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMaxLength() { return 0; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "REUSE_TX_PL"; }

	}

	/**
	 * Read RX payload width for the top R_RX_PAYLOAD in the RX FIFO.
	 * Note: Flush RX FIFO if the read value is larger than 32 bytes.
	 * 
	 * Data bytes : 1.
	 * 
	 * Note: The bits in the FEATURE register have to be set.
	 */
	public static final RRxPlWidCommand R_RX_PL_WID = new RRxPlWidCommand(); 

	public static class RRxPlWidCommand implements SpiCommand {

		/** {@inheritDoc} */
		@Override
		public byte getCommand() { return 0b01100000; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMinLength() { return 1; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMaxLength() { return 1; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "R_RX_PL_WID"; }

	};

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
	public static final WAckPayloadCommand W_ACK_PAYLOAD = new WAckPayloadCommand(); 

	public static class WAckPayloadCommand implements SpiCommand {

		/** {@inheritDoc} */
		@Override
		public byte getCommand() { return (byte) 0b10101000; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMinLength() { return 1; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMaxLength() { return 32; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "W_ACK_PAYLOAD"; }

	};

	/**
	 * Used in TX mode. Disables AUTOACK on this specific packet.
	 * 
	 * Data bytes : 1 to 32 LSByte first.
	 * 
	 * Note: The bits in the FEATURE register have to be set.
	 */
	public static final WTxPayloadNoackCommand W_TX_PAYLOAD_NOACK = new WTxPayloadNoackCommand(); 

	public static class WTxPayloadNoackCommand implements SpiCommand {

		/** {@inheritDoc} */
		@Override
		public byte getCommand() { return (byte) 0b10110000; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMinLength() { return 1; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMaxLength() { return 32; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "W_TX_PAYLOAD_NOACK"; }

	}

	/**
	 * No Operation. Might be used to read the STATUS register.
	 * 
	 * Data bytes : 0.
	 */
	public static final NopCommand NOP = new NopCommand(); 

	public static class NopCommand implements SpiCommand {

		/** {@inheritDoc} */
		@Override
		public byte getCommand() { return (byte) 0b11111111; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMinLength() { return 0; }

		/** {@inheritDoc} */
		@Override
		public byte getDataMaxLength() { return 0; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "NOP"; }

	}

}
