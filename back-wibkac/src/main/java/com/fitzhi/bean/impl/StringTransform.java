package com.fitzhi.bean.impl;

import com.fitzhi.bean.StaffHandler;

/**
 * 
 * <p>
 * Utility interface to be used with the {@link StaffHandler#isElligible(fr.skiller.data.internal.Staff, String, StringTransform)}
 * </p>
 *
 * @author Fr&eacute;d&eacute;ric VIDAL
 *
 */
public interface StringTransform {
	
	/**
	 * Transform a string into a new transformed one.
	 * @param input the input string
	 * @return the new string after the transformation
	 */
	String process(String input);
}
