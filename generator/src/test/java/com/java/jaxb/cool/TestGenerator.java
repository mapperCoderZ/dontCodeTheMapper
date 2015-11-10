package com.java.jaxb.cool;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Launch testMake() to test the generator.
 * 
 * @author GillesOFraisse
 *
 */
public class TestGenerator extends TestCase {
	public void testMake() {
		Generator g = new Generator();
		// A and B types must be in your classpath and have basic JAXB
		// annotations (XMLType, Size ...)
		// see the result in console
		System.out.println(g.mapAToB(com.developpez.hugo.modela.Profile.class, com.developpez.hugo.modelb.Profile.class,
				true, true, true));
	}

	/**
	 * Utility method used in rendered Java code.
	 * 
	 * @param value
	 *            nullable
	 * @param minLength
	 * @param maxLength
	 * @return null or string
	 */
	public String formatString(String value, Integer minLength, Integer maxLength) {
		if (value == null) {
			return null;
		}
		int max = value.length();
		if (maxLength != null) {
			max = maxLength.intValue();
		}
		int min = 0;
		if (minLength != null) {
			min = minLength.intValue();
		}
		String res;
		if (min > 0) {
			res = String.format("%1$-" + min + "s", value.substring(0, Math.min(value.length(), max - 1)));
		} else {
			res = value.substring(0, Math.min(value.length(), max));
		}
		return res;
	}

	public void testFormatString() {
		Assert.assertEquals(formatString("test", 9, 15), "test     ");
		Assert.assertEquals(formatString("", 9, 15), "         ");
		Assert.assertEquals(formatString("test grandeur nature", 9, 15), "test grandeur ");
		Assert.assertEquals(formatString("test", 0, 1), "t");
		Assert.assertEquals(formatString("test", 0, 2), "te");
		Assert.assertEquals(formatString(" ", 2, 10), "  ");
		Assert.assertEquals(formatString(" ", 2, null), "  ");
		Assert.assertEquals(formatString("azerty", null, 5), "azert");
	}

}
