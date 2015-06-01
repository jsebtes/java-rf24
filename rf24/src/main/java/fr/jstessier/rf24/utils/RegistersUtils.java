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

import fr.jstessier.rf24.Registers.Register;
import fr.jstessier.rf24.Registers.RegisterBits;
import fr.jstessier.rf24.Registers.RegisterByte;
import fr.jstessier.rf24.Registers.RegisterByteWithBits;
import fr.jstessier.rf24.Registers.RegisterBytes;
import fr.jstessier.rf24.annotations.Address;
import fr.jstessier.rf24.annotations.Length;
import fr.jstessier.rf24.annotations.Mask;
import fr.jstessier.rf24.annotations.ResetValue;
import fr.jstessier.rf24.annotations.ResetValues;
import fr.jstessier.rf24.annotations.Shift;
import fr.jstessier.rf24.exceptions.BitsNotBelongsToRegisterException;
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

	/**
	 * Return the address of a register.
	 * 
	 * @param register	The register.
	 * @return	The register address.
	 */
	public static byte getRegisterAddress(final Class<? extends Register> register) {
		return register.getAnnotation(Address.class).value();
	}

	/**
	 * Return the reset value of a one byte register.
	 * 
	 * @param register	The register.
	 * @return	The reset value.
	 */
	public static byte getRegisterResetValue(final Class<? extends RegisterByte> register) {
		return register.getAnnotation(ResetValue.class).value();
	}

	/**
	 * Return the mask of a bits compound register.
	 * 
	 * @param register	The register.
	 * @return	The mask.
	 */
	public static byte getRegisterMask(final Class<? extends RegisterByteWithBits> register) {
		return register.getAnnotation(Mask.class).value();
	}

	/**
	 * Return the number of bytes of a "multi bytes" register.
	 * 
	 * @param register	The register.
	 * @return	Number of bytes of a "multi bytes" register.
	 */
	public static byte getRegisterLength(final Class<? extends RegisterBytes> register) {
		return register.getAnnotation(Length.class).value();
	}

	/**
	 * Return reset values of a "multi bytes" register.
	 * 
	 * @param register	The register.
	 * @return	Reset values.
	 */
	public static byte[] getRegisterResetValues(final Class<? extends RegisterBytes> register) {
		return register.getAnnotation(ResetValues.class).value();
	}


	public static byte getRegisterBitsShift(final Class<? extends RegisterBits> bits) {
		return bits.getAnnotation(Shift.class).value();
	}

	public static byte getRegisterBitsMask(final Class<? extends RegisterBits> bits) {
		return bits.getAnnotation(Mask.class).value();
	}

	public static byte getRegisterBitsResetValue(final Class<? extends RegisterBits> bits) {
		return bits.getAnnotation(ResetValue.class).value();
	}


	public static void checkRegisterMask(final Class<RegisterByteWithBits> register, final byte value) {
		final byte mask = getRegisterMask(register);
		if ((mask & value) != value) {
			throw new RegisterMaskException(register.getClass().getSimpleName(), value, mask);
		}
	}

	public static byte updateRegisterBits(final Class<? extends RegisterByteWithBits> register, final byte registerValue, 
			final Class<? extends RegisterBits> bits, final boolean bitsValue) {
		return updateRegisterBits(register, registerValue, bits, bitsValue ? (byte) 1 : 0); 
	}

	public static byte updateRegisterBits(final Class<? extends RegisterByteWithBits> register, final byte registerValue, 
			final Class<? extends RegisterBits> bits, final byte bitsValue) {
		checkBitsBelongToRegister(register, bits);
		checkRegisterBitsMask(register, bits, bitsValue);

		final byte shift = getRegisterBitsShift(bits);
		final byte shiftedBitsValue = (byte) (bitsValue << shift);
		final byte clearedRegisterValue = (byte) ((byte) ~ (getRegisterBitsMask(bits) << shift) & registerValue);

		return (byte) (clearedRegisterValue | shiftedBitsValue);
	}

	public static byte resetRegisterBits(final Class<? extends RegisterByteWithBits> register, final byte registerValue, 
			final Class<? extends RegisterBits> bits) {
		return updateRegisterBits(register, registerValue, bits, getRegisterBitsResetValue(bits));
	}

	public static byte getBitsValue(final Class<? extends RegisterByteWithBits> register, final byte registerValue, 
			final Class<? extends RegisterBits> bits) {
		checkBitsBelongToRegister(register, bits);
		return (byte) ((Byte.toUnsignedInt(registerValue) >>> getRegisterBitsShift(bits)) & getRegisterBitsMask(bits));
	}

	public static boolean bitIsTrue(final Class<? extends RegisterByteWithBits> register, final byte registerValue, 
			final Class<? extends RegisterBits> bits) {
		if (getRegisterBitsMask(bits) != 1) {
			throw new IllegalArgumentException(bits.getSimpleName() + "is not one bit length");
		}
		return getBitsValue(register, registerValue, bits) == 1;
	}

	public static boolean bitIsFalse(final Class<? extends RegisterByteWithBits> register, final byte registerValue, 
			final Class<? extends RegisterBits> bits) {
		return !bitIsTrue(register, registerValue, bits);
	}

	private static void checkRegisterBitsMask(final Class<? extends RegisterByteWithBits> register, 
			final Class<? extends RegisterBits> bits, final byte bitsValue) {
		final byte bitsMask = getRegisterBitsMask(bits);
		if ((bitsMask & bitsValue) != bitsValue) {
			throw new RegisterBitsMaskException(register.getSimpleName(), bits.getSimpleName(), bitsValue, bitsMask);
		}
	}

	private static void checkBitsBelongToRegister(final Class<? extends RegisterByteWithBits> register, 
			final Class<? extends RegisterBits> bits) {
		if (register.equals(bits.getSuperclass())) {
			throw new BitsNotBelongsToRegisterException(register.getSimpleName(), bits.getClass().getSimpleName());
		}
	}

}
