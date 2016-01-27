package fr.jstessier;

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

import fr.jstessier.rf24.RF24;
import fr.jstessier.rf24.enums.DataPipe;
import fr.jstessier.rf24.enums.DataRates;
import fr.jstessier.rf24.enums.OutputPower;
import fr.jstessier.rf24.exceptions.RF24Exception;
import fr.jstessier.rf24.hardware.RF24HardwarePi4j;
import fr.jstessier.rf24.utils.BytesUtils;
import fr.jstessier.rf24.utils.ThreadUtils;

/**
 * 
 * @author J.S. TESSIER
 */
public class PingPairDyn {

	public static void main(String[] args) throws RF24Exception {
		boolean ping = true;
		boolean ackpayload = true;
		if (ping) {
			if (ackpayload) {
				pingOutWithAckPayload();
			}
			else {
				pingOut();
			}
		}
		else {
			pongBack();
		}
	}

	private static void pingOutWithAckPayload() throws RF24Exception {
		RF24 rf24 = new RF24(new RF24HardwarePi4j((byte) 0, (byte) 3));
		rf24.initialize();
		rf24.setDataRatesAndOutputPower(DataRates.DR_250_KBPS, OutputPower.RF_0_DBM);

		rf24.openWritingPipe(new byte[] { (byte) 0xD2, (byte) 0xF2, (byte) 0xF2, (byte) 0xF2, (byte) 0xF2 });
		rf24.openReadingPipe(DataPipe.P1, new byte[] { (byte) 0xD1, (byte) 0xF1, (byte) 0xF1, (byte) 0xF1, (byte) 0xF1 });

		final byte[] fullPayload = "ABCDEFGHIJKLMNOPQRSTUVWXYZ789012".getBytes();

		ThreadUtils.delay(1000);

		int nextPayloadSize = 4;
		while (true) {
			try {
				byte[] payloadPingOut = new byte[nextPayloadSize];
				System.arraycopy(fullPayload, 0, payloadPingOut, 0, nextPayloadSize);
				System.out.println("Now sending length " + nextPayloadSize);
				byte[] payloadAck = rf24.sendPayloadAndReadAckPayload(payloadPingOut);

				if (payloadAck.length > 0) {
					System.out.println(String.format("Got ack payload size=%2d value=%s", payloadAck.length, BytesUtils.bytesToString(payloadAck)));
				}
				else {
					System.out.println("No payload in ack");
				}

				nextPayloadSize++;
				if (nextPayloadSize > 32) {
					nextPayloadSize = 4;
				}
				
			} catch (RF24Exception e) {
				e.printStackTrace();
				rf24.resetAllInterrupts();
				rf24.flushRx();
				rf24.flushTx();
				rf24.stopListening();
			}

			ThreadUtils.delay(100);
		}
	}

	private static void pingOut() throws RF24Exception {
		RF24 rf24 = new RF24(new RF24HardwarePi4j((byte) 0, (byte) 3));
		rf24.initialize();

		rf24.openWritingPipe(new byte[] { (byte) 0xD2, (byte) 0xF2, (byte) 0xF2, (byte) 0xF2, (byte) 0xF2 });
		rf24.openReadingPipe(DataPipe.P1, new byte[] { (byte) 0xD1, (byte) 0xF1, (byte) 0xF1, (byte) 0xF1, (byte) 0xF1 });

		final byte[] fullPayload = "ABCDEFGHIJKLMNOPQRSTUVWXYZ789012".getBytes();

		ThreadUtils.delay(1000);

		int nextPayloadSize = 4;
		while (true) {
			try {
				byte[] payloadPingOut = new byte[nextPayloadSize];
				System.arraycopy(fullPayload, 0, payloadPingOut, 0, nextPayloadSize);
				System.out.println("Now sending length " + nextPayloadSize);
				rf24.sendPayload(payloadPingOut);

				rf24.startListening();

				nextPayloadSize++;
				if (nextPayloadSize > 32) {
					nextPayloadSize = 4;
				}

				long startTimeMillis = System.currentTimeMillis();
				boolean timeout = false;
				while (!rf24.isRxDataAvailable() && !timeout) {
					// Timeout
					if (System.currentTimeMillis() - startTimeMillis > 1000) {
						timeout = true;
					}
					ThreadUtils.delay(5);
				}

				if (timeout) {
					System.out.println("Failed, response timed out.");
				}
				else {
					byte length = rf24.getDynamicPayloadSize();
					if(length != payloadPingOut.length) {
						byte[] payloadPongBack = rf24.readPayload(length);
						System.out.println(String.format("Failed, response length response size=%2d value=%s expectedLength=%2d", length, BytesUtils.bytesToString(payloadPongBack), payloadPingOut.length));
					}
					else {
						byte[] payloadPongBack = rf24.readPayload(length);
						System.out.println(String.format("Got response size=%2d value=%s", length, BytesUtils.bytesToString(payloadPongBack)));
					}
				}

				rf24.stopListening();
			} catch (RF24Exception e) {
				e.printStackTrace();
				rf24.resetAllInterrupts();
				rf24.flushRx();
				rf24.flushTx();
				rf24.stopListening();
			}

			ThreadUtils.delay(100);
		}

	}

	private static void pongBack() throws RF24Exception {
		RF24 rf24 = new RF24(new RF24HardwarePi4j((byte) 0, (byte) 3));
		rf24.initialize();

		rf24.openWritingPipe(new byte[] { (byte) 0xD1, (byte) 0xF1, (byte) 0xF1, (byte) 0xF1, (byte) 0xF1 });
		rf24.openReadingPipe(DataPipe.P1, new byte[] { (byte) 0xD2, (byte) 0xF2, (byte) 0xF2, (byte) 0xF2, (byte) 0xF2 });
		rf24.startListening();

		while (true) {
			try {
				if (rf24.isRxDataAvailable()) {
					byte[] payload = new byte[100];
					
					while (rf24.isRxDataAvailable()) {
						byte length = rf24.getDynamicPayloadSize();
						payload = rf24.readPayload(length);
						System.out.println(String.format("Got payload size=%2d value=%s", length, BytesUtils.bytesToString(payload)));
					}

					rf24.stopListening();

					// Send the final one back.
					rf24.sendPayload(payload);
					System.out.println("Sent response.");

					rf24.startListening();
				}
			} catch (RF24Exception e) {
				e.printStackTrace();
				rf24.resetAllInterrupts();
				rf24.flushRx();
				rf24.flushTx();
				rf24.startListening();
			}
		}

	}
	
}
