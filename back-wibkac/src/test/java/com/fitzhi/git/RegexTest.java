package com.fitzhi.git;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;

import junit.framework.TestCase;

public class RegexTest extends TestCase {

	@org.junit.Test
	public void test() {
		String s = "jhdqskjhdsqkjhkj/dqshgqdjhqgjh/hjgqsjhgdjhgqj/toto.java";
		Pattern p = Pattern.compile("(.java$|.js$|.css$|.html$|.ts$)");
		Matcher matcher = p.matcher(s);
		Assert.assertTrue(matcher.find());
		
		s = "jhdqskjhdsqkjhkj/dqshg.javaqdjhqgjh/hjgqsjhgdjhgqj/toto.doc";
		matcher = p.matcher(s);
		Assert.assertFalse(matcher.find());
	}
	
	@org.junit.Test
	/**
	 * ^ 					- start of string anchor
	 * (?!.*DontMatchThis) 	- a negative lookahead checking if there are any 0 or more characters (matched with greedy .* subpattern - NOTE a lazy .*? version (matching as few characters as possible before the next subpattern match) might get the job done quicker if DontMatchThis is expected closer to the string start) followed with DontMatchThis
	 *	.* 					- any zero or more characters, as many as possible, up to
	 *	$  					- the end of string (see Anchor Characters: Dollar ($)).
	 */
	public void test2() {
		String s = "jhdqskjhd/app/vendor/sqkjhkj/dqshgqdjhqgjh/hjgqsjhgdjhgqj/toto.java";
		Pattern p = Pattern.compile("^(?!.*/app/vendor/).*$");
		Matcher matcher = p.matcher(s);
		Assert.assertFalse(matcher.find());
		
		s = "jhdqskjhd/app/venor/sqkjhkj/dqshgqdjhqgjh/hjgqsjhgdjhgqj/toto.java";
		matcher = p.matcher(s);
		Assert.assertTrue(matcher.find());
	}
}
