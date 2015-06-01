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

import fr.jstessier.rf24.Registers.RegisterBits;
import fr.jstessier.rf24.Registers.RegisterByte;
import fr.jstessier.rf24.Registers.RegisterByteWithBits;
import fr.jstessier.rf24.Registers.RegisterBytes;
import fr.jstessier.rf24.SpiCommands.SpiCommand;
import fr.jstessier.rf24.enums.AddressFieldWidth;
import fr.jstessier.rf24.enums.AutomaticRetransmitDelay;
import fr.jstessier.rf24.enums.DataPipe;
import fr.jstessier.rf24.enums.DataRates;
import fr.jstessier.rf24.enums.OutputPower;
import fr.jstessier.rf24.enums.WritePayloadType;
import fr.jstessier.rf24.exceptions.RF24Exception;
import fr.jstessier.rf24.exceptions.RxFifoTooLargeException;
import fr.jstessier.rf24.exceptions.WritePayloadMaxRetriesException;
import fr.jstessier.rf24.exceptions.WritePayloadTimeoutException;
import fr.jstessier.rf24.hardware.RF24Hardware;
import fr.jstessier.rf24.utils.BytesUtils;
import fr.jstessier.rf24.utils.RegistersUtils;
import fr.jstessier.rf24.utils.SpiCommandsUtils;
import fr.jstessier.rf24.utils.ThreadUtils;

/**
 * 
 * @author J.S. TESSIER
 */
public class RF24 {

	/** Minimum payload size. */
	public static final byte MIN_PAYLOAD_SIZE = 1;

	/** Maximum payload size. */
	public static final byte MAX_PAYLOAD_SIZE = 32;

	/** Maximum RX FIFO buffer size. */
	public static final byte MAX_RX_FIFO_SIZE = 32;


	/** Interface with hardware for communication with RF module (SPI and GPIO). */
	private final RF24Hardware rf24Hardware;


	// Configuration

	/** Write Payload timeout in Millisecond. */
	private int writePayloadTimeout = 60;


	// Internal state

	/** Is the module in RX mode. */
	private boolean listening = false;

	/** TX address for data pipe 0. */
	private byte[] txAddressP0 = new byte[] { (byte) 0xE7, (byte) 0xE7, (byte) 0xE7, (byte) 0xE7, (byte) 0xE7 };

	/** RX address for data pipe 0. */
	private byte[] rxAddressP0 = new byte[] { (byte) 0xE7, (byte) 0xE7, (byte) 0xE7, (byte) 0xE7, (byte) 0xE7 };


	/* ============
	 * Constructors
	 * ============ */

	/**
	 * Constructor.
	 * 
	 * @param rf24Hardware	Interface with hardware for communication with RF module (SPI and GPIO).
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24(RF24Hardware rf24Hardware) throws RF24Exception {
		if (rf24Hardware == null) {
			throw new IllegalArgumentException("rf24Hardware is mandotory");
		}
		this.rf24Hardware = rf24Hardware;
	}


	/* =============
	 * Configuration
	 * ============= */

	/**
	 * Initialize the module with default configuration and place it in standby mode 1.
	 * The configuration can be override after by the user.
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 initialize() throws RF24Exception {

		// Power down the module
		powerDown();

		// Reset CONFIG to default value.
		writeRegisterValue(Registers.CONFIG.class, RegistersUtils.getRegisterResetValue(Registers.CONFIG.class));

		// Enable 16-bits CRC
		enableCRC2bytes();

		// Set address lenth to 5 bytes (default value)
		setAddressFieldWidth(AddressFieldWidth.WIDTH_5_BYTES);

		// Set retransmit delay to 1500μS for working whith all data rates with ACK
		// Set number of retry to 15
		setRetries(AutomaticRetransmitDelay.ARD_1500_US, (byte) 15);

		// Set frequency channel to 2476 MHz
		// This channel should be universally safe and not bleed over into adjacent spectrum.
		setFrequencyChannel((byte) 76);

		// Set data rates to a slower and most reliable speed
		setDataRatesAndOutputPower(DataRates.DR_1_MBPS, OutputPower.RF_0_DBM);

		// Enable dynamic payload and auto acknowledgment for all pipes
		enableDynamicPayloadAndAutoAcknowledgmentOnAllPipes();

		// Enable payload in acknowledgment
		enableAcknowledgmentWithPayload();

		// Reset current status and flush buffers
		// Notice reset and flush is the last thing we do
		resetAllInterrupts();
		flushRx();
		flushTx();

		// Power up the module
		powerUp();

		return this;
	}

	/**
	 * Enable CRC on 1 byte (8 bit).
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 enableCRC1byte() throws RF24Exception {
		byte configValue = readRegisterValue(Registers.CONFIG.class);
		configValue = RegistersUtils.updateRegisterBits(Registers.CONFIG.class, configValue, Registers.CONFIG.EN_CRC.class, true);
		configValue = RegistersUtils.updateRegisterBits(Registers.CONFIG.class, configValue, Registers.CONFIG.CRCO.class, false);
		writeRegisterValue(Registers.CONFIG.class, configValue);
		return this;
	}

	/**
	 * Enable CRC on 2 bytes (16 bits).
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 enableCRC2bytes() throws RF24Exception {
		byte configValue = readRegisterValue(Registers.CONFIG.class);
		configValue = RegistersUtils.updateRegisterBits(Registers.CONFIG.class, configValue, Registers.CONFIG.EN_CRC.class, true);
		configValue = RegistersUtils.updateRegisterBits(Registers.CONFIG.class, configValue, Registers.CONFIG.CRCO.class, true);
		writeRegisterValue(Registers.CONFIG.class, configValue);
		return this;
	}

	/**
	 * Disablt CRC. Does not have effect if one pipe work with Auto Acknowledgment.
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 disableCRC() throws RF24Exception {
		byte configValue = readRegisterValue(Registers.CONFIG.class);
		configValue = RegistersUtils.updateRegisterBits(Registers.CONFIG.class, configValue, Registers.CONFIG.EN_CRC.class, false);
		configValue = RegistersUtils.updateRegisterBits(Registers.CONFIG.class, configValue, Registers.CONFIG.CRCO.class, false);
		writeRegisterValue(Registers.CONFIG.class, configValue);
		return this;
	}

	/**
	 * Enable RX for all pipes.
	 * By default RX is only enable on pipe 0 and 1.
	 * 
	 * @return The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 enableRxDataOnAllPipes() throws RF24Exception {
		return enableRxDataOnPipes(DataPipe.values());
	}

	/**
	 * Enable RX for a list of pipes.
	 * By default RX is only enable on pipe 0 and 1.
	 * 
	 * @param dataPipes	The list of pipes.
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 enableRxDataOnPipes(DataPipe... dataPipes) throws RF24Exception {
		if (dataPipes == null || dataPipes.length == 0) {
			return this;
		}

		Class<? extends RegisterBits> bitEnRxAddr = null;
		for (DataPipe dataPipe : dataPipes) {
			switch (dataPipe) {
			case P0:
				bitEnRxAddr = Registers.EN_RXADDR.ERX_P0.class;
				break;
			case P1:
				bitEnRxAddr = Registers.EN_RXADDR.ERX_P1.class;
				break;
			case P2:
				bitEnRxAddr = Registers.EN_RXADDR.ERX_P2.class;
				break;
			case P3:
				bitEnRxAddr = Registers.EN_RXADDR.ERX_P3.class;
				break;
			case P4:
				bitEnRxAddr = Registers.EN_RXADDR.ERX_P4.class;
				break;
			case P5:
				bitEnRxAddr = Registers.EN_RXADDR.ERX_P5.class;
				break;
			default:
				throw new RuntimeException("This may not append because all enum values are tested in the switch");
			}
		}

		byte enRxAddrValue = readRegisterValue(Registers.EN_RXADDR.class);
		enRxAddrValue = RegistersUtils.updateRegisterBits(Registers.EN_RXADDR.class, enRxAddrValue, bitEnRxAddr, true);
		writeRegisterValue(Registers.EN_RXADDR.class, enRxAddrValue);

		return this;
	}

	/**
	 * Set pipes address field width.
	 * 
	 * @param addressFieldWidth	The address field width (3 to 5 bytes).
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 setAddressFieldWidth(AddressFieldWidth addressFieldWidth) throws RF24Exception {
		byte setupawValue = RegistersUtils.updateRegisterBits(
				Registers.SETUP_AW.class, (byte) 0,
				Registers.SETUP_AW.AW.class, addressFieldWidth.getValue());
		writeRegisterValue(Registers.SETUP_AW.class, setupawValue);
		return this;
	}

	/**
	 * Set the max number of retries and the delay between two retries. 
	 * 
	 * @param delay	The delay between 2 retries.
	 * @param count	The max number of retries.
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 setRetries(AutomaticRetransmitDelay delay, byte count) throws RF24Exception {
		byte setupRetrValue = 0;
		setupRetrValue = RegistersUtils.updateRegisterBits(
				Registers.SETUP_RETR.class, setupRetrValue,
				Registers.SETUP_RETR.ARD.class, delay.getValue());
		setupRetrValue = RegistersUtils.updateRegisterBits(
				Registers.SETUP_RETR.class, setupRetrValue,
				Registers.SETUP_RETR.ARC.class, count);
		writeRegisterValue(Registers.SETUP_RETR.class, setupRetrValue);
		return this;
	}

	/**
	 * Set the frequency channel (7 bit -> [0 - 127]).
	 * Frequency is 2400 + frequencyChannel MHz.
	 * Channel 0 is 2400 MHz.
	 * Channel 127 is 2527 MHz.
	 * 
	 * @param frequencyChannel
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 setFrequencyChannel(byte frequencyChannel) throws RF24Exception {
		byte rfchValue = RegistersUtils.updateRegisterBits(
				Registers.RF_CH.class, (byte) 0,
				Registers.RF_CH.RF_CH_BITS.class, frequencyChannel);
		writeRegisterValue(Registers.RF_CH.class, rfchValue);
		return this;
	}

	/**
	 * 
	 * @param dataRates
	 * @param outputPower
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 setDataRatesAndOutputPower(DataRates dataRates, OutputPower outputPower) throws RF24Exception {
		byte rfsetupValue = readRegisterValue(Registers.RF_SETUP.class);
		rfsetupValue = RegistersUtils.updateRegisterBits(
				Registers.RF_SETUP.class, rfsetupValue,
				Registers.RF_SETUP.RF_DR_LOW.class, dataRates.getRfDrLow());
		rfsetupValue = RegistersUtils.updateRegisterBits(
				Registers.RF_SETUP.class, rfsetupValue,
				Registers.RF_SETUP.RF_DR_HIGH.class, dataRates.getRfDrHigh());
		rfsetupValue = RegistersUtils.updateRegisterBits(
				Registers.RF_SETUP.class, rfsetupValue,
				Registers.RF_SETUP.RF_PWR.class, outputPower.getValue());
		writeRegisterValue(Registers.RF_SETUP.class, rfsetupValue);
		return this;
	}

	/**
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 enableDynamicPayloadAndAutoAcknowledgmentOnAllPipes() throws RF24Exception {
		// Set EN_DPL to 1 in FEATURE register to enable Dynamic Payload Length for the system
		byte featureValue = readRegisterValue(Registers.FEATURE.class);
		featureValue = RegistersUtils.updateRegisterBits(Registers.FEATURE.class, featureValue, Registers.FEATURE.EN_DPL.class, true);
		writeRegisterValue(Registers.FEATURE.class, featureValue);

		// Enable Dynamic Payload Length for all pipes
		// Set all DPL_PX to 1 in DYNPD register (the rest of the register is reserved)
		writeRegisterValue(Registers.DYNPD.class, (byte) 0b00111111);

		// Enable AutoAcknowledgment (require for Dynamic Payload working)
		enableAutoAcknowledgmentOnAllPipes();

		return this;
	}

	/**
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 disableDynamicPayloadOnAllPipes() throws RF24Exception {
		// Set EN_DPL to 1 in FEATURE register to enable Dynamic Payload Length for the system
		byte featureValue = readRegisterValue(Registers.FEATURE.class);
		featureValue = RegistersUtils.updateRegisterBits(Registers.FEATURE.class, featureValue, Registers.FEATURE.EN_DPL.class, false);
		writeRegisterValue(Registers.FEATURE.class, featureValue);

		// Disable Dynamic Payload Length for all pipes
		// Set all DPL_PX to 0 in DYNPD register
		writeRegisterValue(Registers.DYNPD.class, (byte) 0);

		return this;
	}

	/**
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 enableAutoAcknowledgmentOnAllPipes() throws RF24Exception {
		// Enable Auto Acknowledgment for all pipes
		// Set all ENAA_PX to 1 in EN_AA register (the rest of the register is reserved)
		writeRegisterValue(Registers.EN_AA.class, (byte) 0b00111111);
		return this;
	}

	/**
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 disableAutoAcknowledgmentOnAllPipes() throws RF24Exception {
		// Disable Auto Acknowledgment for all pipes
		// Set all ENAA_PX to 0 in EN_AA register
		writeRegisterValue(Registers.EN_AA.class, (byte) 0);
		return this;
	}

	/**
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 enableAcknowledgmentWithPayload() throws RF24Exception {
		// Set EN_ACK_PAY to 1 in FEATURE register to enable Payload with Acknowledgment
		byte featureValue = readRegisterValue(Registers.FEATURE.class);
		featureValue = RegistersUtils.updateRegisterBits(Registers.FEATURE.class, featureValue, Registers.FEATURE.EN_ACK_PAY.class, true);
		writeRegisterValue(Registers.FEATURE.class, featureValue);
		return this;
	}

	/**
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 disableAcknowledgmentWithPayload() throws RF24Exception {
		// Set EN_ACK_PAY to 0 in FEATURE register to disable Payload with Acknowledgment
		byte featureValue = readRegisterValue(Registers.FEATURE.class);
		featureValue = RegistersUtils.updateRegisterBits(Registers.FEATURE.class, featureValue, Registers.FEATURE.EN_ACK_PAY.class, false);
		writeRegisterValue(Registers.FEATURE.class, featureValue);
		return this;
	}
	
	public RF24 powerUp() throws RF24Exception {
		byte configValue = readRegisterValue(Registers.CONFIG.class);
		if (RegistersUtils.bitIsFalse(Registers.CONFIG.class, configValue, Registers.CONFIG.PWR_UP.class)) {
			configValue = RegistersUtils.updateRegisterBits(Registers.CONFIG.class, configValue, Registers.CONFIG.PWR_UP.class, true);
			writeRegisterValue(Registers.CONFIG.class, configValue);
			// Delay for nRF24L01+ go from power down to standby mode (Tpd2stby = 4.5ms)
			ThreadUtils.delay(5);
		}
		return this;
	}

	public RF24 powerDown() throws RF24Exception {
		rf24Hardware.setPinChipEnableLow();	// Guarantee CE is low on powerDown
		byte configValue = readRegisterValue(Registers.CONFIG.class);
		configValue = RegistersUtils.updateRegisterBits(Registers.CONFIG.class, configValue, Registers.CONFIG.PWR_UP.class, false);
		writeRegisterValue(Registers.CONFIG.class, configValue);
		return this;
	}

	/**
	 * Flush TX buffer.
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 flushTx() throws RF24Exception {
		sendSpiCommand(SpiCommands.FLUSH_TX.class);
		return this;
	}

	/**
	 * Flush RX buffer.
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 flushRx() throws RF24Exception {
		sendSpiCommand(SpiCommands.FLUSH_RX.class);
		return this;
	}

	public boolean isRxDataAvailable() throws RF24Exception {
		return isRxDataAvailable(null);
	}

	public boolean isRxDataAvailable(DataPipe dataPipe) throws RF24Exception {
		byte fifoStatusValue = readRegisterValue(Registers.FIFO_STATUS.class);
		boolean rxEmpty = RegistersUtils.getBitsValue(Registers.FIFO_STATUS.class, fifoStatusValue, Registers.FIFO_STATUS.RX_EMPTY.class) == 1;
		if (!rxEmpty && dataPipe != null) {
			byte statusValue = getStatus();
			byte dataPipeNumber = RegistersUtils.getBitsValue(Registers.STATUS.class, statusValue, Registers.STATUS.RX_P_NO.class);
			return dataPipeNumber == dataPipe.getIndex();
		}
		return !rxEmpty;
	}

	private void checkDataPipeAddressFullAvailableLength(DataPipe dataPipe, byte[] address) throws RF24Exception {
		if (address == null || address.length == 0) {
			throw new IllegalArgumentException("The address is mandatory");
		}
		AddressFieldWidth addressFieldWidth = AddressFieldWidth.getFromValue(readRegisterValue(Registers.SETUP_AW.class));
		if (address.length != addressFieldWidth.getWidth()) {
			throw new IllegalArgumentException("The address must have a length of "
					+ addressFieldWidth.getWidth() + " bytes for DataPipe " + dataPipe);
		}
	}

	public RF24 openWritingPipe(byte[] address) throws RF24Exception {
		checkDataPipeAddressFullAvailableLength(DataPipe.P0, address);

		// Set TX_ADDR with the transmit address
		writeRegisterValues(Registers.TX_ADDR.class, address);

		// Backup the TX address. This is needed because pipe 0 need to have 2 address (1 for RX, 1 for TX).
		txAddressP0 = address;

		// If not listening data, set the RX address of the pipe 0 with the TX address
		// Otherwise, the TX address will be set when stop listening
		if (!listening) {
			// Set RX_ADDR_P0 equal to TX_ADDR address to handle automatic acknowledge if this is a PTX device
			writeRegisterValues(Registers.RX_ADDR_P0.class, address);
		}

		return this;
	}

	public RF24 openReadingPipe(DataPipe dataPipe, byte[] address) throws RF24Exception {
		openReadingPipe(dataPipe, address, MAX_PAYLOAD_SIZE);
		return this;
	}

	public RF24 openReadingPipe(DataPipe dataPipe, byte[] address, byte payloadSize) throws RF24Exception {

		Class<? extends RegisterBytes> registerRxAddr = null;
		Class<? extends RegisterByte> registerRxPw = null;
		Class<? extends RegisterBits> registerRxPwBits = null;

		switch (dataPipe) {
		case P0:
			registerRxAddr = Registers.RX_ADDR_P0.class;
			registerRxPw = Registers.RX_PW_P0.class;
			registerRxPwBits = Registers.RX_PW_P0.RW_PW_P0_BITS.class;
			break;
		case P1:
			registerRxAddr = Registers.RX_ADDR_P1.class;
			registerRxPw = Registers.RX_PW_P1.class;
			registerRxPwBits = Registers.RX_PW_P1.RW_PW_P1_BITS.class;
			break;
		case P2:
		case P3:
		case P4:
		case P5:
			throw new IllegalArgumentException(dataPipe.name() + " has an address on only 1 byte "
					+ "use openReadingPipe(DataPipe dataPipe, byte address, byte payloadSize) instead");
		default:
			throw new RuntimeException("This may not append because all enum values are tested in the switch");
		}

		checkDataPipeAddressFullAvailableLength(dataPipe, address);

		if (payloadSize < MIN_PAYLOAD_SIZE || payloadSize > MAX_PAYLOAD_SIZE) {
			throw new IllegalArgumentException("payloadSize is out of range ["
					+ MIN_PAYLOAD_SIZE + " - " + MAX_PAYLOAD_SIZE + "]");
		}

		if (dataPipe == DataPipe.P0) {
			// Backup the RX address. This is needed because pipe 0 need to have 2 address (1 for RX, 1 for TX).
			rxAddressP0 = address;
			// If listening data, set the RX address of the pipe 0 with the RX address
			// Otherwise, the RX address will be set when start listening
			if (listening) {
				writeRegisterValues(registerRxAddr, address);
			}
		} else {
			writeRegisterValues(registerRxAddr, address);
		}

		byte payloadSizeValue = (byte) (payloadSize & RegistersUtils.getRegisterBitsMask(registerRxPwBits));
		writeRegisterValue(registerRxPw, payloadSizeValue);

		return this;
	}

	public RF24 openReadingPipe(DataPipe dataPipe, byte address) throws RF24Exception {
		openReadingPipe(dataPipe, address, MAX_PAYLOAD_SIZE);
		return this;
	}

	public RF24 openReadingPipe(DataPipe dataPipe, byte address, byte payloadSize) throws RF24Exception {

		Class<? extends RegisterByte> registerRxAddr = null;
		Class<? extends RegisterByte> registerRxPw = null;
		Class<? extends RegisterBits> registerRxPwBits = null;

		switch (dataPipe) {
		case P0:
		case P1:
			throw new IllegalArgumentException(dataPipe.name() + " has an address on 5 bytes "
					+ "use openReadingPipe(DataPipe dataPipe, byte[] address, byte payloadSize) instead");
		case P2:
			registerRxAddr = Registers.RX_ADDR_P2.class;
			registerRxPw = Registers.RX_PW_P2.class;
			registerRxPwBits = Registers.RX_PW_P2.RW_PW_P2_BITS.class;
			break;
		case P3:
			registerRxAddr = Registers.RX_ADDR_P3.class;
			registerRxPw = Registers.RX_PW_P3.class;
			registerRxPwBits = Registers.RX_PW_P3.RW_PW_P3_BITS.class;
			break;
		case P4:
			registerRxAddr = Registers.RX_ADDR_P4.class;
			registerRxPw = Registers.RX_PW_P4.class;
			registerRxPwBits = Registers.RX_PW_P4.RW_PW_P4_BITS.class;
			break;
		case P5:
			registerRxAddr = Registers.RX_ADDR_P5.class;
			registerRxPw = Registers.RX_PW_P5.class;
			registerRxPwBits = Registers.RX_PW_P5.RW_PW_P5_BITS.class;
			break;
		default:
			throw new RuntimeException("This may not append because all enum values are tested in the switch");
		}

		if (payloadSize < MIN_PAYLOAD_SIZE || payloadSize > MAX_PAYLOAD_SIZE) {
			throw new IllegalArgumentException("payloadSize is out of range ["
					+ MIN_PAYLOAD_SIZE + " - " + MAX_PAYLOAD_SIZE + "]");
		}

		byte payloadSizeValue = (byte) (payloadSize & RegistersUtils.getRegisterBitsMask(registerRxPwBits));
		writeRegisterValue(registerRxAddr, address);
		writeRegisterValue(registerRxPw, payloadSizeValue);

		return this;
	}

	public RF24 closeReadingPipe(DataPipe dataPipe) throws RF24Exception {
		byte enrxaddr = readRegisterValue(Registers.EN_RXADDR.class);

		Class<? extends RegisterBits> bitEnRxAddr = null;
		Class<? extends RegisterByteWithBits> registerRxPw = null;

		switch (dataPipe) {
		case P0:
			bitEnRxAddr = Registers.EN_RXADDR.ERX_P0.class;
			registerRxPw = Registers.RX_PW_P0.class;
			break;
		case P1:
			bitEnRxAddr = Registers.EN_RXADDR.ERX_P1.class;
			registerRxPw = Registers.RX_PW_P1.class;
			break;
		case P2:
			bitEnRxAddr = Registers.EN_RXADDR.ERX_P2.class;
			registerRxPw = Registers.RX_PW_P2.class;
			break;
		case P3:
			bitEnRxAddr = Registers.EN_RXADDR.ERX_P3.class;
			registerRxPw = Registers.RX_PW_P3.class;
			break;
		case P4:
			bitEnRxAddr = Registers.EN_RXADDR.ERX_P4.class;
			registerRxPw = Registers.RX_PW_P4.class;
			break;
		case P5:
			bitEnRxAddr = Registers.EN_RXADDR.ERX_P5.class;
			registerRxPw = Registers.RX_PW_P5.class;
			break;
		default:
			throw new RuntimeException("This may not append because all enum values are tested in the switch");
		}

		enrxaddr = RegistersUtils.updateRegisterBits(Registers.EN_RXADDR.class, enrxaddr, bitEnRxAddr, false);
		writeRegisterValue(Registers.EN_RXADDR.class, enrxaddr);
		writeRegisterValue(registerRxPw, (byte) 0);

		return this;
	}

	public RF24 startListening() throws RF24Exception {

		// Reset current status and flush buffers
		resetAllInterrupts();
		flushRx();
		flushTx();

		// Restore pipe 0 RX address
		writeRegisterValues(Registers.RX_ADDR_P0.class, rxAddressP0);

		byte configValue = readRegisterValue(Registers.CONFIG.class);
		configValue = RegistersUtils.updateRegisterBits(Registers.CONFIG.class, configValue, Registers.CONFIG.PRIM_RX.class, true);
		writeRegisterValue(Registers.CONFIG.class, configValue);

		rf24Hardware.setPinChipEnableHigh();

		listening = true;

		return this;
	}

	public RF24 stopListening() throws RF24Exception {

		rf24Hardware.setPinChipEnableLow();

		byte configValue = readRegisterValue(Registers.CONFIG.class);
		configValue = RegistersUtils.updateRegisterBits(Registers.CONFIG.class, configValue, Registers.CONFIG.PRIM_RX.class, false);
		writeRegisterValue(Registers.CONFIG.class, configValue);

		// Restore pipe 0 TX address
		writeRegisterValues(Registers.RX_ADDR_P0.class, txAddressP0);

		// Reset current status and flush buffers
		resetAllInterrupts();
		flushRx();
		flushTx();

		listening = false;

		return this;
	}

	/**
	 * Clear RX interrupts. 
	 * Write 0x70 to the STATUS register.
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 resetAllInterrupts() throws RF24Exception {
		// Set RX_DR, TX_DS, and MAX_RT to 1 in STATUS register
		writeRegisterValue(Registers.STATUS.class, (byte) 0b01110000);
		return this;
	}
	
	/**
	 * Clear RX interrupts. 
	 * Write 0x40 to the STATUS register.
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 resetRxInterrupt() throws RF24Exception {
		// Set RX_DR to 1 in STATUS register
		writeRegisterValue(Registers.STATUS.class, (byte) 0b01000000);
		return this;
	}

	/**
	 * Clear TX interrupts. 
	 * Write 0x30 to the STATUS register.
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 resetTxInterrupt() throws RF24Exception {
		// Set TX_DS, and MAX_RT to 1 in STATUS register
		writeRegisterValue(Registers.STATUS.class, (byte) 0b00110000);
		return this;
	}

	/**
	 * Return the RX payload width.
	 * 
	 * @return	The RX payload width.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 * @throws RxFifoTooLargeException If payload width is too large.
	 */
	public byte getDynamicPayloadSize() throws RF24Exception {
		byte result = sendSpiCommand(SpiCommands.R_RX_PL_WID.class)[1];
		if (result > MAX_RX_FIFO_SIZE) {
			flushRx();
			throw new RxFifoTooLargeException();
		}
		return result;
	}

	public byte[] readPayload(byte length) throws RF24Exception {
		try {
			byte[] result = sendSpiCommand(SpiCommands.R_RX_PAYLOAD.class, new byte[length]);
			resetRxInterrupt();
			// Remove the first byte - it is the value of the STATUS register
			return BytesUtils.removeFirstByte(result);
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to read payload", e);
		}
	}

	public void sendPayload(byte... payload) throws RF24Exception {
		sendPayload(WritePayloadType.W_TX_PAYLOAD, payload);
	}

	public void sendPayload(WritePayloadType writePayloadType, byte... payload) throws RF24Exception {

		byte status = writePayload(writePayloadType, payload);

		rf24Hardware.setPinChipEnableHigh();
		ThreadUtils.delayMicroseconds(10);
		rf24Hardware.setPinChipEnableLow();

		long startTimeMillis = System.currentTimeMillis();
		boolean dataSent = false;
		boolean maxRetry = false;
		boolean timeout = false;

		while (!dataSent && !maxRetry && !timeout) {
			long now = System.currentTimeMillis();
			// Transmitted or Max retry
			status = getStatus();
			dataSent = RegistersUtils.bitIsTrue(Registers.STATUS.class, status, Registers.STATUS.TX_DS.class);
			maxRetry = RegistersUtils.bitIsTrue(Registers.STATUS.class, status, Registers.STATUS.MAX_RT.class);
			// Timeout
			if (!dataSent && !maxRetry) {
				timeout = now - startTimeMillis > writePayloadTimeout;
			}
		}

		resetTxInterrupt();

		if (!dataSent) {
			if (maxRetry) {
				throw new WritePayloadMaxRetriesException();
			} else if (timeout) {
				throw new WritePayloadTimeoutException();
			}
		}
	}

	public byte[] sendPayloadAndReadAckPayload(byte... payload) throws RF24Exception {
		if (!isAcknowledgmentWithPayloadEnabled()) {
			throw new IllegalStateException("sendPayloadAndReadAckPayload cannot be invoke if AcknowledgmentWithPayload is not enable");
		}
		sendPayload(payload);
		if (isRxDataAvailable()) {
			return readPayload(getDynamicPayloadSize());
		}
		else {
			return new byte[0];
		}
	}

	public byte writePayload(WritePayloadType writePayloadType, byte... payload) throws RF24Exception {
		if (listening) {
			throw new IllegalStateException("writePayload cannot be invoke in RX mode");
		}

		Class<? extends SpiCommand> spiCommand = null;
		if (writePayloadType == WritePayloadType.W_TX_PAYLOAD_NO_ACK) {
			spiCommand = SpiCommands.W_TX_PAYLOAD_NOACK.class;
		} else {
			spiCommand = SpiCommands.W_TX_PAYLOAD.class;
		}

		try {
			// First return byte is the value of the STATUS register
			return sendSpiCommand(spiCommand, payload)[0];
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to write payload", e);
		}
	}

	public byte writeAckPayload(WritePayloadType writePayloadType, byte... payload) throws RF24Exception {
		if (!listening) {
			throw new IllegalStateException("writeAckPayload cannot be invoke in TX mode");
		}
		try {
			// First return byte is the value of the STATUS register
			return sendSpiCommand(SpiCommands.W_ACK_PAYLOAD.class, payload)[0];
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to write ack payload", e);
		}
	}

	public byte enableReuseTxPayload() throws RF24Exception {
		// First return byte is the value of the STATUS register
		return sendSpiCommand(SpiCommands.REUSE_TX_PL.class, null)[0];
	}


	/* =====================
	 * Read / Write register
	 * ===================== */

	/**
	 * 
	 * @param register
	 * @return
	 * @throws RF24Exception
	 */
	public byte readRegisterValue(Class<? extends RegisterByte> register) throws RF24Exception {
		try {
			// The first byte is the value of the STATUS register
			return readRegister(RegistersUtils.getRegisterAddress(register), (byte) 1)[1];
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to read register " + register.getSimpleName(), e);
		}
	}

	/**
	 * 
	 * @param register
	 * @return
	 * @throws RF24Exception
	 */
	public byte[] readRegisterValues(Class<? extends RegisterBytes> register) throws RF24Exception {
		try {
			byte[] result = readRegister(RegistersUtils.getRegisterAddress(register), RegistersUtils.getRegisterLength(register));
			// Remove the first byte - it is the value of the STATUS register
			return BytesUtils.removeFirstByte(result);
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to read register " + register.getSimpleName(), e);
		}
	}

	/**
	 * Read the value of a register.
	 * 
	 * @param registerAddress	The address register.
	 * @return	The value
	 * @throws RF24Exception
	 */
	protected byte[] readRegister(byte registerAddress, byte registerLength) throws RF24Exception {
		try {
			return sendSpiCommand(SpiCommandsUtils.getSpiCommand(
					SpiCommands.R_REGISTER.class, registerAddress),
					new byte[registerLength]);
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to read register " + registerAddress, e);
		}
	}

	/**
	 * Write a value in a one byte register.
	 * 
	 * @param register	The register.
	 * @param value		The value to write.
	 * @return The STATUS register value.
	 */
	
	@SuppressWarnings("unchecked")
	public byte writeRegisterValue(Class<? extends RegisterByte> register, byte value) throws RF24Exception {
		if (RegisterByteWithBits.class.isAssignableFrom(register)) {
			RegistersUtils.checkRegisterMask((Class<RegisterByteWithBits>) register, value);
		}
		return writeRegister(RegistersUtils.getRegisterAddress(register), value);
	}

	/**
	 * Write a value in a multi bytes register.
	 * 
	 * @param register	The register.
	 * @param value	Values to write.
	 * @return	The STATUS register value.
	 */
	public byte writeRegisterValues(Class<? extends RegisterBytes> register, byte... value) throws RF24Exception {
		if (RegistersUtils.getRegisterLength(register) > value.length) {
			throw new IllegalArgumentException("values length cannot be longer than register length (values.length= "
					+ value.length + "register.length=" + RegistersUtils.getRegisterLength(register) + ")");
		}
		try {
			return writeRegister(RegistersUtils.getRegisterAddress(register), value);
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to write register "	+ register.getSimpleName(), e);
		}
	}

	/**
	 * Write a value in a multi bytes register.
	 * 
	 * @param registerAddress	The address register.
	 * @param value			Values to write.
	 * @return	The STATUS register value.
	 */
	protected byte writeRegister(byte registerAddress, byte... value) throws RF24Exception {
		try {
			return sendSpiCommand(SpiCommandsUtils.getSpiCommand(SpiCommands.W_REGISTER.class, registerAddress), value)[0];
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to write register " + registerAddress, e);
		}
	}


	/* ================
	 * Send SPI Command
	 * ================ */

	public byte[] sendSpiCommand(Class<? extends SpiCommand> spiCommand) throws RF24Exception {
		try {
			return sendSpiCommand(spiCommand, new byte[SpiCommandsUtils.getDataMaxLength(spiCommand)]);
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to send SPI command " + spiCommand.getSimpleName(), e);
		}
	}

	public byte[] sendSpiCommand(Class<? extends SpiCommand> spiCommand, byte... data) throws RF24Exception {
		SpiCommandsUtils.checkDataLength(spiCommand, data);
		try {
			return sendSpiCommand(SpiCommandsUtils.getSpiCommand(spiCommand), data);
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to send SPI command " + spiCommand.getSimpleName(), e);
		}
	}

	protected byte[] sendSpiCommand(byte spiCommand, byte... data) throws RF24Exception {
		byte[] packet = new byte[(data == null) ? 1 : data.length + 1];
		packet[0] = spiCommand;
		if (data != null && data.length > 0) {
			System.arraycopy(data, 0, packet, 1, data.length);
		}
		try {
			return rf24Hardware.spiWrite(packet);
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to send SPI command " + BytesUtils.bytesToHex(spiCommand), e);
		}
	}

	/* =======
	 * Getters
	 * ======= */

	public int getWritePayloadTimeout() {
		return writePayloadTimeout;
	}

	public void setWritePayloadTimeout(int writePayloadTimeout) {
		this.writePayloadTimeout = writePayloadTimeout;
	}

	public AddressFieldWidth getAddressFieldWidth() throws RF24Exception {
		byte setupawValue = readRegisterValue(Registers.SETUP_AW.class);
		byte awValue = RegistersUtils.getBitsValue(Registers.SETUP_AW.class, setupawValue, Registers.SETUP_AW.AW.class);
		return AddressFieldWidth.getFromValue(awValue);
	}

	public AutomaticRetransmitDelay getAutomaticRetransmitDelay() throws RF24Exception {
		byte setupretrValue = readRegisterValue(Registers.SETUP_RETR.class);
		byte ardValue = RegistersUtils.getBitsValue(Registers.SETUP_RETR.class, setupretrValue, Registers.SETUP_RETR.ARD.class);
		return AutomaticRetransmitDelay.getFromValue(ardValue);
	}

	public byte getAutomaticRetransmitCount() throws RF24Exception {
		byte setupretrValue = readRegisterValue(Registers.SETUP_RETR.class);
		return RegistersUtils.getBitsValue(Registers.SETUP_RETR.class, setupretrValue, Registers.SETUP_RETR.ARC.class);
	}

	public byte getFrequencyChannel() throws RF24Exception {
		byte rfchValue = readRegisterValue(Registers.RF_CH.class);
		return RegistersUtils.getBitsValue(Registers.RF_CH.class, rfchValue, Registers.RF_CH.RF_CH_BITS.class);
	}

	public DataRates getDataRates() throws RF24Exception {
		byte rfsetupValue = readRegisterValue(Registers.RF_SETUP.class);
		boolean rfDrLow = RegistersUtils.bitIsTrue(Registers.RF_SETUP.class, rfsetupValue, Registers.RF_SETUP.RF_PWR.class);
		boolean rfDrHigh = RegistersUtils.bitIsTrue(Registers.RF_SETUP.class, rfsetupValue, Registers.RF_SETUP.RF_PWR.class);
		return DataRates.getFromValue(rfDrLow, rfDrHigh);
	}

	public OutputPower getOutputPower() throws RF24Exception {
		byte rfsetupValue = readRegisterValue(Registers.RF_SETUP.class);
		byte rfpwrValue = RegistersUtils.getBitsValue(Registers.RF_SETUP.class, rfsetupValue, Registers.RF_SETUP.RF_PWR.class);
		return OutputPower.getFromValue(rfpwrValue);
	}

	/**
	 * Return the STATUS register value.
	 * 
	 * @return	The STATUS register value.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public byte getStatus() throws RF24Exception {
		return sendSpiCommand(SpiCommands.NOP.class)[0];
	}

	public boolean isDynamicPayloadsEnabled() throws RF24Exception {
		byte featureValue = readRegisterValue(Registers.FEATURE.class);
		return RegistersUtils.bitIsTrue(Registers.FEATURE.class, featureValue, Registers.FEATURE.EN_DPL.class);
	}

	public boolean isAcknowledgmentWithPayloadEnabled() throws RF24Exception {
		byte featureValue = readRegisterValue(Registers.FEATURE.class);
		return RegistersUtils.bitIsTrue(Registers.FEATURE.class, featureValue, Registers.FEATURE.EN_ACK_PAY.class);
	}

	public boolean isDynamicPayloadsEnabledOnPipe(DataPipe dataPipe) throws RF24Exception {
		byte dynpdValue = readRegisterValue(Registers.DYNPD.class);
		return (dynpdValue & dataPipe.getMask()) != 0;
	}

	public boolean isAutoAcknowledgmentEnabledOnPipe(DataPipe dataPipe) throws RF24Exception {
		byte enaaValue = readRegisterValue(Registers.EN_AA.class);
		return (enaaValue & dataPipe.getMask()) != 0;
	}

}
