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
import fr.jstessier.rf24.exceptions.WritePayloadException;
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
	public RF24(final RF24Hardware rf24Hardware) throws RF24Exception {
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
		writeRegisterValue(Registers.CONFIG, Registers.CONFIG.getResetValue());

		// Enable 16-bits CRC
		enableCRC2bytes();

		// Set address length to 5 bytes (default value)
		setAddressFieldWidth(AddressFieldWidth.WIDTH_5_BYTES);

		// Set retransmit delay to 1500μS for working with all data rates with ACK
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
		byte configValue = readRegisterValue(Registers.CONFIG);
		configValue = RegistersUtils.updateRegisterBits(configValue, Registers.CONFIG.EN_CRC, true);
		configValue = RegistersUtils.updateRegisterBits(configValue, Registers.CONFIG.CRCO, false);
		writeRegisterValue(Registers.CONFIG, configValue);
		return this;
	}

	/**
	 * Enable CRC on 2 bytes (16 bits).
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 enableCRC2bytes() throws RF24Exception {
		byte configValue = readRegisterValue(Registers.CONFIG);
		configValue = RegistersUtils.updateRegisterBits(configValue, Registers.CONFIG.EN_CRC, true);
		configValue = RegistersUtils.updateRegisterBits(configValue, Registers.CONFIG.CRCO, true);
		writeRegisterValue(Registers.CONFIG, configValue);
		return this;
	}

	/**
	 * Disablt CRC. Does not have effect if one pipe work with Auto Acknowledgment.
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 disableCRC() throws RF24Exception {
		byte configValue = readRegisterValue(Registers.CONFIG);
		configValue = RegistersUtils.updateRegisterBits(configValue, Registers.CONFIG.EN_CRC, false);
		configValue = RegistersUtils.updateRegisterBits(configValue, Registers.CONFIG.CRCO, false);
		writeRegisterValue(Registers.CONFIG, configValue);
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

		RegisterBits bitEnRxAddr = null;
		for (DataPipe dataPipe : dataPipes) {
			switch (dataPipe) {
			case P0:
				bitEnRxAddr = Registers.EN_RXADDR.ERX_P0;
				break;
			case P1:
				bitEnRxAddr = Registers.EN_RXADDR.ERX_P1;
				break;
			case P2:
				bitEnRxAddr = Registers.EN_RXADDR.ERX_P2;
				break;
			case P3:
				bitEnRxAddr = Registers.EN_RXADDR.ERX_P3;
				break;
			case P4:
				bitEnRxAddr = Registers.EN_RXADDR.ERX_P4;
				break;
			case P5:
				bitEnRxAddr = Registers.EN_RXADDR.ERX_P5;
				break;
			default:
				throw new RuntimeException("This may not append because all enum values are tested in the switch");
			}
		}

		byte enRxAddrValue = readRegisterValue(Registers.EN_RXADDR);
		enRxAddrValue = RegistersUtils.updateRegisterBits(enRxAddrValue, bitEnRxAddr, true);
		writeRegisterValue(Registers.EN_RXADDR, enRxAddrValue);

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
		byte setupawValue = RegistersUtils.updateRegisterBits((byte) 0, Registers.SETUP_AW.AW, addressFieldWidth.getValue());
		writeRegisterValue(Registers.SETUP_AW, setupawValue);
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
		setupRetrValue = RegistersUtils.updateRegisterBits(setupRetrValue, Registers.SETUP_RETR.ARD, delay.getValue());
		setupRetrValue = RegistersUtils.updateRegisterBits(setupRetrValue, Registers.SETUP_RETR.ARC, count);
		writeRegisterValue(Registers.SETUP_RETR, setupRetrValue);
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
		byte rfchValue = RegistersUtils.updateRegisterBits((byte) 0, Registers.RF_CH.RF_CH, frequencyChannel);
		writeRegisterValue(Registers.RF_CH, rfchValue);
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
		byte rfsetupValue = readRegisterValue(Registers.RF_SETUP);
		rfsetupValue = RegistersUtils.updateRegisterBits(rfsetupValue, Registers.RF_SETUP.RF_DR_LOW, dataRates.getRfDrLow());
		rfsetupValue = RegistersUtils.updateRegisterBits(rfsetupValue, Registers.RF_SETUP.RF_DR_HIGH, dataRates.getRfDrHigh());
		rfsetupValue = RegistersUtils.updateRegisterBits(rfsetupValue, Registers.RF_SETUP.RF_PWR, outputPower.getValue());
		writeRegisterValue(Registers.RF_SETUP, rfsetupValue);
		return this;
	}

	/**
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 enableDynamicPayloadAndAutoAcknowledgmentOnAllPipes() throws RF24Exception {
		// Set EN_DPL to 1 in FEATURE register to enable Dynamic Payload Length for the system
		byte featureValue = readRegisterValue(Registers.FEATURE);
		featureValue = RegistersUtils.updateRegisterBits(featureValue, Registers.FEATURE.EN_DPL, true);
		writeRegisterValue(Registers.FEATURE, featureValue);

		// Enable Dynamic Payload Length for all pipes
		// Set all DPL_PX to 1 in DYNPD register (the rest of the register is reserved)
		writeRegisterValue(Registers.DYNPD, (byte) 0b00111111);

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
		byte featureValue = readRegisterValue(Registers.FEATURE);
		featureValue = RegistersUtils.updateRegisterBits(featureValue, Registers.FEATURE.EN_DPL, false);
		writeRegisterValue(Registers.FEATURE, featureValue);

		// Disable Dynamic Payload Length for all pipes
		// Set all DPL_PX to 0 in DYNPD register
		writeRegisterValue(Registers.DYNPD, (byte) 0);

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
		writeRegisterValue(Registers.EN_AA, (byte) 0b00111111);
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
		writeRegisterValue(Registers.EN_AA, (byte) 0);
		return this;
	}

	/**
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 enableAcknowledgmentWithPayload() throws RF24Exception {
		// Set EN_ACK_PAY to 1 in FEATURE register to enable Payload with Acknowledgment
		byte featureValue = readRegisterValue(Registers.FEATURE);
		featureValue = RegistersUtils.updateRegisterBits(featureValue, Registers.FEATURE.EN_ACK_PAY, true);
		writeRegisterValue(Registers.FEATURE, featureValue);
		return this;
	}

	/**
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 disableAcknowledgmentWithPayload() throws RF24Exception {
		// Set EN_ACK_PAY to 0 in FEATURE register to disable Payload with Acknowledgment
		byte featureValue = readRegisterValue(Registers.FEATURE);
		featureValue = RegistersUtils.updateRegisterBits(featureValue, Registers.FEATURE.EN_ACK_PAY, false);
		writeRegisterValue(Registers.FEATURE, featureValue);
		return this;
	}

	
	/**
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 enableWritePayloadNoAckCommand() throws RF24Exception {
		// Set EN_DYN_ACK to 1 in FEATURE register to enable the W_TX_PAYLOAD_NOACK command
		byte featureValue = readRegisterValue(Registers.FEATURE);
		featureValue = RegistersUtils.updateRegisterBits(featureValue, Registers.FEATURE.EN_DYN_ACK, true);
		writeRegisterValue(Registers.FEATURE, featureValue);
		return this;
	}

	/**
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 disableWritePayloadNoAckCommand() throws RF24Exception {
		// Set EN_DYN_ACK to 0 in FEATURE register to disable the W_TX_PAYLOAD_NOACK command
		byte featureValue = readRegisterValue(Registers.FEATURE);
		featureValue = RegistersUtils.updateRegisterBits(featureValue, Registers.FEATURE.EN_DYN_ACK, false);
		writeRegisterValue(Registers.FEATURE, featureValue);
		return this;
	}
	
	public RF24 powerUp() throws RF24Exception {
		byte configValue = readRegisterValue(Registers.CONFIG);
		if (RegistersUtils.bitIsFalse(configValue, Registers.CONFIG.PWR_UP)) {
			configValue = RegistersUtils.updateRegisterBits(configValue, Registers.CONFIG.PWR_UP, true);
			writeRegisterValue(Registers.CONFIG, configValue);
			// Delay for nRF24L01+ go from power down to standby mode (Tpd2stby = 4.5ms)
			ThreadUtils.delay(5);
		}
		return this;
	}

	public RF24 powerDown() throws RF24Exception {
		rf24Hardware.setPinChipEnableLow();	// Guarantee CE is low on powerDown
		byte configValue = readRegisterValue(Registers.CONFIG);
		configValue = RegistersUtils.updateRegisterBits(configValue, Registers.CONFIG.PWR_UP, false);
		writeRegisterValue(Registers.CONFIG, configValue);
		return this;
	}

	/**
	 * Flush TX buffer.
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 flushTx() throws RF24Exception {
		sendSpiCommand(SpiCommands.FLUSH_TX);
		return this;
	}

	/**
	 * Flush RX buffer.
	 * 
	 * @return	The current RF24 instance.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public RF24 flushRx() throws RF24Exception {
		sendSpiCommand(SpiCommands.FLUSH_RX);
		return this;
	}

	public boolean isRxDataAvailable() throws RF24Exception {
		return isRxDataAvailable(null);
	}

	public boolean isRxDataAvailable(DataPipe dataPipe) throws RF24Exception {
		byte fifoStatusValue = readRegisterValue(Registers.FIFO_STATUS);
		boolean rxEmpty = RegistersUtils.getBitsValue(fifoStatusValue, Registers.FIFO_STATUS.RX_EMPTY) == 1;
		if (!rxEmpty && dataPipe != null) {
			byte statusValue = getStatus();
			byte dataPipeNumber = RegistersUtils.getBitsValue(statusValue, Registers.STATUS.RX_P_NO);
			return dataPipeNumber == dataPipe.getIndex();
		}
		return !rxEmpty;
	}

	private void checkDataPipeAddressFullAvailableLength(DataPipe dataPipe, byte[] address) throws RF24Exception {
		if (address == null || address.length == 0) {
			throw new IllegalArgumentException("The address is mandatory");
		}
		AddressFieldWidth addressFieldWidth = AddressFieldWidth.getFromValue(readRegisterValue(Registers.SETUP_AW));
		if (address.length != addressFieldWidth.getWidth()) {
			throw new IllegalArgumentException("The address must have a length of "
					+ addressFieldWidth.getWidth() + " bytes for DataPipe " + dataPipe);
		}
	}

	public RF24 openWritingPipe(byte[] address) throws RF24Exception {
		checkDataPipeAddressFullAvailableLength(DataPipe.P0, address);

		// Set TX_ADDR with the transmit address
		writeRegisterValues(Registers.TX_ADDR, address);

		// Backup the TX address. This is needed because pipe 0 need to have 2 address (1 for RX, 1 for TX).
		txAddressP0 = address;

		// If not listening data, set the RX address of the pipe 0 with the TX address
		// Otherwise, the TX address will be set when stop listening
		if (!listening) {
			// Set RX_ADDR_P0 equal to TX_ADDR address to handle automatic acknowledge if this is a PTX device
			writeRegisterValues(Registers.RX_ADDR_P0, address);
		}

		return this;
	}

	public RF24 openReadingPipe(DataPipe dataPipe, byte[] address) throws RF24Exception {
		openReadingPipe(dataPipe, address, MAX_PAYLOAD_SIZE);
		return this;
	}

	public RF24 openReadingPipe(DataPipe dataPipe, byte[] address, byte payloadSize) throws RF24Exception {

		RegisterBytes registerRxAddr = null;
		RegisterByte registerRxPw = null;
		RegisterBits registerRxPwBits = null;

		switch (dataPipe) {
		case P0:
			registerRxAddr = Registers.RX_ADDR_P0;
			registerRxPw = Registers.RX_PW_P0;
			registerRxPwBits = Registers.RX_PW_P0.RX_PW_P0;
			break;
		case P1:
			registerRxAddr = Registers.RX_ADDR_P1;
			registerRxPw = Registers.RX_PW_P1;
			registerRxPwBits = Registers.RX_PW_P1.RX_PW_P1;
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

		byte payloadSizeValue = (byte) (payloadSize & registerRxPwBits.getMask());
		writeRegisterValue(registerRxPw, payloadSizeValue);

		return this;
	}

	public RF24 openReadingPipe(DataPipe dataPipe, byte address) throws RF24Exception {
		openReadingPipe(dataPipe, address, MAX_PAYLOAD_SIZE);
		return this;
	}

	public RF24 openReadingPipe(DataPipe dataPipe, byte address, byte payloadSize) throws RF24Exception {

		RegisterByte registerRxAddr = null;
		RegisterByte registerRxPw = null;
		RegisterBits registerRxPwBits = null;

		switch (dataPipe) {
		case P0:
		case P1:
			throw new IllegalArgumentException(dataPipe.name() + " has an address on 5 bytes "
					+ "use openReadingPipe(DataPipe dataPipe, byte[] address, byte payloadSize) instead");
		case P2:
			registerRxAddr = Registers.RX_ADDR_P2;
			registerRxPw = Registers.RX_PW_P2;
			registerRxPwBits = Registers.RX_PW_P2.RX_PW_P2;
			break;
		case P3:
			registerRxAddr = Registers.RX_ADDR_P3;
			registerRxPw = Registers.RX_PW_P3;
			registerRxPwBits = Registers.RX_PW_P3.RX_PW_P3;
			break;
		case P4:
			registerRxAddr = Registers.RX_ADDR_P4;
			registerRxPw = Registers.RX_PW_P4;
			registerRxPwBits = Registers.RX_PW_P4.RX_PW_P4;
			break;
		case P5:
			registerRxAddr = Registers.RX_ADDR_P5;
			registerRxPw = Registers.RX_PW_P5;
			registerRxPwBits = Registers.RX_PW_P5.RX_PW_P5;
			break;
		default:
			throw new RuntimeException("This may not append because all enum values are tested in the switch");
		}

		if (payloadSize < MIN_PAYLOAD_SIZE || payloadSize > MAX_PAYLOAD_SIZE) {
			throw new IllegalArgumentException("payloadSize is out of range ["
					+ MIN_PAYLOAD_SIZE + " - " + MAX_PAYLOAD_SIZE + "]");
		}

		byte payloadSizeValue = (byte) (payloadSize & registerRxPwBits.getMask());
		writeRegisterValue(registerRxAddr, address);
		writeRegisterValue(registerRxPw, payloadSizeValue);

		return this;
	}

	public RF24 closeReadingPipe(DataPipe dataPipe) throws RF24Exception {
		byte enrxaddr = readRegisterValue(Registers.EN_RXADDR);

		RegisterBits bitEnRxAddr = null;
		RegisterByteWithBits registerRxPw = null;

		switch (dataPipe) {
		case P0:
			bitEnRxAddr = Registers.EN_RXADDR.ERX_P0;
			registerRxPw = Registers.RX_PW_P0;
			break;
		case P1:
			bitEnRxAddr = Registers.EN_RXADDR.ERX_P1;
			registerRxPw = Registers.RX_PW_P1;
			break;
		case P2:
			bitEnRxAddr = Registers.EN_RXADDR.ERX_P2;
			registerRxPw = Registers.RX_PW_P2;
			break;
		case P3:
			bitEnRxAddr = Registers.EN_RXADDR.ERX_P3;
			registerRxPw = Registers.RX_PW_P3;
			break;
		case P4:
			bitEnRxAddr = Registers.EN_RXADDR.ERX_P4;
			registerRxPw = Registers.RX_PW_P4;
			break;
		case P5:
			bitEnRxAddr = Registers.EN_RXADDR.ERX_P5;
			registerRxPw = Registers.RX_PW_P5;
			break;
		default:
			throw new RuntimeException("This may not append because all enum values are tested in the switch");
		}

		enrxaddr = RegistersUtils.updateRegisterBits(enrxaddr, bitEnRxAddr, false);
		writeRegisterValue(Registers.EN_RXADDR, enrxaddr);
		writeRegisterValue(registerRxPw, (byte) 0);

		return this;
	}

	public RF24 startListening() throws RF24Exception {

		// Reset current status and flush buffers
		resetAllInterrupts();
		flushRx();
		flushTx();

		// Restore pipe 0 RX address
		writeRegisterValues(Registers.RX_ADDR_P0, rxAddressP0);

		byte configValue = readRegisterValue(Registers.CONFIG);
		configValue = RegistersUtils.updateRegisterBits(configValue, Registers.CONFIG.PRIM_RX, true);
		writeRegisterValue(Registers.CONFIG, configValue);

		rf24Hardware.setPinChipEnableHigh();

		listening = true;

		return this;
	}

	public RF24 stopListening() throws RF24Exception {

		rf24Hardware.setPinChipEnableLow();

		byte configValue = readRegisterValue(Registers.CONFIG);
		configValue = RegistersUtils.updateRegisterBits(configValue, Registers.CONFIG.PRIM_RX, false);
		writeRegisterValue(Registers.CONFIG, configValue);

		// Restore pipe 0 TX address
		writeRegisterValues(Registers.RX_ADDR_P0, txAddressP0);

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
		writeRegisterValue(Registers.STATUS, (byte) 0b01110000);
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
		writeRegisterValue(Registers.STATUS, (byte) 0b01000000);
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
		writeRegisterValue(Registers.STATUS, (byte) 0b00110000);
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
		byte result = sendSpiCommand(SpiCommands.R_RX_PL_WID)[1];
		if (result > MAX_RX_FIFO_SIZE) {
			flushRx();
			throw new RxFifoTooLargeException();
		}
		return result;
	}

	public byte[] readPayload(byte length) throws RF24Exception {
		try {
			byte[] result = sendSpiCommand(SpiCommands.R_RX_PAYLOAD, new byte[length]);
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

		if (WritePayloadType.W_TX_PAYLOAD.equals(writePayloadType)) {
			boolean dataSent = false;
			boolean maxRetry = false;
			boolean timeout = false;

			long startTimeMillis = System.currentTimeMillis();
			while (!dataSent && !maxRetry && !timeout) {
				long now = System.currentTimeMillis();
				// Transmitted or Max retry
				status = getStatus();
				dataSent = RegistersUtils.bitIsTrue(status, Registers.STATUS.TX_DS);
				maxRetry = RegistersUtils.bitIsTrue(status, Registers.STATUS.MAX_RT);
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
				} else {
					throw new WritePayloadException();
				}
			}
		}
		else {
			resetTxInterrupt();
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

		SpiCommand spiCommand = null;
		if (writePayloadType == WritePayloadType.W_TX_PAYLOAD_NO_ACK) {
			spiCommand = SpiCommands.W_TX_PAYLOAD_NOACK;
		} else {
			spiCommand = SpiCommands.W_TX_PAYLOAD;
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
			return sendSpiCommand(SpiCommands.W_ACK_PAYLOAD, payload)[0];
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to write ack payload", e);
		}
	}

	public byte enableReuseTxPayload() throws RF24Exception {
		// First return byte is the value of the STATUS register
		return sendSpiCommand(SpiCommands.REUSE_TX_PL, null)[0];
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
	public byte readRegisterValue(RegisterByte register) throws RF24Exception {
		try {
			// The first byte is the value of the STATUS register
			return readRegister(register.getAddress(), (byte) 1)[1];
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to read register " + register.getName(), e);
		}
	}

	/**
	 * 
	 * @param register
	 * @return
	 * @throws RF24Exception
	 */
	public byte[] readRegisterValues(RegisterBytes register) throws RF24Exception {
		try {
			byte[] result = readRegister(register.getAddress(), register.getLength());
			// Remove the first byte - it is the value of the STATUS register
			return BytesUtils.removeFirstByte(result);
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to read register " + register.getName(), e);
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
			return sendSpiCommand(SpiCommandsUtils.getSpiCommand(SpiCommands.R_REGISTER, registerAddress), new byte[registerLength]);
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
	public byte writeRegisterValue(RegisterByte register, byte value) throws RF24Exception {
		if (RegisterByteWithBits.class.isAssignableFrom(register.getClass())) {
			RegistersUtils.checkRegisterMask((RegisterByteWithBits) register, value);
		}
		return writeRegister(register.getAddress(), value);
	}

	/**
	 * Write a value in a multi bytes register.
	 * 
	 * @param register	The register.
	 * @param value	Values to write.
	 * @return	The STATUS register value.
	 */
	public byte writeRegisterValues(RegisterBytes register, byte... value) throws RF24Exception {
		if (register.getLength() > value.length) {
			throw new IllegalArgumentException("values length cannot be longer than register length (values.length="
					+ value.length + " register.length=" + register.getLength() + ")");
		}
		try {
			return writeRegister(register.getAddress(), value);
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to write register "	+ register.getName(), e);
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
			return sendSpiCommand(SpiCommandsUtils.getSpiCommand(SpiCommands.W_REGISTER, registerAddress), value)[0];
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to write register " + registerAddress, e);
		}
	}


	/* ================
	 * Send SPI Command
	 * ================ */

	public byte[] sendSpiCommand(SpiCommand spiCommand) throws RF24Exception {
		try {
			return sendSpiCommand(spiCommand, new byte[spiCommand.getDataMaxLength()]);
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to send SPI command " + spiCommand.getName(), e);
		}
	}

	public byte[] sendSpiCommand(SpiCommand spiCommand, byte... data) throws RF24Exception {
		SpiCommandsUtils.checkDataLength(spiCommand, data);
		try {
			return sendSpiCommand(SpiCommandsUtils.getSpiCommand(spiCommand), data);
		} catch (RF24Exception e) {
			throw new RF24Exception("Failed to send SPI command " + spiCommand.getName(), e);
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
		byte setupawValue = readRegisterValue(Registers.SETUP_AW);
		byte awValue = RegistersUtils.getBitsValue(setupawValue, Registers.SETUP_AW.AW);
		return AddressFieldWidth.getFromValue(awValue);
	}

	public AutomaticRetransmitDelay getAutomaticRetransmitDelay() throws RF24Exception {
		byte setupretrValue = readRegisterValue(Registers.SETUP_RETR);
		byte ardValue = RegistersUtils.getBitsValue(setupretrValue, Registers.SETUP_RETR.ARD);
		return AutomaticRetransmitDelay.getFromValue(ardValue);
	}

	public byte getAutomaticRetransmitCount() throws RF24Exception {
		byte setupretrValue = readRegisterValue(Registers.SETUP_RETR);
		return RegistersUtils.getBitsValue(setupretrValue, Registers.SETUP_RETR.ARC);
	}

	public byte getFrequencyChannel() throws RF24Exception {
		byte rfchValue = readRegisterValue(Registers.RF_CH);
		return RegistersUtils.getBitsValue(rfchValue, Registers.RF_CH.RF_CH);
	}

	public DataRates getDataRates() throws RF24Exception {
		byte rfsetupValue = readRegisterValue(Registers.RF_SETUP);
		boolean rfDrLow = RegistersUtils.bitIsTrue(rfsetupValue, Registers.RF_SETUP.RF_DR_LOW);
		boolean rfDrHigh = RegistersUtils.bitIsTrue(rfsetupValue, Registers.RF_SETUP.RF_DR_HIGH);
		return DataRates.getFromValue(rfDrLow, rfDrHigh);
	}

	public OutputPower getOutputPower() throws RF24Exception {
		byte rfsetupValue = readRegisterValue(Registers.RF_SETUP);
		byte rfpwrValue = RegistersUtils.getBitsValue(rfsetupValue, Registers.RF_SETUP.RF_PWR);
		return OutputPower.getFromValue(rfpwrValue);
	}

	/**
	 * Return the STATUS register value.
	 * 
	 * @return	The STATUS register value.
	 * @throws RF24Exception	In case of communication error with RF Module.
	 */
	public byte getStatus() throws RF24Exception {
		return sendSpiCommand(SpiCommands.NOP)[0];
	}

	public boolean isDynamicPayloadsEnabled() throws RF24Exception {
		byte featureValue = readRegisterValue(Registers.FEATURE);
		return RegistersUtils.bitIsTrue(featureValue, Registers.FEATURE.EN_DPL);
	}

	public boolean isAcknowledgmentWithPayloadEnabled() throws RF24Exception {
		byte featureValue = readRegisterValue(Registers.FEATURE);
		return RegistersUtils.bitIsTrue(featureValue, Registers.FEATURE.EN_ACK_PAY);
	}

	public boolean isDynamicPayloadsEnabledOnPipe(DataPipe dataPipe) throws RF24Exception {
		byte dynpdValue = readRegisterValue(Registers.DYNPD);
		return (dynpdValue & dataPipe.getMask()) != 0;
	}

	public boolean isAutoAcknowledgmentEnabledOnPipe(DataPipe dataPipe) throws RF24Exception {
		byte enaaValue = readRegisterValue(Registers.EN_AA);
		return (enaaValue & dataPipe.getMask()) != 0;
	}

}
