/*
 * Copyright (C) 2013-2014 Sergey V. Mikayev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.essiembre.eclipse.rbe.model.utils;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Compares two strings (case sensitivity is an option) and takes care of any numbers
 * they contain. Numbers are compared numerically, not lexicographically, i.e.<br>
 * <code>"a12" > "a9"</code><br>
 * If two strings differ only by their case, the result of comparison is
 * the same as for case sensitive lexicographical comparison. This allows not to break
 * general contracts of {@link #equals()} and {@link #hashCode()}.
 */
public class AlphaNumericComparator<T> implements Comparator<T> {
	static private final int COMPARE_RESULT_EQUAL = 0;
	static private final int COMPARE_RESULT_GREATER = 1;
	static private final int COMPARE_RESULT_LESS = -1;

	/**
	 * @return Whether <code>c</code> is a digit or not.
	 */
	static private boolean isDigit(char c) {
		return '0' <= c && c <= '9';
	}

	/**
	 * Returns the number of digits that <code>s</code> contains starting
	 * from <code>index</code>. <code>index</code> is assumed to point to a digit.
	 * @param s
	 * @param index
	 * @return numeric length
	 */
	static private int getNumericSubStringLength(String s, int index) {
		int currentIndex = index;
		while (++currentIndex < s.length()) {
			if (!isDigit(s.charAt(currentIndex))) {
				break;
			}
		}
		return currentIndex - index;
	}

	/**
	 * Left-pads a numeric <code>String</code> with zeros
	 * @param s - <code>String</code> to align
	 * @param requiredLength - the minimum length of the resulting <code>String</code>
	 * @return Aligned <code>String</code>
	 */
	static private String alignNumericString(String s, int requiredLength) {
		int count = requiredLength - s.length();
		if (count > 0) {
			char[] zeros = new char[count];
			Arrays.fill(zeros, '0');
			return new String(zeros) + s;
		}
		return s;
	}

	/**
	 * Performs lexicographical (case insensitive) comparison of two strings
	 * treating any numeric substrings as numbers.
	 * @param s1 - first <code>String</code> to compare
	 * @param s2 - second <code>String</code> to compare
	 * @return A negative integer, zero, or a positive integer depending on
	 * the first <code>String</code> is less than, equal to, or greater than the second.
	 */
	static private int compareAlphaNumericStrings(String s1, String s2) {

		// Perform comparison within the length of the shortest string
		int minLength = Math.min(s1.length(), s2.length());
		for (int index = 0; index < minLength; index++) {
			char c1 = s1.charAt(index);
			char c2 = s2.charAt(index);
			if (isDigit(c1) && isDigit(c2)) {

				// Both characters are digits
				// Align both numeric substrings to the right
				// and do lexicographical compare
				int num1Length = getNumericSubStringLength(s1, index);
				int num2Length = getNumericSubStringLength(s2, index);
				int maxNumLength = Math.max(num1Length, num2Length);
				String numStr1 = alignNumericString(s1.substring(index, index + num1Length), maxNumLength);
				String numStr2 = alignNumericString(s2.substring(index, index + num2Length), maxNumLength);
				int result = numStr1.compareTo(numStr2);
				if (result != COMPARE_RESULT_EQUAL) {
					return result;
				}

				// Both numbers are equal, compare the lengths
				result = num1Length - num2Length;
				if (result != COMPARE_RESULT_EQUAL) {
					return result;
				}

				// Both numeric strings are identical, continue
				index += maxNumLength - 1;
			} else {

				// One of the characters is not a digit
				// Do lexicographical compare
				if (c1 != c2) {
					return c1 - c2;
				}
			}
		}

		// Both strings are identical within the minLength, compare the lengths
		return s1.length() - s2.length();
	}

	private final boolean caseSensitivePreferred;

	/**
	 * Creates an instance of <code>AlphaNumericComparator</code>.
	 * Comparison is case sensitive by default.
	 */
	public AlphaNumericComparator() {
		this(true);
	}

	/**
	 * Creates an instance of <code>AlphaNumericComparator</code>.
	 * @param caseSensitivePreferred - determines whether to ignore case
	 */
	public AlphaNumericComparator(boolean caseSensitivePreferred) {
		super();
		this.caseSensitivePreferred = caseSensitivePreferred;
	}

	@Override
	public int compare(T o1, T o2) {
		if (o1 == null) {
			return o2 == null ? COMPARE_RESULT_EQUAL : COMPARE_RESULT_LESS;
		}
		if (o2 == null) {
			return COMPARE_RESULT_GREATER;
		}
		if (!caseSensitivePreferred) {
			int result = compareAlphaNumericStrings(o1.toString().toLowerCase(), o2.toString().toLowerCase());
			if (result != COMPARE_RESULT_EQUAL) {
				return result;
			}
		}
		return compareAlphaNumericStrings(o1.toString(), o2.toString());
	}
}
