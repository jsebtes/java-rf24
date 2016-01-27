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

import fr.jstessier.rf24.Registers.RegisterBits;
import fr.jstessier.rf24.Registers.RegisterByteWithBits;
import fr.jstessier.rf24.exceptions.RegisterBitsMaskException;
import fr.jstessier.rf24.exceptions.RegisterMaskException;

/**
 * Utilities for registers.
 * 
 * @author J.S. TESSIER
 */
public class RegistersUtils {

	/**
	 * Private constructor.
	 */
	private RegistersUtils() {
		// NOP
	}

	public static byte updateRegisterBits(final byte registerValue, final RegisterBits bits, final boolean bitsValue) {
		if (bits.getMask() != 1) {
			throw new IllegalArgumentException(bits.getName() + "is not one bit length");
		}
		return updateRegisterBits(registerValue, bits, bitsValue ? (byte) 1 : 0); 
	}

	public static byte updateRegisterBits(final byte registerValue, final RegisterBits bits, final byte bitsValue) {
		checkRegisterBitsMask(bits, bitsValue);

		final byte shift = bits.getShift();
		final byte shiftedBitsValue = (byte) (bitsValue << shift);
		final byte clearedRegisterValue = (byte) ((byte) ~ (bits.getMask() << shift) & registerValue);

		return (byte) (clearedRegisterValue | shiftedBitsValue);
	}

	public static byte resetRegisterBits(final byte registerValue, final RegisterBits bits) {
		return updateRegisterBits(registerValue, bits, bits.getResetValue());
	}

	public static byte getBitsValue(final byte registerValue, final RegisterBits bits) {
		return (byte) ((Byte.toUnsignedInt(registerValue) >>> bits.getShift()) & bits.getMask());
	}

	public static boolean bitIsTrue(final byte registerValue, final RegisterBits bits) {
		if (bits.getMask() != 1) {
			throw new IllegalArgumentException(bits.getName() + "is not one bit length");
		}
		return getBitsValue(registerValue, bits) == 1;
	}

	public static boolean bitIsFalse(final byte registerValue, final RegisterBits bits) {
		return !bitIsTrue(registerValue, bits);
	}

	public static void checkRegisterMask(final RegisterByteWithBits register, final byte value) {
		final byte mask = register.getMask();
		if ((mask & value) != value) {
			throw new RegisterMaskException(register.getClass().getSimpleName(), value, mask);
		}
	}

	public static void checkRegisterBitsMask(final RegisterBits bits, final byte bitsValue) {
		final byte bitsMask = bits.getMask();
		if ((bitsMask & bitsValue) != bitsValue) {
			throw new RegisterBitsMaskException(bits.getName(), bitsValue, bitsMask);
		}
	}

}
