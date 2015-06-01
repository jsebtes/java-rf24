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

import fr.jstessier.rf24.annotations.Address;
import fr.jstessier.rf24.annotations.Length;
import fr.jstessier.rf24.annotations.Mask;
import fr.jstessier.rf24.annotations.ResetValue;
import fr.jstessier.rf24.annotations.ResetValues;
import fr.jstessier.rf24.annotations.Shift;

/**
 * Registers of the nRF24L01+.
 * 
 * @author J.S. TESSIER
 */
public class Registers {

	/**
	 * Marker interface for all registers.
	 * To get Address value.
	 */
	public interface Register {};

	/**
	 * Marker interface for multiple bytes register.
	 * To get ResetValue value.
	 */
	public interface RegisterByte extends Register {};
	
	/**
	 * Marker interface for one register that contains bits values.
	 * To get Mask value.
	 */
	public interface RegisterByteWithBits extends RegisterByte {};

	/**
	 * Marker interface for bits values in register.
	 * To get Shift, Mask and ResetValue values.
	 */
	public interface RegisterBits {};

	/**
	 * Marker interface for multiple bytes register.
	 * To get Length and ResetValue value.
	 */
	public interface RegisterBytes extends Register {};

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
	@Address(0x00)
	@Mask(0b01111111)
	@ResetValue(0b00001000)
	public class CONFIG implements RegisterByteWithBits {

		/**
		 * Bit MASK_RX_DR (R/W, default=0) : Mask interrupt caused by RX_DR.
		 *  1: Interrupt not reflected on the IRQ pin.
		 *  0: Reflect RX_DR as active low interrupt on the IRQ pin.
		 */
		@Shift(6)
		@Mask(1)
		@ResetValue(0)
		public class MASK_RX_DR implements RegisterBits {}

		/**
		 * Bit MASK_TX_DS (R/W, default=0) : Mask interrupt caused by TX_DS.
		 *  1: Interrupt not reflected on the IRQ pin.
		 *  0: Reflect TX_DS as active low interrupt on the IRQ pin.
		 */
		@Shift(5)
		@Mask(1)
		@ResetValue(0)
		public class MASK_TX_DS implements RegisterBits {}

		/**
		 * Bit MASK_MAX_RT (R/W, default=0) : Mask interrupt caused by MAX_RT.
		 *  1: Interrupt not reflected on the IRQ pin?
		 *  0: Reflect MAX_RT as active low interrupt on the IRQ pin.
		 */
		@Shift(4)
		@Mask(1)
		@ResetValue(0)
		public class MASK_MAX_RT implements RegisterBits {}

		/**
		 * Bit EN_CRC (R/W, default=1) : Enable CRC.
		 * Forced high if one of the bits in the EN_AA is high.
		 */
		@Shift(3)
		@Mask(1)
		@ResetValue(1)
		public class EN_CRC implements RegisterBits {}

		/**
		 * Bit CRCO (R/W, default=0) : CRC encoding scheme.
		 *  '0' - 1 byte.
		 *  '1' – 2 bytes.
		 */
		@Shift(2)
		@Mask(1)
		@ResetValue(0)
		public class CRCO implements RegisterBits {}

		/**
		 * Bit PWR_UP (R/W, default=0) : Power up.
		 *  1: POWER UP.
		 *  0: POWER DOWN.
		 */
		@Shift(1)
		@Mask(1)
		@ResetValue(0)
		public class PWR_UP implements RegisterBits {}

		/**
		 * Bit PRIM_RX (R/W, default=0) : RX/TX control.
		 *  1: PRX.
		 *  0: PTX.
		 */
		@Shift(0)
		@Mask(1)
		@ResetValue(0)
		public class PRIM_RX implements RegisterBits {}

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
	@Address(0x01)
	@Mask(0b00111111)
	@ResetValue(0b00111111)
	public class EN_AA implements RegisterByteWithBits {

		/**
		 * Bit ENAA_P5 (R/W, default=1) : Enable auto acknowledgement data pipe 5.
		 */
		@Shift(5)
		@Mask(1)
		@ResetValue(1)
		public class ENAA_P5 implements RegisterBits {}

		/**
		 * Bit ENAA_P4 (R/W, default=1) : Enable auto acknowledgement data pipe 4.
		 */
		@Shift(4)
		@Mask(1)
		@ResetValue(1)
		public class ENAA_P4 implements RegisterBits {}

		/**
		 * Bit ENAA_P3 (R/W, default=1) : Enable auto acknowledgement data pipe 3.
		 */
		@Shift(3)
		@Mask(1)
		@ResetValue(1)
		public class ENAA_P3 implements RegisterBits {}

		/**
		 * Bit ENAA_P2 (R/W, default=1) : Enable auto acknowledgement data pipe 2.
		 */
		@Shift(2)
		@Mask(1)
		@ResetValue(1)
		public class ENAA_P2 implements RegisterBits {}

		/**
		 * Bit ENAA_P1 (R/W, default=1) : Enable auto acknowledgement data pipe 1.
		 */
		@Shift(1)
		@Mask(1)
		@ResetValue(1)
		public class ENAA_P1 implements RegisterBits {}

		/**
		 * Bit ENAA_P0 (R/W, default=1) : Enable auto acknowledgement data pipe 0.
		 */
		@Shift(0)
		@Mask(1)
		@ResetValue(1)
		public class ENAA_P0 implements RegisterBits {}

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
	@Address(0x02)
	@Mask(0b00111111)
	@ResetValue(0b00000011)
	public class EN_RXADDR implements RegisterByteWithBits {

		/**
		 * Bit ERX_P5 (R/W, default=0) : Enable data pipe 5.
		 */
		@Shift(5)
		@Mask(1)
		@ResetValue(0)
		public class ERX_P5 implements RegisterBits {}

		/**
		 * Bit ERX_P4 (R/W, default=0) : Enable data pipe 4.
		 */
		@Shift(4)
		@Mask(1)
		@ResetValue(0)
		public class ERX_P4 implements RegisterBits {}

		/**
		 * Bit ERX_P3 (R/W, default=0) : Enable data pipe 3.
		 */
		@Shift(3)
		@Mask(1)
		@ResetValue(0)
		public class ERX_P3 implements RegisterBits {}

		/**
		 * Bit ERX_P2 (R/W, default=0) : Enable data pipe 2.
		 */
		@Shift(2)
		@Mask(1)
		@ResetValue(0)
		public class ERX_P2 implements RegisterBits {}

		/**
		 * Bit ERX_P1 (R/W, default=1) : Enable data pipe 1.
		 */
		@Shift(1)
		@Mask(1)
		@ResetValue(1)
		public class ERX_P1 implements RegisterBits {}

		/**
		 * Bit ERX_P0 (R/W, default=1) : Enable data pipe 0.
		 */
		@Shift(0)
		@Mask(1)
		@ResetValue(1)
		public class ERX_P0 implements RegisterBits {}

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
	@Address(0x03)
	@Mask(0b00000011)
	@ResetValue(0b00000011)
	public class SETUP_AW implements RegisterByteWithBits {

		/**
		 * Bits AW (R/W, default=11) : RX/TX Address field width.
		 *  '00' - Illegal
		 *  '01' - 3 bytes
		 *  '10' - 4 bytes 
		 *  '11' – 5 bytes
		 * LSByte is used if address width is below 5 bytes
		 */
		@Shift(0)
		@Mask(0b11)
		@ResetValue(0b11)
		public class AW implements RegisterBits {}

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
	@Address(0x04)
	@Mask((byte) 0b11111111)
	@ResetValue(0b00000011)
	public class SETUP_RETR implements RegisterByteWithBits {

		/**
		 * Bits ARD (R/W, default=0000) : Auto Retransmit Delay.
		 *  ‘0000’ – Wait 250μS
		 *  ‘0001’ – Wait 500μS 
		 *  ‘0010’ – Wait 750μS 
		 *  ........
		 *  ‘1111’ – Wait 4000μS
		 * (Delay defined from end of transmission to start of next transmission)
		 */
		@Shift(4)
		@Mask(0b1111)
		@ResetValue(0b0000)
		public class ARD implements RegisterBits {}

		/**
		 * Bits ARC (R/W, default=0000) : Auto Retransmit Count.
		 *  ‘0000’ – Re-Transmit disabled
		 *  ‘0001’ – Up to 1 Re-Transmit on fail of AA
		 *  ......
		 *  ‘1111’ – Up to 15 Re-Transmit on fail of AA
		 */
		@Shift(0)
		@Mask(0b1111)
		@ResetValue(0b0011)
		public class ARC implements RegisterBits {}

	}

	/**
	 * RF_CH : RF Channel.
	 * 
	 * Mnemonic  Bit  Reset Value  Type  Description
	 * ---------------------------------------------
	 * Reserved  7    0            R/W   Only '0' allowed
	 * RF_CH     6:0  0000010      R/W   Sets the frequency channel nRF24L01+ operates on
	 */
	@Address(0x05)
	@Mask(0b01111111)
	@ResetValue(0b0000010)
	public class RF_CH implements RegisterByteWithBits {

		/**
		 * Bits RF_CH (R/W, default=0000010) : Sets the frequency channel nRF24L01+ operates on.
		 */
		@Shift(0)
		@Mask(0b1111111)
		@ResetValue(0b10)
		public class RF_CH_BITS implements RegisterBits {}

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
	@Address(0x06)
	@Mask((byte) 0b10111111)
	@ResetValue(0b00001110)
	public class RF_SETUP implements RegisterByteWithBits {

		/**
		 * Bit CONT_WAVE (R/W, default=0) : Enables continuous carrier transmit when high.
		 */
		@Shift(7)
		@Mask(1)
		@ResetValue(0)
		public class CONT_WAVE implements RegisterBits {}

		/**
		 * Bit RF_DR_LOW (R/W, default=0) : Set RF Data Rate to 250kbps. See RF_DR_HIGH for encoding.
		 * Encoding: [RF_DR_LOW, RF_DR_HIGH]
		 *   ‘00’ – 1Mbps
		 *   ‘01’ – 2Mbps
		 *   ‘10’ – 250kbps
		 *   ‘11’ – Reserved
		 */
		@Shift(5)
		@Mask(1)
		@ResetValue(0)
		public class RF_DR_LOW implements RegisterBits {}

		/**
		 * Bit PLL_LOCK (R/W, default=0) : Force PLL lock signal. Only used in test.
		 */
		@Shift(4)
		@Mask(1)
		@ResetValue(0)
		public class PLL_LOCK implements RegisterBits {}

		/**
		 * Bit RF_DR_HIGH (R/W, default=1) : Select between the high speed data rates. 
		 * This bit is don’t care if RF_DR_LOW is set.
		 * Encoding: [RF_DR_LOW, RF_DR_HIGH]
		 *   ‘00’ – 1Mbps
		 *   ‘01’ – 2Mbps
		 *   ‘10’ – 250kbps
		 *   ‘11’ – Reserved
		 */
		@Shift(3)
		@Mask(1)
		@ResetValue(1)
		public class RF_DR_HIGH implements RegisterBits {}

		/**
		 * Bits RF_PWR (R/W, default=11) : Set RF output power in TX mode.
		 *  '00' –  -18dBm
		 *  '01' –  -12dBm
		 *  '10' –    -6dBm
		 *  '11' –     0dBm
		 */
		@Shift(1)
		@Mask(0b11)
		@ResetValue(0b11)
		public class RF_PWR implements RegisterBits {}

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
	@Address(0x07)
	@Mask(0b01111111)
	@ResetValue(0b00001110)
	public class STATUS implements RegisterByteWithBits {

		/**
		 * Bit RX_DR (R/W, default=0) : Data Ready RX FIFO interrupt. 
		 * Asserted when new data arrives RX FIFO. Write 1 to clear bit.
		 */
		@Shift(6)
		@Mask(1)
		@ResetValue(0)
		public class RX_DR implements RegisterBits {}

		/**
		 * Bit TX_DS (R/W, default=0) : Data Sent TX FIFO interrupt. Asserted when packet transmitted on TX. 
		 * If AUTO_ACK is activated, this bit is set high only when ACK is received. Write 1 to clear bit.
		 */
		@Shift(5)
		@Mask(1)
		@ResetValue(0)
		public class TX_DS implements RegisterBits {}

		/**
		 * Bit MAX_RT (R/W, default=0) : Maximum number of TX retransmits interrupt. Write 1 to clear bit. 
		 * If MAX_RT is asserted it must be cleared to enable further communication.
		 */
		@Shift(4)
		@Mask(1)
		@ResetValue(0)
		public class MAX_RT implements RegisterBits {}

		/**
		 * Bits RX_P_NO  (R, default=111) : Data pipe number for the payload available for reading from RX_FIFO.
		 *  000-101: Data Pipe Number
		 *  110: Not Used
		 *  111: RX FIFO Empty
		 */
		@Shift(1)
		@Mask(0b111)
		@ResetValue(0b111)
		public class RX_P_NO implements RegisterBits {}

		/**
		 * Bit TX_FULL (R, default=0) : TX FIFO full flag.
		 *  1: TX FIFO full. 
		 *  0: Available locations in TX FIFO.
		 */
		@Shift(0)
		@Length(1)
		@ResetValue(0)
		public class TX_FULL implements RegisterBits {}

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
	@Address(0x08)
	@Mask((byte) 0b11111111)
	@ResetValue(0)
	public class OBSERVE_TX implements RegisterByteWithBits {

		/**
		 * Bits PLOS_CNT (R, default=0000) : Count lost packets. The counter is overflow protected to 15, 
		 * and discontinues at max until reset. The counter is reset by writing to RF_CH.
		 */
		@Shift(4)
		@Mask(0b1111)
		@ResetValue(0)
		public class PLOS_CNT implements RegisterBits {}

		/**
		 * Bits ARC_CNT (R, default=0000) : Count retransmitted packets. The counter is reset when transmission 
		 * of a new packet starts.
		 */
		@Shift(0)
		@Mask(0b1111)
		@ResetValue(0)
		public class ARC_CNT implements RegisterBits {}

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
	@Address(0x09)
	@Mask(0b00000001)
	@ResetValue(0)
	public class RPD implements RegisterByteWithBits {

		/**
		 * Bit RPD (R, default=0) : Received Power Detector. This register is called CD (Carrier Detect) in the nRF24L01.
		 * The name is different in nRF24L01+ due to the different input power level threshold for this bit.
		 */
		@Shift(0)
		@Mask(1)
		@ResetValue(0)
		public class RPD_BIT implements RegisterBits {}

	}
	
	/**
	 * RX_ADDR_P0 : Receive address data pipe 0.
	 * 
	 * Mnemonic    Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * RX_ADDR_P0  39:0  0xE7E7E7E7E7  R/W   Receive address data pipe 0. 5 Bytes maximum length. 
	 *                                       (LSByte is written first. Write the number of bytes defined by SETUP_AW)
	 */
	@Address(0x0A)
	@Length(5)
	@ResetValues({ (byte) 0xE7, (byte) 0xE7, (byte) 0xE7, (byte) 0xE7, (byte) 0xE7 })
	public class RX_ADDR_P0 implements RegisterBytes {}

	/**
	 * RX_ADDR_P1 : Receive address data pipe 1.
	 * 
	 * Mnemonic    Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * RX_ADDR_P1  39:0  0xC2C2C2C2C2  R/W   Receive address data pipe 1. 5 Bytes maximum length. 
	 *                                       (LSByte is written first. Write the number of bytes defined by SETUP_AW)
	 */
	@Address(0x0B)
	@Length(5)
	@ResetValues({ (byte) 0xC2, (byte) 0xC2, (byte) 0xC2, (byte) 0xC2, (byte) 0xC2 })
	public class RX_ADDR_P1 implements RegisterBytes {}

	/**
	 * RX_ADDR_P2 : Receive address data pipe 2.
	 * 
	 * Mnemonic    Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * RX_ADDR_P2  7:0   0xC3          R/W   Receive address data pipe 2. Only LSB. MSBytes are equal to RX_ADDR_P1[39:8]
	 */
	@Address(0x0C)
	@ResetValues((byte) 0xC3)
	public class RX_ADDR_P2 implements RegisterByte {}

	/**
	 * RX_ADDR_P3 : Receive address data pipe 3.
	 * 
	 * Mnemonic    Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * RX_ADDR_P3  7:0   0xC4          R/W   Receive address data pipe 3. Only LSB. MSBytes are equal to RX_ADDR_P1[39:8]
	 */
	@Address(0x0D)
	@ResetValue((byte) 0xC4)
	public class RX_ADDR_P3 implements RegisterByte {}

	/**
	 * RX_ADDR_P4 : Receive address data pipe 4.
	 * 
	 * Mnemonic    Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * RX_ADDR_P4  7:0   0xC5          R/W   Receive address data pipe 4. Only LSB. MSBytes are equal to RX_ADDR_P1[39:8]
	 */
	@Address(0x0E)
	@ResetValue((byte) 0xC5)
	public class RX_ADDR_P4 implements RegisterByte {}

	/**
	 * RX_ADDR_P5 : Receive address data pipe 5.
	 * 
	 * Mnemonic    Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * RX_ADDR_P5  7:0   0xC6          R/W   Receive address data pipe 4. Only LSB. MSBytes are equal to RX_ADDR_P1[39:8]
	 */
	@Address(0x0F)
	@ResetValue((byte) 0xC6)
	public class RX_ADDR_P5 implements RegisterByte {}

	/**
	 * TX_ADDR : R/W Transmit address.
	 * 
	 * Mnemonic  Bit   Reset Value   Type  Description
	 * -------------------------------------------------
	 * TX_ADDR   39:0  0xE7E7E7E7E7  R/W   Transmit address. Used for a PTX device only. (LSByte is written first) 
	 *                                     Set RX_ADDR_P0 equal to this address to handle automatic acknowledge if 
	 *                                     this is a PTX device with Enhanced ShockBurst™ enabled.
	 */
	@Address(0x10)
	@Length(5)
	@ResetValues({ (byte) 0xE7, (byte) 0xE7, (byte) 0xE7, (byte) 0xE7, (byte) 0xE7 })
	public class TX_ADDR implements RegisterBytes {}

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
	@Address(0x11)
	@Mask(0b00111111)
	@ResetValue(0)
	public class RX_PW_P0 implements RegisterByteWithBits {

		/**
		 * Bits RX_PW_P0 (R/W, default=0) : Number of bytes in RX payload in data pipe 0 (1 to 32 bytes).
		 *  0 Pipe not used
		 *  1 = 1 byte
		 *  ...
		 *  32 = 32 bytes
		 */
		@Shift(0)
		@Mask(0b111111)
		@ResetValue(0)
		public class RW_PW_P0_BITS implements RegisterBits {}

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
	@Address(0x12)
	@Mask(0b00111111)
	@ResetValue(0)
	public class RX_PW_P1 implements RegisterByteWithBits {

		/**
		 * Bits RX_PW_P1 (R/W, default=0) : Number of bytes in RX payload in data pipe 1 (1 to 32 bytes).
		 *  0 Pipe not used
		 *  1 = 1 byte
		 *  ...
		 *  32 = 32 bytes
		 */
		@Shift(0)
		@Mask(0b111111)
		@ResetValue(0)
		public class RW_PW_P1_BITS implements RegisterBits {}

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
	@Address(0x13)
	@Mask(0b00111111)
	@ResetValue(0)
	public class RX_PW_P2 implements RegisterByteWithBits {

		/**
		 * Bits RX_PW_P2 (R/W, default=0) : Number of bytes in RX payload in data pipe 2 (1 to 32 bytes).
		 *  0 Pipe not used
		 *  1 = 1 byte
		 *  ...
		 *  32 = 32 bytes
		 */
		@Shift(0)
		@Mask(0b111111)
		@ResetValue(0)
		public class RW_PW_P2_BITS implements RegisterBits {}

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
	@Address(0x14)
	@Mask(0b00111111)
	@ResetValue(0)
	public class RX_PW_P3 implements RegisterByteWithBits {

		/**
		 * Bits RX_PW_P3 (R/W, default=0) : Number of bytes in RX payload in data pipe 3 (1 to 32 bytes).
		 *  0 Pipe not used
		 *  1 = 1 byte
		 *  ...
		 *  32 = 32 bytes
		 */
		@Shift(0)
		@Mask(0b111111)
		@ResetValue(0)
		public class RW_PW_P3_BITS implements RegisterBits {}

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
	@Address(0x15)
	@Mask(0b00111111)
	@ResetValue(0)
	public class RX_PW_P4 implements RegisterByteWithBits {

		/**
		 * Bits RX_PW_P4 (R/W, default=0) : Number of bytes in RX payload in data pipe 4 (1 to 32 bytes).
		 *  0 Pipe not used
		 *  1 = 1 byte
		 *  ...
		 *  32 = 32 bytes
		 */
		@Shift(0)
		@Mask(0b111111)
		@ResetValue(0)
		public class RW_PW_P4_BITS implements RegisterBits {}

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
	@Address(0x16)
	@Mask(0b00111111)
	@ResetValue(0)
	public class RX_PW_P5 implements RegisterByteWithBits {

		/**
		 * Bits RX_PW_P5 (R/W, default=0) : Number of bytes in RX payload in data pipe 5 (1 to 32 bytes).
		 *  0 Pipe not used
		 *  1 = 1 byte
		 *  ...
		 *  32 = 32 bytes
		 */
		@Shift(0)
		@Mask(0b111111)
		@ResetValue(0)
		public class RW_PW_P5_BITS implements RegisterBits {}

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
	@Address(0x17)
	@Mask(0b01110011)
	@ResetValue(0b00010001)
	public class FIFO_STATUS implements RegisterByteWithBits {

		/**
		 * Bit TX_REUSE (R, default=0) : Used for a PTX device Pulse the rfce high for at least 10μs to Reuse last
		 * transmitted payload. TX payload reuse is active until W_TX_PAYLOAD or FLUSH TX is executed. TX_REUSE is 
		 * set by the SPI command REUSE_TX_PL, and is reset by the SPI commands W_TX_PAYLOAD or FLUSH TX.
		 */
		@Shift(6)
		@Mask(1)
		@ResetValue(0)
		public class TX_REUSE implements RegisterBits {}

		/**
		 * Bit TX_FULL (R, default=0) : TX FIFO full flag.
		 *  1: TX FIFO full.
		 *  0: Available locations in TX FIFO.
		 */
		@Shift(5)
		@Mask(1)
		@ResetValue(0)
		public class TX_FULL implements RegisterBits {}

		/**
		 * Bit TX_EMPTY (R, default=1) : TX FIFO empty flag.
		 *  1: TX FIFO empty.
		 *  0: Data in TX FIFO.
		 */
		@Shift(4)
		@Mask(1)
		@ResetValue(1)
		public class TX_EMPTY implements RegisterBits {}

		/**
		 * Bit RX_FULL (R, default=0) : RX FIFO full flag.
		 *  1: RX FIFO full.
		 *  0: Available locations in RX FIFO.
		 */
		@Shift(1)
		@Mask(1)
		@ResetValue(0)
		public class RX_FULL implements RegisterBits {}

		/**
		 * Bit RX_EMPTY (R, default=1) : RX FIFO empty flag.
		 *  1: RX FIFO empty.
		 *  0: Data in RX FIFO.
		 */
		@Shift(0)
		@Mask(1)
		@ResetValue(1)
		public class RX_EMPTY implements RegisterBits {}

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
	@Address(0x1C)
	@Mask(0b00111111)
	@ResetValue(0)
	public class DYNPD implements RegisterByteWithBits {

		/**
		 * Bit DPL_P5 (R/W, default=0) : Enable dynamic payload length data pipe 5. (Requires EN_DPL and ENAA_P5)
		 */
		@Shift(5)
		@Mask(1)
		@ResetValue(0)
		public class DPL_P5 implements RegisterBits {}

		/**
		 * Bit DPL_P4 (R/W, default=0) : Enable dynamic payload length data pipe 4. (Requires EN_DPL and ENAA_P4)
		 */
		@Shift(4)
		@Mask(1)
		@ResetValue(0)
		public class DPL_P4 implements RegisterBits {}

		/**
		 * Bit DPL_P3 (R/W, default=0) : Enable dynamic payload length data pipe 3. (Requires EN_DPL and ENAA_P3)
		 */
		@Shift(3)
		@Mask(1)
		@ResetValue(0)
		public class DPL_P3 implements RegisterBits {}

		/**
		 * Bit DPL_P2 (R/W, default=0) : Enable dynamic payload length data pipe 2. (Requires EN_DPL and ENAA_P2)
		 */
		@Shift(2)
		@Mask(1)
		@ResetValue(0)
		public class DPL_P2 implements RegisterBits {}

		/**
		 * Bit DPL_P1 (R/W, default=0) : Enable dynamic payload length data pipe 1. (Requires EN_DPL and ENAA_P1)
		 */
		@Shift(1)
		@Mask(1)
		@ResetValue(0)
		public class DPL_P1 implements RegisterBits {}

		/**
		 * Bit DPL_P0 (R/W, default=0) : Enable dynamic payload length data pipe 0. (Requires EN_DPL and ENAA_P0)
		 */
		@Shift(0)
		@Mask(1)
		@ResetValue(0)
		public class DPL_P0 implements RegisterBits {}
		
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
	@Address(0x1D)
	@Mask(0b00000111)
	@ResetValue(0)
	public class FEATURE implements RegisterByteWithBits {

		/**
		 * Bit EN_DPL (R/W, default=0) : Enables Dynamic Payload Length.
		 */
		@Shift(2)
		@Mask(1)
		@ResetValue(0)
		public class EN_DPL implements RegisterBits {}

		/**
		 * Bit EN_ACK_PAY (R/W, default=0) : Enables Payload with ACK.
		 */
		@Shift(1)
		@Mask(1)
		@ResetValue(0)
		public class EN_ACK_PAY implements RegisterBits {}

		/**
		 * Bit EN_DYN_ACK (R/W, default=0) : Enables the W_TX_PAYLOAD_NOACK command.
		 */
		@Shift(0)
		@Mask(1)
		@ResetValue(0)
		public class EN_DYN_ACK implements RegisterBits {}

	}
	
}
