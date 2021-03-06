/**
 * This file is part of Vitam Project.
 * 
 * Copyright 2012, Frederic Bregier, and individual contributors by the @author tags. See the
 * COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 * 
 * All Vitam Project is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Vitam is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Vitam. If not, see
 * <http://www.gnu.org/licenses/>.
 */
/*
 * Written by Robert Harder and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain
 */
package fr.gouv.culture.vitam.digest;

/**
 * Code from Netty, adapted to be a standard algorithm from byte arrays. Enumeration of supported
 * Base64 dialects.
 * <p>
 * The internal lookup tables in this class has been derived from <a
 * href="http://iharder.sourceforge.net/current/java/base64/">Robert Harder's Public Domain Base64
 * Encoder/Decoder</a>.
 * 
 * @author Netty
 * @author "Frederic Bregier"
 */
public enum Base64Dialect {
	/**
	 * Standard Base64 encoding as described in the Section 3 of <a
	 * href="http://www.faqs.org/rfcs/rfc3548.html">RFC3548</a>.
	 */
	STANDARD(new byte[] {
			(byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E',
			(byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J',
			(byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O',
			(byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T',
			(byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y',
			(byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd',
			(byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i',
			(byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
			(byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's',
			(byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x',
			(byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2',
			(byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
			(byte) '8', (byte) '9', (byte) '+', (byte) '/' },
			new byte[] {
					-9, -9, -9, -9, -9, -9,
					-9, -9, -9, // Decimal 0 - 8
					-5, -5, // Whitespace: Tab and Linefeed
					-9, -9, // Decimal 11 - 12
					-5, // Whitespace: Carriage Return
					-9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 14 - 26
					-9, -9, -9, -9, -9, // Decimal 27 - 31
					-5, // Whitespace: Space
					-9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 33 - 42
					62, // Plus sign at decimal 43
					-9, -9, -9, // Decimal 44 - 46
					63, // Slash at decimal 47
					52, 53, 54, 55, 56, 57, 58, 59, 60, 61, // Numbers zero through nine
					-9, -9, -9, // Decimal 58 - 60
					-1, // Equals sign at decimal 61
					-9, -9, -9, // Decimal 62 - 64
					0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, // Letters 'A' through 'N'
					14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, // Letters 'O' through 'Z'
					-9, -9, -9, -9, -9, -9, // Decimal 91 - 96
					26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, // Letters 'a' through 'm'
					39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, // Letters 'n' through 'z'
					-9, -9, -9, -9, // Decimal 123 - 126
			/*
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 127 - 139
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 140 - 152
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 153 - 165
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 166 - 178
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 179 - 191
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 192 - 204
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 205 - 217
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 218 - 230
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 231 - 243
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9 // Decimal 244 - 255
			 */
			}, true),
	/**
	 * Base64-like encoding that is URL-safe as described in the Section 4 of <a
	 * href="http://www.faqs.org/rfcs/rfc3548.html">RFC3548</a>. It is important to note that data
	 * encoded this way is <em>not</em> officially valid Base64, or at the very least should not be
	 * called Base64 without also specifying that is was encoded using the URL-safe dialect.
	 */
	URL_SAFE(new byte[] {
			(byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E',
			(byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J',
			(byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O',
			(byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T',
			(byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y',
			(byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd',
			(byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i',
			(byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n',
			(byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's',
			(byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x',
			(byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2',
			(byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
			(byte) '8', (byte) '9', (byte) '-', (byte) '_' },
			new byte[] {
					-9, -9, -9, -9, -9, -9,
					-9, -9, -9, // Decimal 0 - 8
					-5, -5, // Whitespace: Tab and Linefeed
					-9, -9, // Decimal 11 - 12
					-5, // Whitespace: Carriage Return
					-9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 14 - 26
					-9, -9, -9, -9, -9, // Decimal 27 - 31
					-5, // Whitespace: Space
					-9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 33 - 42
					-9, // Plus sign at decimal 43
					-9, // Decimal 44
					62, // Minus sign at decimal 45
					-9, // Decimal 46
					-9, // Slash at decimal 47
					52, 53, 54, 55, 56, 57, 58, 59, 60, 61, // Numbers zero through nine
					-9, -9, -9, // Decimal 58 - 60
					-1, // Equals sign at decimal 61
					-9, -9, -9, // Decimal 62 - 64
					0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, // Letters 'A' through 'N'
					14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, // Letters 'O' through 'Z'
					-9, -9, -9, -9, // Decimal 91 - 94
					63, // Underscore at decimal 95
					-9, // Decimal 96
					26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, // Letters 'a' through 'm'
					39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, // Letters 'n' through 'z'
					-9, -9, -9, -9, // Decimal 123 - 126
			/*-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 127 - 139
			  -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 140 - 152
			  -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 153 - 165
			  -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 166 - 178
			  -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 179 - 191
			  -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 192 - 204
			  -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 205 - 217
			  -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 218 - 230
			  -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 231 - 243
			  -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9         // Decimal 244 - 255 */
			}, false),
	/**
	 * Special "ordered" dialect of Base64 described in <a
	 * href="http://www.faqs.org/qa/rfcc-1940.html">RFC1940</a>.
	 */
	ORDERED(new byte[] {
			(byte) '-', (byte) '0', (byte) '1', (byte) '2', (byte) '3',
			(byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8',
			(byte) '9', (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D',
			(byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I',
			(byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
			(byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S',
			(byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X',
			(byte) 'Y', (byte) 'Z', (byte) '_', (byte) 'a', (byte) 'b',
			(byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g',
			(byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l',
			(byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q',
			(byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v',
			(byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z' },
			new byte[] {
					-9, -9, -9, -9, -9, -9,
					-9, -9, -9, // Decimal 0 - 8
					-5, -5, // Whitespace: Tab and Linefeed
					-9, -9, // Decimal 11 - 12
					-5, // Whitespace: Carriage Return
					-9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 14 - 26
					-9, -9, -9, -9, -9, // Decimal 27 - 31
					-5, // Whitespace: Space
					-9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 33 - 42
					-9, // Plus sign at decimal 43
					-9, // Decimal 44
					0, // Minus sign at decimal 45
					-9, // Decimal 46
					-9, // Slash at decimal 47
					1, 2, 3, 4, 5, 6, 7, 8, 9, 10, // Numbers zero through nine
					-9, -9, -9, // Decimal 58 - 60
					-1, // Equals sign at decimal 61
					-9, -9, -9, // Decimal 62 - 64
					11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, // Letters 'A' through 'M'
					24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, // Letters 'N' through 'Z'
					-9, -9, -9, -9, // Decimal 91 - 94
					37, // Underscore at decimal 95
					-9, // Decimal 96
					38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, // Letters 'a' through 'm'
					51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, // Letters 'n' through 'z'
					-9, -9, -9, -9, // Decimal 123 - 126
			/*
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 127 - 139
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 140 - 152
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 153 - 165
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 166 - 178
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 179 - 191
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 192 - 204
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 205 - 217
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 218 - 230
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9, // Decimal 231 - 243
			 * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9 // Decimal 244 - 255
			 */
			}, true);

	final byte[] alphabet;
	final byte[] decodabet;
	final boolean breakLinesByDefault;

	Base64Dialect(byte[] alphabet, byte[] decodabet, boolean breakLinesByDefault) {
		this.alphabet = alphabet;
		this.decodabet = decodabet;
		this.breakLinesByDefault = breakLinesByDefault;
	}
}
