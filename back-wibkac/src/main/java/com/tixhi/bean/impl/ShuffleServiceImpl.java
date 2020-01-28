package com.tixhi.bean.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tixhi.bean.ShuffleService;

/**
 * Main (and unique) implementation of the shuffle service.
 * @author Fr&eacute;d&eacute;ric VIDAL
 */
@Service("shuffleMode")
public class ShuffleServiceImpl implements ShuffleService {

	private final int OFFSET = 14;
	
	/**
	 * Should part of the data be shuffled ?<br/>
	 * For example, the last name property is a good candidate to be shuffled.  
	 * The shuffle mode exists for documentation purpose.
	 * if this <code>boolean</code> is true, the saving process will be deactivated.
	 */
	@Value("${shuffleData}")
	private boolean shuffleData;

	/**
	 * Shuffle the input string into a new one
	 * @param input the input string
	 * @return the shuffled result
	 */
	public String shuffle (final String input) {
		// Nothing to shuffle.
		if ((input == null) || (input.isEmpty())) {
			return input;
		}
		String[] scram = input.split("");
		List<String> letters = Arrays.asList(scram);
		Collections.shuffle(letters);
		
		return	letters.stream().map(this::rotateVowel)
				.collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
				.toString();
	}
	
	@Override
	public String scramble(String input) {
		StringBuilder sb = new StringBuilder();
		String alphabet = "abcdefghijklmopqrstuvwxyzabcdefghijklmopqrstuvwxyz1ABCEDFGHIJKLMNOPQRSTUVWXYXZABCEDFGHIJKLMNOPQRSTUVWXYXZ";
		for (int i=0; i<input.length(); i++) {
			char c = input.charAt(i);
			if (c == ' ') {
				sb.append(c);
				continue;
			}
			if (c == '-') {
				sb.append(c);
				continue;
			}
			if (c == '_') {
				sb.append(c);
				continue;
			}
			if (c == '/') {
				sb.append(c);
				continue;
			}
			int pos = alphabet.indexOf(c);
			sb.append(alphabet.charAt(pos+OFFSET));
		}
		return sb.toString();
	}
	
	
	/**
	 * Scramble the vowels. 
	 * @param s a string containing which might contain a vowel
	 * @return a vowel.
	 */
	private String rotateVowel(String s) {
		String ret = s;
		if ("e".equalsIgnoreCase(s)) {
			ret = "a";
		}
		if ("a".equalsIgnoreCase(s)) {
			ret = "i";
		} 
		if ("i".equalsIgnoreCase(s)) {
			ret = "u";
		} 
		if ("o".equalsIgnoreCase(s)) {
			ret = "e";
		} 
		if ("u".equalsIgnoreCase(s)) {
			ret = "o";
		}						
		return ret;
	}

	@Override
	public boolean isShuffleMode() {
		return this.shuffleData;
	}
	
}
