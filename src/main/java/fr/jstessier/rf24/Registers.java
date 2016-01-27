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
 * Registers of the nRF24L01+.
 * 
 * @author J.S. TESSIER
 */
public class Registers {

	/**
	 * Interface for all registers.
	 * To get Address value.
	 */
	public interface Register {

		/**
		 * Return the address of the register on the SPI bus.
		 * @return	The address.
		 */
		byte getAddress();

		/**
		 * Return the name of the register.
		 * @return	The name.
		 */
		String getName();

	};

	/**
	 * Interface for multiple bytes register.
	 * To get ResetValue value.
	 */
	public interface RegisterByte extends Register {

		/**
		 * Return the reset value of the register.
		 * @return The reset value.
		 */
		byte getResetValue();

	};
	
	/**
	 * Interface for one register that contains bits values.
	 * To get Mask value.
	 */
	public interface RegisterByteWithBits extends RegisterByte {

		/**
		 * Return the mask of the register.
		 * @return	The mask.
		 */
		byte getMask();

	};

	/**
	 * Interface for bits values in register.
	 * To get Shift, Mask and ResetValue values.
	 */
	public interface RegisterBits {

		/**
		 * Return the shift of bits.
		 * @return The reset value.
		 */
		byte getShift();

		/**
		 * Return the mask of bits.
		 * @return	The mask.
		 */
		byte getMask();

		/**
		 * Return the reset value of bits.
		 * @return The reset value.
		 */
		byte getResetValue();

		/**
		 * Return the "parent" register.
		 * @return The "parent" register.
		 */
		RegisterByteWithBits getRegister();

		/**
		 * Return the name of bits.
		 * @return	The name.
		 */
		String getName();

	};

	/**
	 * Interface for multiple bytes register.
	 * To get Length and ResetValue value.
	 */
	public interface RegisterBytes extends Register {

		/**
		 * Return the length (in byte) of the register.
		 * @return	The length.
		 */
		byte getLength();

		/**
		 * Return the reset value of the register.
		 * @return The reset value.
		 */
		byte[] getResetValue();

	};

	/**
	 * CONFIG : Configuration Register.
	 * 
	 * Mnemonic     Bit  Reset Value  Type  Description
	 * ------------------------------------------------
	 * Reserved     7    0            R/W   Only '0' allowed
	 * MASK_RX_DR   6    0            R/W   Mask interrupt caused by RX_DR
	 *                                        1: Interrupt not reflected on the IRQ pin 
	 *                                        0: Reflect RX_DR as active low interrupt on the IRQ pin
	 * MASK_TX_DS   5    0            R/W   Mask interrupt caused by TX_DS
	 *                                        1: Interrupt not reflected on the IRQ pin 
	 *                                        0: Reflect TX_DS as active low interrupt on the IRQ pin
	 * MASK_MAX_RT  4    0            R/W   Mask interrupt caused by MAX_RT 
	 *                                        1: Interrupt not reflected on the IRQ pin
	 *                                        0: Reflect MAX_RT as active low interrupt on the IRQ pin
	 * EN_CRC       3    1            R/W   Enable CRC. Forced high if one of the bits in the EN_AA is high
	 * CRCO         2    0            R/W   CRC encoding scheme 
	 *                                       '0' - 1 byte 
	 *                                       '1' – 2 bytes
	 * PWR_UP       1    0            R/W   1: POWER UP, 0: POWER DOWN
	 * PRIM_RX      0    0            R/W   RX/TX control 
	 *                                      1: PRX, 0: PTX 
	 */
	public static final ConfigRegister CONFIG = new ConfigRegister();

	public static class ConfigRegister implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x00; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b01111111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0b00001000; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "CONFIG"; }

		/**
		 * Bit MASK_RX_DR (R/W, default=0) : Mask interrupt caused by RX_DR.
		 *  1: Interrupt not reflected on the IRQ pin.
		 *  0: Reflect RX_DR as active low interrupt on the IRQ pin.
		 */
		public final RegisterBits MASK_RX_DR = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 6; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return ConfigRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".MASK_RX_DR"; }

		};

		/**
		 * Bit MASK_TX_DS (R/W, default=0) : Mask interrupt caused by TX_DS.
		 *  1: Interrupt not reflected on the IRQ pin.
		 *  0: Reflect TX_DS as active low interrupt on the IRQ pin.
		 */
		public final RegisterBits MASK_TX_DS = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 5; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return ConfigRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".MASK_TX_DS"; }

		};

		/**
		 * Bit MASK_MAX_RT (R/W, default=0) : Mask interrupt caused by MAX_RT.
		 *  1: Interrupt not reflected on the IRQ pin?
		 *  0: Reflect MAX_RT as active low interrupt on the IRQ pin.
		 */
		public final RegisterBits MASK_MAX_RT = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 4; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return ConfigRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".MASK_MAX_RT"; }

		};

		/**
		 * Bit EN_CRC (R/W, default=1) : Enable CRC.
		 * Forced high if one of the bits in the EN_AA is high.
		 */
		public final RegisterBits EN_CRC = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 3; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 1; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return ConfigRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".EN_CRC"; }

		};

		/**
		 * Bit CRCO (R/W, default=0) : CRC encoding scheme.
		 *  '0' - 1 byte.
		 *  '1' – 2 bytes.
		 */
		public final RegisterBits CRCO = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 2; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return ConfigRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".CRCO"; }

		};

		/**
		 * Bit PWR_UP (R/W, default=0) : Power up.
		 *  1: POWER UP.
		 *  0: POWER DOWN.
		 */
		public final RegisterBits PWR_UP = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return ConfigRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".PWR_UP"; }

		};

		/**
		 * Bit PRIM_RX (R/W, default=0) : RX/TX control.
		 *  1: PRX.
		 *  0: PTX.
		 */
		public final RegisterBits PRIM_RX = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return ConfigRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".PRIM_RX"; }

		};

	}

	/**
	 * EN_AA Enhanced ShockBurst : Enable ‘Auto Acknowledgment’ Function.
	 * Disable this functionality to be compatible with nRF2401.
	 * 
	 * Mnemonic  Bit  Reset Value  Type  Description
	 * ---------------------------------------------
	 * Reserved  7:6  00           R/W   Only '00' allowed
	 * ENAA_P5   5    1            R/W   Enable auto acknowledgement data pipe 5
	 * ENAA_P4   4    1            R/W   Enable auto acknowledgement data pipe 4
	 * ENAA_P3   3    1            R/W   Enable auto acknowledgement data pipe 3
	 * ENAA_P2   2    1            R/W   Enable auto acknowledgement data pipe 2
	 * ENAA_P1   1    1            R/W   Enable auto acknowledgement data pipe 1
	 * ENAA_P0   0    1            R/W   Enable auto acknowledgement data pipe 0
	 */
	public static final EnAARegister EN_AA = new EnAARegister();

	public static class EnAARegister implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x01; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b00111111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0b00111111; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "EN_AA"; }

		/**
		 * Bit ENAA_P5 (R/W, default=1) : Enable auto acknowledgement data pipe 5.
		 */
		public final RegisterBits ENAA_P5 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 5; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 1; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return EnAARegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".ENAA_P5"; }

		};

		/**
		 * Bit ENAA_P4 (R/W, default=1) : Enable auto acknowledgement data pipe 4.
		 */
		public final RegisterBits ENAA_P4 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 4; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 1; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return EnAARegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".ENAA_P4"; }

		};

		/**
		 * Bit ENAA_P3 (R/W, default=1) : Enable auto acknowledgement data pipe 3.
		 */
		public final RegisterBits ENAA_P3 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 3; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 1; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return EnAARegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".ENAA_P3"; }

		};

		/**
		 * Bit ENAA_P2 (R/W, default=1) : Enable auto acknowledgement data pipe 2.
		 */
		public final RegisterBits ENAA_P2 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 2; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 1; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return EnAARegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".ENAA_P2"; }

		};

		/**
		 * Bit ENAA_P1 (R/W, default=1) : Enable auto acknowledgement data pipe 1.
		 */
		public final RegisterBits ENAA_P1 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 1; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return EnAARegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".ENAA_P1"; }

		};

		/**
		 * Bit ENAA_P0 (R/W, default=1) : Enable auto acknowledgement data pipe 0.
		 */
		public final RegisterBits ENAA_P0 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 1; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return EnAARegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".ENAA_P0"; }

		};

	}

	/**
	 * EN_RXADDR : Enabled RX Addresses.
	 * 
	 * Mnemonic  Bit  Reset Value  Type  Description
	 * ---------------------------------------------
	 * Reserved  7:6  00           R/W   Only '00' allowed
	 * ERX_P5    5    0            R/W   Enable data pipe 5
	 * ERX_P4    4    0            R/W   Enable data pipe 4
	 * ERX_P3    3    0            R/W   Enable data pipe 3
	 * ERX_P2    2    0            R/W   Enable data pipe 2
	 * ERX_P1    1    1            R/W   Enable data pipe 1
	 * ERX_P0    0    1            R/W   Enable data pipe 0
	 */
	public static final EnRXAddrRegister EN_RXADDR = new EnRXAddrRegister();

	public static class EnRXAddrRegister implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x02; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b00111111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0b00000011; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "EN_RXADDR"; }

		/**
		 * Bit ERX_P5 (R/W, default=0) : Enable data pipe 5.
		 */
		public final RegisterBits ERX_P5 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 5; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return EnRXAddrRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".ERX_P5"; }

		};

		/**
		 * Bit ERX_P4 (R/W, default=0) : Enable data pipe 4.
		 */
		public final RegisterBits ERX_P4 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 4; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return EnRXAddrRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".ERX_P4"; }

		};

		/**
		 * Bit ERX_P3 (R/W, default=0) : Enable data pipe 3.
		 */
		public final RegisterBits ERX_P3 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 3; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return EnRXAddrRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".ERX_P3"; }

		};

		/**
		 * Bit ERX_P2 (R/W, default=0) : Enable data pipe 2.
		 */
		public final RegisterBits ERX_P2 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 2; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return EnRXAddrRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".ERX_P2"; }

		};

		/**
		 * Bit ERX_P1 (R/W, default=1) : Enable data pipe 1.
		 */
		public final RegisterBits ERX_P1 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 1; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return EnRXAddrRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".ERX_P1"; }

		};

		/**
		 * Bit ERX_P0 (R/W, default=1) : Enable data pipe 0.
		 */
		public final RegisterBits ERX_P0 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 1; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return EnRXAddrRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".ERX_P0"; }

		};

	}

	/**
	 * SETUP_AW : Setup of Address Widths (common for all data pipes).
	 * 
	 * Mnemonic  Bit  Reset Value  Type  Description
	 * ---------------------------------------------
	 * Reserved  7:2  000000       R/W   Only '000000' allowed
	 * AW        1:0  11           R/W   RX/TX Address field width 
	 *                                     '00' - Illegal
	 *                                     '01' - 3 bytes
	 *                                     '10' - 4 bytes 
	 *                                     '11' – 5 bytes
	 *                                   LSByte is used if address width is below 5 bytes
	 */
	public static final SetupAWRegister SETUP_AW = new SetupAWRegister();

	public static class SetupAWRegister implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x03; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b00000011; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0b00000011; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "SETUP_AW"; }

		/**
		 * Bits AW (R/W, default=11) : RX/TX Address field width.
		 *  '00' - Illegal
		 *  '01' - 3 bytes
		 *  '10' - 4 bytes 
		 *  '11' – 5 bytes
		 * LSByte is used if address width is below 5 bytes
		 */
		public final RegisterBits AW = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 0b11; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0b11; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return SetupAWRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".AW"; }

		};

	}

	/**
	 * SETUP_RETR : Setup of Automatic Retransmission.
	 * 
	 * Mnemonic  Bit  Reset Value  Type  Description
	 * ---------------------------------------------
	 * ARD       7:4  0000         R/W   Auto Retransmit Delay
	 *                                     ‘0000’ – Wait 250μS
	 *                                     ‘0001’ – Wait 500μS 
	 *                                     ‘0010’ – Wait 750μS 
	 *                                     ........
	 *                                     ‘1111’ – Wait 4000μS
	 *                                   (Delay defined from end of transmission to start of next transmission)
	 * ARC       3:0  0011        R/W    Auto Retransmit Count
	 *                                     ‘0000’ – Re-Transmit disabled
	 *                                     ‘0001’ – Up to 1 Re-Transmit on fail of AA
	 *                                     ......
	 *                                     ‘1111’ – Up to 15 Re-Transmit on fail of AA
	 * 
	 * Note for ARD : 
	 * --------------
	 * 1) Please take care when setting this parameter. If the ACK payload is more than 15 byte in  
	 * 2Mbps mode the ARD must be 500μS or more, if the ACK payload is more than 5byte in 1Mbps mode the ARD must 
	 * be 500μS or more. In 250kbps mode (even when the payload is not in ACK) the ARD must be 500μS or more.
	 * 
	 * 2) This is the time the PTX is waiting for an ACK packet before a retransmit is made. The PTX is in RX mode 
	 * for 250μS (500μS in 250kbps mode) to wait for address match. If the address match is detected, it stays in 
	 * RX mode to the end of the packet, unless ARD elapses. Then it goes to standby-II mode for the rest of the 
	 * specified ARD. After the ARD it goes to TX mode and then retransmits the packet. 
	 */
	public static final SetupRetrRegister SETUP_RETR = new SetupRetrRegister();

	public static class SetupRetrRegister implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x04; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return (byte) 0b11111111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0b00000011; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "SETUP_RETR"; }

		/**
		 * Bits ARD (R/W, default=0000) : Auto Retransmit Delay.
		 *  ‘0000’ – Wait 250μS
		 *  ‘0001’ – Wait 500μS 
		 *  ‘0010’ – Wait 750μS 
		 *  ........
		 *  ‘1111’ – Wait 4000μS
		 * (Delay defined from end of transmission to start of next transmission)
		 */
		public final RegisterBits ARD = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 4; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 0b1111; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0b0000; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return SetupRetrRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".ARD"; }

		};

		/**
		 * Bits ARC (R/W, default=0000) : Auto Retransmit Count.
		 *  ‘0000’ – Re-Transmit disabled
		 *  ‘0001’ – Up to 1 Re-Transmit on fail of AA
		 *  ......
		 *  ‘1111’ – Up to 15 Re-Transmit on fail of AA
		 */
		public final RegisterBits ARC = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 0b1111; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0b0011; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return SetupRetrRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".ARC"; }

		};

	}

	/**
	 * RF_CH : RF Channel.
	 * 
	 * Mnemonic  Bit  Reset Value  Type  Description
	 * ---------------------------------------------
	 * Reserved  7    0            R/W   Only '0' allowed
	 * RF_CH     6:0  0000010      R/W   Sets the frequency channel nRF24L01+ operates on
	 */
	public static final RFChRegister RF_CH = new RFChRegister();

	public static class RFChRegister implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x05; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b01111111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0b0000010; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "RF_CH"; }

		/**
		 * Bits RF_CH (R/W, default=0000010) : Sets the frequency channel nRF24L01+ operates on.
		 */
		public final RegisterBits RF_CH = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 0b1111111; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0b10; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return RFChRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".RF_CH"; }

		};

	}

	/**
	 * RF_SETUP : RF Setup Register.
	 * 
	 * Mnemonic    Bit  Reset Value  Type  Description
	 * -----------------------------------------------
	 * CONT_WAVE   7    0            R/W   Enables continuous carrier transmit when high.
	 * Reserved    6    0            R/W   Only '0' allowed
	 * RF_DR_LOW   5    0            R/W   Set RF Data Rate to 250kbps. See RF_DR_HIGH for encoding.
	 * PLL_LOCK    4    0            R/W   Force PLL lock signal. Only used in test.
	 * RF_DR_HIGH  3    1            R/W   Select between the high speed data rates. This bit is don’t care if RF_DR_LOW is set.
	 *                                     Encoding:
	 *                                     [RF_DR_LOW, RF_DR_HIGH]:
	 *                                       ‘00’ – 1Mbps
	 *                                       ‘01’ – 2Mbps
	 *                                       ‘10’ – 250kbps
	 *                                       ‘11’ – Reserved
	 * RF_PWR      2:1  11           R/W   Set RF output power in TX mode
	 *                                       '00' –  -18dBm
	 *                                       '01' –  -12dBm
	 *                                       '10' –    -6dBm
	 *                                       '11' –     0dBm
	 * Obsolete    0                       Don’t care
	 */
	public static final RFSetupRegister RF_SETUP = new RFSetupRegister();

	public static class RFSetupRegister implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x06; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return (byte) 0b10111111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0b00001110; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "RF_SETUP"; }

		/**
		 * Bit CONT_WAVE (R/W, default=0) : Enables continuous carrier transmit when high.
		 */
		public final RegisterBits CONT_WAVE = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 7; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return RFSetupRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".CONT_WAVE"; }

		};

		/**
		 * Bit RF_DR_LOW (R/W, default=0) : Set RF Data Rate to 250kbps. See RF_DR_HIGH for encoding.
		 * Encoding: [RF_DR_LOW, RF_DR_HIGH]
		 *   ‘00’ – 1Mbps
		 *   ‘01’ – 2Mbps
		 *   ‘10’ – 250kbps
		 *   ‘11’ – Reserved
		 */
		public final RegisterBits RF_DR_LOW = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 5; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return RFSetupRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".RF_DR_LOW"; }

		};

		/**
		 * Bit PLL_LOCK (R/W, default=0) : Force PLL lock signal. Only used in test.
		 */
		public final RegisterBits PLL_LOCK = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 4; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return RFSetupRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".PLL_LOCK"; }

		};

		/**
		 * Bit RF_DR_HIGH (R/W, default=1) : Select between the high speed data rates. 
		 * This bit is don’t care if RF_DR_LOW is set.
		 * Encoding: [RF_DR_LOW, RF_DR_HIGH]
		 *   ‘00’ – 1Mbps
		 *   ‘01’ – 2Mbps
		 *   ‘10’ – 250kbps
		 *   ‘11’ – Reserved
		 */
		public final RegisterBits RF_DR_HIGH = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 3; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 1; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return RFSetupRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".RF_DR_HIGH"; }

		};

		/**
		 * Bits RF_PWR (R/W, default=11) : Set RF output power in TX mode.
		 *  '00' –  -18dBm
		 *  '01' –  -12dBm
		 *  '10' –    -6dBm
		 *  '11' –     0dBm
		 */
		public final RegisterBits RF_PWR = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 0b11; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0b11; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return RFSetupRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".RF_PWR"; }

		};

	}

	/**
	 * STATUS : Status Register (In parallel to the SPI command word applied on the MOSI pin, 
	 * the STATUS register is shifted serially out on the MISO pin).
	 * 
	 * Mnemonic  Bit  Reset Value  Type  Description
	 * ---------------------------------------------
	 * Reserved  7    0            R/W   Only '0' allowed
	 * RX_DR     6    0            R/W   Data Ready RX FIFO interrupt. Asserted when new data arrives RX FIFO. Write 1 to clear bit.
	 * TX_DS     5    0            R/W   Data Sent TX FIFO interrupt. Asserted when packet transmitted on TX. If AUTO_ACK is activated, 
	 *                                   this bit is set high only when ACK is received. Write 1 to clear bit.
	 * MAX_RT    4    0            R/W   Maximum number of TX retransmits interrupt. Write 1 to clear bit. If MAX_RT is asserted it must 
	 *                                   be cleared to enable further communication. 
	 * RX_P_NO   3:1  111          R     Data pipe number for the payload available for reading from RX_FIFO 
	 *                                     000-101: Data Pipe Number
	 *                                     110: Not Used
	 *                                     111: RX FIFO Empty
	 * TX_FULL   0    0            R     TX FIFO full flag. 
	 *                                     1: TX FIFO full. 
	 *                                     0: Available locations in TX FIFO.
	 * 
	 * Note for RX_DR : 
	 * ----------------
	 * The RX_DR IRQ is asserted by a new packet arrival event. The procedure for handling this interrupt should be: 
	 *   1) read payload through SPI, 
	 *   2) clear RX_DR IRQ, 
	 *   3) read FIFO_STATUS to check if there are more payloads available in RX FIFO, 
	 *   4) if there are more data in RX FIFO, repeat from step 1).
	 */
	public static final StatusRegister STATUS = new StatusRegister();

	public static class StatusRegister implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x07; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b01111111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0b00001110; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "STATUS"; }

		/**
		 * Bit RX_DR (R/W, default=0) : Data Ready RX FIFO interrupt. 
		 * Asserted when new data arrives RX FIFO. Write 1 to clear bit.
		 */
		public final RegisterBits RX_DR = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 6; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return StatusRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".RX_DR"; }

		};

		/**
		 * Bit TX_DS (R/W, default=0) : Data Sent TX FIFO interrupt. Asserted when packet transmitted on TX. 
		 * If AUTO_ACK is activated, this bit is set high only when ACK is received. Write 1 to clear bit.
		 */
		public final RegisterBits TX_DS = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 5; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return StatusRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".TX_DS"; }

		};

		/**
		 * Bit MAX_RT (R/W, default=0) : Maximum number of TX retransmits interrupt. Write 1 to clear bit. 
		 * If MAX_RT is asserted it must be cleared to enable further communication.
		 */
		public final RegisterBits MAX_RT = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 4; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return StatusRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".MAX_RT"; }

		};

		/**
		 * Bits RX_P_NO  (R, default=111) : Data pipe number for the payload available for reading from RX_FIFO.
		 *  000-101: Data Pipe Number
		 *  110: Not Used
		 *  111: RX FIFO Empty
		 */
		public final RegisterBits RX_P_NO = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 0b111; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0b111; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return StatusRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".RX_P_NO"; }

		};

		/**
		 * Bit TX_FULL (R, default=0) : TX FIFO full flag.
		 *  1: TX FIFO full. 
		 *  0: Available locations in TX FIFO.
		 */
		public final RegisterBits TX_FULL = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return StatusRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".TX_FULL"; }

		};

	}

	/**
	 * OBSERVE_TX : Transmit observe register.
	 * 
	 * Mnemonic  Bit  Reset Value  Type  Description
	 * ---------------------------------------------
	 * PLOS_CNT  7:4  0000         R     Count lost packets. The counter is overflow protected to 15, and discontinues 
	 *                                   at max until reset. The counter is reset by writing to RF_CH.
	 * ARC_CNT   3:0  0000         R     Count retransmitted packets. The counter is reset when transmission of a new packet starts.
	 */
	public static final ObserveTxRegister OBSERVE_TX = new ObserveTxRegister();

	public static class ObserveTxRegister implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x08; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return (byte) 0b11111111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "OBSERVE_TX"; }

		/**
		 * Bits PLOS_CNT (R, default=0000) : Count lost packets. The counter is overflow protected to 15, 
		 * and discontinues at max until reset. The counter is reset by writing to RF_CH.
		 */
		public final RegisterBits PLOS_CNT = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 4; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 0b1111; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return ObserveTxRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".PLOS_CNT"; }

		};

		/**
		 * Bits ARC_CNT (R, default=0000) : Count retransmitted packets. The counter is reset when transmission 
		 * of a new packet starts.
		 */
		public final RegisterBits ARC_CNT = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 0b1111; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return ObserveTxRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".ARC_CNT"; }

		};

	}

	/**
	 * RPD / CD : Received Power Detector (nRF24L01+) / Carrier Detect (nRF24L01).
	 * 
	 * Mnemonic  Bit  Reset Value  Type  Description
	 * ---------------------------------------------
	 * Reserved  7:1  000000       R
	 * RPD       0    0            R     Received Power Detector. This register is called CD (Carrier Detect) in the nRF24L01. 
	 *                                   The name is different in nRF24L01+ due to the different input power level threshold for this bit.
	 */
	public static final RPDRegister RPD = new RPDRegister(); 

	public static class RPDRegister implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x09; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b00000001; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "RPD"; }

		/**
		 * Bit RPD (R, default=0) : Received Power Detector. This register is called CD (Carrier Detect) in the nRF24L01.
		 * The name is different in nRF24L01+ due to the different input power level threshold for this bit.
		 */
		public final RegisterBits RPD = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return RPDRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".RPD"; }

		};

	}
	
	/**
	 * RX_ADDR_P0 : Receive address data pipe 0.
	 * 
	 * Mnemonic    Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * RX_ADDR_P0  39:0  0xE7E7E7E7E7  R/W   Receive address data pipe 0. 5 Bytes maximum length. 
	 *                                       (LSByte is written first. Write the number of bytes defined by SETUP_AW)
	 */
	public static final RxAddrP0Register RX_ADDR_P0 = new RxAddrP0Register(); 

	public static class RxAddrP0Register implements RegisterBytes {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x0A; }

		/** {@inheritDoc} */
		@Override
		public byte getLength() { return 5; }

		/** {@inheritDoc} */
		@Override
		public byte[] getResetValue() { return new byte[] { 
				(byte) 0xE7, (byte) 0xE7, (byte) 0xE7, (byte) 0xE7, (byte) 0xE7 }; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "RX_ADDR_P0"; }

	}

	/**
	 * RX_ADDR_P1 : Receive address data pipe 1.
	 * 
	 * Mnemonic    Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * RX_ADDR_P1  39:0  0xC2C2C2C2C2  R/W   Receive address data pipe 1. 5 Bytes maximum length. 
	 *                                       (LSByte is written first. Write the number of bytes defined by SETUP_AW)
	 */
	public static final RxAddrP1Register RX_ADDR_P1 = new RxAddrP1Register();

	public static class RxAddrP1Register implements RegisterBytes {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x0B; }

		/** {@inheritDoc} */
		@Override
		public byte getLength() { return 5; }

		/** {@inheritDoc} */
		@Override
		public byte[] getResetValue() { return new byte[] { 
				(byte) 0xC2, (byte) 0xC2, (byte) 0xC2, (byte) 0xC2, (byte) 0xC2 }; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "RX_ADDR_P1"; }

	}

	/**
	 * RX_ADDR_P2 : Receive address data pipe 2.
	 * 
	 * Mnemonic    Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * RX_ADDR_P2  7:0   0xC3          R/W   Receive address data pipe 2. Only LSB. MSBytes are equal to RX_ADDR_P1[39:8]
	 */
	public static final RxAddrP2Register RX_ADDR_P2 = new RxAddrP2Register();

	public static class RxAddrP2Register implements RegisterByte {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x0C; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return (byte) 0xC3; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "RX_ADDR_P2"; }

	}

	/**
	 * RX_ADDR_P3 : Receive address data pipe 3.
	 * 
	 * Mnemonic    Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * RX_ADDR_P3  7:0   0xC4          R/W   Receive address data pipe 3. Only LSB. MSBytes are equal to RX_ADDR_P1[39:8]
	 */
	public static final RxAddrP3Register RX_ADDR_P3 = new RxAddrP3Register(); 

	public static class RxAddrP3Register implements RegisterByte {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x0D; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return (byte) 0xC4; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "RX_ADDR_P3"; }

	}

	/**
	 * RX_ADDR_P4 : Receive address data pipe 4.
	 * 
	 * Mnemonic    Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * RX_ADDR_P4  7:0   0xC5          R/W   Receive address data pipe 4. Only LSB. MSBytes are equal to RX_ADDR_P1[39:8]
	 */
	public static final RxAddrP4Register RX_ADDR_P4 = new RxAddrP4Register();

	public static class RxAddrP4Register implements RegisterByte {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x0E; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return (byte) 0xC5; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "RX_ADDR_P4"; }

	}

	/**
	 * RX_ADDR_P5 : Receive address data pipe 5.
	 * 
	 * Mnemonic    Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * RX_ADDR_P5  7:0   0xC6          R/W   Receive address data pipe 4. Only LSB. MSBytes are equal to RX_ADDR_P1[39:8]
	 */
	public static final RxAddrP6Register RX_ADDR_P5 = new RxAddrP6Register();

	public static class RxAddrP6Register implements RegisterByte {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x0F; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return (byte) 0xC6; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "RX_ADDR_P5"; }

	}

	/**
	 * TX_ADDR : R/W Transmit address.
	 * 
	 * Mnemonic  Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * TX_ADDR   39:0  0xE7E7E7E7E7  R/W   Transmit address. Used for a PTX device only. (LSByte is written first) 
	 *                                     Set RX_ADDR_P0 equal to this address to handle automatic acknowledge if 
	 *                                     this is a PTX device with Enhanced ShockBurst™ enabled.
	 */
	public static final TxAddrRegister TX_ADDR = new TxAddrRegister();

	public static class TxAddrRegister implements RegisterBytes {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x10; }

		/** {@inheritDoc} */
		@Override
		public byte getLength() { return 5; }

		/** {@inheritDoc} */
		@Override
		public byte[] getResetValue() { return new byte[] { 
				(byte) 0xE7, (byte) 0xE7, (byte) 0xE7, (byte) 0xE7, (byte) 0xE7 }; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "TX_ADDR"; }

	}

	/**
	 * RX_PW_P0 : Number of bytes in RX payload in data pipe 0 (1 to 32 bytes).
	 * 
	 * Mnemonic  Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * Reserved  7:6   00            R/W   Only '00' allowed 
	 * RX_PW_P0  5:0   0             R/W   Number of bytes in RX payload in data pipe 0 (1 to 32 bytes).
	 *                                       0 Pipe not used
	 *                                       1 = 1 byte
	 *                                       ...
	 *                                       32 = 32 bytes
	 */
	public static final RxPwP0Register RX_PW_P0 = new RxPwP0Register();

	public static class RxPwP0Register implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x11; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b00111111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "RX_PW_P0"; }

		/**
		 * Bits RX_PW_P0 (R/W, default=0) : Number of bytes in RX payload in data pipe 0 (1 to 32 bytes).
		 *  0 Pipe not used
		 *  1 = 1 byte
		 *  ...
		 *  32 = 32 bytes
		 */
		public final RegisterBits RX_PW_P0 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 0b111111; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return RxPwP0Register.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".RX_PW_P0"; }

		};

	}

	/**
	 * RX_PW_P1 : Number of bytes in RX payload in data pipe 1 (1 to 32 bytes).
	 * 
	 * Mnemonic  Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * Reserved  7:6   00            R/W   Only '00' allowed 
	 * RX_PW_P1  5:0   0             R/W   Number of bytes in RX payload in data pipe 1 (1 to 32 bytes).
	 *                                       0 Pipe not used
	 *                                       1 = 1 byte
	 *                                       ...
	 *                                       32 = 32 bytes
	 */
	public static final RxPwP1Register RX_PW_P1 = new RxPwP1Register();

	public static class RxPwP1Register implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x12; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b00111111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "RX_PW_P1"; }

		/**
		 * Bits RX_PW_P1 (R/W, default=0) : Number of bytes in RX payload in data pipe 1 (1 to 32 bytes).
		 *  0 Pipe not used
		 *  1 = 1 byte
		 *  ...
		 *  32 = 32 bytes
		 */
		public final RegisterBits RX_PW_P1 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 0b111111; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return RxPwP1Register.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".RX_PW_P1"; }

		};

	}

	/**
	 * RX_PW_P2 : Number of bytes in RX payload in data pipe 2 (1 to 32 bytes).
	 * 
	 * Mnemonic  Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * Reserved  7:6   00            R/W   Only '00' allowed 
	 * RX_PW_P2  5:0   0             R/W   Number of bytes in RX payload in data pipe 2 (1 to 32 bytes).
	 *                                       0 Pipe not used
	 *                                       1 = 1 byte
	 *                                       ...
	 *                                       32 = 32 bytes
	 */
	public static final RxPwP2Register RX_PW_P2 = new RxPwP2Register(); 

	public static class RxPwP2Register implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x13; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b00111111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "RX_PW_P2"; }

		/**
		 * Bits RX_PW_P2 (R/W, default=0) : Number of bytes in RX payload in data pipe 2 (1 to 32 bytes).
		 *  0 Pipe not used
		 *  1 = 1 byte
		 *  ...
		 *  32 = 32 bytes
		 */
		public final RegisterBits RX_PW_P2 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 0b111111; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return RxPwP2Register.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".RX_PW_P2"; }

		};

	}

	/**
	 * RX_PW_P3 : Number of bytes in RX payload in data pipe 3 (1 to 32 bytes).
	 * 
	 * Mnemonic  Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * Reserved  7:6   00            R/W   Only '00' allowed 
	 * RX_PW_P3  5:0   0             R/W   Number of bytes in RX payload in data pipe 3 (1 to 32 bytes).
	 *                                       0 Pipe not used
	 *                                       1 = 1 byte
	 *                                       ...
	 *                                       32 = 32 bytes
	 */
	public static final RxPwP3Register RX_PW_P3 = new RxPwP3Register();

	public static class RxPwP3Register implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x14; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b00111111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "RX_PW_P3"; }

		/**
		 * Bits RX_PW_P3 (R/W, default=0) : Number of bytes in RX payload in data pipe 3 (1 to 32 bytes).
		 *  0 Pipe not used
		 *  1 = 1 byte
		 *  ...
		 *  32 = 32 bytes
		 */
		public final RegisterBits RX_PW_P3 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 0b111111; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return RxPwP3Register.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".RX_PW_P3"; }

		};

	}

	/**
	 * RX_PW_P4 : Number of bytes in RX payload in data pipe 4 (1 to 32 bytes).
	 * 
	 * Mnemonic  Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * Reserved  7:6   00            R/W   Only '00' allowed 
	 * RX_PW_P4  5:0   0             R/W   Number of bytes in RX payload in data pipe 4 (1 to 32 bytes).
	 *                                       0 Pipe not used
	 *                                       1 = 1 byte
	 *                                       ...
	 *                                       32 = 32 bytes
	 */
	public static final RxPwP4Register RX_PW_P4 = new RxPwP4Register();

	public static class RxPwP4Register implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x15; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b00111111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "RX_PW_P4"; }

		/**
		 * Bits RX_PW_P4 (R/W, default=0) : Number of bytes in RX payload in data pipe 4 (1 to 32 bytes).
		 *  0 Pipe not used
		 *  1 = 1 byte
		 *  ...
		 *  32 = 32 bytes
		 */
		public final RegisterBits RX_PW_P4 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 0b111111; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return RxPwP4Register.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".RX_PW_P4"; }

		};

	}

	/**
	 * RX_PW_P5 : Number of bytes in RX payload in data pipe 5 (1 to 32 bytes).
	 * 
	 * Mnemonic  Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * Reserved  7:6   00            R/W   Only '00' allowed 
	 * RX_PW_P5  5:0   0             R/W   Number of bytes in RX payload in data pipe 5 (1 to 32 bytes).
	 *                                       0 Pipe not used
	 *                                       1 = 1 byte
	 *                                       ...
	 *                                       32 = 32 bytes
	 */
	public static final RxPwP5Register RX_PW_P5 = new RxPwP5Register();

	public static class RxPwP5Register implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x16; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b00111111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "RX_PW_P5"; }

		/**
		 * Bits RX_PW_P5 (R/W, default=0) : Number of bytes in RX payload in data pipe 5 (1 to 32 bytes).
		 *  0 Pipe not used
		 *  1 = 1 byte
		 *  ...
		 *  32 = 32 bytes
		 */
		public final RegisterBits RX_PW_P5 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 0b111111; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return RxPwP5Register.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".RX_PW_P5"; }

		};

	}

	/**
	 * FIFO_STATUS : FIFO Status Register.
	 * 
	 * Mnemonic  Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * Reserved  7     0             R/W   Only '0' allowed  
	 * TX_REUSE  6     0             R     Used for a PTX device Pulse the rfce high for at least 10μs to Reuse last 
	 *                                     transmitted payload. TX payload reuse is active until W_TX_PAYLOAD or FLUSH TX 
	 *                                     is executed. TX_REUSE is set by the SPI command REUSE_TX_PL, and is reset by 
	 *                                     the SPI commands W_TX_PAYLOAD or FLUSH TX
	 * TX_FULL   5     0             R     TX FIFO full flag.
	 *                                       1: TX FIFO full.
	 *                                       0: Available locations in TX FIFO.
	 * TX_EMPTY  4     1             R     TX FIFO empty flag.
	 *                                       1: TX FIFO empty.
	 *                                       0: Data in TX FIFO.
	 * Reserved  3:2   00            R/W   Only '00' allowed
	 * RX_FULL   1     0             R     RX FIFO full flag.
	 *                                       1: RX FIFO full.
	 *                                       0: Available locations in RX FIFO.
	 * RX_EMPTY  0     1             R     RX FIFO empty flag.
	 *                                       1: RX FIFO empty.
	 *                                       0: Data in RX FIFO.
	 */
	public static final FifoStatusRegister FIFO_STATUS = new FifoStatusRegister();

	public static class FifoStatusRegister implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x17; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b01110011; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0b00010001; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "FIFO_STATUS"; }

		/**
		 * Bit TX_REUSE (R, default=0) : Used for a PTX device Pulse the rfce high for at least 10μs to Reuse last
		 * transmitted payload. TX payload reuse is active until W_TX_PAYLOAD or FLUSH TX is executed. TX_REUSE is 
		 * set by the SPI command REUSE_TX_PL, and is reset by the SPI commands W_TX_PAYLOAD or FLUSH TX.
		 */
		public final RegisterBits TX_REUSE = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 6; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return FifoStatusRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".TX_REUSE"; }

		};

		/**
		 * Bit TX_FULL (R, default=0) : TX FIFO full flag.
		 *  1: TX FIFO full.
		 *  0: Available locations in TX FIFO.
		 */
		public final RegisterBits TX_FULL = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 5; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return FifoStatusRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".TX_FULL"; }

		};

		/**
		 * Bit TX_EMPTY (R, default=1) : TX FIFO empty flag.
		 *  1: TX FIFO empty.
		 *  0: Data in TX FIFO.
		 */
		public final RegisterBits TX_EMPTY = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 4; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 1; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return FifoStatusRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".TX_EMPTY"; }

		};

		/**
		 * Bit RX_FULL (R, default=0) : RX FIFO full flag.
		 *  1: RX FIFO full.
		 *  0: Available locations in RX FIFO.
		 */
		public final RegisterBits RX_FULL = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return FifoStatusRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".RX_FULL"; }

		};

		/**
		 * Bit RX_EMPTY (R, default=1) : RX FIFO empty flag.
		 *  1: RX FIFO empty.
		 *  0: Data in RX FIFO.
		 */
		public final RegisterBits RX_EMPTY = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 1; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return FifoStatusRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".RX_EMPTY"; }

		};

	}

	/**
	 * DYNPD : Enable dynamic payload length.
	 * 
	 * Mnemonic  Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * Reserved  7:6   0             R/W   Only ‘00’ allowed
	 * DPL_P5    5     0             R/W   Enable dynamic payload length data pipe 5. (Requires EN_DPL and ENAA_P5)
	 * DPL_P4    4     0             R/W   Enable dynamic payload length data pipe 4. (Requires EN_DPL and ENAA_P4)
	 * DPL_P3    3     0             R/W   Enable dynamic payload length data pipe 3. (Requires EN_DPL and ENAA_P3)
	 * DPL_P2    2     0             R/W   Enable dynamic payload length data pipe 2. (Requires EN_DPL and ENAA_P2)
	 * DPL_P1    1     0             R/W   Enable dynamic payload length data pipe 1. (Requires EN_DPL and ENAA_P1)
	 * DPL_P0    0     0             R/W   Enable dynamic payload length data pipe 0. (Requires EN_DPL and ENAA_P0)
	 */
	public static final DynPDRegister DYNPD = new DynPDRegister();

	public static class DynPDRegister implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x1C; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b00111111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "DYNDP"; }

		/**
		 * Bit DPL_P5 (R/W, default=0) : Enable dynamic payload length data pipe 5. (Requires EN_DPL and ENAA_P5)
		 */
		public final RegisterBits DPL_P5 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 5; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return DynPDRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".DPL_P5"; }

		};

		/**
		 * Bit DPL_P4 (R/W, default=0) : Enable dynamic payload length data pipe 4. (Requires EN_DPL and ENAA_P4)
		 */
		public final RegisterBits DPL_P4 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 4; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return DynPDRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".DPL_P4"; }

		};

		/**
		 * Bit DPL_P3 (R/W, default=0) : Enable dynamic payload length data pipe 3. (Requires EN_DPL and ENAA_P3)
		 */
		public final RegisterBits DPL_P3 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 3; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return DynPDRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".DPL_P3"; }

		};

		/**
		 * Bit DPL_P2 (R/W, default=0) : Enable dynamic payload length data pipe 2. (Requires EN_DPL and ENAA_P2)
		 */
		public final RegisterBits DPL_P2 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 2; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return DynPDRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".DPL_P2"; }

		};

		/**
		 * Bit DPL_P1 (R/W, default=0) : Enable dynamic payload length data pipe 1. (Requires EN_DPL and ENAA_P1)
		 */
		public final RegisterBits DPL_P1 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return DynPDRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".DPL_P1"; }

		};

		/**
		 * Bit DPL_P0 (R/W, default=0) : Enable dynamic payload length data pipe 0. (Requires EN_DPL and ENAA_P0)
		 */
		public final RegisterBits DPL_P0 = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return DynPDRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".DPL_P0"; }

		};
		
	}

	/**
	 * FEATURE : Feature Register
	 * 
	 * Mnemonic    Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * Reserved    7:3   0             R/W   Only ‘00000’ allowed
	 * EN_DPL      2     0             R/W   Enables Dynamic Payload Length
	 * EN_ACK_PAY  1     0             R/W   Enables Payload with ACK
	 * EN_DYN_ACK  0     0             R/W   Enables the W_TX_PAYLOAD_NOACK command

	 * Note for EN_ACK_PAY :
	 * ---------------------
	 * If ACK packet payload is activated, ACK packets have dynamic payload lengths and the Dynamic Payload
	 * Length feature should be enabled for pipe 0 on the PTX and PRX. This is to ensure that they receive the
	 * ACK packets with payloads. If the ACK payload is more than 15 byte in 2Mbps mode the ARD must be
	 * 500μS or more, and if the ACK payload is more than 5 byte in 1Mbps mode the ARD must be 500μS or
	 * more. In 250kbps mode (even when the payload is not in ACK) the ARD must be 500μS or more.
	 */
	public static final FeatureRegister FEATURE = new FeatureRegister();

	public static class FeatureRegister implements RegisterByteWithBits {

		/** {@inheritDoc} */
		@Override
		public byte getAddress() { return 0x1D; }

		/** {@inheritDoc} */
		@Override
		public byte getMask() {	return 0b00000111; }

		/** {@inheritDoc} */
		@Override
		public byte getResetValue() { return 0; }

		/** {@inheritDoc} */
		@Override
		public String getName() { return "FEATURE"; }

		/**
		 * Bit EN_DPL (R/W, default=0) : Enables Dynamic Payload Length.
		 */
		public final RegisterBits EN_DPL = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 2; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return FeatureRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".EN_DPL"; }

		};

		/**
		 * Bit EN_ACK_PAY (R/W, default=0) : Enables Payload with ACK.
		 */
		public final RegisterBits EN_ACK_PAY = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return FeatureRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".EN_ACK_PAY"; }

		};

		/**
		 * Bit EN_DYN_ACK (R/W, default=0) : Enables the W_TX_PAYLOAD_NOACK command.
		 */
		public final RegisterBits EN_DYN_ACK = new RegisterBits() {

			/** {@inheritDoc} */
			@Override
			public byte getShift() { return 0; }

			/** {@inheritDoc} */
			@Override
			public byte getMask() { return 1; }

			/** {@inheritDoc} */
			@Override
			public byte getResetValue() { return 0; }

			/** {@inheritDoc} */
			@Override
			public RegisterByteWithBits getRegister() { return FeatureRegister.this; }

			/** {@inheritDoc} */
			@Override
			public String getName() { return getRegister().getName() + ".EN_DYN_ACK"; }

		};

	}
	
}
